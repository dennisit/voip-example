/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.mid.ui.CanvasGraphicsItem;

public abstract class Button
    extends CanvasGraphicsItem {

    protected static final int BACKGROUND_COLOR = 0xf4f4f4;

    protected Image image;
    protected Image pressedImage;
    protected boolean pressed;

    public Button(Image image, Image pressedImage) {
        super(image.getWidth(), image.getHeight());
        this.image = image;
        this.pressedImage = pressedImage;
    }

    /**
     * @see com.nokia.mid.ui.CanvasGraphicsItem#paint(javax.microedition.lcdui.Graphics)
     */
    protected void paint(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (pressed) {
            g.drawImage(pressedImage, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
        else {
            g.drawImage(image, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
    }

    public boolean contains(int x, int y) {
        return (x >= getPositionX() && x < getPositionX() + getWidth()
            && y >= getPositionY() && y < getPositionY() + getHeight());
    }

    public void pointerPressed(int x, int y) {
        pressed = true;
        repaint();
    }

    public void pointerReleased(int x, int y) {
        if (pressed == true) {
            onClick();
        }
        
        pressed = false;
        repaint();
    }

    public abstract void onClick();
}
