package ru.alexkubrick.android.radioproject

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import ru.alexkubrick.android.radioproject.dataSource.CustomDataSourceFactory
import ru.alexkubrick.android.radioproject.databinding.ActivityMainBinding
import java.io.File

@UnstableApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isPlaying = false
    private lateinit var player: ExoPlayer
    val handler = Handler(Looper.getMainLooper())
    private lateinit var cache: SimpleCache

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        player =
//            ExoPlayer.Builder(this)
//                .setMediaSourceFactory(DefaultMediaSourceFactory(this).setLiveTargetOffsetMs(5000))
//                .build()
//
//        val mediaItem =
//            MediaItem.Builder()
//                .setUri(RadioConstants.RADIOURL)
//                .setLiveConfiguration(
//                    MediaItem.LiveConfiguration.Builder().setMaxPlaybackSpeed(1.02f).build()
//                )
//                .build()
//        player.setMediaItem(mediaItem)
//        player.prepare()
        // Создание кэша
        val cacheDir = File(cacheDir, "radioProject-cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val maxCacheSize: Long = 100 * 1024 * 1024 // 100 МБ
        val cache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(maxCacheSize))

        // Настройка CacheDataSource
        val upstreamDataSourceFactory = DefaultDataSource.Factory(this)
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)

        // управление процессом загрузки данных (буферизацией)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(30000, 60000, 1000, 5000)
            .build()
        // управление скоростью воспроизведения live-потоков (прямых трансляций)
        val livePlaybackSpeedControl = DefaultLivePlaybackSpeedControl.Builder()
            .setFallbackMinPlaybackSpeed(0.98f)
            .setFallbackMaxPlaybackSpeed(1.02f)
            .build()

        // билдим плеер
        player = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .setLivePlaybackSpeedControl(livePlaybackSpeedControl)
            .setMediaSourceFactory(ProgressiveMediaSource.Factory(cacheDataSourceFactory))
            .build()


        // настройка mediaSource
        val dataSourceFactory = CustomDataSourceFactory()
        val mediaItem = MediaItem.fromUri(Uri.parse(RadioConstants.RADIOURL))
        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(mediaItem)
        player.setMediaSource(mediaSource)
        player.prepare()
        //player.playWhenReady = true

        binding.playerSeekBar.max = 3600
        binding.twCurrentStation.text = getString(R.string.radio_kniga)
        binding.twTime.text = "00:00"
        binding.bPlay.setImageResource(R.drawable.ic_play)
//        binding.bPlay.setOnClickListener {
//            manageRadioService()
//        }

        binding.bPlay.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                binding.bPlay.setImageResource(R.drawable.ic_play)
            } else {
                player.play()
                binding.bPlay.setImageResource(R.drawable.ic_stop)
            }
        }

        // Обновление ползунка
        val updateSeekBar = object : Runnable {
            override fun run() {
                val currentPosition = player.currentPosition / 1000
                binding.playerSeekBar.progress = currentPosition.toInt()
                binding.twTime.text = getTimeString(currentPosition.toInt())
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateSeekBar)

        // Обработка перемещения ползунка
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress.toLong() * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Обработка состояния буферизации
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        binding.ivStationImage.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        binding.ivStationImage.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        })
    }

    //private var fakeProgress = 0 // Имитация движения ползунка

    @OptIn(UnstableApi::class)
    private fun manageRadioService() {
//        //val serviceIntent = Intent(this, RadioPlayerService::class.java)
//        if (!isPlaying) {
//            //serviceIntent.action = RadioConstants.ACTION.STARTFOREGROUND_ACTION
//            player.play()
//        } else {
//            //serviceIntent.action = RadioConstants.ACTION.STOPFOREGROUND_ACTION
//            player.pause()
//        }
//        //startService(serviceIntent)
//        isPlaying = !isPlaying
//
//        player.addListener(object : Player.Listener {
//            @Deprecated("Deprecated in Java")
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if (playbackState == Player.STATE_READY && player.playWhenReady) {
//                    binding.bPlay.setImageResource(R.drawable.ic_stop)
//                } else {
//                    binding.bPlay.setImageResource(R.drawable.ic_play)
//                }
//            }
//
//            override fun onPositionDiscontinuity(
//                oldPosition: Player.PositionInfo,
//                newPosition: Player.PositionInfo,
//                reason: Int,
//            ) {
//                val currentPosition = player.currentPosition.toInt() / 1000
//                binding.playerSeekBar.progress = currentPosition
//                binding.twTime.text = getTimeString(currentPosition)
//            }
//
//        })
//
//        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (fromUser) {
//                    player.seekTo(progress.toLong() * 1000)
//                    fakeProgress = progress // Обновляем SeekBar, если пользователь переместил
//                    binding.twTime.text = getTimeString(progress)
//                }
//            }
//
//            override fun onStartTrackingTouch(p0: SeekBar?) {}
//
//            override fun onStopTrackingTouch(p0: SeekBar?) {}
//
//        })

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
        cache.release()
        handler.removeCallbacksAndMessages(null)
    }

    private fun getTimeString(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        return String.format("%02d:%02d", min, sec)
    }
}