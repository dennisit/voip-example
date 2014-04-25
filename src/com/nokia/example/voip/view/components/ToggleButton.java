/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class ToggleButton
    extends Button {

    private boolean toggled;

    public ToggleButton(Image image, Image pressedImage) {
        super(image, pressedImage);
    }

    /**
     * @see com.nokia.example.voip.view.components.Button#paint(javax.microedition.lcdui.Graphics)
     */
    protected void paint(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (pressed || toggled) {
            g.drawImage(pressedImage, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
        else {
            g.drawImage(image, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
    }

    /**
     * @see com.nokia.example.voip.view.components.Button#pointerReleased(int, int)
     */
    public void pointerReleased(int x, int y) {
        toggled = !toggled;
        super.pointerReleased(x, y);
    }

    public boolean isToggled() {
        return toggled;
    }
}
