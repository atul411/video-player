package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URI;

public class AllowAccessActivity extends AppCompatActivity {

   Button allowBtn;
   public static final int STORAGE_PERMISSION = 1;
   public static final int REQUEST_PERMISSION_SETTING = 2;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_allow_access);
      allowBtn = findViewById(R.id.allow_access);
      allowBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
               startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
               finish();
            } else {
               ActivityCompat.requestPermissions(AllowAccessActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            }
         }
      });
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (requestCode == STORAGE_PERMISSION) {
         for (int i = 0; i < permissions.length; i++) {
            String per = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
               Boolean showRationale = shouldShowRequestPermissionRationale(per);
               if (!showRationale) {
                  //user never asked permission
                  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                  alertDialog.setTitle("App Permission");
                  alertDialog.setMessage("For allowing this app to play the video you must allow this app to access to your storage").setPositiveButton("Open setting", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                     }
                  }).create().show();
               } else {
                  ActivityCompat.requestPermissions(AllowAccessActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
               }
            } else {
               startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
               finish();
            }
         }
      }
   }
}