/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip;

import java.io.IOException;
import java.util.Stack;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import com.nokia.mid.ui.CategoryBar;
import com.nokia.mid.ui.ElementListener;
import com.nokia.mid.ui.IconCommand;

import com.nokia.example.voip.controller.ContactsManager;
import com.nokia.example.voip.view.CallLogView;
import com.nokia.example.voip.view.ContactsView;
import com.nokia.example.voip.view.DialerView;
import com.nokia.example.voip.view.SettingsView;
import com.nokia.example.voip.view.TabbedView;

public class ViewManager {

    private VoIPExample voipExample;

    private Stack viewStack;
    private Command backCommand;
    private Command showSettingsViewCommand;
    private CategoryBar categoryBar;
    private CommandListener globalCommandListener;

    private static final int TAB_CONTACTS = 0;
    private static final int TAB_DIALER = 1;
    private static final int TAB_CALL_LOG = 2;

    /**
     * Constructor.
     * @param voipExample
     */
    public ViewManager(VoIPExample voipExample) {
        this.voipExample = voipExample;
        
        viewStack = new Stack();
        
        globalCommandListener = new GlobalCommandListener();
        
        backCommand = new Command("Back", Command.BACK, 0);
        showSettingsViewCommand = new Command("Settings", Command.SCREEN, 2);
        
        try {
            // Create CategoryBar for tab navigation
            IconCommand contactsViewIconCommand = new IconCommand(
                "Contacts",
                Image.createImage("/icon_contacts_unselected.png"),
                Image.createImage("/icon_contacts_selected.png"),
                Command.SCREEN,
                0);
            IconCommand dialerViewIconCommand = new IconCommand(
                "Dialer",
                Image.createImage("/icon_dialer_unselected.png"),
                Image.createImage("/icon_dialer_selected.png"),
                Command.SCREEN,
                0);
            IconCommand callLogViewIconCommand = new IconCommand(
                "Call log",
                Image.createImage("/icon_call_log_unselected.png"),
                Image.createImage("/icon_call_log_selected.png"),
                Command.SCREEN,
                0);
            
            categoryBar = new CategoryBar(new IconCommand[] {
                contactsViewIconCommand, dialerViewIconCommand,
                callLogViewIconCommand }, false);
            categoryBar.setVisibility(true);
            
            categoryBar.setElementListener(new ElementListener() {
                
                public void notifyElementSelected(
                        CategoryBar bar,
                        int selectedIndex) {
                    switch (selectedIndex) {
                        case TAB_CONTACTS:
                            setView(new ContactsView(
                                ViewManager.this,
                                ViewManager.this.voipExample
                                    .getContactsManager()));
                            break;
                        case TAB_DIALER:
                            setView(new DialerView(ViewManager.this));
                            break;
                        case TAB_CALL_LOG:
                            setView(new CallLogView());
                            break;
                    }
                }
            });
        } catch (IOException e) {
            // Shouldn't happen
        }
    }

    private void setCurrentView(Displayable view) {
        categoryBar.setVisibility(view instanceof TabbedView);
        
        if (view instanceof ContactsManager.Observer) {
            voipExample.getContactsManager().addObserver(
                (ContactsManager.Observer) view);
        }
        
        show(view);
    }

    public Display getDisplay() {
        return Display.getDisplay(voipExample);
    }

    public void show(Displayable view) {
        Display.getDisplay(voipExample).setCurrent(view);
    }

    private void setView(Displayable view) {
        viewStack.pop();
        viewStack.push(view);
        view.addCommand(backCommand);
        
        if (view instanceof TabbedView) {
            view.addCommand(showSettingsViewCommand);
        }
        
        setCurrentView(view);
    }

    public void pushView(Displayable view) {
        viewStack.push(view);
        view.addCommand(backCommand);
        
        if (view instanceof TabbedView) {
            view.addCommand(showSettingsViewCommand);
        }
        
        setCurrentView(view);
    }

    public void popView() {
        Object view = viewStack.pop();
        
        if (view instanceof ContactsManager.Observer) {
            voipExample.getContactsManager().removeObserver(
                (ContactsManager.Observer) view);
        }
        
        if (viewStack.empty()) {
            voipExample.notifyDestroyed();
        } else {
            setCurrentView((Displayable) viewStack.peek());
        }
    }

    public CommandListener getGlobalCommandListener() {
        return globalCommandListener;
    }

    private class GlobalCommandListener
        implements CommandListener {
        
        public void commandAction(Command c, Displayable d) {
            if (c.getCommandType() == Command.BACK) {
                popView();
            }
            
            if (c == showSettingsViewCommand) {
                pushView(new SettingsView(
                    voipExample.getSettingsManager(),
                    voipExample.getSettingsHelper(),
                    ViewManager.this));
            }
        }
    }
}
