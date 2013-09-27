/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view.components;

import java.io.IOException;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.example.voip.model.Contact;

/**
 * A custom list item with title and subtitle.
 */
public class CustomContactListItem extends CustomItem {
    // Constants
    private static final String TEXT_CUTTER_IMAGE_URI = "/text-cutter.png";
    private static final int DEFAULT_WIDTH = 240;
    private static final int MARGIN = 5;
    private static final int TITLE_TEXT_COLOR = 0x464646;
    private static final int SUBTITLE_TEXT_COLOR = 0xacacac;
    private static final int BG_HILIGHT_COLOR = 0xe1e1e1;
    private static final int JITTER_THRESHOLD = 12;
    private static final int INVALID = -1;

    // Members
    private Image textCutterImage = null;
    private Font titleFont = Font.getDefaultFont();
    private Font subtitleFont = null;
    private Contact contact = null;
    private Listener listener = null;
    private String title = null;
    private String subtitle = null;
    private int width = DEFAULT_WIDTH;
    private int height = 0;
    private int lastX = INVALID;
    private int lastY = INVALID;
    private boolean pressed = false;

    /**
     * Constructor.
     * @param title
     * @param subtitle
     */
    public CustomContactListItem(String title, String subtitle) {
        super(null);
        this.title = title;
        this.subtitle = subtitle;
        init();
    }

    /**
     * Constructor.
     * @param contact
     */
    public CustomContactListItem(Contact contact) {
        super(null);
        this.contact = contact;
        
        if (contact != null) {
            title = contact.getName();
            subtitle = contact.getVoipAddress();
        }
        
        init();
    }

    /**
     * Common construction.
     */
    private void init() {
        subtitleFont = Font.getFont(titleFont.getFace(), titleFont.getStyle(), Font.SIZE_SMALL);
        height = titleFont.getHeight() + subtitleFont.getHeight() + MARGIN;
    }

    /**
     * Private, default constructor.
     */
    private CustomContactListItem() {
        super(null);
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    public String getTitle() {
        return title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        repaint();
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        
        if (contact != null) {
            title = contact.getName();
            subtitle = contact.getVoipAddress();
        }
        
        repaint();
    }

    public Contact getContact() {
        return contact;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setPressed(boolean pressed) {
        if (this.pressed != pressed) {
            this.pressed = pressed;
            repaint();
        }
    }

    protected int getMinContentHeight() {
        return height;
    }

    protected int getMinContentWidth() {
        return width;
    }

    protected int getPrefContentHeight(int width) {
        return height;
    }

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
        
        if (pressed) {
            graphics.setColor(BG_HILIGHT_COLOR);
            graphics.fillRect(0, 0, width, height);
        }
        
        if (title != null) {
            graphics.setColor(TITLE_TEXT_COLOR);
            graphics.setFont(titleFont);
            graphics.drawString(title, MARGIN, MARGIN, Graphics.TOP | Graphics.LEFT);
        }
        
        if (subtitle != null) {
            graphics.setColor(SUBTITLE_TEXT_COLOR);
            graphics.setFont(subtitleFont);
            graphics.drawString(subtitle, MARGIN,
                MARGIN + titleFont.getHeight(), Graphics.TOP | Graphics.LEFT);
        }
        
        final int maxWidth = this.width - MARGIN * 2;
        
        if (titleFont.stringWidth(title) > maxWidth
                || subtitleFont.stringWidth(subtitle) > maxWidth)
        {
            // Draw the text cutter
            if (textCutterImage == null) {
                try {
                    textCutterImage = Image.createImage(TEXT_CUTTER_IMAGE_URI);
                }
                catch (IOException e) {
                    System.out.println("CustomListItem.paint(): Failed to load text cutter image!");
                    return;
                }
            }
            
            // Scaling the image would be a more sophisticated solution, but
            // drawing the image twice works as well
            graphics.drawImage(textCutterImage,
                this.width - textCutterImage.getWidth(), 0,
                Graphics.TOP | Graphics.LEFT);
            graphics.drawImage(textCutterImage,
                this.width - textCutterImage.getWidth(),
                textCutterImage.getHeight(), Graphics.TOP | Graphics.LEFT);
        }
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#pointerPressed(int, int)
     */
    protected void pointerPressed(int x, int y) {
        lastX = x;
        lastY = y;
        setPressed(true);
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#pointerDragged(int, int)
     */
    protected void pointerDragged(int x, int y) {
        if (lastX != INVALID
                && (Math.abs(lastX - x) > JITTER_THRESHOLD
                    || Math.abs(lastY - y) > JITTER_THRESHOLD))
        {
            lastX = INVALID;
            lastY = INVALID;
            setPressed(false);
        }
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#pointerReleased(int, int)
     */
    protected void pointerReleased(int x, int y) {
        if (lastX != INVALID
                && listener != null
                && Math.abs(lastX - x) <= JITTER_THRESHOLD
                && Math.abs(lastY - y) <= JITTER_THRESHOLD)
        {
            listener.onTapped(this);
        }
        
        lastX = INVALID;
        lastY = INVALID;
        setPressed(false);
    }

    /**
     * Listener interface.
     */
    public interface Listener {
        void onTapped(CustomContactListItem item);
    }
}
