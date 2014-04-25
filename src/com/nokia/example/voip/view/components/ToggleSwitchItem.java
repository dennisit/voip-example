/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view.components;

import java.io.IOException;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class ToggleSwitchItem
    extends CustomItem {

    private static Image IMAGE_TOGGLE_SWITCH_TOGGLED;
    private static Image IMAGE_TOGGLE_SWITCH_UNTOGGLED;

    private String label;
    private boolean toggled;
    private boolean pressed;

    static {
        try {
            IMAGE_TOGGLE_SWITCH_TOGGLED = Image
                .createImage("/toggle_switch_toggled.png");
            IMAGE_TOGGLE_SWITCH_UNTOGGLED = Image
                .createImage("/toggle_switch_untoggled.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ToggleSwitchItem(String label) {
        super("");
        this.label = label;
    }

    protected int getMinContentWidth() {
        return 240;
    }

    protected int getMinContentHeight() {
        return 50;
    }

    protected int getPrefContentWidth(int height) {
        return 240;
    }

    protected int getPrefContentHeight(int width) {
        return 50;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
     */
    protected void paint(Graphics g, int w, int h) {
        g.setColor(Theme.FOREGROUND_COLOR);
        g.drawString(label, 0, 5, Graphics.TOP | Graphics.LEFT);
        
        Image i = toggled ? IMAGE_TOGGLE_SWITCH_TOGGLED
            : IMAGE_TOGGLE_SWITCH_UNTOGGLED;
        g.drawImage(i, w - i.getWidth(), 0, Graphics.TOP | Graphics.LEFT);
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        repaint();
    }

    public boolean isToggled() {
        return toggled;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#pointerPressed(int, int)
     */
    public void pointerPressed(int x, int y) {
        pressed = true;
        repaint();
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#pointerReleased(int, int)
     */
    public void pointerReleased(int x, int y) {
        if (pressed == true) {
            setToggled(!toggled);
        }
        
        pressed = false;
        repaint();
    }
}
