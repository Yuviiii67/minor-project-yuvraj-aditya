package com.example.mealscale.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.mealscale.R

class TimerActivity : AppCompatActivity() {
    
    private lateinit var timerTextView: TextView
    private lateinit var stepDescriptionTextView: TextView
    private lateinit var startTimerButton: Button
    private lateinit var pauseTimerButton: Button
    private lateinit var resetTimerButton: Button
    
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var timerRunning = false
    private var totalTime: Long = 0
    
    private var stepDescription: String = ""
    private var notificationId = 1
    
    companion object {
        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_STEP_DESCRIPTION = "extra_step_description"
        const val CHANNEL_ID = "mealscale_timer_channel"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        
        initializeViews()
        getIntentData()
        setupButtons()
        createNotificationChannel()
    }
    
    private fun initializeViews() {
        timerTextView = findViewById(R.id.timerTextView)
        stepDescriptionTextView = findViewById(R.id.stepDescriptionTextView)
        startTimerButton = findViewById(R.id.startTimerButton)
        pauseTimerButton = findViewById(R.id.pauseTimerButton)
        resetTimerButton = findViewById(R.id.resetTimerButton)
    }
    
    private fun getIntentData() {
        val duration = intent.getIntExtra(EXTRA_DURATION, 0)
        stepDescription = intent.getStringExtra(EXTRA_STEP_DESCRIPTION) ?: "Cooking Step"
        
        timeLeftInMillis = (duration * 1000).toLong()
        totalTime = timeLeftInMillis
        
        stepDescriptionTextView.text = stepDescription
        updateTimerText()
    }
    
    private fun setupButtons() {
        startTimerButton.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }
        
        pauseTimerButton.setOnClickListener {
            pauseTimer()
        }
        
        resetTimerButton.setOnClickListener {
            resetTimer()
        }
        
        updateButtonStates()
    }
    
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }
            
            override fun onFinish() {
                timerRunning = false
                timeLeftInMillis = 0
                updateTimerText()
                showTimerFinishedNotification()
                updateButtonStates()
            }
        }.start()
        
        timerRunning = true
        updateButtonStates()
    }
    
    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        updateButtonStates()
    }
    
    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = totalTime
        timerRunning = false
        updateTimerText()
        updateButtonStates()
    }
    
    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }
    
    private fun updateButtonStates() {
        startTimerButton.text = if (timerRunning) "Pause" else "Start"
        pauseTimerButton.isEnabled = timerRunning
        resetTimerButton.isEnabled = !timerRunning && timeLeftInMillis < totalTime
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MealScale Timer"
            val descriptionText = "Notifications for cooking timers"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showTimerFinishedNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("MealScale Timer")
            .setContentText("Timer finished: $stepDescription")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(notificationSound)
            .setAutoCancel(true)
        
        notificationManager.notify(notificationId, notificationBuilder.build())
        notificationId++
    }
    
    override fun onPause() {
        super.onPause()
        // Optional: Keep timer running in background
        // Or pause it based on your requirements
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
