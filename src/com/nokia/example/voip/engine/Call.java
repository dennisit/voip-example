/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.engine;

import com.nokia.mid.voip.VoipAudioCall;
import com.nokia.mid.voip.VoipAudioCallStateListener;
import com.nokia.mid.voip.VoipCauses;
import com.nokia.mid.voip.VoipManager;
import com.nokia.mid.voip.VoipStates;
import com.nokia.example.voip.VoIPExample;
import java.util.Vector;

public class Call
    implements VoipAudioCallStateListener {

    private VoipAudioCall call = null;
    private CallStateListener listener = null;
    private boolean callGotThrough = false;
        
    public void setCallStateListener(CallStateListener listener) {
        this.listener = listener;
    }

    public boolean isActive() {
        if (call == null || call.getCallState() == VoipCauses.CAUSE_DISCONNECTED) {
            return false;
        }
        
        return true;
    }

    public String getAddress() {
        // Get the SIP address of the call instance.
        String address = "";
        
        if (call != null) {
            address = call.getCallAddress();
            
            // Remove "sip:" prefix from the address
            if (address.startsWith("sip:")) {
                address = address.substring(4);
            }
        }
        
        return address;
    }

    public void makeCall(String voipAddress) {
        // Set up an outgoing call instance.
        call = VoipManager.createVoipAudioMOCallInstance(voipAddress);
        call.setVoipAudioCallStateListener(this);
        boolean showCallerId = VoIPExample.getInstance().getSettingsManager()
            .getSettings().isShowCallerId();
        call.startCall(showCallerId);
    }

    public void receiveCall(int callId) {
        // Set up an incoming call instance.
        call = VoipManager.createVoipAudioMTCallInstance(callId);
        call.setVoipAudioCallStateListener(this);
    }

    public void answer() {
        if (call != null) {
            call.startCall(true);
        }
    }

    public void reject() {
        if (call != null) {
            call.endCall();
        }
    }

    public void end() {
        if (call != null) {
            call.endCall();
        }
    }

    public void setMute(boolean state) {
        if (call.isMuted() != state) {
            call.toggleMute();
        }
    }

    public void setSpeaker(boolean state) {
        call.setSpeakerMode(state);
    }

    public void setHold(boolean state) {
        if (call.isOnHold() == true && state == false) {
            call.retrieveCall();
        }
        else if (call.isOnHold() == false && state == true) {
            call.holdCall();
        } 
    }

    public void sendDTMF(String digits) {
        if(callGotThrough){
            call.sendDTMF(digits);
        }
    }

    public boolean isActiveCall() {
        if(callGotThrough) {
            return true;
        }
        else {
            return false;
        }
    } 
   
    public void voipAudioCallUpdated(int state, int cause, int callId) {
        System.out.println("Call state: " + state + ", cause: " + cause
            + ", callId:" + callId);
        
        if (state == VoipStates.AUDIOCALL_ENDED || 
           (state == VoipStates.AUDIOCALL_CALL & cause == VoipCauses.CAUSE_DISCONNECTED)) {
            System.out.println("Call disconnected");
            callGotThrough = false;
            // Create a new thread to tear down the call instance and notify
            // the call state listener about disconnection after few seconds.
            new Thread() {
                public void run() {
                    try {
                        if (listener != null) {
                            listener.onCallDisconnected();
                        }
                        
                        Thread.sleep(3000);
                        call.setVoipAudioCallStateListener(null);
                        call = null;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        
        if (state == VoipStates.AUDIOCALL_CALL && cause == VoipCauses.CAUSE_ACTIVE) {
            callGotThrough = true;
        }
    }
}
