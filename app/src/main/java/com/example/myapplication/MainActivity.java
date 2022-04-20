package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   private ArrayList<MediaFiles> mediaFiles = new ArrayList<>();
   private ArrayList<String> allFolderList = new ArrayList<>();
   RecyclerView recyclerView;
   VideoFoldersAdapter adapter;
   SwipeRefreshLayout swipeRefreshLayout;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      recyclerView = findViewById(R.id.folders_rv);
      swipeRefreshLayout = findViewById(R.id.swipe_refresh_folders);
      swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            showFolders();
            swipeRefreshLayout.setRefreshing(false);
         }
      });
      showFolders();
   }

   private void showFolders() {
      mediaFiles = fetchMedia();
      adapter = new VideoFoldersAdapter(mediaFiles, allFolderList, this);
      recyclerView.setAdapter(adapter);
      recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//      adapter.notifyDataSetChanged();
   }

   private ArrayList<MediaFiles> fetchMedia() {
      ArrayList<MediaFiles> mediaFilesArrayList = new ArrayList<>();
      Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
      if (cursor != null && cursor.moveToNext()) {
         do {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
            @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
            @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            @SuppressLint("Range") String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            MediaFiles mediaFiles = new MediaFiles(id, title, displayName, size, duration, path, dateAdded);
            int index = path.lastIndexOf("/");
            String subString = path.substring(0, index);
            if (!allFolderList.contains(subString)) {
               allFolderList.add(subString);
            }
            mediaFilesArrayList.add(mediaFiles);
         } while (cursor.moveToNext());
      }
      return mediaFilesArrayList;
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.folder_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      int id = item.getItemId();
      switch (id) {
         case R.id.rateUs:
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            break;
         case R.id.share_app:
            Intent share_intent = new Intent(Intent.ACTION_SEND);
            share_intent.putExtra(Intent.EXTRA_TEXT, "Install this amazing video player app\n"+"https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            share_intent.setType("text/plain");
            startActivity(Intent.createChooser(share_intent, "Share via Apps"));
            break;
         case R.id.refresh_folders:
            finish();
            startActivity(getIntent());
            break;
      }
      return true;
   }
}