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

import com.smartg.icc.tag.Tag;
import com.smartg.icc.tag.TagType;
import com.smartg.icc.tag.Tag.ICurve;

public class Curve extends Tag implements ICurve {

    int entryCount;
    float[] values;

    Curve inverse;

    float gamma;

    public Curve(float[] values) {
	this(TagType.CURVE_TYPE, values);
    }

    public Curve(TagType tagType, float[] values) {
	super(tagType);
	this.values = values;
	setValues(values);
    }

    protected void setValues(float[] values) {
	if (values == null) {
	    entryCount = 0;
	} else if (values.length == 1) {
	    gamma = values[0];
	    entryCount = 1;
	} else {
	    entryCount = values.length;
	}
    }

    boolean isIdentity() {
	return entryCount == 0;
    }

    boolean isGamma() {
	return entryCount == 1;
    }

    boolean isCurve() {
	return entryCount > 1;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("Curve: {");
	sb.append("[entryCount=" + entryCount + "] ");
	sb.append("[isIdentity=" + isIdentity() + "] ");
	sb.append("[isGamma=" + isGamma() + "] ");
	sb.append("[isCurve=" + isCurve() + "] ");
	if (isGamma()) {
	    sb.append("[gamma=" + gamma + "]");
	}
	sb.append("}");
	return sb.toString();
    }

    public float get(float a) {
	if (isIdentity()) {
	    return a;
	} else if (isGamma()) {
	    return (float) Math.pow(a, gamma);
	} else {
	    if (a > 1.0f) {
		return values[values.length - 1];
	    } else if (a < 0) {
		return values[0];
	    }
	    int index = (int) (a * values.length);

	    if (index >= values.length) {
		return values[values.length - 1];
	    }
	    if (index <= 0) {
		return values[0];
	    }
	    int index2 = index + 1;
	    float length = values.length;
	    float smin = index / length;
	    float smax = index2 / length;
	    float dmin = values[index - 1];
	    float dmax = values[index2 - 1];
	    return interpolate(a, smin, smax, dmin, dmax);
	}
    }

    protected final float interpolate(float x, float smin, float smax, float dmin, float dmax) {
	float dHeight = dmax - dmin;
	float sHeight = smax - smin;
	return dmin + ((x - smin) * (dHeight / sHeight));
    }

    public ICurve inverse() {
	if (inverse == null) {
	    if (entryCount > 1) {
		float[] ivalues = new float[entryCount];
		int prevIndex = 0;
		for (int i = 0; i < entryCount; i++) {
		    float f = values[i];
		    int index = (int) (f * (entryCount - 1));
		    if (index < entryCount) {
			for (int j = prevIndex + 1; j <= index; j++) {
			    ivalues[j] = i / (float) entryCount;
			}
		    }
		    prevIndex = index;
		}
		inverse = new Curve(getTagType(), ivalues);
		inverse.inverse = this;
	    } else if (entryCount == 1) {
		float[] ivalues = new float[1];
		float igamma = 1f / gamma;
		ivalues[0] = igamma;
		inverse = new Curve(getTagType(), ivalues);
		inverse.inverse = this;
	    } else {
		inverse = this;
	    }
	}
	return inverse;
    }

    public int getEntryCount() {
	return entryCount;
    }

    public float getGamma() {
	return gamma;
    }

    public float[] getValues() {
	return values;
    }
}