package com.kmslock.app

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class TimerService : Service() {

    companion object {
        const val TAG = "TimerService"
        const val EXTRA_DURATION = "duration"
        const val ACTION_TIMER_TICK = "com.kmslock.app.ACTION_TIMER_TICK"
        const val ACTION_TIMER_FINISHED = "com.kmslock.app.ACTION_TIMER_FINISHED"
        const val EXTRA_TIME_LEFT = "time_left"
    }

    private var countDownTimer: CountDownTimer? = null
    private var timeLeft: Long = 0
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val duration = intent?.getLongExtra(EXTRA_DURATION, 0) ?: 0

        if (duration > 0) {
            startTimer(duration)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startTimer(durationMillis: Long) {
        stopTimer()

        timeLeft = durationMillis

        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                broadcastTimerTick(millisUntilFinished)
                Log.d(TAG, "Timer tick: ${millisUntilFinished / 1000}s remaining")
            }

            override fun onFinish() {
                timeLeft = 0
                broadcastTimerFinished()
                Log.d(TAG, "Timer finished")
                stopSelf()
            }
        }

        countDownTimer?.start()
        Log.d(TAG, "Timer started for ${durationMillis / 1000}s")
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    fun getTimeLeft(): Long = timeLeft

    private fun broadcastTimerTick(timeLeft: Long) {
        val intent = Intent(ACTION_TIMER_TICK)
        intent.putExtra(EXTRA_TIME_LEFT, timeLeft)
        sendBroadcast(intent)
    }

    private fun broadcastTimerFinished() {
        val intent = Intent(ACTION_TIMER_FINISHED)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        Log.d(TAG, "TimerService destroyed")
    }
}
