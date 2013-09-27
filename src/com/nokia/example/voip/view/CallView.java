/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.engine.Call;
import com.nokia.example.voip.engine.CallStateListener;
import com.nokia.example.voip.model.Contact;
import com.nokia.example.voip.view.components.Button;
import com.nokia.example.voip.view.components.ToggleButton;

public class CallView
    extends Canvas
    implements CallStateListener {

    private static final int BACKGROUND_COLOR = 0xf4f4f4;
    private static final int FOREGROUND_COLOR = 0x555555;

    private static Image IMAGE_END_CALL;
    private static Image IMAGE_END_CALL_PRESSED;
    private static Image IMAGE_SPEAKER_ENABLED;
    private static Image IMAGE_SPEAKER_DISABLED;
    private static Image IMAGE_MUTE_ENABLED;
    private static Image IMAGE_MUTE_DISABLED;
    private static Image IMAGE_HOLD_ENABLED;
    private static Image IMAGE_HOLD_DISABLED;
    private static Image IMAGE_DIALER_ENABLED;
    private static Image IMAGE_DIALER_DISABLED;

    private Contact contact;
    private Call call;
    private ViewManager viewManager;
    private Vector buttons;
    private CallCounter callCounter;
    private String callDuration = "";
    private Button endCallButton; 
    private HoldButton holdButton;
    private DialerButton dialerButton;
    private MuteButton muteButton;
    private SpeakerButton speakerButton;
    
    static {
        try {
            IMAGE_END_CALL = Image.createImage("/end_call.png");
            IMAGE_END_CALL_PRESSED = Image.createImage("/end_call_pressed.png");
            IMAGE_SPEAKER_ENABLED = Image.createImage("/speaker_enabled.png");
            IMAGE_SPEAKER_DISABLED = Image.createImage("/speaker_disabled.png");
            IMAGE_MUTE_ENABLED = Image.createImage("/mute_enabled.png");
            IMAGE_MUTE_DISABLED = Image.createImage("/mute_disabled.png");
            IMAGE_HOLD_ENABLED = Image.createImage("/hold_enabled.png");
            IMAGE_HOLD_DISABLED = Image.createImage("/hold_disabled.png");
            IMAGE_DIALER_ENABLED = Image
                .createImage("/icon_dialer_selected.png");
            IMAGE_DIALER_DISABLED = Image
                .createImage("/icon_dialer_unselected.png");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public CallView(ViewManager viewManager, Contact contact, Call call) {
        this.viewManager = viewManager;
        this.contact = contact;
        this.call = call;
        setTitle("VoIP call");
        setCommandListener(new CallViewCommandListener());
        call.setCallStateListener(this);
        
        callCounter = new CallCounter(this, call);
        buttons = new Vector();
        
        endCallButton = new EndCallButton();
        addButton(
            endCallButton,
            getWidth() / 2 - endCallButton.getWidth() / 2,
            100);
        endCallButton.setVisible(true);
        
        int w = (getWidth() - 10) / 2;
        int h = 30;
        int x0 = 5;
        int y0 = 160;
        int y1 = 205;
        
        speakerButton = new SpeakerButton();
        int x = x0 + w - w / 2  - speakerButton.getWidth() / 2;
        int y = y0 + h / 2 - speakerButton.getHeight() / 2;
        addButton(speakerButton, x, y);
        speakerButton.setVisible(true);
        
        dialerButton = new DialerButton();
        x = x0 + 2 * w - w /2  - dialerButton.getWidth() / 2;
        y = y0 + h / 2 - dialerButton.getHeight() / 2;
        addButton(dialerButton, x, y);
        dialerButton.setVisible(true);
        
        muteButton = new MuteButton();
        x = x0 + w - w / 2 - muteButton.getWidth() / 2;
        y = y1 + h / 2 - muteButton.getHeight() / 2;
        addButton(muteButton, x, y);
        muteButton.setVisible(false);
        
        holdButton = new HoldButton();
        x = x0 + 2 * w - w /2  - holdButton.getWidth() / 2;
        y = y1 + h / 2 - holdButton.getHeight() / 2;
        addButton(holdButton, x, y);
        holdButton.setVisible(false);
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
                
        int durationStrWidth = g.getFont().stringWidth(callDuration);
        
        g.drawString(callDuration, getWidth() / 2 - durationStrWidth / 2, 
                140, Graphics.TOP | Graphics.LEFT);
    }

    public final void addButton(Button button, int x, int y) {
        buttons.addElement(button);
        button.setPosition(x, y);
        button.setParent(this);
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

    public void showMuteHold(boolean toggle) {
        muteButton.setVisible(toggle);
        muteButton.repaint();
        holdButton.setVisible(toggle);
        holdButton.repaint();
        repaint();
    }
    
    public void onCallDisconnected() {
        call.setCallStateListener(null);
        viewManager.popView();
    }
    /**
     * mutator method for defining the call duration string
     * prior to calling the repaint. 
     */
    protected void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    private void confirmEndCall() {
        final Alert alert = new Alert("End call");
        alert.setType(AlertType.CONFIRMATION);
        alert.setString("Terminate call?");
        alert.addCommand(new Command("OK", Command.OK, 0));
        alert.addCommand(new Command("Cancel", Command.CANCEL, 1));
        
        alert.setCommandListener(new CommandListener() {
            
            public void commandAction(Command c, Displayable d) {
                if (c.getCommandType() == Command.OK) {
                    call.end();
                    viewManager.popView();
                }
                else if (c.getCommandType() == Command.CANCEL) {
                    viewManager.show(CallView.this);
                }
            }
        });
        
        viewManager.show(alert);
    }

    private class EndCallButton
        extends Button {
        
        public EndCallButton() {
            super(IMAGE_END_CALL, IMAGE_END_CALL_PRESSED);
        }
        
        public void onClick() {
            call.end();
        }
    }

    private class SpeakerButton
        extends ToggleButton {
        
        public SpeakerButton() {
            super(IMAGE_SPEAKER_DISABLED, IMAGE_SPEAKER_ENABLED);
        }
        
        public void onClick() {
            System.out.println("speaker button clicked");
            call.setSpeaker(isToggled());
        }
    }

    private class MuteButton
        extends ToggleButton {
        
        public MuteButton() {
            super(IMAGE_MUTE_DISABLED, IMAGE_MUTE_ENABLED);
        }
        
        public void onClick() {
            System.out.println("mute button clicked");
            call.setMute(isToggled());
        }
    }

    private class HoldButton
        extends ToggleButton {
        
        public HoldButton() {
            super(IMAGE_HOLD_DISABLED, IMAGE_HOLD_ENABLED);
        }
        
        public void onClick() {
            System.out.println("hold button clicked");
            call.setHold(isToggled());
        }
    }

    private class DialerButton
        extends Button {
        
        public DialerButton() {
            super(IMAGE_DIALER_DISABLED, IMAGE_DIALER_ENABLED);
        }
        
        public void onClick() {
            System.out.println("dialer button clicked");
            viewManager.pushView(new DTMFView());
        }
    }

    private class CallViewCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            if (c.getCommandType() == Command.BACK) {
                confirmEndCall();
            }
        }
    }

    private class DTMFView
        extends Form {
        
        private TextField dialerField;
        private Item callButton;
        
        public DTMFView() {
            super("Send DTMF tones");
            
            dialerField = new TextField(
                null,
                null,
                Integer.MAX_VALUE,
                TextField.PHONENUMBER);
            
            dialerField.setString("");
            append(dialerField);
            
            callButton = new StringItem("", "Send DTMF", StringItem.BUTTON);
            Command sendCommand = new Command("Send DTMF", Command.ITEM, 0);
            callButton.setDefaultCommand(sendCommand);
            
            callButton.setItemCommandListener(new ItemCommandListener() {
                public void commandAction(Command c, Item item) {
                    String dtmf = dialerField.getString();
                    
                    if (dtmf.length() > 0) {
                        call.sendDTMF(dtmf);
                    }
                    
                    viewManager.popView();
                }
            });
            
            append(callButton);
            setCommandListener(viewManager.getGlobalCommandListener());
        }
    }
}
