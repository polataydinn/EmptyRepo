package com.application.phonerecord

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.aykuttasil.callrecord.CallRecord
import com.aykuttasil.callrecord.receiver.CallRecordReceiver
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class MyService : Service() {
    private var callRecord: CallRecord? = null

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {

                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    val filePath =
                        callRecord?.recordDirPath + "/" + callRecord?.recordDirName + "/"

                    val files = File(filePath).listFiles()
                    val fileNames = arrayOfNulls<String>(files.size)
                    files?.mapIndexed { index, item ->
                        fileNames[index] = item?.name
                    }
                    val fileName = fileNames.filter { it?.contains(callRecord?.recordFileName.toString()) == true }[0]
                    val fileUri = Uri.fromFile(File (filePath + fileName))
                    println(fileName)
                    println(filePath + fileName)
                    fileName?.let { upload(fileUri, it) }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter("android.intent.action.PHONE_STATE")
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        val notification = NotificationCompat.Builder(this, "call_recording")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Call Recording")
            .setContentText("Recording call in progress...")
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "call_recording",
                "Call Recording",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startRecording() {
        callRecord = CallRecord.Builder(this)
            .setRecordFileName(UUID.randomUUID().toString())
            .setRecordDirName("RecordDirName")
            .setRecordDirPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path) // optional & default value
            .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT) // optional & default value
            .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT) // optional & default value
            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION) // optional & default value
            .setShowSeed(showSeed = false)
            .setLogEnable(isEnable = false)
            .setShowPhoneNumber(showNumber = false)
            .build()

        callRecord?.let {
            it.enableSaveFile()
            it.startCallReceiver()
        }
    }

    private fun stopRecording() {
        callRecord?.disableSaveFile()
        callRecord?.stopCallReceiver()
    }

    fun upload(file: Uri, fileName: String) {
        file.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()
            byteArray?.let { Repository.uploadFile(it, fileName) }
        }
    }
}

class CallRecord(callRecord: CallRecord) : CallRecordReceiver(callRecord) {
    override fun onIncomingCallEnded(context: Context, number: String?, start: Date, end: Date) {
        super.onIncomingCallEnded(context, number, start, end)
        println("breakpoint")
    }

    override fun onOutgoingCallEnded(context: Context, number: String?, start: Date, end: Date) {
        super.onOutgoingCallEnded(context, number, start, end)
        println("breakpoint")
    }
}