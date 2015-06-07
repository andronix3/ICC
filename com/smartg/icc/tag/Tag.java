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

import java.text.NumberFormat;

import com.smartg.icc.DateTimeNumber;
import com.smartg.icc.MDArray;

public abstract class Tag {

    protected static NumberFormat format = createNF();

    private static NumberFormat createNF() {
	NumberFormat instance = NumberFormat.getNumberInstance();
	instance.setMaximumFractionDigits(4);
	instance.setMinimumFractionDigits(4);
	return instance;
    }

    private final TagType tagType;
    private long reserved;

    protected Tag(TagType tagType) {
	this.tagType = tagType;
    }

    public TagType getTagType() {
	return tagType;
    }

    public long getReserved() {
	return reserved;
    }

    public void setReserved(long reserved) {
	this.reserved = reserved;
    }

    public static class ChromaticityType extends Tag {
	int numberOfDeviceChannels;
	int phosphorOrColorantType;
	float[] xy;

	public ChromaticityType(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Tag:ChromaticityType {");
	    sb.append("[numberOfDeviceChannels=" + numberOfDeviceChannels + "], ");
	    sb.append("[phosphorOrColorantType=" + phosphorOrColorantType + "], \n");
	    sb.append("xy: {");
	    for (int i = 0; i < numberOfDeviceChannels; i++) {
		int index = i + i;
		sb.append("[");
		sb.append(format.format(xy[index]));
		sb.append(", ");
		sb.append(format.format(xy[index + 1]));
		sb.append("]");
		if (i < numberOfDeviceChannels - 1) {
		    sb.append(", ");
		}
	    }
	    sb.append("}\n");
	    sb.append("}");

	    return sb.toString();
	}

	public int getNumberOfDeviceChannels() {
	    return numberOfDeviceChannels;
	}

	public int getPhosphorOrColorantType() {
	    return phosphorOrColorantType;
	}

	public float[] getXy() {
	    return xy;
	}
    }

    public static class ColorantOrder extends Tag {

	int colorantCount;
	int[] colorantIndexes;

	ColorantOrder(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Tag:ColorantOrder {");
	    sb.append("[colorantCount=" + colorantCount);
	    sb.append("], ");
	    sb.append("[colorantIndexes={");
	    for (int i = 0; i < colorantIndexes.length; i++) {
		sb.append("[" + colorantIndexes[i] + "]");
		if (i < colorantIndexes.length - 1) {
		    sb.append(",");
		}
	    }
	    sb.append("]}");
	    return sb.toString();
	}

	public int getColorantCount() {
	    return colorantCount;
	}

	public int[] getColorantIndexes() {
	    return colorantIndexes;
	}
    }

    public static class ColorantTable extends Tag {

	int colorantCount;

	String[] colorantNames;

	int[] pcsValues0;
	int[] pcsValues1;
	int[] pcsValues2;

	ColorantTable(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("ColorantTable { ");
	    sb.append("[colorantCount=" + colorantCount + "] ");
	    for (int i = 0; i < colorantCount; i++) {
		sb.append("\n[");
		sb.append(colorantNames[i]);
		sb.append(" ");
		sb.append(pcsValues0[i]);
		sb.append(" ");
		sb.append(pcsValues1[i]);
		sb.append(" ");
		sb.append(pcsValues2[i]);
		sb.append("]");
	    }
	    sb.append("}");
	    return sb.toString();
	}

	public int getColorantCount() {
	    return colorantCount;
	}

	public String[] getColorantNames() {
	    return colorantNames;
	}

	public int[] getPcsValues0() {
	    return pcsValues0;
	}

	public int[] getPcsValues1() {
	    return pcsValues1;
	}

	public int[] getPcsValues2() {
	    return pcsValues2;
	}
    }

    public static interface ICurve {
	float get(float a);

	ICurve inverse();
    }

    public static class Data extends Tag {

	int dataFlag;
	byte[] string;

	protected Data(TagType tagType) {
	    super(tagType);
	}

	boolean isBinary() {
	    return dataFlag == 0;
	}

	boolean isAscii() {
	    return dataFlag == 1;
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Data: {\n");
	    sb.append("[isBinary=" + isBinary() + "] ");
	    sb.append("[isAscii=" + isAscii() + "] ");
	    sb.append("data:[");
	    if (isAscii()) {
		sb.append(new String(string));
	    } else {
		for (int i = 0; i < string.length; i++) {
		    sb.append(Integer.toHexString(string[i] & 0xFF));
		}
	    }
	    sb.append("]\n}");

	    return sb.toString();
	}

	public int getDataFlag() {
	    return dataFlag;
	}

	public byte[] getString() {
	    return string;
	}
    }

