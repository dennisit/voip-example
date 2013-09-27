/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.model;

public class Contact {
    private String name;
    private String voipAddress;

    public Contact(String name, String voipAddress) {
        this.name = name;
        this.voipAddress = voipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVoipAddress() {
        return voipAddress;
    }

    public void setVoipAddress(String voipAddress) {
        this.voipAddress = voipAddress;
    }

    public String toString() {
        if (name.equals("")) {
            return voipAddress;
        }
        
        return name;
    }
}
