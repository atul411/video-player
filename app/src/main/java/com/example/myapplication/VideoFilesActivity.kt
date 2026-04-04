package com.example.myapplication

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VideoFilesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var videoFilesArrayList = ArrayList<MediaFiles>()
    private lateinit var videoFilesAdapter: VideoFilesAdapter
    private lateinit var folderName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_files)
        recyclerView = findViewById(R.id.videos_rv)
        folderName = intent.getStringExtra("folderName") ?: ""
        supportActionBar?.title = folderName
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        Log.d("atul_yadav", "fetchMedia: $uri")
        showVideoFiles()
    }

    private fun showVideoFiles() {
        videoFilesArrayList = fetchMedia(folderName)
        videoFilesAdapter = VideoFilesAdapter(videoFilesArrayList, this)
        recyclerView.adapter = videoFilesAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        videoFilesAdapter.notifyDataSetChanged()
    }

    @SuppressLint("Range")
    private fun fetchMedia(folderName: String): ArrayList<MediaFiles> {
        val videoFiles = ArrayList<MediaFiles>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Video.Media.DATA} like ?"
        val selectionArg = arrayOf("%$folderName%")
        Log.d("atul_yadav", "fetchMedia: ${uri.encodedPath}")
        val cursor: Cursor? = applicationContext.contentResolver.query(uri, null, selection, selectionArg, null)
        if (cursor != null && cursor.moveToNext()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                videoFiles.add(MediaFiles(id, title, displayName, size, duration, path, dateAdded))
            } while (cursor.moveToNext())
        }
        return videoFiles
    }
}
