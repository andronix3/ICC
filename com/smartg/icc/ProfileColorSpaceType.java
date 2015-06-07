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


public enum ProfileColorSpaceType {
    CS_XYZ(0x58595A20, "XYZ ", "XYZ Data", ColorSpace.TYPE_XYZ, 3),
    CS_LAB(0x4C616220, "Lab ", "Lab Data", ColorSpace.TYPE_Lab, 3),
    CS_LUV(0x4C757620, "Luv ", "Luv Data", ColorSpace.TYPE_Luv, 3),
    CS_YCC(0x59436272, "YCbr", "YCbr Data", ColorSpace.TYPE_YCbCr, 3),
    CS_YXY(0x59787920, "Yxy ", "Yxy Data", ColorSpace.TYPE_Yxy, 3),
    CS_RGB(0x52474220, "RGB ", "RGB Data", ColorSpace.TYPE_RGB, 3),
    CS_GRAY(0x47524159, "GRAY", "GRAY Data", ColorSpace.TYPE_GRAY, 1),
    CS_HSV(0x48535620, "HSV ", "HSV Data", ColorSpace.TYPE_HSV, 3),
    CS_HLS(0x484C5320, "HLS ", "HLS Data", ColorSpace.TYPE_HLS, 3),
    CS_CMYK(0x434D594B, "CMYK", "CMYK Data", ColorSpace.TYPE_CMYK, 4),
    CS_CMY(0x434D5920, "CMY ", "CMY  Data", ColorSpace.TYPE_CMY, 3),
    CS_2CLR(0x32434C52, "2CLR", "2 color data", ColorSpace.TYPE_2CLR, 2),
    CS_3CLR(0x33434C52, "3CLR", "3 color data", ColorSpace.TYPE_3CLR, 3),
    CS_4CLR(0x34434C52, "4CLR", "4 color data", ColorSpace.TYPE_4CLR, 4),
    CS_5CLR(0x35434C52, "5CLR", "5 color data", ColorSpace.TYPE_5CLR, 5),
    CS_6CLR(0x36434C52, "6CLR", "6 color data", ColorSpace.TYPE_6CLR, 6),
    CS_7CLR(0x37434C52, "7CLR", "7 color data", ColorSpace.TYPE_7CLR, 7),
    CS_8CLR(0x38434C52, "8CLR", "8 color data", ColorSpace.TYPE_8CLR, 8),
    CS_9CLR(0x39434C52, "9CLR", "9 color data", ColorSpace.TYPE_9CLR, 9),
    CS_ACLR(0x41434C52, "ACLR", "10 color data", ColorSpace.TYPE_ACLR, 10),
    CS_BCLR(0x42434C52, "BCLR", "11 color data", ColorSpace.TYPE_BCLR, 11),
    CS_CCLR(0x43434C52, "CCLR", "12 color data", ColorSpace.TYPE_CCLR, 12),
    CS_DCLR(0x44434C52, "DCLR", "13 color data", ColorSpace.TYPE_DCLR, 13),
    CS_ECLR(0x45434C52, "ECLR", "14 color data", ColorSpace.TYPE_ECLR, 14),
    CS_FCLR(0x46434C52, "FCLR", "15 color data", ColorSpace.TYPE_FCLR, 15),
    
    ;
    
    private final String description;
    private final long value;
    private final String signature;
    private final int javaColorSpace;
    private final int numComponents;
    
    ProfileColorSpaceType(long value, String signature, String description, int javaColorSpace, int numComponents) {
	this.value = value;
	this.signature = signature;
	this.description = description;
	this.javaColorSpace = javaColorSpace;
	this.numComponents = numComponents;
	
	if(!SignatureUtils.toString(value).equals(signature)) {
	    throw new RuntimeException();
	}
    }

    @Override
    public String toString() {
	return description;
    }
    
    public static ProfileColorSpaceType get(long signature) {
	String s = "CS_" + SignatureUtils.toString(signature).trim().toUpperCase();
	return ProfileColorSpaceType.valueOf(s);
    }

    public String getDescription() {
	return description;
    }

    public long getValue() {
	return value;
    }

    public String getSignature() {
	return signature;
    }

    public int getJavaColorSpace() {
	return javaColorSpace;
    }

    public int getNumComponents() {
        return numComponents;
    }
}
