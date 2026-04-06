# KMS Lock - Device Lock Manager

A powerful Android app that locks your device with Device Admin permissions. Once activated, the device becomes completely unusable except for whitelisted apps until the timer expires. No pause button, no emergency bypass.

## Features

- **Device Admin Integration**: Prevents uninstallation and enables device-level locking
- **Timer-based Locking**: Set duration from 5 minutes to 1 hour
- **App Whitelisting**: Select which apps remain accessible (e.g., Phone, Messages)
- **No Bypass**: Once started, the lock cannot be paused or cancelled
- **Lock Screen**: Full-screen lock display with countdown timer
- **Persistent**: Survives device reboots

## Project Structure

```
kms-lock/
├── app/
│   ├── build.gradle                    # App-level build configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/kmslock/app/
│   │   │   │   ├── MainActivity.kt              # Main UI
│   │   │   │   ├── LockService.kt               # Lock management service
│   │   │   │   ├── LockScreenActivity.kt        # Full-screen lock display
│   │   │   │   ├── TimerService.kt              # Timer countdown service
│   │   │   │   ├── DeviceAdminReceiver.kt       # Device admin handler
│   │   │   │   ├── BootReceiver.kt              # Boot completion handler
│   │   │   │   ├── AppListAdapter.kt            # App list UI adapter
│   │   │   │   └── AppInfo.kt                   # App data model
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml        # Main screen layout
│   │   │   │   │   ├── activity_lock_screen.xml # Lock screen layout
│   │   │   │   │   └── item_app.xml             # App list item layout
│   │   │   │   ├── xml/
│   │   │   │   │   └── device_admin_receiver.xml # Device admin policies
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml               # Color definitions
│   │   │   │   │   ├── strings.xml              # String resources
│   │   │   │   │   └── themes.xml               # Theme definitions
│   │   │   │   └── drawable/
│   │   │   │       └── status_background.xml    # UI drawables
│   │   │   └── AndroidManifest.xml              # App manifest
│   │   └── test/                                # Unit tests
│   └── proguard-rules.pro                       # ProGuard configuration
├── build.gradle                        # Top-level build configuration
├── settings.gradle                     # Gradle settings
└── README.md                           # This file
```

## Prerequisites

- Android Studio (latest version)
- Android SDK 24+ (API level 24+)
- Java 11 or higher
- Gradle 8.0+

## Setup Instructions

### 1. Clone/Download the Project

```bash
# Navigate to the project directory
cd kms-lock
```

### 2. Open in Android Studio

1. Open Android Studio
2. Click "Open an existing Android Studio project"
3. Navigate to the `kms-lock` directory
4. Click "Open"

### 3. Install Dependencies

Android Studio will automatically download required dependencies. If not:
1. Go to `File` → `Sync Now`
2. Wait for Gradle sync to complete

### 4. Configure Signing (Optional but Recommended)

For production builds, create a signing configuration:

1. Go to `Build` → `Generate Signed Bundle/APK`
2. Click "Create new..."
3. Fill in your keystore details
4. Click "Next" and complete the process

### 5. Build the APK

#### Debug Build (for testing):
```bash
# Via Android Studio
Build → Build Bundle(s)/APK(s) → Build APK(s)

# Via Command Line
./gradlew assembleDebug
```

#### Release Build (for distribution):
```bash
# Via Android Studio
Build → Build Bundle(s)/APK(s) → Build Bundle(s)

# Via Command Line
./gradlew assembleRelease
```

### 6. Install on Device

```bash
# Connect your Android device via USB
# Enable USB Debugging on your device

# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or install release APK
adb install app/build/outputs/apk/release/app-release.apk
```

## Usage

### First Time Setup

1. **Launch the app**
   - Open KMS Lock on your Android device

2. **Enable Device Admin**
   - Click "Enable Device Admin" button
   - Review the permissions
   - Click "Activate" to confirm

3. **Select Duration**
   - Choose lock duration: 5m, 10m, 30m, or 1h

4. **Whitelist Apps**
   - Scroll through the app list
   - Check apps you want to keep accessible (e.g., Phone, Messages)
   - At least one app must be selected

5. **Start Lock**
   - Click "START LOCK" button
   - Device will immediately lock
   - Only whitelisted apps will be accessible
   - Full-screen lock display shows countdown timer

