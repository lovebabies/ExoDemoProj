package com.example.android.exoplayerproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer.setOnClickListener {
            startActivity(Intent(this, MediaPlayerAct::class.java))
        }

        exoplayer.setOnClickListener {
            startActivity(Intent(this, ExoplayerACt::class.java))
        }

    }
}