    public static class DateTime extends Tag {

	DateTimeNumber dateTime;

	DateTime(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("DateTime:{\n");
	    sb.append(dateTime.toString());
	    sb.append("\n}");
	    return sb.toString();
	}

	public DateTimeNumber getDateTime() {
	    return dateTime;
	}
    }

    public static class Lut extends Tag {

	int numberOfInputChannels; // i
	int numberOfOutputChannels;// o
	int numberOfClutGridPoints;// g
	int padding;
	float e00, e01, e02, e10, e11, e12, e20, e21, e22;
	int numberOfInputTableEntries;// n
	int numberOfOutputTableEntries;// m

	float[] inputTables;
	float[] clutValues;
	float[] outputTables;

	CLUT clut;

	protected Lut(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Lut: {\n");
	    sb.append("[numberOfInputChannels=" + numberOfInputChannels + "]\n");
	    sb.append("[numberOfOutputChannels=" + numberOfOutputChannels + "]\n");
	    sb.append("[numberOfClutGridPoints=" + numberOfClutGridPoints + "]\n");
	    sb.append("[e00=" + format.format(e00) + "] ");
	    sb.append("[e00=" + format.format(e01) + "] ");
	    sb.append("[e00=" + format.format(e02) + "] ");
	    sb.append("[e00=" + format.format(e10) + "] ");
	    sb.append("[e00=" + format.format(e11) + "] ");
	    sb.append("[e00=" + format.format(e12) + "] ");
	    sb.append("[e00=" + format.format(e20) + "] ");
	    sb.append("[e00=" + format.format(e21) + "] ");
	    sb.append("[e00=" + format.format(e22) + "]\n");
	    sb.append("[numberOfInputTableEntries=" + numberOfInputTableEntries + "]\n");
	    sb.append("[numberOfOutputTableEntries=" + numberOfOutputTableEntries + "]\n");
	    sb.append("\n}");
	    return sb.toString();
	}

	public CLUT getClut() {
	    return clut;
	}

	public int getNumberOfInputChannels() {
	    return numberOfInputChannels;
	}

	public int getNumberOfOutputChannels() {
	    return numberOfOutputChannels;
	}

	public int getNumberOfClutGridPoints() {
	    return numberOfClutGridPoints;
	}

	public float getE00() {
	    return e00;
	}

	public float getE01() {
	    return e01;
	}

	public float getE02() {
	    return e02;
	}

	public float getE10() {
	    return e10;
	}

	public float getE11() {
	    return e11;
	}

	public float getE12() {
	    return e12;
	}

	public float getE20() {
	    return e20;
	}

	public float getE21() {
	    return e21;
	}

	public float getE22() {
	    return e22;
	}

	public int getNumberOfInputTableEntries() {
	    return numberOfInputTableEntries;
	}

	public int getNumberOfOutputTableEntries() {
	    return numberOfOutputTableEntries;
	}

	public float[] getInputTables() {
	    return inputTables;
	}

	public float[] getClutValues() {
	    return clutValues;
	}

	public float[] getOutputTables() {
	    return outputTables;
	}
    }

    public static class GridPoint {
	float[] data;

	GridPoint(int size) {
	    data = new float[size];
	}

	public float[] getData() {
	    return data;
	}
    }

    public static class CLUT {
	final int inputCount, outputCount;
	int[] gridPointCount;
	GridPoint[] table;
	MDArray mda;

	CLUT(int inputCount, int[] gridPointCount, int outputCount) {
	    this.inputCount = inputCount;
	    this.gridPointCount = gridPointCount;
	    this.outputCount = outputCount;

	    int size = gridPointCount[0];
	    for (int i = 1; i < inputCount; i++) {
		size *= gridPointCount[i];
	    }

	    table = new GridPoint[size];
	    for (int i = 0; i < table.length; i++) {
		table[i] = new GridPoint(outputCount);
	    }
	    int[] dims = new int[inputCount];
	    for (int i = 0; i < dims.length; i++) {
		dims[i] = gridPointCount[i];
	    }
	    mda = new MDArray(dims);
	}

	public float[] get(int[] coords) {
	    return table[mda.getOffset(coords)].data;
	}

	public int getInputCount() {
	    return inputCount;
	}

	public int getOutputCount() {
	    return outputCount;
	}

