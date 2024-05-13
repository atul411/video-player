package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class VideoFilesActivity extends AppCompatActivity {

   private static final String TAG = VideoFilesActivity.class.getSimpleName();
   RecyclerView recyclerView;
   private ArrayList<MediaFiles> videoFilesArrayList = new ArrayList<>();
   VideoFilesAdapter videoFilesAdapter;
   String folder_name;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_video_files);
      recyclerView = findViewById(R.id.videos_rv);
      folder_name = getIntent().getStringExtra("folderName");
      getSupportActionBar().setTitle(folder_name);
      Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
      Log.d("atul_yadav", "fetchMedia: " + uri.toString());
      showVideoFiles();
   }

   private void showVideoFiles() {
      videoFilesArrayList = fetchMedia(folder_name);
      videoFilesAdapter = new VideoFilesAdapter(videoFilesArrayList, this);
      recyclerView.setAdapter(videoFilesAdapter);
      recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
      videoFilesAdapter.notifyDataSetChanged();
   }

   private ArrayList<MediaFiles> fetchMedia(String folderName) {
      ArrayList<MediaFiles> videoFiles = new ArrayList<>();
      Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
      String selection = MediaStore.Video.Media.DATA + " like ?";
      String[] selectionArg = new String[]{"%" + folderName + "%"};
      Log.d("atul_yadav", "fetchMedia: " + uri.getEncodedPath());
      Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, selection, selectionArg, null);
      if (cursor != null && cursor.moveToNext()) {
         do {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
            @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
            @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            @SuppressLint("Range") String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
            MediaFiles mediaFiles = new MediaFiles(id, title, displayName, size, duration, path, dateAdded);
            videoFiles.add(mediaFiles);
         } while (cursor.moveToNext());
      }
      return videoFiles;
   }
}