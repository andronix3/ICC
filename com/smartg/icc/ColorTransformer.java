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

import com.smartg.color.ColorMatrix;
import com.smartg.icc.tag.Tag.ICurve;

public abstract class ColorTransformer {

    public abstract float[] toPCS(float[] src, float[] dst);

    public abstract float[] fromPCS(float[] src, float[] dst);

    static class CT1 extends ColorTransformer {

	ICurve grayTRC, grayTRCi;

	protected CT1(ICurve grayTRC) {
	    this.grayTRC = grayTRC;
	    this.grayTRCi = grayTRC.inverse();
	}

	public float[] toPCS(float[] src, float[] dst) {
	    float deviceG = src[0];
	    float pcsG = grayTRC.get(deviceG);
	    dst[0] = pcsG;
	    return dst;
	}

	public float[] fromPCS(float[] src, float[] dst) {
	    float pcsG = src[0];
	    float deviceG = grayTRCi.get(pcsG);
	    dst[0] = deviceG;
	    return dst;
	}
    }

    static class CT3 extends ColorTransformer {
	float[] redMatrixColumn, greenMatrixColumn, blueMatrixColumn;
	ICurve redTRC, greenTRC, blueTRC;
	ICurve redTRCi, greenTRCi, blueTRCi;

	final float rX, rY, rZ, gX, gY, gZ, bX, bY, bZ;
	final float rXi, rYi, rZi, gXi, gYi, gZi, bXi, bYi, bZi;

	protected CT3(float[] redMatrixColumn, float[] greenMatrixColumn, float[] blueMatrixColumn, ICurve redTRC, ICurve greenTRC, ICurve blueTRC) {
	    this.redMatrixColumn = redMatrixColumn;
	    this.greenMatrixColumn = greenMatrixColumn;
	    this.blueMatrixColumn = blueMatrixColumn;

	    this.redTRC = redTRC;
	    this.greenTRC = greenTRC;
	    this.blueTRC = blueTRC;

	    rX = redMatrixColumn[0];
	    rY = redMatrixColumn[1];
	    rZ = redMatrixColumn[2];

	    gX = greenMatrixColumn[0];
	    gY = greenMatrixColumn[1];
	    gZ = greenMatrixColumn[2];

	    bX = blueMatrixColumn[0];
	    bY = blueMatrixColumn[1];
	    bZ = blueMatrixColumn[2];

	    ColorMatrix cm = new ColorMatrix(redMatrixColumn, greenMatrixColumn, blueMatrixColumn);
	    ColorMatrix cmi = cm.inverse();

//	    cmi.print(Tag.format, "inverse");

	    rXi = (float) cmi.Rx;
	    gXi = (float) cmi.Gx;
	    bXi = (float) cmi.Bx;

	    rYi = (float) cmi.Ry;
	    gYi = (float) cmi.Gy;
	    bYi = (float) cmi.By;

	    rZi = (float) cmi.Rz;
	    gZi = (float) cmi.Gz;
	    bZi = (float) cmi.Bz;

	    redTRCi = redTRC.inverse();
	    greenTRCi = greenTRC.inverse();
	    blueTRCi = blueTRC.inverse();
	}

	public float[] toPCS(float[] src, float[] dst) {
	    float deviceR = src[0];
	    float deviceG = src[1];
	    float deviceB = src[2];

	    float linearR = redTRC.get(deviceR);
	    float linearG = greenTRC.get(deviceG);
	    float linearB = blueTRC.get(deviceB);

	    float pcsX = rX * linearR + gX * linearG + bX * linearB;
	    float pcsY = rY * linearR + gY * linearG + bY * linearB;
	    float pcsZ = rZ * linearR + gZ * linearG + bZ * linearB;

	    dst[0] = pcsX;
	    dst[1] = pcsY;
	    dst[2] = pcsZ;
	    return dst;
	}

	public float[] fromPCS(float[] src, float[] dst) {
	    float pcsX = src[0];
	    float pcsY = src[1];
	    float pcsZ = src[2];

	    float linearR = rXi * pcsX + gXi * pcsY + bXi * pcsZ;
	    float linearG = rYi * pcsX + gYi * pcsY + bYi * pcsZ;
	    float linearB = rZi * pcsX + gZi * pcsY + bZi * pcsZ;

	    float deviceR = redTRCi.get(linearR);
	    float deviceG = greenTRCi.get(linearG);
	    float deviceB = blueTRCi.get(linearB);

	    if (deviceR > 1) {
		deviceR = 1;
	    } else if (deviceR < 0) {
		deviceR = 0;
	    }
	    if (deviceG > 1) {
		deviceG = 1;
	    } else if (deviceG < 0) {
		deviceG = 0;
	    }
	    if (deviceB > 1) {
		deviceB = 1;
	    } else if (deviceB < 0) {
		deviceB = 0;
	    }

	    dst[0] = deviceR;
	    dst[1] = deviceG;
	    dst[2] = deviceB;
	    return dst;
	}
    }
}
