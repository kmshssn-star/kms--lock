package com.kmslock.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kmslock.app.databinding.ActivityLockScreenBinding

class LockScreenActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LockScreenActivity"
    }

    private lateinit var binding: ActivityLockScreenBinding
    private var timeLeft: Long = 0
    private val timerReceiver = TimerReceiver()

    inner class TimerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                TimerService.ACTION_TIMER_TICK -> {
                    timeLeft = intent.getLongExtra(TimerService.EXTRA_TIME_LEFT, 0)
                    updateTimerDisplay()
                }
                TimerService.ACTION_TIMER_FINISHED -> {
                    Log.d(TAG, "Timer finished, unlocking device")
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure window to show on lock screen
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Register timer receiver
        val filter = IntentFilter().apply {
            addAction(TimerService.ACTION_TIMER_TICK)
            addAction(TimerService.ACTION_TIMER_FINISHED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timerReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(timerReceiver, filter)
        }

        Log.d(TAG, "Lock screen activity created")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timerReceiver)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Block all key events (Home, Back, Volume, Power)
        return when (keyCode) {
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_POWER -> true
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        // Prevent back button
    }

    override fun onPause() {
        super.onPause()
        // Bring activity back to foreground
        val intent = Intent(this, LockScreenActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun updateTimerDisplay() {
        val seconds = timeLeft / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        val timeString = when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
            minutes > 0 -> String.format("%02d:%02d", minutes, seconds % 60)
            else -> String.format("%02d", seconds % 60)
        }

        binding.timerText.text = timeString
        Log.d(TAG, "Timer display updated: $timeString")
    }
}
