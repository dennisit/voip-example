/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.engine;

import java.lang.ref.WeakReference;

import com.nokia.mid.voip.VoipSettingsStateListener;
import com.nokia.mid.voip.VoipStates;

/**
 * A VoIP settings state listener that can forward the events to multiple other
 * listeners.
 */
public class SettingsStateListener
    implements VoipSettingsStateListener
{
    public static final int UNKNOWN = -1;
    private static final int MAX_LISTENER_COUNT = 5;

    private static SettingsStateListener instance = null;
    private WeakReference[] listeners = null;
    private int lastState = UNKNOWN;
    private int lastCause = UNKNOWN;

    /** 
     * @return The singleton instance of this class.
     */
    public static SettingsStateListener getInstance() {
        if (instance == null) {
            instance = new SettingsStateListener();
        }
        
        return instance;
    }

    /**
     * Constructor.
     */
    private SettingsStateListener() {
        listeners = new WeakReference[MAX_LISTENER_COUNT];
    }

    /**
     * @see com.nokia.mid.voip.VoipSettingsStateListener#voipSettingsUpdated(int, int)
     */
    public void voipSettingsUpdated(int state, int cause) {
        System.out.println("SettingsStateListener.voipSettingsUpdated(): State: "
                + voipStateToString(state) + " (" + state + "), cause: " + cause);
        
        lastState = state;
        lastCause = cause;
        
        for (int i = 0; i < MAX_LISTENER_COUNT; ++i) {
            VoipSettingsStateListener listener = null;
            
            if (listeners[i] != null && listeners[i].get() != null) {
                listener = (VoipSettingsStateListener)listeners[i].get();
                listener.voipSettingsUpdated(state,  cause);
            }
        }
    }

    public int getLastState() {
        return lastState;
    }

    public int getLastCause() {
        return lastCause;
    }

    /**
     * Adds a listener who to forward the settings updated events.
     * @param listener The listener or null if none.
     * @return True if the listener was added successfully, false otherwise.
     */
    public boolean addListener(VoipSettingsStateListener listener) {
        for (int i = 0; i < MAX_LISTENER_COUNT; ++i) {
            if (listeners[i] == null || listeners[i].get() == null) {
                listeners[i] = new WeakReference(listener);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Removes the given listener.
     * @param listener The listener to remove.
     * @return True if successful, false otherwise.
     */
    public boolean removeListener(VoipSettingsStateListener listener) {
        if (listener == null) {
            return false;
        }
        
        for (int i = 0; i < MAX_LISTENER_COUNT; ++i) {
            if (listeners[i] != null && listeners[i].get() == listener) {
                listeners[i] = null;
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns a string matching the "enumerated" state.
     * @param state The state.
     * @return A string matching the given state.
     */
    public static final String voipStateToString(final int state) {
        switch (state) {
            case VoipStates.AUDIOCALL_CALL: return "AUDIOCALL_CALL";
            case VoipStates.AUDIOCALL_COLP: return "AUDIOCALL_COLP";
            case VoipStates.AUDIOCALL_ENDED: return "AUDIOCALL_ENDED";
            case VoipStates.AUDIOCALL_NOTIF: return "AUDIOCALL_NOTIF";
            case VoipStates.AUDIOCALL_OPERROR: return "AUDIOCALL_OPERROR";
            case VoipStates.SETTINGS_ERROR: return "SETTINGS_ERROR";
            case VoipStates.SETTINGS_OFFLINE: return "SETTINGS_OFFLINE";
            case VoipStates.SETTINGS_ONLINE: return "SETTINGS_ONLINE";
            case VoipStates.SETTINGS_WRITTEN: return "SETTINGS_WRITTEN";
            default:
                break;
        }
        
        return "(unknown state)";
    }
}
