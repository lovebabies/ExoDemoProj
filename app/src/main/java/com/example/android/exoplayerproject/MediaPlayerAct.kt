package com.example.android.exoplayerproject

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.act_media.*
import kotlinx.android.synthetic.main.play_back_layout.*

/**
 * Author: Jayden Nguyen
 * Created date: 11/23/2019
 */
const val PERMISSION_CODE = 100
class MediaPlayerAct : AppCompatActivity() {

    private val mSourceList = ArrayList<String>()
    private val mediaPlayer = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_media)
        checkPermission()
        btnStart.setOnClickListener {
            playSong("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
        }

        btnStop.setOnClickListener {
            stopSong()
        }
    }

    fun getSongFromDevices(): ArrayList<String> {
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        val resultList = arrayListOf<String>()
        if (cursor == null) {
            Log.d("HuyNQ1211", "No song detected")
        } else {
            while(cursor.moveToNext()) {
                var fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                resultList.add(fullPath)
            }
        }

        return resultList

    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE ), PERMISSION_CODE)
        } else {
            mSourceList.addAll(getSongFromDevices())
        }
    }

    fun playSong(path: String) {
         mediaPlayer.apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(path)
            prepareAsync()
             setOnPreparedListener {
                 runOnUiThread {
                     start()
                 }
             }
        }
    }

    fun stopSong() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            Log.d("HuyNQ1211", "Request Permission Success")
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongFromDevices()
            }else {
                Log.d("HuyNQ1211", "Request Permission Failed")
            }
        }
    }
}