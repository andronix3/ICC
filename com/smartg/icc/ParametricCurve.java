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

public class ParametricCurve extends Tag implements ICurve {
    private int functionType;
    private int reserved2;
    private float[] params;

    private Curve inverse;

    private static String[] functions = { "Y = X^k", "Y = (aX+B)^k for (X >= -b/a), Y=0 for (X < -b/a)",
    	"Y = (aX+B)^k + c for (X >= -b/a), Y=c for (X < -b/a)", "Y = (aX+B)^k for (X >= d), Y=cX for (X < d)",
    	"Y = (aX+B)^k + e for (X >= d), Y=cX + f for (X < d)", };

    private static String[][] paramNames = { { "k" }, { "k", "a", "b" }, { "k", "a", "b", "c" }, { "k", "a", "b", "c", "d" },
    	{ "k", "a", "b", "c", "d", "e", "f" } };

    public ParametricCurve(int functionType, float[] params) {
        this(TagType.PARAMETRIC_CURVE_TYPE, functionType, params);
    }
    
    public ParametricCurve(TagType tagType, int functionType, float[] params) {
        super(tagType);
        this.functionType = functionType;
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ParametricCurve {\n");
        sb.append("[functionType=" + functionType);
        sb.append(" ( ");
        sb.append(functions[functionType]);
        sb.append(" )]\n ");

        String[] names = paramNames[functionType];
        sb.append("params: {");
        for (int i = 0; i < params.length; i++) {
    	sb.append("[");
    	sb.append(names[i]);
    	sb.append("=");
    	sb.append(format.format(params[i]));
    	sb.append("]");
        }
        sb.append("}\n}");
        return sb.toString();
    }

    public float get(float x) {

        float k, a, b, c, d, e, f;
        switch (functionType) {
        case 0:
    	return (float) Math.pow(x, params[0]);
        case 1:
    	k = params[0];
    	a = params[1];
    	b = params[2];
    	if (x >= -b / a) {
    	    return (float) Math.pow(a * x + b, k);
    	}
    	return 0;
        case 2:
    	k = params[0];
    	a = params[1];
    	b = params[2];
    	c = params[3];
    	if (x >= -b / a) {
    	    return (float) Math.pow(a * x + b, k) + c;
    	}
    	return c;
        case 3:
    	k = params[0];
    	a = params[1];
    	b = params[2];
    	c = params[3];
    	d = params[4];
    	if (x >= d) {
    	    return (float) Math.pow(a * x + b, k);
    	}
    	return c * x;
        case 4:
    	k = params[0];
    	a = params[1];
    	b = params[2];
    	c = params[3];
    	d = params[4];
    	e = params[5];
    	f = params[6];
    	if (x >= d) {
    	    return (float) Math.pow(a * x + b, k) + e;
    	}
    	return c * x + f;
        }
        return 0;
    }

    public ICurve inverse() {
        if (inverse == null) {
    	int entryCount = 1024;
    	float[] ivalues = new float[entryCount];
    	for (int i = 0; i < entryCount; i++) {
    	    float f = get(i / (float) entryCount);
    	    int index = (int) (f * entryCount);
    	    ivalues[index] = i;
    	}
    	inverse = new Curve(getTagType(), ivalues);
        }
        return inverse;
    }

    public int getFunctionType() {
        return functionType;
    }

    public float[] getParams() {
        return params;
    }

    public static String[][] getParamNames() {
        return paramNames;
    }

    public int getReserved2() {
	return reserved2;
    }

    public void setReserved2(int reserved2) {
	this.reserved2 = reserved2;
    }
}