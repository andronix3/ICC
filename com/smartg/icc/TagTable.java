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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.imagero.uio.RandomAccessInput;
import com.imagero.uio.io.IOutils;
import com.smartg.icc.tag.Tag;
import com.smartg.icc.tag.TagReader;
import com.smartg.icc.tag.TagType;

class TagTable {
    private int tagCount;
    private TagTableEntry[] tags;
    private HashMap<TagType, TagTableEntry> tagMap = new HashMap<TagType, TagTableEntry>();

    TagTable(InputStream in) throws IOException {
	tagCount = IOutils.readIntBE(in);
	tags = new TagTableEntry[tagCount];
	for (int i = 0; i < tagCount; i++) {
	    TagType tagType = TagType.create(IOutils.readUnsignedIntBE(in));
	    long offset = IOutils.readUnsignedIntBE(in);
	    long size = IOutils.readUnsignedIntBE(in);
	    TagTableEntry entry = new TagTableEntry(tagType, offset, size);
	    tags[i] = entry;
	    tagMap.put(tagType, entry);
	}
    }

    void print() {
	Logger l = Logger.getLogger("com.imagero.icc");
	for (int i = 0; i < tagCount; i++) {
	    l.info(tags[i] + "\t" + tags[i].getTag());
	}
    }

    Enumeration<TagTableEntry> tags() {
	return new Enumeration<TagTableEntry>() {
	    int index;

	    public boolean hasMoreElements() {
		return index < tags.length;
	    }

	    public TagTableEntry nextElement() {
		return tags[index++];
	    }
	};
    }

    Tag getTag(TagType tagType) {
	TagTableEntry tagTableEntry = tagMap.get(tagType);
	if (tagTableEntry != null) {
	    return tagTableEntry.getTag();
	}
	return null;
    }

    void readTags(RandomAccessInput rai) throws IOException {
	Enumeration<TagTableEntry> tags = tags();
	while (tags.hasMoreElements()) {
	    TagTableEntry next = tags.nextElement();
	    InputStream in0 = rai.createInputStream(next.getOffset(), next.getSize());
	    long sig = IOutils.readUnsignedIntBE(in0);
	    TagType tagType = TagType.create(sig);
	    if (tagType != null) {
		TagReader reader = tagType.getTagReader();
		if (reader != null) {
		    Tag tag = reader.read((int) next.getSize(), in0);
		    next.setTag(tag);
		} else {
		    Logger.getGlobal().log(Level.WARNING, "TagReader not found for " + tagType);
		}
	    } else {
		Logger.getGlobal().log(Level.WARNING, "Unknown TagType value: " + tagType);
	    }
	}
    }

}
