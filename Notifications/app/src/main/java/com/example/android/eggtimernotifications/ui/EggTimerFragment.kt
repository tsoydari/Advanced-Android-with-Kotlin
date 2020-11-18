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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.eggtimernotifications.R
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_egg_timer.*

class EggTimerFragment : Fragment(R.layout.fragment_egg_timer) {

    private val TOPIC = "breakfast"

    private val timerViewModel: EggTimerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()

        // TODO: Step 1.7 call create channel
    }

    private fun initObservers() {
        timerViewModel.elapsedTime.observe(viewLifecycleOwner, Observer {time ->
            val seconds = time / 1000
            tvTime.text = if (seconds < 60) seconds.toString() else DateUtils.formatElapsedTime(seconds)
        })

        timerViewModel.timeSelection.observe(viewLifecycleOwner, Observer {pos ->
            spinnerMinutes.setSelection(pos)
        })

        timerViewModel.alarmOn.observe(viewLifecycleOwner, Observer { on ->
            switchOnOff.isChecked = on
        })
    }

    private fun initListeners() {

        spinnerMinutes.onItemSelectedListener = SpinnerListener(timerViewModel)
        switchOnOff.setOnCheckedChangeListener { _, isChecked ->
            timerViewModel.setAlarm(isChecked)
        }
    }


    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel

        // TODO: Step 1.6 END create a channel

    }

    companion object {
        fun newInstance() = EggTimerFragment()
    }

    class SpinnerListener(private val viewModel: EggTimerViewModel): AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            viewModel.setTimeSelected(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }
}

