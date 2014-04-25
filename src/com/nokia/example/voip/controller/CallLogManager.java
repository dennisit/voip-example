/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.nokia.example.voip.model.CallLogItem;

public class CallLogManager
    extends AbstractRMSManager {

    private final Vector callLogItems;

    public CallLogManager() {
        super("CallLog");
        callLogItems = new Vector();
    }

    public void addLogItem(String voipAddress, byte callType) {
        CallLogItem cli = new CallLogItem(
            callType,
            voipAddress,
            System.currentTimeMillis());
        
        try {
            addLogItem(cli);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addLogItem(CallLogItem cli)
        throws IOException {
        
        synchronized (callLogItems) {
            callLogItems.insertElementAt(cli, 0);
        }
        
        saveState();
    }

    public void removeLogItem(CallLogItem cli)
        throws IOException {
        
        synchronized (callLogItems) {
            callLogItems.removeElement(cli);
        }
        
        saveState();
    }

    public Vector getCallLog(byte type) {
        Vector filteredLog = new Vector();
        
        synchronized (callLogItems) {
            Enumeration e = callLogItems.elements();
            
            while (e.hasMoreElements()) {
                CallLogItem c = (CallLogItem) e.nextElement();
                
                if (c.getCallType() == type) {
                    filteredLog.addElement(c);
                }
            }
        }
        
        return filteredLog;
    }

    /**
     * @see com.nokia.example.voip.controller.AbstractRMSManager#store(java.io.DataOutputStream)
     */
    protected void store(DataOutputStream dos)
        throws IOException {
        
        synchronized (callLogItems) {
            Enumeration e = callLogItems.elements();
            
            while (e.hasMoreElements()) {
                CallLogItem c = (CallLogItem) e.nextElement();
                dos.writeByte(c.getCallType());
                dos.writeUTF(c.getVoipAddress());
                dos.writeLong(c.getTimestamp());
            }
        }
    }

    /**
     * @see com.nokia.example.voip.controller.AbstractRMSManager#restore(java.io.DataInputStream)
     */
    protected void restore(DataInputStream dis)
        throws IOException {
        
        synchronized (callLogItems) {
            callLogItems.removeAllElements();
            
            while (dis.available() > 0) {
                CallLogItem c = new CallLogItem(
                    dis.readByte(),
                    dis.readUTF(),
                    dis.readLong());
                callLogItems.addElement(c);
            }
        }
    }
}
