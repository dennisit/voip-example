VoipExample
=============

This example shows how to create a simple VoIP client MIDlet for making 
internet calls over SIP protocol. It demonstrates the usage the VoIP API 
on Nokia Asha software platform 1.1 and how to handle VoIP calls. 

This example is hosted in GitHub:
https://github.com/nokia-developer/voip-example

For more information on implementation, see the wiki:
https://github.com/nokia-developer/voip-example/wiki


1. Usage
-------------------------------------------------------------------------------

You can choose to register the application with your preferred SIP provider 
as follows:

* From the Dialer view, swipe the option's menu from the bottom of the screen
  and select `Settings`.
* Type the user name and password of your VoIP account and then add the 
  registrar address of your preferred VoIP service. If uncertain, please contact
  your VoIP service provider.

You can use the dialer to directly make a call. Your VoIP provider might charge you
for this depending on your subscription plan. You can also save your contacts as follows:

* From the Contacts view, swipe the option's menu from the bottom of the screen
  and select `Add new contact` 
* Type a friendly name with which you wish to save the contact and your contact's 
  VoIP address in the format sip:user@provider.com
* Select Save.

To call on of your contacts, tap it and then select the Call button at the bottom of the
screen.

2. Important classes
-------------------------------------------------------------------------------

| Class | Description |
| ----- | ----------- |
| Call | The instance representing a call. Depending on the status of the call, the application views are updated |
| SettingsHelper | It is used to load a number of codec and network related settings from a resource xml file|
| ViewManager| Handles the transition from one view to another when the call status is changed, or the user interacts with the app|


3. Compatibility
-------------------------------------------------------------------------------

Nokia Asha software platform 1.1.


4. Known issues and limitations
-------------------------------------------------------------------------------

* Only one SIP account can be registered at a time
* The VoIP API requires that the device is connected to a Wi-Fi access point.
* Making VoIP calls, while connected to 3G/4G HotSpots that act as a Wi-Fi access point, fails. 
* The VoIP API cannot connect to remote VoIP servers behind a firewall


5. Building, installing, and running the application
-------------------------------------------------------------------------------

5.1 Packaging the application using Nokia Asha SDK 1.1
------------------------------------------------------

You cannot install the application on the device with the IDE, but you can 
package the application: After you have imported the project, locate the
Application Descriptor in the Package Explorer window and open it. Open the 
Overview tab (by default it is the first tab on the left) and click Create
package. Select the destination directory and click Finish.


5.2 Installing application binary to phone
------------------------------------------

Connect the phone to the computer with USB cable or Bluetooth. Locate the
application binary (.jar file). Copy the file to your phone, locate it and tap
to install. After the application is installed, locate the application icon from
the application menu and launch the application by selecting the icon.


6. License
-------------------------------------------------------------------------------

See the license text file delivered with this project. The license file is also
available online at
https://github.com/nokia-developer/voip-example/blob/master/LICENSE.TXT


7. Version history
-------------------------------------------------------------------------------

* 1.0.1 The timer for displaying call duration is canceled upon call termination.
* 1.0   The initial release.
