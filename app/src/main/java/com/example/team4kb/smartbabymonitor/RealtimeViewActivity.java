package com.example.team4kb.smartbabymonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.io.IOException;

public class RealtimeViewActivity extends AppCompatActivity {

    MyApplication myApp;
    private Context mContext = this;

    WebView webview_realtimeview;
    Button button_list_var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_view);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        myApp = (MyApplication) getApplication();

        webview_realtimeview = findViewById(R.id.realtimeview_webview);
        webview_realtimeview.loadUrl("http://doctor15.iptime.org:15000/?action=stream/frame.mjpg");
        webview_realtimeview.getSettings().setLoadWithOverviewMode(true);
        webview_realtimeview.getSettings().setUseWideViewPort(true);

        button_list_var = (Button) findViewById(R.id.button_realtimeview_list);
        button_list_var.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
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
                    Intent intent = new Intent(RealtimeViewActivity.this, FileListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
