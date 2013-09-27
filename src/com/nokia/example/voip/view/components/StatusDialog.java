package com.nokia.example.voip.view.components;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;

import com.nokia.mid.voip.VoipStates;

/**
 * A simple dialog displaying the changes in the VoIP state.
 */
public class StatusDialog extends Alert {
    // Constants
    private static final int TIMEOUT = 5000; // Milliseconds

    private StatusDialog(String title) {
        super(title);
    }

    /**
     * Displays the dialog whose content is based on the given state and cause.
     * @param display The Display instance.
     * @param state The VoIP settings state.
     * @param cause The cause associated with the state.
     */
    public static void show(Display display, int state, int cause) {
        if (display == null) {
            return;
        }
        
        StatusDialog dialog = new StatusDialog("State changed");
        dialog.setTimeout(TIMEOUT);
        dialog.setType(AlertType.INFO);
        
        String message = null;
        boolean showCode = true;
        
        switch (state) {
            case VoipStates.SETTINGS_ERROR:
                message = "Error occured, code:";
                break;
            case VoipStates.SETTINGS_OFFLINE:
                message = "Offline, code:";
                break;
            case VoipStates.SETTINGS_ONLINE:
                message = "VoIP is now online!";
                showCode = false;
                break;
            default:
                message = "Code:";
        }
        
        if (showCode) {
            message += " " + state + "; " + cause;
        }
        
        dialog.setString(message);
        
        try {
            display.setCurrent(dialog);
        }
        catch (Exception e) {
        }
    }
}
