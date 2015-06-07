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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.smartg.color.ReferenceWhite;

public class ICC_BasedCS extends ColorSpace {

    private static final long serialVersionUID = 5590636058186378516L;

    private static final double KAPPA = 24389.0 / 27.0;
    private static final double ETA = 216.0 / 24389.0;
    private static final double w = 1.0 / 3.0;

    private ICCProfile profile;
    private ReferenceWhite refWhite;
    private ColorTransformer transformer;
    private boolean isLabPCS;

    private ICCProfile rgbProfile = ICCProfile.createSRGB();
    private ColorTransformer rgbTransformer = rgbProfile.getColorTransformer();
    private ReferenceWhite rgbRefWhite = new ReferenceWhite(rgbProfile.getWhitePoint());
    private boolean isLabPCS_2 = rgbProfile.getProfileConnectionSpace().getJavaColorSpace() == ColorSpace.TYPE_Lab;

    public static ICC_BasedCS getSRGB() {
	return new ICC_BasedCS(ICCProfile.createSRGB());
    }

    public ICC_BasedCS(ICCProfile profile) {
	super(profile.getColorSpaceType().getJavaColorSpace(), profile.getColorSpaceType().getNumComponents());
	this.profile = profile;
	this.refWhite = new ReferenceWhite(profile.getWhitePoint());
	// transformer = profile.getColorTransformer();
	isLabPCS = profile.getProfileConnectionSpace().getJavaColorSpace() == ColorSpace.TYPE_Lab;
    }

    public ColorTransformer getTransformer() {
	if (transformer == null) {
	    transformer = profile.getColorTransformer();
	}
	return transformer;
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
	return fromCIEXYZ(colorvalue, new float[getNumComponents()]);
    }

    public final float[] fromCIEXYZ(float[] src, float[] dest) {
	if (isLabPCS) {
	    xyz2lab(src[0], src[1], src[2], dest, refWhite);
	    return getTransformer().fromPCS(dest, dest);
	}
	return getTransformer().fromPCS(src, dest);
    }

    @Override
    public final float[] fromRGB(float[] rgbvalue) {
	return fromRGB(rgbvalue, new float[rgbProfile.getNumComponents()]);
    }

    public final float[] fromRGB(float[] rgbvalue, float[] dst) {
	rgbTransformer.toPCS(rgbvalue, dst);
	if (isLabPCS_2) {
	    lab2xyz(dst[0], dst[1], dst[2], dst, rgbRefWhite);
	    return fromCIEXYZ(dst, dst);
	}
	return fromCIEXYZ(rgbvalue, dst);
    }

    @Override
    public final float[] toCIEXYZ(float[] colorvalue) {
	return toCIEXYZ(colorvalue, new float[getNumComponents()]);
    }

    public final float[] toCIEXYZ(float[] colorvalue, float[] dst) {
	ColorTransformer tr = getTransformer();
	if (tr != null) {
	    tr.toPCS(colorvalue, dst);
	} else {
	    Logger.getGlobal().log(Level.WARNING, "No transformer found for ICC_BasedCS");
	}
	if (isLabPCS) {
	    lab2xyz(dst[0], dst[1], dst[2], dst, refWhite);
	}
	return dst;
    }

    @Override
    public final float[] toRGB(float[] colorvalue) {
	return toRGB(colorvalue, new float[rgbProfile.getNumComponents()]);
    }

    public final float[] toRGB(float[] colorvalue, float[] dest) {
	float[] ciexyz = toCIEXYZ(colorvalue, dest);
	if (isLabPCS_2) {
	    xyz2lab(ciexyz[0], ciexyz[1], ciexyz[2], dest, rgbRefWhite);
	    rgbTransformer.fromPCS(dest, dest);
	} else {
	    rgbTransformer.fromPCS(ciexyz, dest);
	}
	return dest;
    }

    private final void lab2xyz(float L, float a, float b, float[] dest, ReferenceWhite refWhite) {
	double fy = (L + 16) / 116;
	double fx = (a / 500) + fy;
	double fz = fy - (b / 200);

	double yr = (L > 8.0 ? fy * fy * fy : L / KAPPA);

	double xr = fx * fx * fx;
	if (xr <= ETA) {
	    xr = (116 * fx - 16) / KAPPA;
	}

	double zr = fz * fz * fz;
	if (zr <= ETA) {
	    zr = (116 * fz - 16) / KAPPA;
	}
	dest[0] = (float) (xr * refWhite.Xn);
	dest[1] = (float) (yr * refWhite.Yn);
	dest[2] = (float) (zr * refWhite.Zn);
    }

    private final void xyz2lab(float X, float Y, float Z, float[] dest, ReferenceWhite refWhite) {
	double xr = X / refWhite.Xn;
	double yr = Y / refWhite.Yn;
	double zr = Z / refWhite.Zn;

	double fx, fy, fz;

	if (xr > ETA) {
	    fx = Math.pow(xr, w);
	} else {
	    fx = (KAPPA * xr + 16.0) / 116.0;
	}

	if (yr > ETA) {
	    fy = Math.pow(yr, w);
	} else {
	    fy = (KAPPA * yr + 16.0) / 116.0;
	}

	if (zr > ETA) {
	    fz = Math.pow(zr, w);
	} else {
	    fz = (KAPPA * zr + 16.0) / 116.0;
	}

	float L = (float) (116 * fy - 16);
	float a = (float) (500 * (fx - fy));
	float b = (float) (200 * (fy - fz));

	dest[0] = L;
	dest[1] = a;
	dest[2] = b;
    }

    public ICCProfile getProfile() {
	return profile;
    }
}
