package ru.alexkubrick.android.radioproject

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import ru.alexkubrick.android.radioproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isPlaying = false
    private lateinit var player: ExoPlayer

    private fun getTimeString(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        val time = String.format("%02d:%02d",min,sec)
        return time
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        player =
            ExoPlayer.Builder(this)
                .setMediaSourceFactory(DefaultMediaSourceFactory(this).setLiveTargetOffsetMs(5000))
                .build()

        val mediaItem =
            MediaItem.Builder()
                .setUri(RadioConstants.RADIOURL)
                .setLiveConfiguration(
                    MediaItem.LiveConfiguration.Builder().setMaxPlaybackSpeed(1.02f).build()
                )
                .build()
        player.setMediaItem(mediaItem)
        player.prepare()

        binding.playerSeekBar.max = 3600
        binding.twCurrentStation.text = getString(R.string.radio_kniga)
        binding.twTime.text = "00:00"
        binding.bPlay.setImageResource(R.drawable.ic_play)
        binding.bPlay.setOnClickListener {
            manageRadioService()
        }
    }

    val handler = Handler(Looper.getMainLooper())
    private var fakeProgress = 0 // Имитация движения ползунка

    @OptIn(UnstableApi::class)
    private fun manageRadioService() {
        //val serviceIntent = Intent(this, RadioPlayerService::class.java)
        if (!isPlaying) {
            //serviceIntent.action = RadioConstants.ACTION.STARTFOREGROUND_ACTION
            player.play()
        } else {
            //serviceIntent.action = RadioConstants.ACTION.STOPFOREGROUND_ACTION
            player.pause()
        }
        //startService(serviceIntent)
        isPlaying = !isPlaying

        player.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY && player.playWhenReady) {
                    binding.bPlay.setImageResource(R.drawable.ic_stop)
                } else {
                    binding.bPlay.setImageResource(R.drawable.ic_play)
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int,
            ) {
                val currentPosition = player.currentPosition.toInt() / 1000
                binding.playerSeekBar.progress = currentPosition
                binding.twTime.text = getTimeString(currentPosition)
            }

        })

        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress.toLong() * 1000)
                    fakeProgress = progress // Обновляем SeekBar, если пользователь переместил
                    binding.twTime.text = getTimeString(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        player.addListener(
            object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    val cause = error.cause
                    if (cause is HttpDataSource.HttpDataSourceException) {
                        val httpError = cause
                        if (httpError is HttpDataSource.InvalidResponseCodeException) {
                            // ошибка 400, 500
                            val responseCode = httpError.responseCode
                            Toast.makeText(
                                applicationContext,
                                "Ошибка сервера: $responseCode. Попробуйте снова.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // ошибка соединения
                            Toast.makeText(
                                applicationContext,
                                "Сетевая ошибка. Проверьте соединение с интернетом.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (cause is PlaybackException) {
                        // общая ошибка воспроизведения
                        Toast.makeText(
                            applicationContext,
                            "Ошибка воспроизведения. Попробуйте снова.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // неизвестная ошибка
                        Toast.makeText(
                            applicationContext,
                            "Произошла ошибка: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )

        handler.post(object : Runnable {
            override fun run() {
                val realPosition = player.currentPosition.toInt() / 1000
                binding.twTime.text = getTimeString(realPosition) // Реальное время для текста

                if (player.isPlaying) {
                    // Имитация движения ползунка
                    fakeProgress++
                    if (fakeProgress <= binding.playerSeekBar.max) {
                        binding.playerSeekBar.progress = fakeProgress
                    }
                } else {
                    fakeProgress = realPosition // Синхронизировать при паузе
                }

                handler.postDelayed(this, 3000)
            }
        })
    }
//        player.addListener(
//            object : Player.Listener {
//                override fun onIsPlayingChanged(isPlaying: Boolean) {
//                    if (isPlaying) {
//                        binding.bPlay.setImageResource(R.drawable.ic_stop)
//                    } else {
//                        binding.bPlay.setImageResource(R.drawable.ic_play)
//                        // Not playing because playback is paused, ended, suppressed, or the player
//                        // is buffering, stopped or failed. Check player.playWhenReady,
//                        // player.playbackState, player.playbackSuppressionReason and
//                        // player.playerError for details.
//                    }
//                }
//            }
//        )

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}