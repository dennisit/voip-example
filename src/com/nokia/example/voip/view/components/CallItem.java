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

public abstract class CallItem
    extends CustomItem {

    private static Image IMAGE_CALL;
    private static Image IMAGE_CALL_PRESSED;

    private boolean pressed;

    static {
        try {
            IMAGE_CALL = Image.createImage("/call.png");
            IMAGE_CALL_PRESSED = Image.createImage("/call_pressed.png");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public CallItem() {
        super("");
    }

    protected int getMinContentWidth() {
        return 240;
    }

    protected int getMinContentHeight() {
        return IMAGE_CALL.getHeight() + 10;
    }

    protected int getPrefContentWidth(int height) {
        return 240;
    }

    protected int getPrefContentHeight(int width) {
        return IMAGE_CALL.getHeight() + 10;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
     */
    protected void paint(Graphics g, int w, int h) {
        Image image = pressed ? IMAGE_CALL_PRESSED : IMAGE_CALL;
        
        g.drawImage(
            image,
            w / 2 - image.getWidth() / 2,
            h / 2 - image.getHeight() / 2,
            Graphics.TOP | Graphics.LEFT);
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
        if (pressed) {
            onClick();
        }
        
        pressed = false;
        repaint();
    }

    public abstract void onClick();
}
