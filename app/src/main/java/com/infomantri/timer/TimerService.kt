package com.infomantri.timer

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData

class TimerService: Service(){

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private var mTimer = 0
    private  var mSec = 0L
    private var mCounter = 3600000L
    private var mCountDownTimer: CountDownTimer? = null

    companion object {

        val mutableTime = MutableLiveData<Long>()
        val mutableStatus = MutableLiveData<Timer>()
        val mutableDoServiceStopped = MutableLiveData<Boolean>()

        fun startService(context: Context){
            val timerIntent = Intent(context, TimerService::class.java)
            ContextCompat.startForegroundService(context, timerIntent)
        }

        fun  pauseService(context: Context) {
            val timerIntent = Intent(context, TimerService::class.java)
            timerIntent.action = AppConstant.PAUSE_TIMER_ACTION

            ContextCompat.startForegroundService(context, timerIntent)
        }

        fun stopService(context: Context) {
            val timerIntent = Intent(context, TimerService::class.java)
            timerIntent.action = AppConstant.STOPFOREGROUND_ACTION

            ContextCompat.startForegroundService(context, timerIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                AppConstant.STOPFOREGROUND_ACTION -> {
                    Log.e("Match", "STOP")

                    mCountDownTimer?.cancel()
                    mCounter = 3600000L
                    mTimer = 0
                    mSec = 0
                    mutableTime.value = 0L
                    mutableStatus.value = Stop
                    mutableDoServiceStopped.value = true
                }

                AppConstant.PAUSE_TIMER_ACTION -> {
                    Log.e("Match", "PAUSE")

                    mutableStatus.value = Pause
                    mutableDoServiceStopped.value = false
                    mCountDownTimer?.cancel()
                }

                else -> {
                    Log.e("Match", "START")
                    mutableDoServiceStopped.value = false

                    startTimer(mCounter)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun startTimer(timeLeft: Long){
        mCountDownTimer = object :  CountDownTimer(timeLeft, 1) {
            override fun onFinish() {}

            override fun onTick(millis: Long) {
                mCounter = millis
                mSec = (3600000L - mCounter).div(1000)
                mTimer = mSec.toInt()

                mutableStatus.value = Resume

                sendNotification()
                Log.e("Match", "mTimer: ${mTimer} mSec: ${mSec}")
                mutableTime.value = mSec
            }
        }.start()
    }

    private fun sendNotification() {
        val intent = Intent(applicationContext, TimerActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(AppConstant.TIMER_NOTIFICATION, false)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.action = "" + Math.random()
        intent.putExtras(Bundle().apply { putBoolean("from_notification", true) })

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val remoteCustomView = RemoteViews(packageName, R.layout.timer_notification).apply{
            setImageViewResource(R.id.image, R.drawable.ic_timer)

            setTextViewText(R.id.tvNotifTimer, "${mSec / 60}m : ${mSec % 60 }s")
        }

        val notificationBuilder =
            NotificationCompat.Builder(this, "TimerNotificationChannel")
                .setSmallIcon(R.drawable.ic_timer)
                .setContent(remoteCustomView)
                .setSound(defaultSoundUri)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(AppConstant.Services.STOPWATCH_TIMER,
                notificationBuilder.build())
        } else {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(
                AppConstant.Services.NOTIFY_TIMER_ID,
                notificationBuilder.build())
        }

    }

}