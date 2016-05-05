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
package com.smartg.icc;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.imagero.reader.IOParameterBlock;
import com.imagero.uio.RandomAccessInput;
import com.smartg.icc.tag.Tag;
import com.smartg.icc.tag.Tag.Desc;
import com.smartg.icc.tag.Tag.ICurve;
import com.smartg.icc.tag.Tag.MultiLocalizedUnicode;
import com.smartg.icc.tag.Tag.XYZ;
import com.smartg.icc.tag.TagType;

public class ICCProfile {
	private ICC_Header header;
	private TagTable tagTable;

	private float[] redColumn;
	private float[] greenColumn;
	private float[] blueColumn;
	private float[] whitePoint;
	private float[] blackPoint;

	private ICurve redTRC, greenTRC, blueTRC, grayTRC;

	public ICCProfile(ICC_Profile profile) throws IOException {
		this(new IOParameterBlock(profileBytes(profile)));
	}

	static byte[] profileBytes(ICC_Profile icc) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		icc.write(out);
		return out.toByteArray();
	}

	public static ICCProfile createSRGB() {
		ICCProfile profile = null;
		try {
			profile = new ICCProfile(ICC_Profile.getInstance(ColorSpace.CS_sRGB));
		} catch (Throwable ex) {
			// throw new RuntimeException(ex);
		}
		return profile;
	}

	public ICCProfile(IOParameterBlock iopb) throws IOException {
		RandomAccessInput rai = iopb.getSourceStream();

		InputStream in = rai.createInputStream(0);
		header = new ICC_Header(in);
		tagTable = new TagTable(in);

		tagTable.readTags(rai);

		Tag red = getTag(TagType.RED_MATRIX_COLUMN_SIG);
		Tag green = getTag(TagType.GREEN_MATRIX_COLUMN_SIG);
		Tag blue = getTag(TagType.BLUE_MATRIX_COLUMN_SIG);

		Tag white = getTag(TagType.MEDIA_WHITE_POINT_SIG);
		Tag black = getTag(TagType.MEDIA_BLACK_POINT_SIG);

		redTRC = (ICurve) getTag(TagType.RED_TRC_SIG);
		greenTRC = (ICurve) getTag(TagType.GREEN_TRC_SIG);
		blueTRC = (ICurve) getTag(TagType.BLUE_TRC_SIG);
		grayTRC = (ICurve) getTag(TagType.BLUE_TRC_SIG);

		if (red != null) {
			XYZ redxyz = (XYZ) red;
			redColumn = redxyz.xyzNumbers[0];
		}
		if (green != null) {
			XYZ greenxyz = (XYZ) green;
			greenColumn = greenxyz.xyzNumbers[0];
		}
		if (blue != null) {
			XYZ bluexyz = (XYZ) blue;
			blueColumn = bluexyz.xyzNumbers[0];
		}
		if (white != null) {
			XYZ whitexyz = (XYZ) white;
			whitePoint = whitexyz.xyzNumbers[0];
		}
		if (black != null) {
			XYZ blackxyz = (XYZ) black;
			blackPoint = blackxyz.xyzNumbers[0];
		}
	}

	public Tag getTag(TagType tagType) {
		return tagTable.getTag(tagType);
	}

	public float[][] getMatrix() {
		float[][] m = new float[3][3];

		m[0][0] = redColumn[0];
		m[1][0] = redColumn[1];
		m[2][0] = redColumn[2];

		m[0][1] = greenColumn[0];
		m[1][1] = greenColumn[1];
		m[2][1] = greenColumn[2];

		m[0][2] = blueColumn[0];
		m[1][2] = blueColumn[1];
		m[2][2] = blueColumn[2];
		return m;
	}

	public int getNumComponents() {
		return getColorSpaceType().getNumComponents();
	}

	public float[] getRedColumn() {
		return redColumn;
	}

	public float[] getGreenColumn() {
		return greenColumn;
	}

	public float[] getBlueColumn() {
		return blueColumn;
	}

	public float[] getWhitePoint() {
		return whitePoint.clone();
	}

	public float[] getBlackPoint() {
		return blackPoint.clone();
	}

	public long getProfileSize() {
		return header.profileSize_4;
	}

	public long getPreferred_CMM_Type() {
		return header.preferred_CMM_Type_4;
	}

	public long getProfileVersion() {
		return header.profileVersion_4;
	}

	public ProfileClass getProfileClass() {
		return header.profileClass;
	}

	public ProfileColorSpaceType getColorSpaceType() {
		return header.colorSpaceType;
	}

	public ProfileColorSpaceType getProfileConnectionSpace() {
		return header.profileConnectionSpace;
	}

	public DateTimeNumber getCreationDate() {
		return header.creationDate;
	}

	public PrimaryPlatform getPrimaryPlatform() {
		return header.primaryPlatform;
	}

	public int getProfileFlags() {
		return header.profileFlags_4;
	}

	public String getDeviceManufacturer() {
		if (header.deviceManufacturer == null) {
			header.deviceManufacturer = new String(header.deviceManufacturer_4);
		}
		return header.deviceManufacturer;
	}

	public long getDeviceModel() {
		return header.deviceModel_4;
	}

	public DeviceAttributes getDeviceAttributes() {
		return header.deviceAttributes;
	}

	public RenderingIntent getRenderingIntent() {
		return header.renderingIntent;
	}

	public double[] getPCS_Illuminant() {
		return header.PCS_Illuminant.clone();
	}

	public String getProfileCreator() {
		if (header.profileCreator == null) {
			header.profileCreator = new String(header.profileCreator_4);
		}
		return header.profileCreator;
	}

	public String getProfileID() {
		if (header.profileID == null) {
			header.profileID = new String(header.profileID_16);
		}
		return header.profileID;
	}

	public ColorTransformer getColorTransformer() {
		int cs = header.colorSpaceType.getJavaColorSpace();
		int pcs = header.profileConnectionSpace.getJavaColorSpace();
		if (cs == ColorSpace.TYPE_RGB && (pcs == ColorSpace.TYPE_XYZ || pcs == ColorSpace.TYPE_Lab)) {
			return new ColorTransformer.CT3(redColumn, greenColumn, blueColumn, redTRC, greenTRC, blueTRC);
		}
		if (cs == ColorSpace.TYPE_GRAY) {
			return new ColorTransformer.CT1(grayTRC);
		}
		return null;
	}

	private String description;

	public String getProfileDescription() {
		if (description == null) {
			Tag tag = tagTable.getTag(TagType.PROFILE_DESCRIPTION_SIG);
			if (tag != null) {
				if (tag instanceof Desc) {
					Desc desc = (Desc) tag;
					byte[] data = desc.getString();
					description = new String(data, 0, data.length - 1);
				} else if (tag instanceof MultiLocalizedUnicode) {
					MultiLocalizedUnicode mulu = (MultiLocalizedUnicode) tag;
					description = mulu.getNames()[0];
				}
			} else {
				description = "";
			}
		}
		return description;
	}

	public void print() {
		header.print();
		tagTable.print();
	}
}
