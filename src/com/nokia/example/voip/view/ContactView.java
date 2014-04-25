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
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.VoIPExample;
import com.nokia.example.voip.controller.ContactsManager;
import com.nokia.example.voip.model.Contact;

public class ContactView
    extends Form
    implements ContactsManager.Observer {

    private ViewManager viewManager;
    private Contact contact;
    private ContactsManager contactsManager;

    private TextField nameTextField;
    private TextField voipAddressTextField;

    private Command callCommand;
    private Command editCommand;
    private Command deleteCommand;

    public ContactView(
            Contact contact,
            ViewManager viewManager,
            ContactsManager contactsManager) {
        super("VoIP contact");
        this.contact = contact;
        this.viewManager = viewManager;
        this.contactsManager = contactsManager;
        
        nameTextField = new TextField(
            "Name",
            contact.getName(),
            Integer.MAX_VALUE,
            TextField.UNEDITABLE);
        
        append(nameTextField);
        
        voipAddressTextField = new TextField(
            "VoIP address",
            contact.getVoipAddress(),
            Integer.MAX_VALUE,
            TextField.UNEDITABLE);
        
        append(voipAddressTextField);
        
        callCommand = new Command("Call", Command.SCREEN, 0);
        addCommand(callCommand);
        
        editCommand = new Command("Edit", Command.SCREEN, 1);
        addCommand(editCommand);
        
        deleteCommand = new Command("Delete contact", Command.SCREEN, 2);
        addCommand(deleteCommand);
        
        setCommandListener(new ContactViewCommandListener());
    }

    public void contactsChanged(Vector contacts) {
        nameTextField.setString(contact.getName());
        voipAddressTextField.setString(contact.getVoipAddress());
    }

    private class ContactViewCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            if (c.getCommandType() == Command.BACK) {
                viewManager.popView();
            }
            
            if (c == callCommand) {
                VoIPExample.getInstance().call(contact.getVoipAddress());
            }
            else if (c == editCommand) {
                viewManager.pushView(new ContactEditView(
                    contact,
                    viewManager,
                    contactsManager));
            }
            else if (c == deleteCommand) {
                try {
                    contactsManager.removeContact(contact);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                viewManager.popView();
            }
        }
    }
}
