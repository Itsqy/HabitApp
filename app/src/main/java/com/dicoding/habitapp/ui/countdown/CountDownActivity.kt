package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.databinding.ActivityAddHabitBinding
import com.dicoding.habitapp.databinding.ActivityCountDownBinding
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {
    lateinit var binding: ActivityCountDownBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCountDownBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Count Down"

        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

        //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.setInitialTime(habit.minutesFocus)
        viewModel.currentTimeString.observe(this@CountDownActivity) { counting ->

            binding.apply {
                tvCountDown.text = counting
            }

        }
        //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
        viewModel.eventCountDownFinish.observe(this@CountDownActivity) { state ->
            updateButtonState(state)
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            val timeCountDown = viewModel.getInitTime()
            val dataCountDown = workDataOf(HABIT_ID to habit.id, HABIT_TITLE to habit.title)
            viewModel.startTimer()
            updateButtonState(false)


            val notification: WorkRequest =
                OneTimeWorkRequestBuilder<NotificationWorker>().setInputData(dataCountDown)
                    .setInitialDelay(timeCountDown!!, TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(this).enqueue(notification)

        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            viewModel.resetTimer()
            updateButtonState(true)
            WorkManager.getInstance(this).cancelAllWork()

        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = !isRunning
    }
}