package com.example.android.exoplayerproject

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.act_exo.*
import kotlinx.android.synthetic.main.play_back_layout.*

/**
 * Author: Jayden Nguyen
 * Created date: 11/23/2019
 */
class ExoplayerACt : AppCompatActivity() {

    lateinit var exoplayer: SimpleExoPlayer

    private var isPlaying = false
    private var isMute = false
    private var currentVolume = 0f
    private var duration: Long = 0
    private var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_exo)

        initExoPlayer()

        Glide.with(this).load("https://videothumbs.lotuscdn.vn/10356057984064877/121169285283319808/2019/11/24/con-gai-chui-xuong-gam-giuong-15746052116161410753175.jpg")
            .placeholder(R.drawable.ic_launcher_background).dontAnimate().into(imgThumb)

        startBtn.setOnClickListener {
            play("https://hls.mediacdn.vn/kenh14/2019/11/19/co-dau-viet-bi-sat-hai-15741408986101107842406-15741484745051169223447-c664d.mp4")
            isPlaying = true

        }

        stopBtn.setOnClickListener {
            releasePlayer()
        }

        icVolume.setOnClickListener {
            isMute = !isMute
            if (isMute) {
                currentVolume = exoplayer.volume
                exoplayer.volume = 0f
                icVolume.setImageResource(R.drawable.ic_mute)
            } else {
                exoplayer.volume = currentVolume
                icVolume.setImageResource(R.drawable.ic_unmute)
            }
        }


        icPlay.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                icPlay.setImageResource(R.drawable.ic_pause)
                exoplayer.playWhenReady = true
            } else {
                icPlay.setImageResource(R.drawable.ic_play)
                exoplayer.playWhenReady = false
            }
        }

        seekProgress.max = 100

        seekProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    exoplayer.seekTo(((progress.toFloat()/100)  * getDuration()).toLong() )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        exoplayer.addListener(object : Player.EventListener{

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                updateProgressBar()
                if (playbackState == Player.STATE_READY) {
                    imgThumb.visibility = View.GONE
                }
                if (!playWhenReady) {
                    imgThumb.visibility = View.VISIBLE
                } else {
                    imgThumb.visibility = View.GONE
                }
            }
        })

    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }


    private fun initExoPlayer() {
        val trackSelector = DefaultTrackSelector()
        val loadControl = DefaultLoadControl()
        val renderFactory = DefaultRenderersFactory(this)

        exoplayer = ExoPlayerFactory.newSimpleInstance(this, renderFactory, trackSelector, loadControl)
        exoplayer.volume = 0.7f
        player_view.player = exoplayer
    }

    private fun releasePlayer() {
        exoplayer.stop()
        exoplayer.release()
    }

    fun play(url: String) {
        val userAgent = Util.getUserAgent(this, this.resources.getString(R.string.app_name))

        val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, userAgent)).setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(url))
        exoplayer.prepare(mediaSource)

        exoplayer.playWhenReady = true
    }

    private fun getDuration() : Long {
        return if (exoplayer.duration == C.TIME_UNSET) {
            0
        } else {
            exoplayer.duration
        }
    }

    private fun seekTo(timeInMil: Int) {
        var seekPos = timeInMil.toLong()
        exoplayer.seekTo(seekPos)
    }

    private fun updateProgressBar() {
        var duration = exoplayer.duration
        var position = exoplayer.currentPosition
        seekProgress.progress = ((position.toFloat()/duration.toFloat()) * 100).toInt()
        var bufferedPos = exoplayer.bufferedPosition
        seekProgress.secondaryProgress = ((bufferedPos.toFloat()/duration.toFloat()) * 100).toInt()

        handler.removeCallbacks(updateProgressAction)
        val playbackState = exoplayer.playbackState
        Log.d("HuyNQ1111", "player state is $playbackState")
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            var delayMs : Long = 1000
            if (exoplayer.playWhenReady && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (position % 1000)
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000
            }

            handler.postDelayed(
                updateProgressAction
            , delayMs)
        }
    }

    private val updateProgressAction = Runnable {
        updateProgressBar()
    }
}