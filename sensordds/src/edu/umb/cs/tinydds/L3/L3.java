/*$Id: L3.java,v 1.3 2008/08/29 20:26:44 pruet Exp $
 
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

import edu.umb.cs.tinydds.*;
import edu.umb.cs.tinydds.utils.Observable;

/**
 *
 * @author pruet
 */
public abstract class L3 extends Observable {
    public static final long NO_ADDRESS = Long.MIN_VALUE;
    public static final long BROADCAST_ADDRESS = Long.MAX_VALUE;
    public static long myAddress = 0;
    public abstract int send(AbstractMessage msg);
    public static long getAddress() {
        return myAddress;
    }
}
