package com.kmslock.app

import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log

class LockService : Service() {

    companion object {
        const val TAG = "LockService"
        const val ACTION_LOCK = "com.kmslock.app.ACTION_LOCK"
        const val ACTION_UNLOCK = "com.kmslock.app.ACTION_UNLOCK"
        const val EXTRA_DURATION = "duration"
    }

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): LockService = this@LockService
    }

    override fun onCreate() {
        super.onCreate()
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, DeviceAdminReceiver::class.java)
        Log.d(TAG, "LockService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_LOCK -> {
                val duration = intent.getLongExtra(EXTRA_DURATION, 0)
                lockDevice(duration)
            }
            ACTION_UNLOCK -> {
                unlockDevice()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun lockDevice(durationMillis: Long) {
        if (!isDeviceAdminActive()) {
            Log.e(TAG, "Device admin is not active")
            return
        }

        try {
            // Lock the device
            devicePolicyManager.lockNow()
            Log.d(TAG, "Device locked for $durationMillis ms")

            // Start the lock screen activity
            val lockIntent = Intent(this, LockScreenActivity::class.java)
            lockIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(lockIntent)

            // Start timer service
            val timerIntent = Intent(this, TimerService::class.java)
            timerIntent.putExtra(TimerService.EXTRA_DURATION, durationMillis)
            startService(timerIntent)

        } catch (e: Exception) {
            Log.e(TAG, "Error locking device: ${e.message}")
        }
    }

    fun unlockDevice() {
        try {
            // Stop the lock screen
            val intent = Intent(this, LockScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            Log.d(TAG, "Device unlocked")
        } catch (e: Exception) {
            Log.e(TAG, "Error unlocking device: ${e.message}")
        }
    }

    private fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }

    fun isAdminActive(): Boolean = isDeviceAdminActive()

    fun enableDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin for KMS Lock")
        startActivity(intent)
    }
}
