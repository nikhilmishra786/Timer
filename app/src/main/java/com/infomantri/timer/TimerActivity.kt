package com.infomantri.timer

import android.annotation.TargetApi
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {

    private var mTimer = 0
    private var mDoServiceStopped = true
    private var mStartTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        setOnclickListeners()
        observeTimer()
        observeSession()
        observeStatus()
    }

    private fun setOnclickListeners() {

        tvTimerCounterStart.setOnClickListener {
            initListeners()
        }
        tvTimerCounter.setOnClickListener{
            initListeners()
        }
    }

    private fun initListeners() {
        setStartTime()

        when{
            tvTimerCounterStart.text.toString() == "Start" -> {
                showPause()
                TimerService.startService(this@TimerActivity)
            }

            tvTimerCounterStart.text.toString() == "Pause" -> {
                showResume()
                TimerService.pauseService(this@TimerActivity)
            }

            tvTimerCounterStart.text.toString() == "Resume" -> {
                showPause()
                TimerService.startService(this@TimerActivity)
            }
        }
    }

    private fun clearTimerSession() {
        tvTimerCounter.text = "0m : 0s"
        tvTimerCounterStart.text = "Start"
        tvTimerCounterStart.background = getDrawable(R.drawable.green_circle_bg)
        TimerService.stopService(this@TimerActivity)
    }

    private fun setStartTime() {
        if(mStartTime == 0L) {
            mStartTime = Calendar.getInstance().timeInMillis
        }
    }

    private fun showPause() {
        tvTimerCounterStart.text = "Pause"
        tvTimerCounterStart.background = getDrawable(R.drawable.pause_circle_bg)

        startRotation()
    }

    private fun showResume() {
        tvTimerCounterStart.text = "Resume"
        tvTimerCounterStart.background = getDrawable(R.drawable.resume_circle_bg)

        ivRing.clearAnimation()
    }

    private fun startRotation() {
        ivRing.startAnimation(
            AnimationUtils.loadAnimation(this@TimerActivity, R.anim.rotate)
        )
    }

    private fun observeStatus() {
        TimerService.mutableDoServiceStopped.observe(this,
            androidx.lifecycle.Observer {
             mDoServiceStopped = it
            })
    }

    private fun observeTimer() {
        TimerService.mutableTime.observe(this,
            androidx.lifecycle.Observer {
                Log.i("Timer", "${tvTimerCounterStart.text.toString() == "Start"}")
                if(it != 0L && tvTimerCounterStart.text.toString() == "Start") {
                    showPause()
                }

                mTimer = it.toInt()
                tvTimerCounter.text = "${it /60}m : ${it % 60}s"
            })
    }

    private fun observeSession() {
        TimerService.mutableStatus.observe(this, androidx.lifecycle.Observer {
            when(it) {
                is Pause -> {
                    showResume()
                }
            }
        })
    }

}