	public int[] getGridPointCount() {
	    return gridPointCount;
	}

	public GridPoint getGridPoint(int index) {
	    return table[index];
	}
    }

    public static class LutAtoB extends Tag {
	int numberOfInputChannels;
	int numberOfOutputChannels;
	int padding2;
	long offsetToFirstB_Curve;
	long offsetToMatrix;
	long offsetToFirstM_Curve;
	long offsetToCLUT;
	long offsetToFirstA_Curve;

	ICurve[] bCurves;
	ICurve[] mCurves;
	ICurve[] aCurves;

	float[] matrix = new float[12];

	CLUT clut;

	protected LutAtoB() {
	    super(TagType.LUT_A_TO_B_TYPE);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("LutAtoB/LutBtoA: {\n");
	    sb.append("[numberOfInputChannels=" + numberOfInputChannels + "]\n");
	    sb.append("[numberOfOutputChannels=" + numberOfOutputChannels + "]\n");
	    if (bCurves != null) {
		addCurves(bCurves, sb, "B Curves:\n");
	    }
	    if (mCurves != null) {
		addCurves(mCurves, sb, "M Curves:\n");
	    }
	    if (aCurves != null) {
		addCurves(aCurves, sb, "A Curves:\n");
	    }
	    sb.append("Matrix { ");
	    for (int i = 0; i < matrix.length; i++) {
		sb.append(format.format(matrix[i]));
		sb.append(", ");
	    }
	    sb.append(" }\n");

	    sb.append("\n}");
	    return sb.toString();
	}

	private void addCurves(ICurve[] curves, StringBuffer sb, String s) {
	    sb.append(s);
	    for (int i = 0; i < curves.length; i++) {
		sb.append(curves[i].toString());
		sb.append("\n");
	    }
	    sb.append("\n");
	}

	public int getNumberOfInputChannels() {
	    return numberOfInputChannels;
	}

	public int getNumberOfOutputChannels() {
	    return numberOfOutputChannels;
	}

	public long getOffsetToFirstB_Curve() {
	    return offsetToFirstB_Curve;
	}

	public long getOffsetToMatrix() {
	    return offsetToMatrix;
	}

	public long getOffsetToFirstM_Curve() {
	    return offsetToFirstM_Curve;
	}

	public long getOffsetToCLUT() {
	    return offsetToCLUT;
	}

	public long getOffsetToFirstA_Curve() {
	    return offsetToFirstA_Curve;
	}

	public ICurve[] getBCurves() {
	    return bCurves;
	}

	public ICurve[] getMCurves() {
	    return mCurves;
	}

	public ICurve[] getACurves() {
	    return aCurves;
	}

	public float[] getMatrix() {
	    return matrix;
	}

	public CLUT getClut() {
	    return clut;
	}
    }

    static String[] stdIlluminants = { "unknown", "D50", "D65", "D93", "F2", "D55", "A", "E", "F8" };

    public static class Measurement extends Tag {
	int standardObserver;
	float[] xyz = new float[3];
	int geometry;
	float flare;
	int standardIlluminant;

	static String[] stdObservers = { "unknown", "CIE 1931", "CIE 1964" };
	static String[] geometryEncodings = { "unknown", "0/45 or 45/0", "0/d or d/0" };

	protected Measurement(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Measurement {\n");
	    sb.append("[standardObserver=" + standardObserver);
	    sb.append(" (");
	    sb.append(stdObservers[standardObserver]);
	    sb.append(") ]\n ");

	    sb.append("XYZ: {");
	    sb.append("[" + format.format(xyz[0]) + "]");
	    sb.append("[" + format.format(xyz[1]) + "]");
	    sb.append("[" + format.format(xyz[2]) + "]}\n");

	    sb.append("[geometry=" + geometry);
	    sb.append(" (");
	    sb.append(geometryEncodings[geometry]);
	    sb.append(" ) ]\n");

	    sb.append("[flare=" + format.format(flare));
	    sb.append("]\n");

	    sb.append("[standardIlluminant=" + standardIlluminant);
	    sb.append(" (");
	    sb.append(stdIlluminants[standardIlluminant]);
	    sb.append(") ]\n}");

	    return sb.toString();
	}

	public int getStandardObserver() {
	    return standardObserver;
	}

	public float[] getXYZ() {
	    return xyz;
	}

	public int getGeometry() {
	    return geometry;
	}

	public float getFlare() {
	    return flare;
	}

	public int getStandardIlluminant() {
	    return standardIlluminant;
	}
    }

