package com.infomantri.timer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class RebootBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Action: " + intent?.action, Toast.LENGTH_LONG).show()

        when{
            intent?.action == "android.net.conn.CONNECTIVITY_CHANGE" -> {
                "Network Connection is Changed..." toast (context)
            }
            intent?.action == "android.intent.action.REBOOT" -> {
                "Device is Rebooted..." toast(context)
            }
            intent?.action == "android.intent.action.BOOT_COMPLETED" -> {
                "Device Boot is Completed..." toast (context)
            }
        }

    }

    private infix fun String.toast(context: Context?) {
        Toast.makeText(context,"Network Connection is Changed...",Toast.LENGTH_LONG).show()
    }

}