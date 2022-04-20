package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener{
   PlayerView playerView;
   SimpleExoPlayer player;
   int position;
   String videoTitle;
   ArrayList<MediaFiles> mVideoFiles = new ArrayList<>();
   TextView title;
   ConcatenatingMediaSource concatenatingMediaSource;
   ImageView nextButton, previousButton;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setFullScreen();
      setContentView(R.layout.activity_video_player);
      playerView = findViewById(R.id.exoplayer_view);
      title = findViewById(R.id.video_title);
      nextButton = findViewById(R.id.exo_next);
      previousButton = findViewById(R.id.exo_prev);
      nextButton.setOnClickListener(this);
      previousButton.setOnClickListener(this);
      getSupportActionBar().hide();
      position = getIntent().getIntExtra("position", 1);
      videoTitle = getIntent().getStringExtra("title");
      mVideoFiles = getIntent().getParcelableArrayListExtra("videoArrayList");
      title.setText(videoTitle);
      playVideo();
   }

   private void playVideo() {
      String path = mVideoFiles.get(position).getPath();
      Uri uri = Uri.parse(path);
      player = new SimpleExoPlayer.Builder(this).build();
      DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
      concatenatingMediaSource = new ConcatenatingMediaSource();
      for (int i = 0; i < mVideoFiles.size(); i++) {
         new File(String.valueOf(mVideoFiles.get(i)));
         MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
         concatenatingMediaSource.addMediaSource(mediaSource);
      }
      playerView.setPlayer(player);
      playerView.setKeepScreenOn(true);
      player.prepare(concatenatingMediaSource);
      player.seekTo(position, C.TIME_UNSET);
      playError();
   }

   private void playError() {
      player.addListener(new Player.Listener() {
         @Override
         public void onPlayerError(PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
            Toast.makeText(VideoPlayerActivity.this, "Video playing error", Toast.LENGTH_SHORT).show();
         }
      });
      player.setPlayWhenReady(true);
   }

   @Override
   public void onBackPressed() {
      super.onBackPressed();
      if (player.isPlaying()) {
         player.stop();
      }
   }

   @Override
   protected void onPause() {
      super.onPause();
      player.setPlayWhenReady(false);
      player.getPlaybackState();
   }

   @Override
   protected void onResume() {
      super.onResume();
      player.setPlayWhenReady(true);
      player.getPlaybackState();
   }

   @Override
   protected void onRestart() {
      super.onRestart();
      player.setPlayWhenReady(true);
      player.getPlaybackState();
   }
   private void setFullScreen(){
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()){
         case R.id.exo_next:
            try {
               player.stop();
               position++;
               playVideo();
            }catch (Exception ex){
               Toast.makeText(this, "No next video", Toast.LENGTH_SHORT).show();
               finish();
            }
            break;
         case R.id.exo_prev:
            try {
               player.stop();
               position--;
               playVideo();
            }catch (Exception ex){
               Toast.makeText(this, "No previous video", Toast.LENGTH_SHORT).show();
               finish();
            }
            break;
      }
   }
}