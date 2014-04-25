/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.TextField;

import com.nokia.mid.voip.VoipSettingsStateListener;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.VoIPExample;
import com.nokia.example.voip.engine.SettingsStateListener;
import com.nokia.example.voip.view.components.CallItem;
import com.nokia.example.voip.view.components.StatusDialog;
import com.nokia.example.voip.view.components.StatusIndicator;

public class DialerView
    extends Form
    implements TabbedView,
               VoipSettingsStateListener {

    private ViewManager viewManager;
    private TextField dialerField;
    private Item callButton;

    /**
     * Constructor.
     * @param viewManager
     */
    public DialerView(ViewManager viewManager) {
        super("Dialer");
        
        this.viewManager = viewManager;
        
        append(new StatusIndicator());
        append(new Spacer(240, 15));
        
        dialerField = new TextField(
            null,
            null,
            Integer.MAX_VALUE,
            TextField.PHONENUMBER);
        dialerField.setString("");
        
        append(dialerField);
        
        callButton = new CallItem() {
            public void onClick() {
                if (!dialerField.getString().equals("")) {
                    String voipAddress = dialerField.getString();
                    dialerField.setString("");
                    VoIPExample.getInstance().call(voipAddress);
                }
            }
        };
        
        append(callButton);
        
        setCommandListener(viewManager.getGlobalCommandListener());
        SettingsStateListener.getInstance().addListener(this);
    }

    /**
     * @see com.nokia.mid.voip.VoipSettingsStateListener#voipSettingsUpdated(int, int)
     */
    public void voipSettingsUpdated(int state, int cause) {
        if (viewManager.getDisplay().getCurrent() == this) {
            StatusDialog.show(viewManager.getDisplay(), state, cause);
        }
    }
}
