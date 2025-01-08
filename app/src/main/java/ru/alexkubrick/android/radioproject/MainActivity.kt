package ru.alexkubrick.android.radioproject

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import ru.alexkubrick.android.radioproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isPlaying = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bPlay.setImageResource(R.drawable.ic_play)
        binding.bPlay.setOnClickListener {
            manageRadioService()
        }
    }

    private fun manageRadioService() {
        val serviceIntent = Intent(this, RadioPlayerService::class.java)

        if (!isPlaying) {
            serviceIntent.action = RadioConstants.ACTION.STARTFOREGROUND_ACTION
            binding.bPlay.setImageResource(R.drawable.ic_stop)
        } else {
            serviceIntent.action = RadioConstants.ACTION.STOPFOREGROUND_ACTION
            binding.bPlay.setImageResource(R.drawable.ic_play)
        }

        startService(serviceIntent)
        isPlaying = !isPlaying
    }

    private fun controlRadio() {

    }


    companion object {
        var RadioPlayerServiceInstance: RadioPlayerService? = null
    }
}