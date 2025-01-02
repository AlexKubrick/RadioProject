package ru.alexkubrick.android.radioproject

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.alexkubrick.android.radioproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var radioPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val radioUri = Uri.parse(RADIOURL)
        radioPlayer = MediaPlayer.create(this, radioUri)

        binding.playPauseButton.text = "Играть"
        binding.playPauseButton.setOnClickListener {
            if (radioPlayer?.isPlaying != true) {
                radioPlayer?.start()
                binding.playPauseButton.text = "Остановить"
            } else {
                radioPlayer?.pause()
                binding.playPauseButton.text = "Играть"
            }
        }
    }

    companion object {
        const val RADIOURL = "https://bookradio.hostingradio.ru:8069/fm" //   audio/mpeg
    }
}