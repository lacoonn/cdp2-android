package com.example.team4kb.smartbabymonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static Activity activity;

    private MyApplication myApp;
    private Context mContext = this;

    LinearLayout button_main_realtimeview;
    LinearLayout button_main_settings;
    LinearLayout button_main_management;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myApp.getSocket() != null) myApp.shutdownSocket();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
        // 읽기 권한 요청

        activity = MainActivity.this;

        myApp = (MyApplication) getApplication();

        FirebaseMessaging.getInstance().subscribeToTopic("baby");
        // Get token
        String token = FirebaseInstanceId.getInstance().getToken();

        button_main_realtimeview = findViewById(R.id.button_main_realtimeview);
        button_main_realtimeview.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RealtimeViewActivity.class));
            }
        });

        button_main_management = findViewById(R.id.button_main_management);
        button_main_management.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApp.getSocket() == null) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("라즈베리파이와 연결할 수 없습니다")
                            .setCancelable(true)
                            .show();
                    try {
                        myApp.setSocket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), FileListActivity2.class));
                }
            }
        });

        button_main_settings = findViewById(R.id.button_main_settings);
        button_main_settings.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }
}
