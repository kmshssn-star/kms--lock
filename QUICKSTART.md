# KMS Lock - Quick Start Guide

## 5-Minute Setup

### Step 1: Download Android Studio
- Go to https://developer.android.com/studio
- Download and install Android Studio
- Launch Android Studio

### Step 2: Open the Project
1. Click "Open an existing Android Studio project"
2. Select the `kms-lock` folder
3. Click "Open"
4. Wait for Gradle sync to complete (2-3 minutes)

### Step 3: Connect Your Android Device
1. Enable USB Debugging on your Android phone:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → Enable "USB Debugging"
2. Connect phone to computer via USB cable
3. Android Studio should detect your device

### Step 4: Build and Run
1. Click the green "Run" button (▶) in Android Studio
2. Select your device
3. Click "OK"
4. App will install and launch on your device

### Step 5: First Use
1. Click "Enable Device Admin"
2. Review permissions and click "Activate"
3. Select lock duration (5m, 10m, 30m, 1h)
4. Check apps you want to keep accessible
5. Click "START LOCK"

## Building APK for Distribution

### Debug APK (for testing):
```
Build → Build Bundle(s)/APK(s) → Build APK(s)
```
APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (for distribution):
```
Build → Build Bundle(s)/APK(s) → Build Bundle(s)
```
APK location: `app/build/outputs/bundle/release/app-release.aab`

## Troubleshooting

### "Device not detected"
- Enable USB Debugging on your phone
- Install USB drivers for your phone model
- Try a different USB cable

### "Gradle sync failed"
- File → Sync Now
- File → Invalidate Caches → Invalidate and Restart

### "Build failed"
- Check Java version: `java -version` (should be 11+)
- Update Android Studio: Help → Check for Updates

## Key Files to Understand

| File | Purpose |
|------|---------|
| `MainActivity.kt` | Main UI and app selection |
| `LockService.kt` | Device locking logic |
| `LockScreenActivity.kt` | Full-screen lock display |
| `TimerService.kt` | Countdown timer |
| `DeviceAdminReceiver.kt` | Device admin integration |
| `AndroidManifest.xml` | App permissions and components |

## Important Notes

⚠️ **Before Using:**
- This app locks your device with NO bypass
- Only whitelist essential apps
- Set reasonable durations for testing
- Do not use on shared devices

## Next Steps

1. **Test on your device**: Set a short 5-minute lock to test
2. **Read the full README.md**: For detailed documentation
3. **Customize**: Modify colors, strings, or features as needed
4. **Build Release**: Create a signed APK for distribution

## Need Help?

- Check `README.md` for detailed documentation
- Review code comments in Java files
- Check Android logcat: `adb logcat | grep KMS`
- Visit Android Developer docs: https://developer.android.com

Enjoy using KMS Lock! 🔒
