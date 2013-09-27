/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
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

import com.nokia.example.voip.model.Settings;

public class SettingsManager
    extends AbstractRMSManager {

    private final Settings settings;

    public SettingsManager() {
        super("Settings");
        settings = new Settings();
    }

    public Settings getSettings() {
        return settings;
    }

    protected synchronized void store(DataOutputStream dos)
        throws IOException {
        dos.writeUTF(settings.getUsername());
        dos.writeUTF(settings.getPassword());
        dos.writeUTF(settings.getRegistrar());
        dos.writeUTF(settings.getRingtoneUrl());
        dos.writeBoolean(settings.isShowCallerId());
    }

    protected synchronized void restore(DataInputStream dis)
        throws IOException {
        settings.setUsername(dis.readUTF());
        settings.setPassword(dis.readUTF());
        settings.setRegistrar(dis.readUTF());
        settings.setRingtoneUrl(dis.readUTF());
        settings.setShowCallerId(dis.readBoolean());
    }
}
