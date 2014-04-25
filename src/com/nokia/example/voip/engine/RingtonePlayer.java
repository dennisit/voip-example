/**
 * Copyright (c) 2013-2014 Microsoft Mobile. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */

package com.nokia.example.voip.engine;

import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

import com.nokia.example.voip.VoIPExample;

public class RingtonePlayer {
    Player player;

    public void startRinging() {
        try {
            // Try to play the set custom ringtone file.
            String url = VoIPExample.getInstance().getSettingsManager()
                .getSettings().getRingtoneUrl();
            FileConnection connection =
                    (FileConnection) Connector.open(url, Connector.READ);
            InputStream stream = connection.openInputStream();
            player = Manager.createPlayer(stream, "audio/mp3");
            player.setLoopCount(-1);
            player.start();
            return;
        }
        catch (Exception e) {
            System.out.println("Playing custom ringtone failed, "
                + "using default ringtone instead");
            player = null;
        }
        
        try {
            // If custom ringtone playing fails, try to play the default one.
            InputStream stream = getClass()
                .getResourceAsStream("/ringtone.mp3");
            player = Manager.createPlayer(stream, "audio/mp3");
            player.setLoopCount(-1);
            player.start();
        }
        catch (Exception e) {
            System.out.println("Playing ringtone failed!");
            player = null;
        }
    }

    public void stopRinging() {
        if (player != null) {
            try {
                player.stop();
            }
            catch (MediaException e) {
                System.out.println("Stopping ringtone failed!");
            }
            
            player = null;
        }
    }
}
