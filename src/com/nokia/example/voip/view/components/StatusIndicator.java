/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view.components;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.example.voip.engine.SettingsStateListener;
import com.nokia.mid.voip.VoipSettingsStateListener;
import com.nokia.mid.voip.VoipStates;

/**
 * A custom component to display the offline/online status.
 */
public class StatusIndicator
    extends CustomItem
    implements VoipSettingsStateListener
{
    // Constants
    private static final String STATUS_STRING = "My status: ";
    private static final String ONLINE_STRING = "Online";
    private static final String OFFLINE_STRING = "Offline";
    private static final String ERROR_STRING = "Error";
    private static final int DEFAULT_WIDTH = 240;
    private static final int HEIGHT = 20;
    private static final int MARGIN = 5;
    private static final int ICON_SIZE = 8;
    private static final int TEXT_COLOR = 0x444444;
    private static final int ONLINE_COLOR = 0x22ee22;
    private static final int OFFLINE_COLOR = 0xee2222;
    private static final int FONT_SIZE = Font.SIZE_SMALL;

    // Members
    private Font font = null;
    private String stateString = null;
    private int width = DEFAULT_WIDTH;
    private int state = VoipStates.SETTINGS_OFFLINE;

    /** 
     * Constructor
     */
    public StatusIndicator() {
        super(null);
        Font defaultFont = Font.getDefaultFont();
        font = Font.getFont(defaultFont.getFace(), defaultFont.getStyle(), FONT_SIZE);
        
        // Get the last known state and start listening to forwarded events
        SettingsStateListener stateListener = SettingsStateListener.getInstance();
        stateListener.addListener(this);
        setState(stateListener.getLastState());
    }

    /**
     * Constructor. Not to be used.
     * @param label
     */
    private StatusIndicator(String label) {
        super(label);
    }

    /**
     * Sets the state and repaints the component.
     * @param state The new state.
     */
    public void setState(int state) {
        if (this.state != state && state != SettingsStateListener.UNKNOWN) {
            this.state = state;
            repaint();
        }
    }

    /** 
     * @return The current state.
     */
    public int getState() {
        return state;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getMinContentHeight()
     */
    protected int getMinContentHeight() {
        return HEIGHT;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getMinContentWidth()
     */
    protected int getMinContentWidth() {
        return width;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getPrefContentHeight(int)
     */
    protected int getPrefContentHeight(int width) {
        return HEIGHT;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getPrefContentWidth(int)
     */
    protected int getPrefContentWidth(int height) {
        return width;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
     */
    protected void paint(Graphics graphics, int width, int h) {
        if (this.width != width) {
            this.width = width;
        }
        
        int color = 0xf4f4f4;
        
        switch (state) {
            case VoipStates.SETTINGS_ERROR:
                stateString = ERROR_STRING;
                color = OFFLINE_COLOR;
                break;
            case VoipStates.SETTINGS_OFFLINE:
                stateString = OFFLINE_STRING;
                color = OFFLINE_COLOR;
                break;
            case VoipStates.SETTINGS_ONLINE:
                stateString = ONLINE_STRING;
                color = ONLINE_COLOR;
                break;
            default:
                break;
        }
        
        graphics.setColor(color);
        graphics.fillRoundRect(MARGIN, (HEIGHT - ICON_SIZE) / 2,
            ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        
        graphics.setColor(TEXT_COLOR);
        graphics.setFont(font);
        graphics.drawString(STATUS_STRING + stateString, MARGIN * 2 + ICON_SIZE,
            (HEIGHT - font.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
    }

    /**
     * @see com.nokia.mid.voip.VoipSettingsStateListener#voipSettingsUpdated(int, int)
     */
    public void voipSettingsUpdated(int state, int cause) {
        System.out.println("StatusIndicator.voipSettingsUpdated(): " + state + ", " + cause);
        setState(state);
    }
}
