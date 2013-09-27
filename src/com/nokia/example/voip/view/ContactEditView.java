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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.VoIPExample;
import com.nokia.example.voip.controller.ContactsManager;
import com.nokia.example.voip.model.Contact;

public class ContactEditView
    extends Form {

    private ViewManager viewManager;
    private ContactsManager contactsManager;
    private Contact contact;
    private TextField nameTextField;
    private TextField voipAddressTextField;
    private Command saveCommand;

    public ContactEditView(ViewManager viewManager,
                           ContactsManager contactsManager)
    {
        super("Add contact");      
        contact = new Contact("", "");
        init(viewManager, contactsManager);
    }

    public ContactEditView(Contact contact,
                           ViewManager viewManager,
                           ContactsManager contactsManager)
    {
        super("Edit contact");
        this.contact = contact;
        init(viewManager, contactsManager);
    }

    private final void init(ViewManager viewManager,
                            ContactsManager contactsManager)
    {
        this.viewManager = viewManager;
        this.contactsManager = contactsManager;
        
        nameTextField = new TextField(
            "Name",
            contact.getName(),
            Integer.MAX_VALUE,
            TextField.INITIAL_CAPS_WORD);
        
        append(nameTextField);
        
        voipAddressTextField = new TextField(
            "VoIP address",
            contact.getVoipAddress(),
            Integer.MAX_VALUE,
            TextField.EMAILADDR);
        
        append(voipAddressTextField);
        
        saveCommand = new Command("Save", Command.SCREEN, 0);
        addCommand(saveCommand);
        setCommandListener(new ContactEditViewCommandListener());
    }

    private boolean validate() {
        if (nameTextField.getString().equals("")
            || voipAddressTextField.getString().equals(""))
        {
            Alert errorAlert = new Alert("Error");
            errorAlert.setType(AlertType.ERROR);
            errorAlert.setString("Please fill in all fields.");
            Display.getDisplay(VoIPExample.getInstance())
                .setCurrent(errorAlert);
            return false;
        }
        
        return true;
    }

    private class ContactEditViewCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            
            if (c.getCommandType() == Command.BACK) {
                viewManager.popView();
            }
            if (c == saveCommand) {
                if (validate()) {
                    try {
                        contact.setName(nameTextField.getString());
                        contact
                            .setVoipAddress(voipAddressTextField.getString());
                        contactsManager.addContact(contact);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    viewManager.popView();
                }
            }
        }
    }
}
