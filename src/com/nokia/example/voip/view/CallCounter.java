/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */
package com.nokia.example.voip.view;

import java.util.Timer;
import java.util.TimerTask;

import com.nokia.example.voip.engine.Call;
/**
 * A class used for showing the mute and hold options
 * when the call is active and connected. 
 * The call duration counter is also activated during
 * a connected call.
 */
public class CallCounter
    extends TimerTask {
    
    private CallView callView;
    private Timer timer; 
    private String durationString;
    // the call duration in seconds
    private int duration = 0;  
    private Call call;
    
    public CallCounter(CallView callView, Call call) {
        this.callView = callView; 
        this.call = call;
        timer = new Timer();
        timer.schedule(this, 0, 1000);
    }

    public void run() {
        if(call.isActiveCall()) {
            duration++;  
            durationString = convertDurationToString(duration);
            callView.setCallDuration(durationString);
            callView.showMuteHold(true);
        }
        else {
            callView.showMuteHold(false);
        }
    }
    /*
     * Converts the call duration from seconds to
     * hh:mm:ss format 
     */
    private String convertDurationToString(int duration) {
        int remainingSeconds = duration % 60;
        int minutes = (duration - remainingSeconds) / 60;
        int remainingMinutes = minutes % 60;
        int remainingHours = (minutes - remainingMinutes) / 60;
               
        return formatTime(remainingHours) + ":" + 
               formatTime(remainingMinutes) + ":" + 
               formatTime(remainingSeconds);
    }
     /**
     * Adds a leading zero so that the call 
     * duration format always remains xx:xx:xx.
     * 
     */
    private String formatTime(int digits) {
        String result;
        
        if(digits < 10) {
            result = "0" + String.valueOf(digits);
        }
        else {
            result = String.valueOf(digits);
        }
            
        return result;
    }
    
}
