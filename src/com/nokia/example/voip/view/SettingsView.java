/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.nokia.mid.ui.FileSelect;
import com.nokia.mid.ui.FileSelectDetail;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.controller.SettingsManager;
import com.nokia.example.voip.engine.SettingsHelper;
import com.nokia.example.voip.model.Settings;
import com.nokia.example.voip.view.components.ToggleSwitchItem;

public class SettingsView
    extends Form {

    private Settings settings;
    private SettingsManager settingsManager;
    private SettingsHelper settingsHelper;
    private ViewManager viewManager;
    private Command showRingtonePickerCommand;
    private Command saveCommand;

    private TextField usernameTextField;
    private TextField passwordTextField;
    private TextField registrarTextField;
    private StringItem ringToneItem;
    private ToggleSwitchItem showCallerIdToggleSwitch;

    private static final String DEFAULT_RINGTONE = "Default";

    public SettingsView(
            SettingsManager settingsManager,
            SettingsHelper settingsHelper,
            ViewManager viewManager) {
        super("Settings");
        this.settingsManager = settingsManager;
        this.settingsHelper = settingsHelper;
        this.viewManager = viewManager;
        this.settings = settingsManager.getSettings();
        
        setCommandListener(new SettingsViewCommandListener());
        
        usernameTextField = new TextField(
            "Username",
            settings.getUsername(),
            Integer.MAX_VALUE,
            TextField.NON_PREDICTIVE);
        append(usernameTextField);
        
        passwordTextField = new TextField(
            "Password",
            settings.getPassword(),
            Integer.MAX_VALUE,
            TextField.PASSWORD);
        append(passwordTextField);
        
        registrarTextField = new TextField(
            "Registrar",
            settings.getRegistrar(),
            Integer.MAX_VALUE,
            TextField.URL);
        append(registrarTextField);
        
        String ringtoneUrl = (settings.getRingtoneUrl() == null)
            ? DEFAULT_RINGTONE : settings.getRingtoneUrl();
        ringToneItem = new StringItem("Ringtone", ringtoneUrl, StringItem.PLAIN);
        showRingtonePickerCommand = new Command("Select", Command.ITEM, 0);
        ringToneItem.setDefaultCommand(showRingtonePickerCommand);
        
        ringToneItem.setItemCommandListener(new ItemCommandListener() {
            public void commandAction(Command c, Item item) {
                showRingtonePicker();
            }
        });
        
        append(ringToneItem);
        
        showCallerIdToggleSwitch = new ToggleSwitchItem("Show caller ID");
        showCallerIdToggleSwitch.setToggled(settings.isShowCallerId());
        append(showCallerIdToggleSwitch);
        
        saveCommand = new Command("Save", Command.SCREEN, 0);
        addCommand(saveCommand);
    }

    private void showRingtonePicker() {
        new Thread() {
            public void run() {
                // Instantiate the FileSelect with types
                FileSelectDetail[] arrSelectedFiles;
                
                try {
                    arrSelectedFiles = FileSelect.launch(
                        System.getProperty("fileconn.dir.tones"),
                        FileSelect.MEDIA_TYPE_AUDIO,
                        false);
                    
                    // If files were selected and returned
                    if (arrSelectedFiles != null) {
                        ringToneItem.setText(arrSelectedFiles[0].url);
                    }
                    else {
                        ringToneItem.setText(DEFAULT_RINGTONE);
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    private class SettingsViewCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            if (c.getCommandType() == Command.BACK) {
                viewManager.popView();
            }
            
            if (c == saveCommand) {
                settings.setUsername(usernameTextField.getString());
                settings.setPassword(passwordTextField.getString());
                settings.setRegistrar(registrarTextField.getString());
                settings.setRingtoneUrl(ringToneItem.getText());
                settings.setShowCallerId(showCallerIdToggleSwitch.isToggled());
                
                try {
                    settingsManager.saveState();
                    settingsHelper.deleteSettings();
                    settingsHelper.writeSettings();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                viewManager.popView();
            }
        }
    }
}
