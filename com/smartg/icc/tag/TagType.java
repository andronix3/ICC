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

import com.smartg.icc.tag.TagReader.ChromaticityTypeReader;
import com.smartg.icc.tag.TagReader.ColorantOrderReader;
import com.smartg.icc.tag.TagReader.ColorantTableReader;
import com.smartg.icc.tag.TagReader.CurveReader;
import com.smartg.icc.tag.TagReader.DataReader;
import com.smartg.icc.tag.TagReader.DateTimeReader;
import com.smartg.icc.tag.TagReader.LutAtoB_Reader;
import com.smartg.icc.tag.TagReader.LutReader;
import com.smartg.icc.tag.TagReader.MeasurementReader;
import com.smartg.icc.tag.TagReader.MultiLocalizedUnicodeReader;
import com.smartg.icc.tag.TagReader.NamedColor2Reader;
import com.smartg.icc.tag.TagReader.ParametricCurveReader;
import com.smartg.icc.tag.TagReader.S15Fixed16ArrayReader;
import com.smartg.icc.tag.TagReader.SignatureReader;
import com.smartg.icc.tag.TagReader.TextReader;
import com.smartg.icc.tag.TagReader.U16Fixed16ArrayReader;
import com.smartg.icc.tag.TagReader.UInt16ArrayReader;
import com.smartg.icc.tag.TagReader.UInt32ArrayReader;
import com.smartg.icc.tag.TagReader.UInt64ArrayReader;
import com.smartg.icc.tag.TagReader.UInt8ArrayReader;
import com.smartg.icc.tag.TagReader.ViewingConditionsReader;
import com.smartg.icc.tag.TagReader.XYZ_Reader;

public enum TagType {
    COLORANT_ORDER_TYPE(0x636c726f, "clro", "Colorant Order Type", new ColorantOrderReader()), CHROMATICITY_TYPE(0x6368726D, "chrm", "Chromaticity Type",
	    new ChromaticityTypeReader()), COLORANT_TABLE_TYPE(0x636c7274, "clrt", "Colorant Table Type", new ColorantTableReader()), CURVE_TYPE(0x63757276,
	    "curv", "Curve Type", new CurveReader()), DATA_TYPE(0x64617461, "data", "Data Type", new DataReader()), DATE_TIME_TYPE(0x6474696D, "dtim",
	    "Date Time Type", new DateTimeReader()), LUT_16_TYPE(0x6D667432, "mft2", "lut 16 Type", new LutReader()), LUT_8_TYPE(0x6D667431, "mft1",
	    "lut 8 Type", new LutReader()), LUT_A_TO_B_TYPE(0x6D414220, "mAB ", "lut A to B Type", new LutAtoB_Reader()), LUT_B_TO_A_TYPE(0x6D424120, "mBA ",
	    "lut B to A Type", new LutAtoB_Reader()), MEASUREMENT_TYPE(0x6D656173, "meas", "Measurement Type", new MeasurementReader()), MULTI_LOCALIZED_UNICODE_TYPE(
	    0x6D6C7563, "mluc", "MultiLocalized Unicode Type", new MultiLocalizedUnicodeReader()), NAMED_COLOR_2_TYPE(0x6E636C32, "ncl2", "Named Color 2 Type",
	    new NamedColor2Reader()), PARAMETRIC_CURVE_TYPE(0x70617261, "para", "Parametric Curve Type", new ParametricCurveReader()), PROFILE_SEQUENCE_DESC_TYPE(
	    0x70736571, "pseq", "Profile Sequence Desc Type"), RESPONSE_CURVE_SET_16_TYPE(0x72637332, "rcs2", "Response Curve Set 16 Type"), S_15_FIXED_16_ARRAY_TYPE(
	    0x73663332, "sf32", "s16Fixed16Array Type", new S15Fixed16ArrayReader()), SIGNATURE_TYPE(0x73696720, "sig ", "Signature Type",
	    new SignatureReader()), TEXT_TYPE(0x74657874, "text", "Text Type", new TextReader()), U_16_FIXED_16_ARRAY_TYPE(0x75663332, "uf32",
	    "u16Fixed16Array Type", new U16Fixed16ArrayReader()), U_INT_16_ARRAY_TYPE(0x75693136, "ui16", "uInt16Array Type", new UInt16ArrayReader()), U_INT_32_ARRAY_TYPE(
	    0x75693332, "ui32", "uInt32Array Type", new UInt32ArrayReader()), U_INT_64_ARRAY_TYPE(0x75693634, "ui64", "uInt64Array Type",
	    new UInt64ArrayReader()), U_INT_8_ARRAY_TYPE(0x75693038, "ui08", "uInt8Array Type", new UInt8ArrayReader()), VIEWING_CONDITIONS_TYPE(0x76696577,
	    "view", "Viewing Conditions Type", new ViewingConditionsReader()), XYZ_TYPE(0x58595A20, "XYZ ", "XYZ Type", new XYZ_Reader()),

