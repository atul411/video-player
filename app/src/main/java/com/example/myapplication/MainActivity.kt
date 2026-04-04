package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var mediaFiles = ArrayList<MediaFiles>()
    private var allFolderList = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoFoldersAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.folders_rv)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_folders)
        swipeRefreshLayout.setOnRefreshListener {
            showFolders()
            swipeRefreshLayout.isRefreshing = false
        }
        showFolders()
    }

    private fun showFolders() {
        mediaFiles = fetchMedia()
        adapter = VideoFoldersAdapter(mediaFiles, allFolderList, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    @SuppressLint("Range")
    private fun fetchMedia(): ArrayList<MediaFiles> {
        val mediaFilesArrayList = ArrayList<MediaFiles>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToNext()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val mediaFile = MediaFiles(id, title, displayName, size, duration, path, dateAdded)
                val index = path.lastIndexOf("/")
                val subString = path.substring(0, index)
                if (!allFolderList.contains(subString)) {
                    allFolderList.add(subString)
                }
                mediaFilesArrayList.add(mediaFile)
            } while (cursor.moveToNext())
        }
        return mediaFilesArrayList
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.folder_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rateUs -> {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=${applicationContext.packageName}")
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
            R.id.share_app -> {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Install this amazing video player app\nhttps://play.google.com/store/apps/details?id=${applicationContext.packageName}")
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, "Share via Apps"))
            }
            R.id.refresh_folders -> {
                finish()
                startActivity(intent)
            }
        }
        return true
    }
}
