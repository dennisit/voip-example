/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.VoIPExample;
import com.nokia.example.voip.controller.CallLogManager;
import com.nokia.example.voip.controller.ContactsManager;
import com.nokia.example.voip.model.CallLogItem;
import com.nokia.example.voip.model.Contact;

public class CallLogView
    extends List
    implements TabbedView {

    private static final String[] titles =
        { "Received calls", "Dialed calls", "Missed calls" };

    private ContactsManager contactsManager;
    private CallLogManager callLogManager;
    private ViewManager viewManager;

    public CallLogView() {
        super("Call log", List.IMPLICIT);
        
        contactsManager = VoIPExample.getInstance().getContactsManager();
        callLogManager = VoIPExample.getInstance().getCallLogManager();
        viewManager = VoIPExample.getInstance().getViewManager();
        
        append(titles[CallLogItem.TYPE_RECEIVED], null);
        append(titles[CallLogItem.TYPE_DIALED], null);
        append(titles[CallLogItem.TYPE_MISSED], null);
        
        setCommandListener(new CallLogViewCommandListener());
    }

    private class CallLogViewCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            viewManager.getGlobalCommandListener().commandAction(c, d);
            
            if (c == List.SELECT_COMMAND) {
                viewManager.pushView(new CallLog((byte) getSelectedIndex()));
            }
        }
    }

    private class CallLog
        extends List {
        
        private Vector data;
        
        public CallLog(byte type) {
            super(titles[type], List.IMPLICIT);
            setCommandListener(new CommandListener() {
                
                public void commandAction(Command c, Displayable d) {
                    if (c.getCommandType() == Command.BACK) {
                        viewManager.popView();
                    }
                    else if (c == List.SELECT_COMMAND) {
                        CallLogItem cli = (CallLogItem) data
                            .elementAt(getSelectedIndex());
                        Contact contact = contactsManager.findContact(cli
                            .getVoipAddress());
                        
                        if (contact == null) {
                            contact = new Contact("", cli.getVoipAddress());
                        }
                        
                        viewManager.pushView(new ContactView(
                            contact,
                            viewManager,
                            contactsManager));
                    }
                }
            });
            
            data = callLogManager.getCallLog(type);
            Enumeration e = data.elements();
            
            while (e.hasMoreElements()) {
                CallLogItem cli = (CallLogItem) e.nextElement();
                Date d = new Date(cli.getTimestamp());
                String ds = d.toString().substring(4, 16);
                Contact c = contactsManager.findContact(cli.getVoipAddress());
                
                if (c != null) {
                    append(c.toString() + "  -  " + ds, null);
                }
                else {
                    append(cli.toString() + "  -  " + ds, null);
                }
            }
        }
    }
}
