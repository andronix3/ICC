/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of imagero Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.smartg.icc.tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.imagero.uio.io.IOutils;
import com.smartg.icc.Curve;
import com.smartg.icc.DateTimeNumber;
import com.smartg.icc.ParametricCurve;
import com.smartg.icc.tag.Tag.CLUT;
import com.smartg.icc.tag.Tag.ChromaticityType;
import com.smartg.icc.tag.Tag.ColorantOrder;
import com.smartg.icc.tag.Tag.ColorantTable;
import com.smartg.icc.tag.Tag.Data;
import com.smartg.icc.tag.Tag.DateTime;
import com.smartg.icc.tag.Tag.Desc;
import com.smartg.icc.tag.Tag.ICurve;
import com.smartg.icc.tag.Tag.Lut;
import com.smartg.icc.tag.Tag.LutAtoB;
import com.smartg.icc.tag.Tag.Measurement;
import com.smartg.icc.tag.Tag.MultiLocalizedUnicode;
import com.smartg.icc.tag.Tag.NamedColor2;
import com.smartg.icc.tag.Tag.S15Fixed16Array;
import com.smartg.icc.tag.Tag.Signature;
import com.smartg.icc.tag.Tag.Text;
import com.smartg.icc.tag.Tag.UInt16Array;
import com.smartg.icc.tag.Tag.UInt32Array;
import com.smartg.icc.tag.Tag.UInt64Array;
import com.smartg.icc.tag.Tag.UInt8Array;
import com.smartg.icc.tag.Tag.ViewingConditions;
import com.smartg.icc.tag.Tag.XYZ;

public abstract class TagReader {
    TagType tagType;

    public abstract Tag read(int length, InputStream in) throws IOException;

    protected static float read_s15Fixed16(InputStream in) throws IOException {
	float d = IOutils.readIntBE(in);
	return d / 65535f;
	// float k = (short) IOutils.readShort4D(in);
	// float f = IOutils.readShort4D(in);
	// f = f / 65535f;
	// return k + f;
    }

    protected static float read_u16Fixed16(InputStream in) throws IOException {
	float d = IOutils.readIntBE(in);
	return d / 65535f;
    }

    TagReader() {

    }

    TagReader(TagType tagType) {
	this.tagType = tagType;
    }

    static class ChromaticityTypeReader extends TagReader {

	ChromaticityTypeReader() {
	    super(TagType.CHROMATICITY_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    ChromaticityType res = new ChromaticityType(tagType);

	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.numberOfDeviceChannels = IOutils.readShortLE(in);
	    res.phosphorOrColorantType = IOutils.readShortLE(in);
	    res.xy = new float[res.numberOfDeviceChannels * 2];
	    for (int i = 0; i < res.xy.length; i++) {
		float k = IOutils.readShortBE(in);
		float f = IOutils.readShortBE(in);
		f = f / (65535 - 1);
		res.xy[i] = k + f;
	    }
	    return res;
	}
    }

    static class ColorantOrderReader extends TagReader {

