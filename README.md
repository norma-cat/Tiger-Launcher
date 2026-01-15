<div style="text-align:center">

# Tiger Launcher

<img src="app/src/main/res/drawable/tiger_launcher_foreground.png" width="778" alt="App Icon"/>

A personalized fork of Dragon Launcher (see Credits)

## Features

### Lightweight, Intuitive, Fast gesture based launcher

* You can customize your list of fast access apps easily in the settings
* Use the app drawer to pick an app and browse apps

### Gestures & Actions

* Configurable swipe actions
* Drawer enter key actions
* Fast access app launching
* Gesture-first navigation with minimal UI clutter

### App Drawer

* Swipe-down to close drawer
* Configurable enter key behavior
* App shortcuts support (when provided by apps)
* Tap or long-press for quick actions
* Search bar no longer auto-focuses

### Status Bar

* **Fully customizable**
* Optional visibility per user preference
* Custom time and date formatting
* Next alarm display
* Visual integration with launcher theme

### Appearance

* Deep customization features
* Customize each colors separately for every little action / surface
* Change wallpaper, apply blur, separate wallpaper on drawer / main screen
* Icon packs support
* Widgets support, arrange freely your widgets as well as floating apps / actions from Tiger

### Backup
* Manual backups to phone's storage
* Auto backup feature : auto backups on every app focus change


## Privacy & security

* **No** data collection
* Tiger Launcher doesn't have access to the internet -> it cannot steal your data
* No intrusive permissions requested for the app to work
* Uses Accessibility permissions (optionally) to:
  1. Expand notifications panel (needed by android)
     - Uses accessibility to expand the notifications panel
     - Expanding the quick actions / notifications isn't required on some android version, there is a hidden settings in the debug tab, to change the way it opens the quick actions, cause on my phone, the notifications action opens que quick actions
  2. Lock screen (needed by android)
     - It uses accessibility to lock the screen, as you pressed the lock button
  3. Open recent apps (needed by android)
     - You can choose to open the recent apps, just like when you click on the Square button, or do the recent apps gesture
  4. Auto launch Tiger on system launcher detected (used by some Xiaomi users that cannot set another default launcher without loosing the gesture navigation)
     - It will launch Tiger Launcher when the accessibility detects that you entered your system launcher, for a default launcher-like experience when it cannot be set as android default (configurable in debug settings)
* Can set the Launcher as device admin
  * used for some OEMs o prevent it from killing the app, especially on Xiaomi/ HyperOS devices that have really strong battery optimisation features
  * used also for users that need the previous auto launch on system launcher, to prevent the accessibility services to be killed
* All data stored locally (you can backup manually or use the auto backup feature)

---
## Screenshots
<p style="text-align: center;">
    <img src="metadata/images/demo.gif" alt="App demo animation"/>
</p>

<p style="text-align: center;">
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg" width="22%" alt="App ScreenShot 1"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/16.jpg" width="22%" alt="App ScreenShot 16"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg" width="22%" alt="App ScreenShot 2"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/3.jpg" width="22%" alt="App ScreenShot 3"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/16.jpg" width="22%" alt="App ScreenShot 16"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/4.jpg" width="22%" alt="App ScreenShot 4"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/5.jpg" width="22%" alt="App ScreenShot 5"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/6.jpg" width="22%" alt="App ScreenShot 6"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/7.jpg" width="22%" alt="App ScreenShot 7"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/8.jpg" width="22%" alt="App ScreenShot 8"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/9.jpg" width="22%" alt="App ScreenShot 9"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/10.jpg" width="22%" alt="App ScreenShot 10"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/11.jpg" width="22%" alt="App ScreenShot 11"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/13.jpg" width="22%" alt="App ScreenShot 13"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/14.jpg" width="22%" alt="App ScreenShot 14"/>
  <img src="https://github.com/Elnix90/Tiger-Launcher/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/15.jpg" width="22%" alt="App ScreenShot 15"/>
</p></div>

## Usage

* **Long click 3 seconds to access settings**
* Tap or long-press apps to quickly launch, view options, or uninstall on drawer
* Customize gestures and visual settings via the Settings menu.
* Change background for main screen / drawer, add blur to it

---

### What's this icon ?

| Icons                                | Meaning                                                                                                           |
|--------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| ![image](assets/documentation/1.png) | Enter / Exit the nest (click before on the nest you created on one circle)                                        |
| ![image](assets/documentation/2.png) | Edit toggle between points and swipe distance.                                                                    |
| ![image](assets/documentation/3.png) | Toggle points snapping (if not enabled, you can move freely the points, else they snap in rounded position - 15°) |
| ![image](assets/documentation/4.png) | Toggle auto-separate points when you drag them                                                                    |



## Credits

* Dragon Launcher was inspired by [CCLauncher](https://github.com/mlm-games/CCLauncher). The Dragon Launcher team adapted some concepts, features, and code structures from this project
* Dragon Launcher was created by [Elnix](https://github.com/Elnix90), and actively maintained
* Tiger Launcher is just a few small tweaks to adapt it for my own usage/experience

## Contributing

* Support the actual/main project: [Dragon Launcher (github)](https://github.com/Elnix90/Dragon-Launcher)
* Join Dragon-Launcher's [discord server](https://discord.gg/XXKXQeXpvF) to discuss changes and suggestions

## Note
* This is just a personalized fork for myself. I'm partially using it to learn and partially just customizing it to my personal tastes. I'm not expecting anyone else to really care about it or take an interest. 

---
## License

* This project is open-source under the **GPL 3 Licence**.
