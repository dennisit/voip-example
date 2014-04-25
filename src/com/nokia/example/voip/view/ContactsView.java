/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.view;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.nokia.mid.voip.VoipSettingsStateListener;

import com.nokia.example.voip.ViewManager;
import com.nokia.example.voip.VoIPExample;
import com.nokia.example.voip.controller.ContactsManager;
import com.nokia.example.voip.engine.SettingsStateListener;
import com.nokia.example.voip.model.Contact;
import com.nokia.example.voip.view.components.CustomContactListItem;
import com.nokia.example.voip.view.components.StatusDialog;
import com.nokia.example.voip.view.components.StatusIndicator;

public class ContactsView
    extends Form
    implements ContactsManager.Observer,
               CustomContactListItem.Listener,
               TabbedView,
               VoipSettingsStateListener {

    private ViewManager viewManager;
    private ContactsManager contactsManager;
    private Command addContactCommand;
    /**
     * Constructor.
     * @param midlet
     * @param contactsManager
     */
    public ContactsView(ViewManager midlet, ContactsManager contactsManager) {
        super("Contacts");
        this.viewManager = midlet;
        this.contactsManager = contactsManager;
        append(new StatusIndicator());
        addContactCommand = new Command("Add new contact", Command.SCREEN, 0);
        
        addCommand(addContactCommand);
        
        setCommandListener(new ContactsViewCommandListener());
        contactsChanged(contactsManager.getContacts());
        SettingsStateListener.getInstance().addListener(this);
    }

    /**
     * @see com.nokia.example.voip.controller.ContactsManager.Observer#contactsChanged(java.util.Vector)
     */
    public synchronized final void contactsChanged(final Vector contacts) {
        final ContactsView self = this;
        
        Display.getDisplay(VoIPExample.getInstance()).callSerially(
            new Runnable() {
                public void run() {
                    // Remove all contacts from list
                    deleteAll();
                    
                    append(new StatusIndicator());
                    
                    // Append contacts to list
                    Enumeration e = contacts.elements();
                    
                    while (e.hasMoreElements()) {
                        Contact c = (Contact) e.nextElement();
                        CustomContactListItem item = new CustomContactListItem(c);
                        item.setListener(self);
                        append(item);
                    }
                }
            });
    }

    /**
     * @see com.nokia.example.voip.view.components.CustomContactListItem.Listener#onTapped(
     * com.nokia.example.voip.view.components.CustomContactListItem)
     */
    public void onTapped(CustomContactListItem item) {
        Contact contact = item.getContact();
        
        if (contact != null) {
            viewManager.pushView(new ContactView(
                contact,
                viewManager,
                contactsManager));
        }
    }

    /**
     * @see com.nokia.mid.voip.VoipSettingsStateListener#voipSettingsUpdated(int, int)
     */
    public void voipSettingsUpdated(int state, int cause) {
        if (viewManager.getDisplay().getCurrent() == this) {
            StatusDialog.show(viewManager.getDisplay(), state, cause);
        }
    }

    private class ContactsViewCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            viewManager.getGlobalCommandListener().commandAction(c, d);
            if (c == addContactCommand) {
                viewManager.pushView(new ContactEditView(
                    viewManager,
                    contactsManager));
            }
        }
    }
}
