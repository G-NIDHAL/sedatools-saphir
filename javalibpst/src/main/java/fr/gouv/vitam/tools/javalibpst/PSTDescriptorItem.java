/**
 * Copyright 2010 Richard Johnson & Orin Eman
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * ---
 * <p>
 * This file is part of javalibpst.
 * <p>
 * javalibpst is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * javalibpst is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with javalibpst. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.gouv.vitam.tools.javalibpst;

import java.io.IOException;

/**
 * The descriptor items contain information that describes a PST object.
 * This is like extended table entries, usually when the data cannot fit in a
 * traditional table item.
 *
 * This is an entry of type SLENTRY or SIENTRY
 * see [MS-PST]: Outlook Personal Folders (.pst) File Format
 *
 * @author Richard Johnson
 */
class PSTDescriptorItem {
    PSTDescriptorItem(final byte[] data, final int offset, final PSTFile pstFile, int entryType) {
        this.pstFile = pstFile;

        if (pstFile.getPSTFileType() == PSTFile.PST_TYPE_ANSI) {
            this.descriptorIdentifier = (int) PSTObject.convertLittleEndianBytesToLong(data, offset, offset + 4);
            this.offsetIndexIdentifier = ((int) PSTObject.convertLittleEndianBytesToLong(data, offset + 4, offset + 8))
                    & 0xfffffffe;
            if (entryType == PSTFile.SLBLOCK_ENTRY)
                this.subNodeOffsetIndexIdentifier = (int) PSTObject.convertLittleEndianBytesToLong(data, offset + 8,
                        offset + 12) & 0xfffffffe;
            else
                this.subNodeOffsetIndexIdentifier = 0;
        } else {
            this.descriptorIdentifier = (int) PSTObject.convertLittleEndianBytesToLong(data, offset, offset + 4);
            this.offsetIndexIdentifier = ((int) PSTObject.convertLittleEndianBytesToLong(data, offset + 8, offset + 16))
                    & 0xfffffffe;
            if (entryType == PSTFile.SLBLOCK_ENTRY)
                this.subNodeOffsetIndexIdentifier = (int) PSTObject.convertLittleEndianBytesToLong(data, offset + 16,
                        offset + 24) & 0xfffffffe;
            else
                this.subNodeOffsetIndexIdentifier = 0;
        }
    }

    public byte[] getData() throws IOException, PSTException {
        if (this.dataBlockData != null) {
            return this.dataBlockData;
        }

        final PSTNodeInputStream in = this.pstFile.readLeaf(this.offsetIndexIdentifier);
        final byte[] out = new byte[(int) in.length()];
        in.readCompletely(out);
        this.dataBlockData = out;
        return this.dataBlockData;
    }

    public int[] getBlockOffsets() throws IOException, PSTException {
        if (this.dataBlockOffsets != null) {

            return this.dataBlockOffsets;
        }
        final Long[] offsets = this.pstFile.readLeaf(this.offsetIndexIdentifier).getBlockOffsets();
        final int[] offsetsOut = new int[offsets.length];
        for (int x = 0; x < offsets.length; x++) {
            offsetsOut[x] = offsets[x].intValue();
        }
        return offsetsOut;
    }

    public int getDataSize() throws IOException, PSTException {
        return this.pstFile.getLeafSize(this.offsetIndexIdentifier);
    }

    // Public data
    int descriptorIdentifier;
    int offsetIndexIdentifier;
    int subNodeOffsetIndexIdentifier;

    // These are private to ensure that getData()/getBlockOffets() are used
    // private PSTFile.PSTFileBlock dataBlock = null;
    byte[] dataBlockData = null;
    int[] dataBlockOffsets = null;
    private final PSTFile pstFile;

    @Override
    public String toString() {
        return "PSTDescriptorItem\n" + "   descriptorIdentifier: " + this.descriptorIdentifier + "\n"
                + "   offsetIndexIdentifier: " + this.offsetIndexIdentifier + "\n" + "   subNodeOffsetIndexIdentifier: "
                + this.subNodeOffsetIndexIdentifier + "\n";

    }

}