	ColorantOrderReader() {
	    super(TagType.COLORANT_ORDER_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    ColorantOrder res = new ColorantOrder(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.colorantCount = IOutils.readIntBE(in);
	    res.colorantIndexes = new int[res.colorantCount];
	    for (int i = 0; i < res.colorantCount; i++) {
		res.colorantIndexes[i] = in.read();
	    }
	    return res;
	}
    }

    static class ColorantTableReader extends TagReader {

	ColorantTableReader() {
	    super(TagType.COLORANT_TABLE_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    ColorantTable res = new ColorantTable(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.colorantCount = IOutils.readIntBE(in);
	    res.colorantNames = new String[res.colorantCount];
	    res.pcsValues0 = new int[res.colorantCount];
	    res.pcsValues1 = new int[res.colorantCount];
	    res.pcsValues2 = new int[res.colorantCount];
	    for (int i = 0; i < res.colorantCount; i++) {
		byte[] b = new byte[32];
		IOutils.readFully(in, b);
		res.colorantNames[i] = new String(b);
		res.pcsValues0[i] = IOutils.readShortBE(in);
		res.pcsValues1[i] = IOutils.readShortBE(in);
		res.pcsValues2[i] = IOutils.readShortBE(in);
	    }
	    return res;
	}
    }

    static class CurveReader extends TagReader {

	CurveReader() {
	    super(TagType.CURVE_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    long reserved = IOutils.readUnsignedIntBE(in);
	    int entryCount = IOutils.readIntBE(in);
	    float[] values = new float[entryCount];
	    if (entryCount > 1) {
		for (int i = 0; i < entryCount; i++) {
		    int k = IOutils.readShortBE(in);
		    values[i] = k / 65535f;
		}
	    } else if (entryCount == 1) {
		float k = in.read();
		float f = in.read();
		f = f / 256;
		values[0] = k + f;
	    }
	    Curve res = new Curve(tagType, values);
	    res.setReserved(reserved);
	    return res;
	}
    }

    static class DataReader extends TagReader {

	DataReader() {
	    super(TagType.DATA_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    Data res = new Data(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.dataFlag = IOutils.readIntBE(in);
	    res.string = new byte[length - 12];
	    IOutils.readFully(in, res.string);
	    return res;
	}
    }

    static class DateTimeReader extends TagReader {

	DateTimeReader() {
	    super(TagType.DATE_TIME_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    DateTime res = new DateTime(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.dateTime = DateTimeNumber.read(in);
	    return res;
	}
    }

    static class LutReader extends TagReader {

	LutReader() {
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    Lut res = new Lut(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.numberOfInputChannels = in.read();
	    res.numberOfOutputChannels = in.read();
	    res.numberOfClutGridPoints = in.read();
	    res.padding = in.read();

	    res.e00 = read_s15Fixed16(in);
	    res.e01 = read_s15Fixed16(in);
	    res.e02 = read_s15Fixed16(in);
	    res.e10 = read_s15Fixed16(in);
	    res.e11 = read_s15Fixed16(in);
	    res.e12 = read_s15Fixed16(in);
	    res.e20 = read_s15Fixed16(in);
	    res.e21 = read_s15Fixed16(in);
	    res.e22 = read_s15Fixed16(in);

	    res.numberOfInputTableEntries = IOutils.readShortBE(in);
	    res.numberOfOutputTableEntries = IOutils.readShortBE(in);

	    int N = res.numberOfInputTableEntries;
	    int I = res.numberOfInputChannels;
	    int G = res.numberOfClutGridPoints;
	    int O = res.numberOfOutputChannels;
	    int M = res.numberOfOutputTableEntries;

	    res.inputTables = new float[N * I];
	    res.clutValues = new float[(int) (Math.pow(G, I) * O)];
	    res.outputTables = new float[M * O];
	    int[] gpCount = new int[I];
	    for (int i = 0; i < gpCount.length; i++) {
		gpCount[i] = G;
	    }
	    res.clut = new CLUT(I, gpCount, O);

	    if (tagType.getValue() == ICC_Constants.LUT_16_TYPE) {
		for (int i = 0; i < res.inputTables.length; i++) {
		    res.inputTables[i] = IOutils.readShortBE(in) / 65535f;
		}

		/* int count = */readClut16(res.clut, in);

		for (int i = 0; i < res.outputTables.length; i++) {
		    res.outputTables[i] = IOutils.readShortBE(in) / 65535f;
		}
	    } else if (tagType.getValue() == ICC_Constants.LUT_8_TYPE) {
		for (int i = 0; i < res.inputTables.length; i++) {
		    res.inputTables[i] = in.read() / 255f;
		}

		// for (int i = 0; i < res.clutValues.length; i++) {
		// res.clutValues[i] = in.read() / 255f;
		// }
		/* int count = */readClut8(res.clut, in);

		for (int i = 0; i < res.outputTables.length; i++) {
		    res.outputTables[i] = in.read() / 255f;
		}
	    }
	    return res;
	}
    }

    static int readClut16(CLUT clut, InputStream in) throws IOException {
	int count = 0;

	for (int g = 0; g < clut.table.length; g++) {
	    for (int o = 0; o < clut.outputCount; o++) {
		clut.table[g].data[o] = read_u16Fixed16(in) / 65535f;
		count++;
	    }
	}
	return count;
    }

    static int readClut8(CLUT clut, InputStream in) throws IOException {
	int count = 0;

	for (int g = 0; g < clut.table.length; g++) {
	    for (int o = 0; o < clut.outputCount; o++) {
		clut.table[g].data[o] = in.read() / 255f;
		count++;
	    }
	}

	return count;
    }

    static class LutAtoB_Reader extends TagReader {

	LutAtoB_Reader() {
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    LutAtoB res = new LutAtoB();
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.numberOfInputChannels = in.read();
	    res.numberOfOutputChannels = in.read();
	    res.padding2 = IOutils.readShortBE(in);
	    res.offsetToFirstB_Curve = IOutils.readUnsignedIntBE(in);
	    res.offsetToMatrix = IOutils.readUnsignedIntBE(in);
	    res.offsetToFirstM_Curve = IOutils.readUnsignedIntBE(in);
	    res.offsetToCLUT = IOutils.readUnsignedIntBE(in);
	    res.offsetToFirstA_Curve = IOutils.readUnsignedIntBE(in);

	    byte[] data = new byte[length - 32];
	    IOutils.readFully(in, data);

	    if (res.offsetToFirstB_Curve > 0) {
		int offset = (int) (res.offsetToFirstB_Curve - 32);
		res.bCurves = readCurves(offset, res.numberOfOutputChannels, data);
	    }

	    if (res.offsetToFirstM_Curve > 0) {
		int offset = (int) (res.offsetToFirstM_Curve - 32);
		res.mCurves = readCurves(offset, res.numberOfOutputChannels, data);
	    }

	    if (res.offsetToFirstA_Curve > 0) {
		int offset = (int) (res.offsetToFirstA_Curve - 32);
		res.aCurves = readCurves(offset, res.numberOfInputChannels, data);
	    }

	    if (res.offsetToMatrix > 0) {
		int offset = (int) (res.offsetToMatrix - 32);
		readMatrix(offset, data, res.matrix);
	    }

	    if (res.offsetToCLUT > 0) {
		int offset = (int) (res.offsetToCLUT - 32);
		res.clut = readClutInfo(offset, data, res.numberOfInputChannels, res.numberOfOutputChannels);
	    }

	    return res;
	}

	CLUT readClutInfo(int offset, byte[] data, int inputCount, int outputCount) throws IOException {
	    int[] gridPoints = new int[15];
	    InputStream in = new ByteArrayInputStream(data, offset, data.length - offset);
	    for (int i = 0; i < 15; i++) {
		gridPoints[i] = in.read();
	    }
	    int precision = in.read();
	    // skip padding
	    in.read();
	    in.read();
	    in.read();

	    CLUT clut = new CLUT(inputCount, gridPoints, outputCount);
	    if (precision == 1) {
		readClut8(clut, in);
	    } else if (precision == 2) {
		readClut16(clut, in);
	    } else {
		Logger.getGlobal().log(Level.SEVERE, "Unknown CLUT precision: " + precision);
	    }
	    return clut;
	}

	void readMatrix(int offset, byte[] data, float[] matrix) throws IOException {
	    InputStream in = new ByteArrayInputStream(data, offset, data.length - offset);
	    for (int i = 0; i < matrix.length; i++) {
		matrix[i] = read_s15Fixed16(in);
	    }
	}
    }

    static ICurve[] readCurves(int offset, int count, byte[] data) throws IOException {
	InputStream in = new ByteArrayInputStream(data, offset, data.length - offset);
	ICurve[] res = new ICurve[count];

	for (int i = 0; i < count; i++) {
	    long t0 = findTag(in);
	    TagType ttype = TagType.create(t0);
	    if (ttype != null) {
		TagReader reader = ttype.getTagReader();
		if (reader != null) {
		    Tag tag = reader.read(data.length, in);
		    if (tag instanceof ICurve) {
			res[i] = (ICurve) tag;
		    } else {
			Logger.getGlobal().log(Level.WARNING, "Unexpected tag:" + tag);
		    }
		} else {
		    Logger.getGlobal().log(Level.WARNING, "TagReader not found");
		}
	    } else {
		Logger.getGlobal().log(Level.WARNING, "Unknown TagType: " + t0);
	    }
	}

	return res;
    }

    static long findTag(InputStream in) throws IOException {
	long tag = 0;
	int k = 0;
	while (k == 0) {
	    k = in.read();
	}
	tag = k;
	for (int i = 0; i < 3; i++) {
	    k = in.read();
	    tag = (tag << 8) | k;
	}
	return tag;
    }

    static class ParametricCurveReader extends TagReader {

	ParametricCurveReader() {
	    super(TagType.PARAMETRIC_CURVE_TYPE);
	}

	@Override
	public Tag read(final int length, final InputStream in) throws IOException {
	    long reserved = IOutils.readUnsignedIntBE(in);
	    int functionType = IOutils.readShortBE(in);
	    int reserved2 = IOutils.readShortBE(in);
	    float[] params = {};

	    switch (functionType) {
	    case 0:
		params = new float[1];
		break;
	    case 1:
		params = new float[3];
		break;
	    case 2:
		params = new float[4];
		break;
	    case 3:
		params = new float[5];
		break;
	    case 4:
		params = new float[7];
		break;
	    }
	    for (int i = 0; i < params.length; i++) {
		params[i] = read_s15Fixed16(in);
	    }

	    ParametricCurve res = new ParametricCurve(tagType, functionType, params);
	    res.setReserved(reserved);
	    res.setReserved2(reserved2);

	    return res;
	}
    }

    static class MeasurementReader extends TagReader {

	MeasurementReader() {
	    super(TagType.MEASUREMENT_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    Measurement res = new Measurement(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.standardObserver = IOutils.readIntBE(in);

	    res.xyz[0] = read_s15Fixed16(in);
	    res.xyz[1] = read_s15Fixed16(in);
	    res.xyz[2] = read_s15Fixed16(in);

	    res.geometry = IOutils.readIntBE(in);
	    res.flare = read_u16Fixed16(in);
	    res.standardIlluminant = IOutils.readIntBE(in);
	    return res;
	}
    }

    static class MultiLocalizedUnicodeReader extends TagReader {

	MultiLocalizedUnicodeReader() {
	    super();
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    MultiLocalizedUnicode res = new MultiLocalizedUnicode(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.numberOfNames = IOutils.readIntBE(in);
	    res.nameRecordSize = IOutils.readUnsignedIntBE(in) & 0xFF;

	    res.nameLanguageCode = new int[res.numberOfNames];
	    res.nameCountryCode = new int[res.numberOfNames];
	    res.nameLength = new long[res.numberOfNames];
	    res.nameOffset = new long[res.numberOfNames];
	    res.names = new String[res.numberOfNames];

	    for (int i = 0; i < res.numberOfNames; i++) {
		res.nameLanguageCode[i] = IOutils.readShortBE(in);
		res.nameCountryCode[i] = IOutils.readShortBE(in);
		res.nameLength[i] = IOutils.readUnsignedIntBE(in);
		res.nameOffset[i] = IOutils.readUnsignedIntBE(in);
	    }
	    for (int i = 0; i < res.numberOfNames; i++) {
		byte[] data = new byte[(int) res.nameLength[i]];
		IOutils.readFully(in, data);
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(data), "UTF-16");
		CharBuffer cBuffer = CharBuffer.allocate(data.length);
		while (reader.ready()) {
		    reader.read(cBuffer);
		}
		cBuffer.flip();
		res.names[i] = cBuffer.toString();
		// res.names[i] =
	    }
	    // TODO read strings
	    return res;
	}
    }

    static class NamedColor2Reader extends TagReader {

	NamedColor2Reader() {
	    super(TagType.NAMED_COLOR_2_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    NamedColor2 res = new NamedColor2(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.vendorSpecificFlag = IOutils.readUnsignedIntBE(in);

	    res.countOfNamedColors = IOutils.readIntBE(in);
	    res.numberOfDevideCoordinatesForEachNamedColor = IOutils.readIntBE(in);

	    IOutils.readFully(in, res.prefixForEachColorName);
	    IOutils.readFully(in, res.suffixForEachColorName);

	    res.colorNames = new byte[res.countOfNamedColors][32];
	    res.colorPCS_Coordinates = new long[res.countOfNamedColors][3];
	    res.colorDeviceCoordinates = new long[res.countOfNamedColors][res.numberOfDevideCoordinatesForEachNamedColor];

	    for (int i = 0; i < res.countOfNamedColors; i++) {
		IOutils.readFully(in, res.colorNames[i]);

		res.colorPCS_Coordinates[i][0] = IOutils.readUnsignedIntBE(in);
		res.colorPCS_Coordinates[i][1] = IOutils.readUnsignedIntBE(in);
		res.colorPCS_Coordinates[i][2] = IOutils.readUnsignedIntBE(in);

		for (int j = 0; j < res.numberOfDevideCoordinatesForEachNamedColor; j++) {
		    res.colorDeviceCoordinates[i][j] = IOutils.readUnsignedIntBE(in);
		}
	    }
	    return res;
	}
    }

    static class S15Fixed16ArrayReader extends TagReader {

	S15Fixed16ArrayReader() {
	    super(TagType.S_15_FIXED_16_ARRAY_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    S15Fixed16Array res = new S15Fixed16Array(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    int count = (length - 8) / 4;
	    res.values = new float[count];
	    for (int i = 0; i < count; i++) {
		res.values[i] = read_s15Fixed16(in);
	    }
	    return res;
	}
    }

    static class U16Fixed16ArrayReader extends TagReader {

	U16Fixed16ArrayReader() {
	    super(TagType.U_16_FIXED_16_ARRAY_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    S15Fixed16Array res = new S15Fixed16Array(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    int count = (length - 8) / 4;
	    res.values = new float[count];
	    for (int i = 0; i < count; i++) {
		res.values[i] = read_u16Fixed16(in);
	    }
	    return res;
	}
    }

    static class SignatureReader extends TagReader {

	SignatureReader() {
	    super(TagType.SIGNATURE_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    Signature res = new Signature(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.signature = IOutils.readUnsignedIntBE(in);
	    return res;
	}
    }

    static class TextReader extends TagReader {

	TextReader() {
	    super(TagType.TEXT_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    Text res = new Text(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.string = new byte[length - 8];
	    IOutils.readFully(in, res.string);
	    return res;
	}
    }
    
    static class DescReader extends TagReader {

	DescReader() {
	    super(TagType.TEXT_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    Desc res = new Desc(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    res.size = IOutils.readUnsignedIntBE(in);
	    res.string = new byte[(int) res.getSize()];
	    IOutils.readFully(in, res.string);
	    return res;
	}
    }


    static class UInt16ArrayReader extends TagReader {

	UInt16ArrayReader() {
	    super(TagType.U_INT_16_ARRAY_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    UInt16Array res = new UInt16Array(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    int count = (length - 8) / 2;
	    res.values = new int[count];
	    for (int i = 0; i < count; i++) {
		res.values[i] = IOutils.readShortBE(in);
	    }
	    return res;
	}
    }

    static class UInt32ArrayReader extends TagReader {

	UInt32ArrayReader() {
	    super(TagType.U_INT_32_ARRAY_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    UInt32Array res = new UInt32Array(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    int count = (length - 8) / 4;
	    res.values = new long[count];
	    for (int i = 0; i < count; i++) {
		res.values[i] = IOutils.readUnsignedIntBE(in);
	    }
	    return res;
	}
    }

    static class UInt64ArrayReader extends TagReader {

	UInt64ArrayReader() {
	    super(TagType.U_INT_64_ARRAY_TYPE);
	}

	@Override
	public Tag read(final int length, final InputStream in) throws IOException {
	    UInt64Array res = new UInt64Array(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    int count = (length - 8) / 8;
	    res.values = new long[count];
	    for (int i = 0; i < count; i++) {
		res.values[i] = IOutils.readLongBE(in);
	    }
	    return res;
	}
    }

    static class UInt8ArrayReader extends TagReader {

	UInt8ArrayReader() {
	    super(TagType.U_INT_8_ARRAY_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    UInt8Array res = new UInt8Array(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    int count = (length - 8);
	    res.values = new int[count];
	    for (int i = 0; i < count; i++) {
		res.values[i] = in.read();
	    }
	    return res;
	}
    }

    static class ViewingConditionsReader extends TagReader {

	ViewingConditionsReader() {
	    super(TagType.VIEWING_CONDITIONS_SIG);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    ViewingConditions res = new ViewingConditions(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));

	    res.setIlluminant(0, read_s15Fixed16(in));
	    res.setIlluminant(1, read_s15Fixed16(in));
	    res.setIlluminant(2, read_s15Fixed16(in));

	    res.setSurround(0, read_s15Fixed16(in));
	    res.setSurround(1, read_s15Fixed16(in));
	    res.setSurround(2, read_s15Fixed16(in));

	    res.setIlluminantType(IOutils.readIntBE(in));

	    return res;
	}
    }

    static class XYZ_Reader extends TagReader {

	XYZ_Reader() {
	    super(TagType.XYZ_TYPE);
	}

	@Override
	public Tag read(int length, InputStream in) throws IOException {
	    XYZ res = new XYZ(tagType);
	    res.setReserved(IOutils.readUnsignedIntBE(in));
	    int count = (length - 8) / 12;
	    res.xyzNumbers = new float[count][3];
	    for (int i = 0; i < count; i++) {
		res.xyzNumbers[i][0] = read_s15Fixed16(in);
		res.xyzNumbers[i][1] = read_s15Fixed16(in);
		res.xyzNumbers[i][2] = read_s15Fixed16(in);
	    }
	    return res;
	}
    }
}
