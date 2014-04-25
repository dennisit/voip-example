/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip;

import java.io.IOException;

import javax.microedition.midlet.MIDlet;

import com.nokia.mid.voip.VoipMTCallListener;
import com.nokia.mid.voip.VoipManager;

import com.nokia.example.voip.controller.CallLogManager;
import com.nokia.example.voip.controller.ContactsManager;
import com.nokia.example.voip.controller.SettingsManager;
import com.nokia.example.voip.engine.Call;
import com.nokia.example.voip.engine.RingtonePlayer;
import com.nokia.example.voip.engine.SettingsHelper;
import com.nokia.example.voip.model.CallLogItem;
import com.nokia.example.voip.model.Contact;
import com.nokia.example.voip.view.CallView;
import com.nokia.example.voip.view.ContactsView;
import com.nokia.example.voip.view.IncomingCallView;

/**
 * The main class of the application.
 */
public class VoIPExample
    extends MIDlet
    implements VoipMTCallListener {

    private static VoIPExample instance;

    private ViewManager viewManager;
    private ContactsManager contactsManager;
    private CallLogManager callLogManager;
    private SettingsManager settingsManager;
    private SettingsHelper settingsHelper;
    private RingtonePlayer ringtonePlayer;
    private Call call = null;

    /**
     * Constructor.
     */
    public VoIPExample() {
        if (!VoipManager.setVoipMTCallListener(this)) {
            System.out.println("VoIP API not supported!");
        }
    }

    /** 
     * @return The instance of this class.
     */
    public static VoIPExample getInstance() {
        return instance;
    }

    /**
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    public void startApp() {
        if (instance == null) {
            instance = this;
            
            contactsManager = new ContactsManager();
            settingsManager = new SettingsManager();
            settingsHelper = new SettingsHelper(settingsManager);
            callLogManager = new CallLogManager();
            
            try {
                contactsManager.loadState();
                settingsManager.loadState();
                callLogManager.loadState();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            
            ringtonePlayer = new RingtonePlayer();
            
            viewManager = new ViewManager(this);
            
            ContactsView contactsView = new ContactsView(
                viewManager,
                contactsManager);
            viewManager.pushView(contactsView);
        }
        
        // Check the argument values if the MIDlet was launched by the platform
        // VoIP service due to an incoming call.
        String invokeReason = this.getAppProperty("arg-0");
        
        if (invokeReason.equals(VoipManager.VOIP_MT_CALL_ALERTING)
            || invokeReason.equals(VoipManager.VOIP_MT_CALL_WAITING))
        {
            System.out.println("MIDlet launched due to an incoming call");
            
            try {
                int callId = Integer.parseInt(this.getAppProperty("arg-1"));
                handleIncomingCall(callId);
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid call ID!");
            }
        }
    }

    /**
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    public void pauseApp() {
    }

    /**
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    public void destroyApp(boolean unconditional) {
        if (call != null && call.isActive()) {
            call.end();
        }
        
        VoipManager.setVoipMTCallListener(null);
    }

    /**
     * Tries to make a VoIP class to the given address. Adds the event also to
     * the call log.
     * @param voipAddress The recipient for the the call.
     */
    public void call(String voipAddress) {
        System.out.println("Outgoing call to: " + voipAddress);
        
        // Add a call log entry.
        Contact contact = contactsManager.findContact(voipAddress);
        
        if (contact == null) {
            contact = new Contact("", voipAddress);
        }
        
        callLogManager.addLogItem(voipAddress, CallLogItem.TYPE_DIALED);
        
        call = new Call();
        call.makeCall(voipAddress);
        
        viewManager.pushView(new CallView(viewManager, contact, call));
    }

    public ContactsManager getContactsManager() {
        return contactsManager;
    }

    public CallLogManager getCallLogManager() {
        return callLogManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public SettingsHelper getSettingsHelper() {
        return settingsHelper;
    }

    public RingtonePlayer getRingtonePlayer() {
        return ringtonePlayer;
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    /**
     * @see com.nokia.mid.voip.VoipMTCallListener#onIncomingCall(int)
     */
    public void onIncomingCall(int callId) {
        // Handling of multiple simultaneous calls (one active & others on hold)
        // is not implemented in this example. However, the platform VoIP
        // service supports it, so the functionality can be extended to handle
        // multiple calls accordingly.
        if (call == null || !call.isActive()) {
            handleIncomingCall(callId);
        }
        else {
            System.out.println("Incoming call ignored due to an active call");
        }
    }

    /**
     * Handles an incoming call.
     * @param callId The call ID of the caller.
     */
    private void handleIncomingCall(int callId) {
        call = new Call();
        call.receiveCall(callId);
        ringtonePlayer.startRinging();
        
        // Find the caller from the saved contacts to show the name.
        String voipAddress = call.getAddress();
        Contact contact = contactsManager.findContact(voipAddress);
        
        if (contact == null) {
            contact = new Contact("", voipAddress);
        }
        
        System.out.println("Incoming call from: " + voipAddress);
        
        viewManager.pushView(new IncomingCallView(viewManager, contact, call));
    }
}
