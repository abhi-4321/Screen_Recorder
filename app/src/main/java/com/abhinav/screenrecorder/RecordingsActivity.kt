package com.abhinav.screenrecorder

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.widget.Toolbar


class RecordingsActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var videoListView: ListView
    private lateinit var surfaceView: SurfaceView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoList: ArrayList<String>
    private lateinit var toolbar: Toolbar
    private lateinit var frame: LinearLayout
    private var currentVideoIndex = 0
    private var media = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordings)

        videoListView = findViewById(R.id.videoListView)
        surfaceView = findViewById(R.id.surfaceView)
        surfaceView.holder.addCallback(this)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        videoList = getVideoList()
        val videoListAdapter = ArrayAdapter(this, R.layout.list_item, videoList)
        videoListView.adapter = videoListAdapter

        // Set item click listener to play the selected video
        videoListView.setOnItemClickListener { _, _, position, _ ->

            frame.visibility = View.INVISIBLE
            surfaceView.visibility = View.VISIBLE
            currentVideoIndex = position
            playCurrentVideo()
        }

        // Start playing the first video
        playCurrentVideo()
    }

    private fun getVideoList(): ArrayList<String> {
        val videoList = ArrayList<String>()
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val selection = "${MediaStore.Video.Media.DATA} like ?"
        val selectionArgs = arrayOf("%/Movies/%")
        val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            while (it.moveToNext()) {
                val videoPath = it.getString(columnIndex)
                videoList.add(videoPath)
            }
        }

        return videoList
    }


    private fun playCurrentVideo() {
        if (currentVideoIndex >= 0 && currentVideoIndex < videoList.size) {
            val videoPath = videoList[currentVideoIndex]
            playVideo(videoPath)
        }
    }

    private fun playVideo(videoPath: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(videoPath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            media = 1
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d("TAG", "Sur Change")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mediaPlayer.release()
        Log.d("TAG", "Sur Dest")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d("TAG", "Sur Create")
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDisplay(holder)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (media == 1)
            mediaPlayer.release()
    }

    override fun onBackPressed() {
        if (surfaceView.visibility == View.VISIBLE) {
            frame.visibility = View.VISIBLE
            surfaceView.visibility = View.INVISIBLE
        } else {
            super.onBackPressed()
        }
    }
}