    A_TO_B0_SIG(0x41324230, "A2B0", "AToB0Tag"), A_TO_B1_SIG(0x41324231, "A2B1", "AToB1Tag"), A_TO_B2_SIG(0x41324232, "A2B2", "AToB2Tag"), BLUE_MATRIX_COLUMN_SIG(
	    0x6258595A, "bXYZ", "Blue Matrix Column Tag"), BLUE_TRC_SIG(0x62545243, "bTRC", "Blue TRC Tag"), B_TO_A0_SIG(0x42324130, "B2A0", "BToA0Tag"), B_TO_A1_SIG(
	    0x42324131, "B2A1", "BToA1Tag"), B_TO_A2_SIG(0x42324132, "B2A2", "BToA2Tag"), CALIBRATION_DATE_TIME_SIG(0x63616C74, "calt",
	    "Calibration Date Time Tag"), CHAR_TARGET_SIG(0x74617267, "targ", "Char Target Tag"), CHROMATIC_ADAPTATION_SIG(0x63686164, "chad",
	    "Chromatic Adaptation Tag"), CHROMATICITY_SIG(0x6368726D, "chrm", "Chromaticity Tag"), COLORANT_ORDER_SIG(0x636C726F, "clro", "Colorant Order Tag"), COLORANT_TABLE_SIG(
	    0x636C7274, "clrt", "Colorant Table Tag"), COLORANT_TABLE_OUT_SIG(0x636C6F74, "clot", "Colorant Table Out Tag"), COPYRIGHT_SIG(0x63707274, "cprt",
	    "Copyright Tag"), DEVICE_MFG_DESC_SIG(0x646D6E64, "dmnd", "Device Mfg Desc Tag"), DEVICE_MODEL_DESC_SIG(0x646D6464, "dmdd", "Device Model Desc Tag"), GAMUT_SIG(
	    0x67616D74, "gamt", "Gamut Tag"), GRAY_TRC_SIG(0x6B545243, "kTRC", "Gray TRC Tag"), GREEN_MATRIX_COLUMN_SIG(0x6758595A, "gXYZ",
	    "Green Matrix Column Tag"), GREEN_TRC_SIG(0x67545243, "gTRC", "Green TRC Tag"), LUMINANCE_SIG(0x6C756D69, "lumi", "Luminance Tag"), MEASUREMENT_SIG(
	    0x6D656173, "meas", "Measurement Tag"), MEDIA_BLACK_POINT_SIG(0x626B7074, "bkpt", "Media Black Point Tag"), MEDIA_WHITE_POINT_SIG(0x77747074,
	    "wtpt", "Media White Point Tag"), NAMED_COLOR_2_SIG(0x6E636C32, "ncl2", "Named Color 2 Tag"), OUTPUT_RESPONSE_SIG(0x72657370, "resp",
	    "Output Response Tag"), PREVIEW_0_SIG(0x70726530, "pre0", "Preview 0 Tag"), PREVIEW_1_SIG(0x70726531, "pre1", "Preview 1 Tag"), PREVIEW_2_SIG(
	    0x70726532, "pre2", "Preview 2 Tag"), PROFILE_DESCRIPTION_SIG(0x64657363, "desc", "Profile Description Tag", new MultiLocalizedUnicodeReader()), PROFILE_SEQUENCE_DESCRIPTION_SIG(
	    0x70736571, "pseq", "Profile Sequence Description Tag"), RED_MATRIX_COLUMN_SIG(0x7258595A, "rXYZ", "Red Matrix Column Tag"), RED_TRC_SIG(
	    0x72545243, "rTRC", "Red TRC Tag"), TECHNOLOGY_SIG(0x74656368, "tech", "Technology Tag"), VIEWING_CONDITION_DESC_SIG(0x76756564, "vued",
	    "Viewing Condition Desc Tag"), VIEWING_CONDITIONS_SIG(0x76696577, "view", "Viewing Conditions Tag"),

    ;

    private final long value;
    private final String signature;
    private final String description;

    private final TagReader tagReader;

    TagType(long value, String signature, String description) {
	this.value = value;
	this.signature = signature;
	this.description = description;
	tagReader = null;
    }

    TagType(long value, String signature, String description, TagReader tagReader) {
	this.value = value;
	this.signature = signature;
	this.description = description;
	this.tagReader = tagReader;
	tagReader.tagType = this;
    }

    public long getValue() {
	return value;
    }

    public String getSignature() {
	return signature;
    }

    public String getDescription() {
	return description;
    }

    public TagReader getTagReader() {
	return tagReader;
    }

    public static TagType create(long value) {
	for (TagType type : TagType.values()) {
	    if (value == type.value) {
		return type;
	    }
	}
	return null;
//	throw new IllegalArgumentException("TagType not found for " + value + " (" + toString(value) + ")");
//	Logger.getGlobal().log(Level.WARNING, "TagType not found for " + value + " (" + toString(value) + ")");
//	return new UTagType(value);
    }

    static String toString(long value) {
	char[] chars = new char[4];
	for (int i = 0; i < 4; i++) {
	    chars[i] = (char) ((value & 0xFF000000L) >> 24);
	    value <<= 8;
	}
	return new String(chars);
    }
}
