/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.model;

public class CallLogItem {

    public static final byte TYPE_RECEIVED = 0;
    public static final byte TYPE_DIALED = 1;
    public static final byte TYPE_MISSED = 2;

    private byte callType;
    private String voipAddress;
    private long timestamp;

    public CallLogItem(byte callType, String voipAddress, long timestamp) {
        this.callType = callType;
        this.voipAddress = voipAddress;
        this.timestamp = timestamp;
    }

    public byte getCallType() {
        return callType;
    }

    public void setCallType(byte callType) {
        this.callType = callType;
    }

    public String getVoipAddress() {
        return voipAddress;
    }

    public void setVoipAddress(String contactVoipId) {
        this.voipAddress = contactVoipId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return voipAddress;
    }
}
