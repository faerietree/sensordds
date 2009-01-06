/*$Id: AddressFiltering.java,v 1.3 2008/08/29 20:26:44 pruet Exp $

Copyright (c) 2008 University of Massachusetts, Boston 
All rights reserved. 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
Neither the name of the University of Massachusetts, Boston  nor 
the names of its contributors may be used to endorse or promote products 
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OF
MASSACHUSETTS, BOSTON OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umb.cs.tinydds.L3;

import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;

/**
 *
 * @author pruet
 *///TODO: Better mapping mechanism, 
public class AddressFiltering {

    protected long[][] addressList = {
        {1, 5}, {0, 2, 6}, {1, 3, 7}, {2, 8, 4}, {3, 9},
        {0, 6, 10}, {1, 5, 7, 11}, {2, 6, 8, 12}, {3, 7, 13, 9}, {4, 8, 14},
        {5, 11, 15}, {6, 10, 12, 16}, {7, 11, 13, 17}, {8, 12, 14, 18}, {9, 13, 19},
        {10, 16, 20}, {11, 15, 17, 21}, {12, 16, 18, 22}, {13, 17, 19, 23}, {14, 18, 24},
        {15, 21}, {16, 20, 22}, {17, 21, 23}, {18, 22, 24}, {19, 23}
    };
    protected static String[] mapList = {
        "C0A8.0067.0000.F", "0014.4F01.0000.5346", "", "", "", //0-4
        "", "", "", "0014.4F01.0000.5301", "", //5-9
        "0014.4F01.0000.552B", "", "", "", "", //10-14
        "", "", "0014.4F01.0000.43FB", "", "", //15-19
        "", "", "", "", "0014.4F01.0000.441A" //20-24
    };
    protected long[] myNeighbors;

    public AddressFiltering(long myAddress) {
        if (myAddress > addressList.length) {
            myAddress = 0;
        }
        myNeighbors = addressList[(int) myAddress];
    }

    public boolean isMyNeighbor(long address) {
        for (int i = 0; i != myNeighbors.length; i++) {
            if (myNeighbors[i] == address) {
                return true;
            }
        }
        return false;
    }

    public boolean isMyNeighbor(String address) {
        return isMyNeighbor(addressToLong(address));
    }

    public static void setBaseStation(String addr) {
        mapList[0] = addr;
    }

    public static long addressToLong(String address) {
        //FIXME: find better way to handle this
        if (address.startsWith(mapList[0])) {
            mapList[0] = address;
        }
        for (int i = 0; i != mapList.length; i++) {
            if (mapList[i].equalsIgnoreCase(address)) {
                return i;
            }
        }
        String addr = "0000.0000.0000." + Utils.split(address, '.')[3];
        return IEEEAddress.toLong(addr) - 4097;
    }

    public static String longToAddress(long addr) {
        //FIXME: this too
        if (addr < mapList.length && !mapList[(int) addr].equals("")) {
            return mapList[(int) addr];
        }
        String hexAddr = IEEEAddress.toDottedHex(addr + 4097);
        return "c0a8.0067.0000." + Utils.split(hexAddr, '.')[3];
    }
}
