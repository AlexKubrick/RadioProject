package ru.alexkubrick.android.radioproject

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class RadioPlayerService : Service() {
    private lateinit var player: ExoPlayer
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri(RadioConstants.RADIOURL)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == RadioConstants.ACTION.STARTFOREGROUND_ACTION) {
            startForeground(RadioConstants.NOTIFICATION_ID, createNotification())
            playMusic()
            isPlaying = true
        } else if (intent.action == RadioConstants.ACTION.STOPFOREGROUND_ACTION) {
            stopMusic()
            stopForeground(true)
            isPlaying = false
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, RadioPlayerService::class.java).apply {
            action = RadioConstants.ACTION.STOPFOREGROUND_ACTION
        }
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, RadioConstants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Радио Книга")
            .setContentText("Играет")
            .setSmallIcon(R.drawable.ic_radio_player)
            .addAction(R.drawable.ic_radio_player, "Остановить", pendingStopIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun playMusic() {
        if (!player.isPlaying) {
            player.play()
        }
    }

    private fun stopMusic() {
        if (player.isPlaying) {
            player.stop()
        }
    }
}
