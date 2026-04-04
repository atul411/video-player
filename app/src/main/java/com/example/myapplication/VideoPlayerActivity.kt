package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.File

class VideoPlayerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var playerView: PlayerView
    private lateinit var player: SimpleExoPlayer
    private var position = 0
    private lateinit var videoTitle: String
    private var mVideoFiles = ArrayList<MediaFiles>()
    private lateinit var title: TextView
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    private lateinit var nextButton: ImageView
    private lateinit var previousButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_video_player)
        playerView = findViewById(R.id.exoplayer_view)
        title = findViewById(R.id.video_title)
        nextButton = findViewById(R.id.exo_next)
        previousButton = findViewById(R.id.exo_prev)
        nextButton.setOnClickListener(this)
        previousButton.setOnClickListener(this)
        supportActionBar?.hide()
        position = intent.getIntExtra("position", 1)
        videoTitle = intent.getStringExtra("title") ?: ""
        mVideoFiles = intent.getParcelableArrayListExtra("videoArrayList") ?: ArrayList()
        title.text = videoTitle
        playVideo()
    }

    private fun playVideo() {
        val path = mVideoFiles[position].path
        val uri = Uri.parse(path)
        player = SimpleExoPlayer.Builder(this).build()
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"))
        concatenatingMediaSource = ConcatenatingMediaSource()
        for (i in mVideoFiles.indices) {
            File(mVideoFiles[i].toString())
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(uri.toString())))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        playerView.player = player
        playerView.keepScreenOn = true
        player.prepare(concatenatingMediaSource)
        player.seekTo(position, C.TIME_UNSET)
        playError()
    }

    private fun playError() {
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(this@VideoPlayerActivity, "Video playing error", Toast.LENGTH_SHORT).show()
            }
        })
        player.playWhenReady = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (player.isPlaying) player.stop()
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
        player.playbackState
    }

    override fun onResume() {
        super.onResume()
        player.playWhenReady = true
        player.playbackState
    }

    override fun onRestart() {
        super.onRestart()
        player.playWhenReady = true
        player.playbackState
    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.exo_next -> {
                try {
                    player.stop()
                    position++
                    playVideo()
                } catch (ex: Exception) {
                    Toast.makeText(this, "No next video", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            R.id.exo_prev -> {
                try {
                    player.stop()
                    position--
                    playVideo()
                } catch (ex: Exception) {
                    Toast.makeText(this, "No previous video", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
