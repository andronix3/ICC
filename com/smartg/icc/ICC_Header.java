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

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.logging.Logger;

import com.imagero.uio.io.IOutils;

class ICC_Header {

    static NumberFormat format = createNF();

    private static NumberFormat createNF() {
	NumberFormat nf = NumberFormat.getInstance();
	nf.setMaximumFractionDigits(4);
	nf.setMinimumFractionDigits(4);
	return nf;
    }

    long profileSize_4;
    long preferred_CMM_Type_4;
    long profileVersion_4;
    ProfileClass profileClass;
    ProfileColorSpaceType colorSpaceType;
    ProfileColorSpaceType profileConnectionSpace;
    DateTimeNumber creationDate;
    ProfileSignatire signatire;
    PrimaryPlatform primaryPlatform;
    int profileFlags_4;
    byte[] deviceManufacturer_4 = new byte[4];
    long deviceModel_4;
    DeviceAttributes deviceAttributes;
    RenderingIntent renderingIntent;
    double[] PCS_Illuminant = new double[3];
    byte[] profileCreator_4 = new byte[4];
    byte[] profileID_16 = new byte[16];
    byte[] reserved = new byte[28];

    String profileCreator;
    String deviceManufacturer;
    String profileID;

    public ICC_Header(InputStream in) throws IOException {
	profileSize_4 = IOutils.readUnsignedIntBE(in);
	preferred_CMM_Type_4 = IOutils.readUnsignedIntBE(in);
	profileVersion_4 = IOutils.readUnsignedIntBE(in);
	profileClass = ProfileClass.get(IOutils.readUnsignedIntBE(in));
	colorSpaceType = ProfileColorSpaceType.get(IOutils.readUnsignedIntBE(in));
	profileConnectionSpace = ProfileColorSpaceType.get(IOutils.readUnsignedIntBE(in));
	creationDate = DateTimeNumber.read(in);
	signatire = new ProfileSignatire(IOutils.readUnsignedIntBE(in));
	primaryPlatform = PrimaryPlatform.get(IOutils.readUnsignedIntBE(in));
	profileFlags_4 = IOutils.readIntBE(in);
	in.read(deviceManufacturer_4);
	deviceModel_4 = IOutils.readUnsignedIntBE(in);
	deviceAttributes = new DeviceAttributes(IOutils.readLongBE(in));
	renderingIntent = RenderingIntent.getRenderingIntentType(IOutils.readIntBE(in));
	for (int i = 0; i < 3; i++) {
	    float f = IOutils.readUnsignedIntBE(in);
	    f = f / 65535;
	    PCS_Illuminant[i] = f;
	}

	in.read(profileCreator_4);
	in.read(profileID_16);
	in.read(reserved);
    }

    static NumberFormat getFormat() {
	return format;
    }

    public void print() {
	Logger l = Logger.getLogger("com.imagero.icc");
	l.info("ICC Profile:");
	l.info("Profile Size: " + profileSize_4);
	l.info("Preferred CMM Type: " + preferred_CMM_Type_4);
	l.info("Version: " + profileVersion_4);
	l.info("ProfileClass: " + profileClass);
	l.info("Color space type: " + colorSpaceType);
	l.info("Profile Connection Space: " + profileConnectionSpace);
	l.info("Creation date: " + creationDate);
	l.info("Signatire: " + signatire);
	l.info("Primary platform: " + primaryPlatform);
	l.info("Profile flags: " + profileFlags_4);
	l.info("Device manufacturer: " + new String(deviceManufacturer_4));
	l.info("Device model: " + deviceModel_4);
	l.info("Device attributes: " + deviceAttributes);
	l.info("Rendering intent: " + renderingIntent);
	l.info("PCS Illuminant: [" + format.format(PCS_Illuminant[0]) + ", " + format.format(PCS_Illuminant[1]) + ", "
		+ format.format(PCS_Illuminant[2]) + "]");
	l.info("Profile creator: " + new String(profileCreator_4));
	l.info("Profile ID: [");
	for (int i = 0; i < profileID_16.length; i++) {
	    l.info(Integer.toHexString(profileID_16[i] & 0xFF));
	}
	l.info("]");
    }
}
