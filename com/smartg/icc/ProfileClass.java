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

import java.util.HashMap;

public enum ProfileClass {
    InputDeviceProfile(0x73636E72, "scnr", "Input Device profile"), //
    DisplayDeviceProfile(0x6D6E7472, "mntr", "Display Device profile"), // 
    OutputDeviceProfile(0x70727472, "prtr", "Output Device profile"), //
    DeviceLinkProfile(0x6C696E6B, "link", "DeviceLink profile"), //
    ColorSpaceConversionProfile(0x73706163, "spac", "ColorSpace Conversion profile"), //
    AbstractProfile(0x61627374, "abst", "Abstract profile"), //
    NamedColourProfile(0x6E6D636C, "nmcl", "Named colour profile"), //

    ;

    private static HashMap<String, ProfileClass> map = new HashMap<String, ProfileClass>();

    static {
	for (ProfileClass pc : ProfileClass.values()) {
	    map.put(pc.signature, pc);
	}
    }

    public static ProfileClass get(long signature) {
	String key = SignatureUtils.toString(signature);
	return map.get(key);
    }

    private final long value;
    private final String signature;
    private final String description;

    private ProfileClass(long value, String signature, String description) {
	this.description = description;
	this.value = value;
	this.signature = signature;

	String key = SignatureUtils.toString(value);
	if (!key.equals(signature)) {
	    throw new RuntimeException();
	}
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

    @Override
    public String toString() {
	return description;
    }
}
