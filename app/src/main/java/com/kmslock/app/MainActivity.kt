package com.kmslock.app

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kmslock.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val PREFS_NAME = "KmsLockPrefs"
        const val KEY_WHITELISTED_APPS = "whitelisted_apps"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName
    private lateinit var appAdapter: AppListAdapter
    private var selectedDuration: Long = 5 * 60 * 1000 // Default 5 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, DeviceAdminReceiver::class.java)

        setupUI()
        loadInstalledApps()
        checkDeviceAdminStatus()

        Log.d(TAG, "MainActivity created")
    }

    private fun setupUI() {
        // Duration selection
        binding.duration5min.setOnClickListener {
            selectedDuration = 5 * 60 * 1000
            updateDurationButtons()
            Toast.makeText(this, "Duration set to 5 minutes", Toast.LENGTH_SHORT).show()
        }

        binding.duration10min.setOnClickListener {
            selectedDuration = 10 * 60 * 1000
            updateDurationButtons()
            Toast.makeText(this, "Duration set to 10 minutes", Toast.LENGTH_SHORT).show()
        }

        binding.duration30min.setOnClickListener {
            selectedDuration = 30 * 60 * 1000
            updateDurationButtons()
            Toast.makeText(this, "Duration set to 30 minutes", Toast.LENGTH_SHORT).show()
        }

        binding.duration1hour.setOnClickListener {
            selectedDuration = 60 * 60 * 1000
            updateDurationButtons()
            Toast.makeText(this, "Duration set to 1 hour", Toast.LENGTH_SHORT).show()
        }

        // Enable Device Admin
        binding.enableAdminBtn.setOnClickListener {
            enableDeviceAdmin()
        }

        // Start Lock
        binding.startLockBtn.setOnClickListener {
            if (isDeviceAdminActive()) {
                startLock()
            } else {
                Toast.makeText(this, "Please enable Device Admin first", Toast.LENGTH_SHORT).show()
            }
        }

        // App list
        appAdapter = AppListAdapter(emptyList())
        binding.appList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = appAdapter
        }

        updateDurationButtons()
    }

    private fun loadInstalledApps() {
        val apps = mutableListOf<AppInfo>()
        val packageManager = packageManager

        try {
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (app in installedApps) {
                // Skip system apps and KMS Lock itself
                if (app.packageName == packageName) continue
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue

                val appName = packageManager.getApplicationLabel(app).toString()
                val appIcon = packageManager.getApplicationIcon(app)

                apps.add(AppInfo(app.packageName, appName, appIcon, false))
            }

            apps.sortBy { it.name }
            appAdapter.updateApps(apps)
            Log.d(TAG, "Loaded ${apps.size} apps")

        } catch (e: Exception) {
            Log.e(TAG, "Error loading apps: ${e.message}")
        }
    }

    private fun updateDurationButtons() {
        binding.duration5min.isSelected = selectedDuration == 5 * 60 * 1000L
        binding.duration10min.isSelected = selectedDuration == 10 * 60 * 1000L
        binding.duration30min.isSelected = selectedDuration == 30 * 60 * 1000L
        binding.duration1hour.isSelected = selectedDuration == 60 * 60 * 1000L
    }

    private fun enableDeviceAdmin() {
        if (isDeviceAdminActive()) {
            Toast.makeText(this, "Device Admin already enabled", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable Device Admin for KMS Lock")
        startActivity(intent)
    }

    private fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }

    private fun startLock() {
        val selectedApps = appAdapter.getSelectedApps()

        if (selectedApps.isEmpty()) {
            Toast.makeText(this, "Please select at least one app to whitelist", Toast.LENGTH_SHORT).show()
            return
        }

        // Save whitelisted apps
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_WHITELISTED_APPS, selectedApps.toSet()).apply()

        // Start lock service
        val intent = Intent(this, LockService::class.java)
        intent.action = LockService.ACTION_LOCK
        intent.putExtra(LockService.EXTRA_DURATION, selectedDuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        Toast.makeText(this, "Device locked for ${selectedDuration / 1000 / 60} minutes", Toast.LENGTH_LONG).show()
        Log.d(TAG, "Lock started with duration: $selectedDuration ms")
    }

    private fun checkDeviceAdminStatus() {
        if (isDeviceAdminActive()) {
            binding.adminStatus.text = "✓ Device Admin Enabled"
            binding.enableAdminBtn.isEnabled = false
        } else {
            binding.adminStatus.text = "✗ Device Admin Disabled"
            binding.enableAdminBtn.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        checkDeviceAdminStatus()
    }
}
