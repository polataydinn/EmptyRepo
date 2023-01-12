package com.application.phonerecord

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import com.aykuttasil.callrecord.CallRecord
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(this, MyService::class.java)
        startService(serviceIntent)
    }

}