### During Lock

- **Cannot exit**: Home button, back button, and power button are blocked
- **Cannot pause**: No pause or cancel button available
- **Cannot uninstall**: Device Admin prevents app removal
- **Cannot access other apps**: Only whitelisted apps work
- **Countdown timer**: Shows remaining time on lock screen

### After Timer Expires

- Lock screen automatically closes
- Device becomes fully usable again
- All apps become accessible

## Technical Details

### Device Admin Permissions

The app requests the following Device Admin permissions:

- `LIMIT_PASSWORD`: Manage device password
- `WATCH_LOGIN`: Monitor login attempts
- `RESET_PASSWORD`: Reset device password
- `FORCE_LOCK`: Lock the device
- `WIPE_DATA`: Wipe device data (for security)

### Services

1. **LockService**: Manages device locking and app blocking
2. **TimerService**: Handles countdown timer with broadcasts
3. **DeviceAdminReceiver**: Handles Device Admin callbacks
4. **BootReceiver**: Restarts lock on device boot

### Key Classes

- **MainActivity**: Main UI for setup and configuration
- **LockScreenActivity**: Full-screen lock display
- **AppListAdapter**: Manages app list UI
- **LockService**: Core locking functionality
- **TimerService**: Timer management

## Troubleshooting

### "Device Admin is not active"
- Solution: Click "Enable Device Admin" and confirm the prompt

### "Lock doesn't work"
- Ensure Device Admin is enabled
- Restart the app
- Try with a shorter duration first

### "Cannot uninstall the app"
- This is by design (Device Admin protection)
- Disable Device Admin first: Settings → Apps → KMS Lock → Uninstall
- Or: Settings → Security → Device Admin → Disable KMS Lock

### "Timer not counting down"
- Ensure TimerService is running
- Check device battery saver settings
- Restart the app

## Security Notes

⚠️ **Warning**: This app provides strict device locking. Use responsibly:

- Only whitelist essential apps
- Set reasonable lock durations
- Do not use on shared devices without consent
- Remember: There is NO emergency bypass once activated

## Building for Release

### 1. Create Release Keystore

```bash
keytool -genkey -v -keystore kms-lock-release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias kms-lock
```

### 2. Configure Signing in build.gradle

```gradle
signingConfigs {
    release {
        storeFile file("kms-lock-release.keystore")
        storePassword "your_keystore_password"
        keyAlias "kms-lock"
        keyPassword "your_key_password"
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

### 3. Build Release APK

```bash
./gradlew assembleRelease
```

The release APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Development

### Adding New Features

1. Create new Activity/Service in `java/com/kmslock/app/`
2. Add layout XML in `res/layout/`
3. Update `AndroidManifest.xml` if needed
4. Build and test

### Debugging

Enable logging:
```kotlin
Log.d(TAG, "Debug message")
Log.e(TAG, "Error message")
```

View logs:
```bash
adb logcat | grep KMS
```

## API Reference

### LockService

```kotlin
// Start lock
val intent = Intent(context, LockService::class.java)
intent.action = LockService.ACTION_LOCK
intent.putExtra(LockService.EXTRA_DURATION, 5 * 60 * 1000) // 5 minutes
startService(intent)

// Stop lock
val intent = Intent(context, LockService::class.java)
intent.action = LockService.ACTION_UNLOCK
startService(intent)
```

### TimerService

```kotlin
// Listen to timer updates
val filter = IntentFilter(TimerService.ACTION_TIMER_TICK)
registerReceiver(receiver, filter)

// Handle broadcasts
override fun onReceive(context: Context, intent: Intent) {
    val timeLeft = intent.getLongExtra(TimerService.EXTRA_TIME_LEFT, 0)
}
```

## License

This project is provided as-is for educational and personal use.

## Support

For issues or questions, review the code comments or consult Android documentation:
- [Android Device Admin API](https://developer.android.com/guide/topics/admin/device-admin)
- [Android Services](https://developer.android.com/guide/components/services)
- [Android Broadcasts](https://developer.android.com/guide/components/broadcasts)

## Disclaimer

Use this app responsibly. The developer is not responsible for misuse or unintended consequences. This app provides strict device locking with no bypass mechanism - use with caution.
