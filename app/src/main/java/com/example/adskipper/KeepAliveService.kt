package com.example.adskipper

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class KeepAliveService : Service() {
    private val channelId = "skip_ad_keep_alive"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "后台保活服务", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("K40 Pro 跳广告助手运行中")
            .setContentText("正在实时守护开屏界面")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setOngoing(true)
            .build()
        startForeground(1001, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
