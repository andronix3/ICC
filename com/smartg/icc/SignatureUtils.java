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


class SignatureUtils {

    static long toLong(String s) {
        byte[] bytes = s.getBytes();
        long value = byteToInt(bytes, 0) & 0xFFFFFFFFL;
        return value;
    }

    static String toString(long value) {
        byte[] bytes = new byte[4];
	intToByte((int) value,  bytes);
        return new String(bytes);
    }
    
    public static final int byteToInt(byte[] source, int sourceOffset) {
        return ((source[sourceOffset++] & 0xFF) << 24)
                | ((source[sourceOffset++] & 0xFF) << 16)
                | ((source[sourceOffset++] & 0xFF) << 8)
                | (source[sourceOffset++] & 0xFF);
    }

    public static final int intToByte(int v, byte[] dest) {
	int destOffset = 0;
        dest[destOffset++] = (byte) ((v >> 24) & 0xFF);
        dest[destOffset++] = (byte) ((v >> 16) & 0xFF);
        dest[destOffset++] = (byte) ((v >> 8) & 0xFF);
        dest[destOffset++] = (byte) (v & 0xFF);
        return destOffset;
    }
}
