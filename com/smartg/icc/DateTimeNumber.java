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

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;

import com.imagero.uio.io.IOutils;

public class DateTimeNumber {

    private static DateFormat format = DateFormat.getDateTimeInstance();

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    DateTimeNumber(int year, int month, int day, int hour, int minute, int second) {
	this.year = year;
	this.month = month;
	this.day = day;
	this.hour = hour;
	this.minute = minute;
	this.second = second;
    }

    public static DateTimeNumber read(DataInput in) throws IOException {
	int year = in.readShort();
	int month = in.readShort();
	int day = in.readShort();
	int hour = in.readShort();
	int minute = in.readShort();
	int second = in.readShort();

	return new DateTimeNumber(year, month, day, hour, minute, second);
    }

    public static DateTimeNumber read(InputStream in) throws IOException {
	int year = IOutils.readShortBE(in);
	int month = IOutils.readShortBE(in);
	int day = IOutils.readShortBE(in);
	int hour = IOutils.readShortBE(in);
	int minute = IOutils.readShortBE(in);
	int second = IOutils.readShortBE(in);

	return new DateTimeNumber(year, month, day, hour, minute, second);
    }

    public int getYear() {
	return year;
    }

    public int getMonth() {
	return month;
    }

    public int getDay() {
	return day;
    }

    public int getHour() {
	return hour;
    }

    public int getMinute() {
	return minute;
    }

    public int getSecond() {
	return second;
    }

    public String toString() {
	Calendar instance = Calendar.getInstance();
	instance.set(year, month, day, hour, minute, second);
	return format.format(instance.getTime());
    }
}
