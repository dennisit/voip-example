/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.VoIPExample;
import com.nokia.example.voip.engine.Call;
import com.nokia.example.voip.engine.CallStateListener;
import com.nokia.example.voip.model.CallLogItem;
import com.nokia.example.voip.model.Contact;
import com.nokia.example.voip.view.components.Button;

public class IncomingCallView
    extends Canvas
    implements CallStateListener {

    private static final int BACKGROUND_COLOR = 0xf4f4f4;
    private static final int FOREGROUND_COLOR = 0x555555;

    private static Image IMAGE_ANSWER;
    private static Image IMAGE_ANSWER_PRESSED;
    private static Image IMAGE_REJECT;
    private static Image IMAGE_REJECT_PRESSED;

    private Contact contact;
    private Call call;
    private ViewManager viewManager;
    private Vector buttons;

    static {
        try {
            IMAGE_ANSWER = Image.createImage("/call.png");
            IMAGE_ANSWER_PRESSED = Image.createImage("/call_pressed.png");
            IMAGE_REJECT = Image.createImage("/end_call.png");
            IMAGE_REJECT_PRESSED = Image.createImage("/end_call_pressed.png");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public IncomingCallView(ViewManager viewManager, Contact contact, Call call) {
        this.viewManager = viewManager;
        this.contact = contact;
        this.call = call;
        setTitle("Incoming VoIP call");
        setCommandListener(new IncomingCallViewCommandListener());
        call.setCallStateListener(this);
        
        buttons = new Vector();
        
        Button answerButton = new AnswerButton();
        addButton(
            answerButton,
            getWidth() / 2 - answerButton.getWidth() / 2,
            100);
        
        Button rejectButton = new RejectButton();
        addButton(
            rejectButton,
            getWidth() / 2 - rejectButton.getWidth() / 2,
            100 + 2 * rejectButton.getHeight());
    }

    /**
     * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
     */
    protected void paint(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(FOREGROUND_COLOR);
        g.setFont(Font.getFont(
            Font.FACE_SYSTEM,
            Font.STYLE_BOLD,
            Font.SIZE_LARGE));
        
        int stringWidth = g.getFont().stringWidth(contact.getName());
        
        g.drawString(
            contact.getName(),
            getWidth() / 2 - stringWidth / 2,
            20,
            Graphics.TOP | Graphics.LEFT);
        
        g.setFont(Font.getDefaultFont());
        stringWidth = g.getFont().stringWidth(contact.getVoipAddress());
        
        g.drawString(
            contact.getVoipAddress(),
            getWidth() / 2 - stringWidth / 2,
            60,
            Graphics.TOP | Graphics.LEFT);
    }

    public final void addButton(Button button, int x, int y) {
        buttons.addElement(button);
        button.setPosition(x, y);
        button.setParent(this);
        button.setVisible(true);
    }

    private void answerCall() {
        VoIPExample.getInstance().getRingtonePlayer().stopRinging();
        call.answer();
        
        VoIPExample.getInstance().getCallLogManager()
            .addLogItem(contact.getVoipAddress(), CallLogItem.TYPE_RECEIVED);
        
        viewManager.popView();
        viewManager.pushView(new CallView(viewManager, contact, call));
    }

    private void rejectCall() {
        VoIPExample.getInstance().getRingtonePlayer().stopRinging();
        call.reject();
        call.setCallStateListener(null);
        
        VoIPExample.getInstance().getCallLogManager()
            .addLogItem(contact.getVoipAddress(), CallLogItem.TYPE_RECEIVED);
        
        viewManager.popView();
    }

    /**
     * @see com.nokia.example.voip.engine.CallStateListener#onCallDisconnected()
     */
    public void onCallDisconnected() {
        VoIPExample.getInstance().getRingtonePlayer().stopRinging();
        call.setCallStateListener(null);
        
        VoIPExample.getInstance().getCallLogManager()
            .addLogItem(contact.getVoipAddress(), CallLogItem.TYPE_MISSED);
        
        viewManager.popView();
    }

    /**
     * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
     */
    public void pointerPressed(int x, int y) {
        Enumeration e = buttons.elements();
        
        while (e.hasMoreElements()) {
            Button b = (Button) e.nextElement();
            
            if (b.contains(x, y)) {
                b.pointerPressed(x, y);
            }
        }
    }

    /**
     * @see javax.microedition.lcdui.Canvas#pointerReleased(int, int)
     */
    public void pointerReleased(int x, int y) {
        Enumeration e = buttons.elements();
        
        while (e.hasMoreElements()) {
            Button b = (Button) e.nextElement();
            
            if (b.contains(x, y)) {
                b.pointerReleased(x, y);
            }
        }
    }

    private class AnswerButton
        extends Button {
        
        public AnswerButton() {
            super(IMAGE_ANSWER, IMAGE_ANSWER_PRESSED);
        }
        
        public void onClick() {
            answerCall();
        }
    }

    private class RejectButton
        extends Button {
        
        public RejectButton() {
            super(IMAGE_REJECT, IMAGE_REJECT_PRESSED);
        }
        
        public void onClick() {
            rejectCall();
        }
    }

    private class IncomingCallViewCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            if (c.getCommandType() == Command.BACK) {
                rejectCall();
            }
        }
    }
}
