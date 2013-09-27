/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.nokia.example.voip.model.Contact;

public class ContactsManager
    extends AbstractRMSManager {

    private final Vector contacts;
    private Vector filteredContacts;
    private String filter;

    private Vector observers;

    public ContactsManager() {
        super("Contacts");
        contacts = new Vector();
        observers = new Vector();
        filteredContacts = contacts;
    }

    public Vector getContacts() {
        return filteredContacts;
    }

    public void setFilter(String filter) {
        this.filter = filter;
        filter();
    }

    private void filter() {
        if (filter == null) {
            filteredContacts = contacts;
        }
        else {
            Vector newFilteredContacts = new Vector();
            
            synchronized (contacts) {
                filter = filter.toLowerCase(); // Search is not case-sensitive
                Enumeration e = contacts.elements();
                
                while (e.hasMoreElements()) {
                    Contact c = (Contact) e.nextElement();
                    
                    if (c.getName().toLowerCase().startsWith(filter)) {
                        newFilteredContacts.addElement(c);
                    }
                }
            }
            
            filteredContacts = newFilteredContacts;
        }
        
        notifyObservers();
    }

    public Contact findContact(String voipAddress) {
        Contact contact = null;
        Enumeration e = contacts.elements();
        
        // Find a contact that matches the voip address
        while (e.hasMoreElements()) {
            Contact c = (Contact) e.nextElement();
            
            if (c.getVoipAddress().equals(voipAddress)) {
                contact = c;
            }
        }
        
        return contact;
    }

    public void addContact(Contact contact)
        throws IOException {
        
        synchronized (contacts) {
            if (contacts.isEmpty()) {
                contacts.addElement(contact);
            }
            else {
                if (!contacts.contains(contact)) {
                    // Keep contacts alphabetically sorted
                    for (int i = contacts.size() - 1; i >= 0; i--) {
                        if (contact
                            .getName()
                            .toLowerCase()
                            .compareTo(
                                ((Contact) contacts.elementAt(i)).getName()
                                    .toLowerCase()) > 0)
                        {
                            contacts.insertElementAt(contact, i + 1);
                            break;
                        }
                        else if (i == 0) { // Add as first element
                            contacts.insertElementAt(contact, 0);
                            break;
                        }
                    }
                }
            }
        }
        
        filter();
        saveState();
    }

    public void removeContact(Contact contact)
        throws IOException {
        
        synchronized (contacts) {
            contacts.removeElement(contact);
        }
        
        filter();
        saveState();
    }

    public void addObserver(Observer observer) {
        observers.addElement(observer);
    }

    public void removeObserver(Observer observer) {
        observers.removeElement(observer);
    }

    private void notifyObservers() {
        Enumeration e = observers.elements();
        
        while (e.hasMoreElements()) {
            ((ContactsManager.Observer) e.nextElement())
                .contactsChanged(filteredContacts);
        }
    }

    /**
     * @see com.nokia.example.voip.controller.AbstractRMSManager#store(java.io.DataOutputStream)
     */
    protected synchronized void store(DataOutputStream dos)
        throws IOException {
        
        synchronized (contacts) {
            Enumeration e = contacts.elements();
            
            while (e.hasMoreElements()) {
                Contact c = (Contact) e.nextElement();
                dos.writeUTF(c.getName());
                dos.writeUTF(c.getVoipAddress());
            }
        }
    }

    /**
     * @see com.nokia.example.voip.controller.AbstractRMSManager#restore(java.io.DataInputStream)
     */
    protected synchronized void restore(DataInputStream dis)
        throws IOException {
        
        synchronized (contacts) {
            contacts.removeAllElements();
            
            while (dis.available() > 0) {
                String name = dis.readUTF();
                String voipAddress = dis.readUTF();
                Contact c = new Contact(name, voipAddress);
                contacts.addElement(c);
            }
        }
        
        filter();
    }

    /**
     * A listener interface to be implemented by classes who want to get
     * notified when contacts have changed.
     */
    public interface Observer {
        public void contactsChanged(Vector contacts);
    }
}
