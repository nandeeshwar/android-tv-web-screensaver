# Android TV Web Screensaver

Display any website as your Android TV screensaver. Add URLs manually or scan a QR code with your phone.

## Features

- Display any website as your TV screensaver
- Add URLs manually or scan a QR code from your phone
- Start the screensaver directly from the app (no system settings required)
- JavaScript toggle for compatibility with modern sites
- Interactive mode to navigate the website with your remote
- Hardware-accelerated WebView rendering
- Responsive settings UI for both TV and phone

## Setup

1. Install the app on your Android TV
2. Open **Web Screensaver** from the launcher
3. Add a URL (manually or via QR code)
4. Tap **Start Screensaver** to preview

To use as the system screensaver:
1. Go to **Settings > System > Screen saver** (or **Settings > Device Preferences > Screen saver**)
2. Select **Web Screensaver**

## Building

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

Install via adb:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Deploying to Play Store

```bash
bundle install
bundle exec fastlane android internal      # Upload to internal testing
bundle exec fastlane android production     # Promote to production
```

## Testing on emulator

Create an **Android TV (1080p)** virtual device in Android Studio, then:

```bash
# Install the app
./gradlew installDebug

# Trigger the screensaver
adb shell am start-dream

# Open settings directly
adb shell am start -n app.digiplex.screensaver/.SettingsActivity
```

## License

[MIT](LICENSE)
