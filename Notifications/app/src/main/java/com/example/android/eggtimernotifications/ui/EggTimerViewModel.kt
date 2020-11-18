/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.example.android.eggtimernotifications.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.*
import com.example.android.eggtimernotifications.receiver.AlarmReceiver
import com.example.android.eggtimernotifications.R
import kotlinx.coroutines.*

class EggTimerViewModel(private val app: Application) : AndroidViewModel(app) {

    private val REQUEST_CODE = 0
    private val TRIGGER_TIME = "TRIGGER_AT"

    private val minute: Long = 60_000L
    private val second: Long = 1_000L

    private val timerLengthOptions: IntArray
    private val notifyPendingIntent: PendingIntent

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var prefs =
        app.getSharedPreferences("com.example.android.eggtimernotifications", Context.MODE_PRIVATE)
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    val timeSelection = MutableLiveData<Int>()

    val elapsedTime = MutableLiveData<Long>()

    var alarmOn = MutableLiveData<Boolean>()

    private var timer: CountDownTimer? = null

    init {
        alarmOn.value = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_NO_CREATE
        ) != null

        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        timerLengthOptions = app.resources.getIntArray(R.array.minutes_array)

        //If alarm is not null, resume the timer back for this alarm
        alarmOn.value?.run {
            createTimer()
        }
    }

    /**
     * Turns on or off the alarm
     *
     * @param isChecked, alarm status to be set.
     */
    fun setAlarm(isChecked: Boolean) {
        when (isChecked) {
            true -> timeSelection.value?.let { startTimer(it) }
            false -> cancelNotification()
        }
    }

    /**
     * Sets the desired interval for the alarm
     *
     * @param timerLengthSelection, interval timerLengthSelection value.
     */
    fun setTimeSelected(timerLengthSelection: Int) {
        timeSelection.value = timerLengthSelection
    }

    /**
     * Creates a new alarm, notification and timer
     */
    private fun startTimer(timerLengthSelection: Int) {
        alarmOn.value?.let {
            if (it) {
                alarmOn.value = true
                val selectedInterval = when (timerLengthSelection) {
                    0 -> second * 10 //For testing only
                    else ->timerLengthOptions[timerLengthSelection] * minute
                }
                val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

                // TODO: Step 1.5 get an instance of NotificationManager and call sendNotification

                // TODO: Step 1.15 call cancel notification

                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notifyPendingIntent
                )

                viewModelScope.launch {
                    saveTime(triggerTime)
                }
            }
        }
        createTimer()
    }

    /**
     * Creates a new timer
     */
    private fun createTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, second) {

                override fun onTick(millisUntilFinished: Long) {
                    elapsedTime.value = triggerTime - SystemClock.elapsedRealtime()
                    elapsedTime.value?.let {
                        if (it <= 0) {
                            resetTimer()
                        }
                    }
                }

                override fun onFinish() {
                    resetTimer()
                }
            }
            timer?.start()
        }
    }

    /**
     * Cancels the alarm, notification and resets the timer
     */
    private fun cancelNotification() {
        resetTimer()
        alarmManager.cancel(notifyPendingIntent)
    }

    /**
     * Resets the timer on screen and sets alarm value false
     */
    private fun resetTimer() {
        timer?.cancel()
        elapsedTime.value = 0
        alarmOn.value = false
    }

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            prefs.getLong(TRIGGER_TIME, 0)
        }
}