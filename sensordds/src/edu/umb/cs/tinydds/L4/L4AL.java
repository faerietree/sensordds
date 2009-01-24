/*$Id: L4AL.java,v 1.2 2008/08/26 19:35:08 pruet Exp $
 
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


package edu.umb.cs.tinydds.L4;

import com.sun.spot.util.IEEEAddress;
import edu.umb.cs.tinydds.L3.L3Observer;
import edu.umb.cs.tinydds.Message;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;

/**
 *
 * @author pruet
 */
public class L4AL extends L4 implements L3Observer {
    Logger logger;
    
    public L4AL() {
        super();
        logger = new Logger("L4AL");
        logger.logInfo("initiate");
    }
    public int send(Message msg) {
        logger.logInfo("send:send mesg to " + IEEEAddress.toDottedHex(msg.getReceiver()));
        return l3.send(msg);
    }

    public void update(Observable obj, Object arg) {
        logger.logInfo("run:notify observer");
        notifyObservers(arg);
    }

}