    public static class MultiLocalizedUnicode extends Tag {

	int numberOfNames;
	long nameRecordSize;
	int[] nameLanguageCode;
	int[] nameCountryCode;
	int[] nameLength;
	long[] nameOffset;
	String[] names;

	protected MultiLocalizedUnicode(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("MultiLocalizedUnicode {\n");
	    sb.append("[numberOfNames=" + numberOfNames);
	    sb.append("]\n}");
	    return sb.toString();
	}

	public int getNumberOfNames() {
	    return numberOfNames;
	}

	public long getNameRecordSize() {
	    return nameRecordSize;
	}

	public int[] getNameLanguageCode() {
	    return nameLanguageCode;
	}

	public int[] getNameCountryCode() {
	    return nameCountryCode;
	}

	public int[] getNameLength() {
	    return nameLength;
	}

	public long[] getNameOffset() {
	    return nameOffset;
	}

	public String[] getNames() {
	    return names;
	}
    }

    public static class NamedColor2 extends Tag {
	long vendorSpecificFlag;
	int countOfNamedColors;
	int numberOfDevideCoordinatesForEachNamedColor;
	byte[] prefixForEachColorName = new byte[32];
	byte[] suffixForEachColorName = new byte[32];
	byte[][] colorNames;
	long[][] colorPCS_Coordinates;
	long[][] colorDeviceCoordinates;

	protected NamedColor2(TagType tagType) {
	    super(tagType);
	}

	public long getVendorSpecificFlag() {
	    return vendorSpecificFlag;
	}

	public int getCountOfNamedColors() {
	    return countOfNamedColors;
	}

	public int getNumberOfDevideCoordinatesForEachNamedColor() {
	    return numberOfDevideCoordinatesForEachNamedColor;
	}

	public byte[] getPrefixForEachColorName() {
	    return prefixForEachColorName;
	}

	public byte[] getSuffixForEachColorName() {
	    return suffixForEachColorName;
	}

	public byte[][] getColorNames() {
	    return colorNames;
	}

	public long[][] getColorPCS_Coordinates() {
	    return colorPCS_Coordinates;
	}

	public long[][] getColorDeviceCoordinates() {
	    return colorDeviceCoordinates;
	}
    }

    // TODO is it unused?
    public static class ProfileSequenceDesc extends Tag {

	int numberOfDescriptionStructures;

	ProfileDescriptionStructure[] pds;

	protected ProfileSequenceDesc(TagType tagType) {
	    super(tagType);
	}

	public int getNumberOfDescriptionStructures() {
	    return numberOfDescriptionStructures;
	}

	public ProfileDescriptionStructure getPds(int index) {
	    return pds[index];
	}

    }

    public static class ProfileDescriptionStructure {
	long deviceManufacturerSignature;
	long deviceModelSignature;
	long deviceAttributes;
	long deviceTechnologyInfo;
	String deviceManufacturer;
	String deviceModel;

	public long getDeviceManufacturerSignature() {
	    return deviceManufacturerSignature;
	}

	public long getDeviceModelSignature() {
	    return deviceModelSignature;
	}

	public long getDeviceAttributes() {
	    return deviceAttributes;
	}

	public long getDeviceTechnologyInfo() {
	    return deviceTechnologyInfo;
	}

	public String getDeviceManufacturer() {
	    return deviceManufacturer;
	}

	public String getDeviceModel() {
	    return deviceModel;
	}
    }

    public static class ResponseCurveSet16 extends Tag {
	int numberOfChannels;
	int countOfMeasurementTypes;
	long[] relativeOffsets;
	ResponseCurve[] curves;

	protected ResponseCurveSet16(TagType tagType) {
	    super(tagType);
	}

	public int getNumberOfChannels() {
	    return numberOfChannels;
	}

	public int getCountOfMeasurementTypes() {
	    return countOfMeasurementTypes;
	}

	public long[] getRelativeOffsets() {
	    return relativeOffsets;
	}

	public ResponseCurve getResponseCurve(int index) {
	    return curves[index];
	}
    }

    public static class ResponseCurve {
	long measurementUnitSignature;
	int[] numberOfMeasurementsForEachChannel;
	float[] measurements;
	int[] responseI;
	float[] responseF;

	public long getMeasurementUnitSignature() {
	    return measurementUnitSignature;
	}

	public int[] getNumberOfMeasurementsForEachChannel() {
	    return numberOfMeasurementsForEachChannel;
	}

	public float[] getMeasurements() {
	    return measurements;
	}

