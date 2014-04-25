/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public abstract class AbstractRMSManager {

    protected String recordStoreName;

    public AbstractRMSManager(String recordStoreName) {
        this.recordStoreName = recordStoreName;
    }

    public void loadState()
        throws IOException {
        
        try {
            RecordStore snapshot = RecordStore.openRecordStore(
                recordStoreName,
                true);
            
            if (snapshot.getNextRecordID() > 0) {
                RecordEnumeration e = snapshot.enumerateRecords(
                    null,
                    null,
                    false);
                
                if(e.hasNextElement()) {
                    byte[] record = e.nextRecord();
                    e.destroy();
                
                    if (record != null) {
                        restore(new DataInputStream(
                            new ByteArrayInputStream(record)));
                    }
                }
            }
            
            snapshot.closeRecordStore();
        }
        catch (RecordStoreException e) {
            e.printStackTrace();
            System.out.println("Reason: " + e.getMessage());
        }
    }

    public void saveState()
        throws IOException {
        
        try {
            RecordStore snapshot = RecordStore.openRecordStore(
                recordStoreName,
                true);
            
            // Delete existing record(s)
            if (snapshot.getNextRecordID() > 0) {
                RecordEnumeration e = snapshot.enumerateRecords(
                    null,
                    null,
                    false);
                
                while (e.hasNextElement()) {
                    int id = e.nextRecordId();
                    snapshot.deleteRecord(id);
                }
            }
            
            // Store data
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            store(dos);
            
            byte[] data = bos.toByteArray();
            snapshot.addRecord(data, 0, data.length);
            snapshot.closeRecordStore();
        }
        catch (RecordStoreException e) {
            e.printStackTrace();
        }
    }

    protected abstract void store(DataOutputStream dos)
        throws IOException;

    protected abstract void restore(DataInputStream dis)
        throws IOException;
}
