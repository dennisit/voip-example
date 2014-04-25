/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.engine;

import java.io.IOException;
import java.io.InputStream;

import com.nokia.mid.voip.VoipManager;
import com.nokia.mid.voip.VoipSettings;

import com.nokia.example.voip.controller.SettingsManager;
import com.nokia.example.voip.engine.SettingsStateListener;
import com.nokia.example.voip.model.Settings;

public class SettingsHelper {

    private VoipSettings voipSettings;
    private SettingsStateListener settingsStateListener;
    private Settings settings;
    private SettingsManager settingsManager;

    public SettingsHelper(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        voipSettings = VoipManager.createVoipSettingsInstance();
        settingsStateListener = SettingsStateListener.getInstance();
        voipSettings.setVoipSettingsStateListener(settingsStateListener);
    }

    public void writeSettings()
        throws IOException {
        String settingsXml = null;
        settings = settingsManager.getSettings();
        
        settingsXml = readSettingsFile("/voip_settings.xml");
        settingsXml = insertUserSettings(
            settingsXml,
            settings.getUsername(),
            settings.getPassword(),
            settings.getRegistrar());
        
        voipSettings.writeVoipSettings(settingsXml);
    }

    public void deleteSettings() {
        voipSettings.deleteVoipSettings();
    }

    private String readSettingsFile(String filePath)
        throws IOException {
        
        byte[] fileData = null;
        InputStream iStream = getClass().getResourceAsStream(filePath);
        int fileSize = iStream.available();
        
        if (fileSize > 0) {
            fileData = new byte[fileSize];
            iStream.read(fileData, 0, fileData.length);
            iStream.close();
        }
        
        return new String(fileData);
    }

    private String insertUserSettings(
            String settingsXml,
            String username,
            String password,
            String registrar) {
        
        String updatedXml;
        
        updatedXml = replace(settingsXml, "<!-- SIP_USERNAME -->", username);
        updatedXml = replace(updatedXml, "<!-- SIP_PASSWORD -->", password);
        updatedXml = replace(updatedXml, "<!-- SIP_REGISTRAR -->", registrar);
        
        return updatedXml;
    }

    private String replace(String xml, String pattern, String replace) {
        int start = 0;
        int end = 0;
        StringBuffer result = new StringBuffer();
        
        while ((end = xml.indexOf(pattern, start)) >= 0) {
            result.append(xml.substring(start, end));
            result.append(replace);
            start = end + pattern.length();
        }
        
        result.append(xml.substring(start));
        return result.toString();
    }
}
