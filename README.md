# Android TV Web Screensaver

A simple Android TV screensaver that displays any website. Configure the URL, enable JavaScript, and optionally interact with the page using your remote.

## Features

- Display any website as your TV screensaver
- Configurable URL via a TV-friendly settings screen
- JavaScript toggle for compatibility with modern sites
- Optional interactive mode to navigate with your remote
- Hardware-accelerated WebView rendering

## Setup

1. Install the app on your Android TV
2. Go to **Settings > Device Preferences > Screen saver**
3. Select **Web Screensaver**
4. Tap the gear icon to configure the URL

## Building

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

Install via adb:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Testing on emulator

Create an **Android TV (1080p)** virtual device in Android Studio, then:

```bash
# Install the app
./gradlew installDebug

# Trigger the screensaver
adb shell am start-dream

# Open settings directly
adb shell am start -n com.nandeesh.screensaver/.SettingsActivity
```

## License

[MIT](LICENSE)