	public int[] getResponseI() {
	    return responseI;
	}

	public float[] getResponseF() {
	    return responseF;
	}
    }

    public static class S15Fixed16Array extends Tag {
	float[] values;

	protected S15Fixed16Array(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("S15Fixed16Array {\n");
	    sb.append("[values=");
	    for (int i = 0; i < values.length; i++) {
		sb.append(format.format(values[i]));
		sb.append(", ");
	    }
	    sb.append("]\n}");
	    return sb.toString();
	}

	public float[] getValues() {
	    return values;
	}
    }

    public static class U16Fixed16Array extends Tag {
	float[] values;

	protected U16Fixed16Array(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("S15Fixed16Array {\n");
	    sb.append("[values=");
	    for (int i = 0; i < values.length; i++) {
		sb.append(format.format(values[i]));
		sb.append(", ");
	    }
	    sb.append("]\n}");
	    return sb.toString();
	}

	public float[] getValues() {
	    return values;
	}
    }

    public static class UInt16Array extends Tag {
	int[] values;

	protected UInt16Array(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("S15Fixed16Array {\n");
	    sb.append("[values=");
	    for (int i = 0; i < values.length; i++) {
		sb.append(values[i]);
		sb.append(", ");
	    }
	    sb.append("]\n}");
	    return sb.toString();
	}

	public int[] getValues() {
	    return values;
	}
    }

    public static class UInt32Array extends Tag {
	long[] values;

	protected UInt32Array(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("S15Fixed16Array {\n");
	    sb.append("[values=");
	    for (int i = 0; i < values.length; i++) {
		sb.append(values[i]);
		sb.append(", ");
	    }
	    sb.append("]\n}");
	    return sb.toString();
	}

	public long[] getValues() {
	    return values;
	}
    }

    public static class Signature extends Tag {

	long signature;

	protected Signature(TagType tagType) {
	    super(tagType);
	}

	public long getSignature() {
	    return signature;
	}
    }

    public static class UInt8Array extends Tag {
	int[] values;

	public UInt8Array(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("S15Fixed16Array {\n");
	    sb.append("[values=");
	    for (int i = 0; i < values.length; i++) {
		sb.append(values[i]);
		sb.append(", ");
	    }
	    sb.append("]\n}");
	    return sb.toString();
	}

	public int[] getValues() {
	    return values;
	}
    }

    public static class ViewingConditions extends Tag {
	private float[] illuminant = new float[3];
	private float[] surround = new float[3];
	private int illuminantType;

	public ViewingConditions(TagType tagType) {
	    super(tagType);
	}

	public float[] getIlluminant() {
	    return illuminant;
	}

	public float[] getSurround() {
	    return surround;
	}

	public int getIlluminantType() {
	    return illuminantType;
	}

	void setIlluminantType(int illuminantType) {
	    this.illuminantType = illuminantType;
	}

	void setIlluminant(int index, float illuminant) {
	    this.illuminant[index] = illuminant;
	}

	void setSurround(int index, float surround) {
	    this.surround[index] = surround;
	}
    }

    public static class XYZ extends Tag {

	public float[][] xyzNumbers;

	public XYZ(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Tag:XYZ {");
	    int count = xyzNumbers.length;
	    for (int i = 0; i < count; i++) {
		sb.append("[");
		sb.append(format.format(xyzNumbers[i][0]));
		sb.append(", ");
		sb.append(format.format(xyzNumbers[i][1]));
		sb.append(", ");
		sb.append(format.format(xyzNumbers[i][2]));
		sb.append("]");
		if (i < count - 1) {
		    sb.append(", ");
		}
	    }
	    sb.append("}");
	    return sb.toString();
	}

	public float[][] getXYZ_Numbers() {
	    return xyzNumbers;
	}
    }

    public static class UInt64Array extends Tag {
	long[] values;

	protected UInt64Array(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("S15Fixed16Array {\n");
	    sb.append("[values=");
	    for (int i = 0; i < values.length; i++) {
		sb.append(values[i]);
		sb.append(", ");
	    }
	    sb.append("]\n}");
	    return sb.toString();
	}

	public long[] getValues() {
	    return values;
	}
    }

    public static class Text extends Tag {

	byte[] string;

	protected Text(TagType tagType) {
	    super(tagType);
	}

	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Text {\n");
	    sb.append(new String(string));
	    sb.append("\n}");
	    return sb.toString();
	}

	public byte[] getString() {
	    return string;
	}
    }
}
