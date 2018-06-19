package com.example.team4kb.smartbabymonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    MainActivity mainActivity;

    Button button_settings_logout;
    Button button_settings_raspberry;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mainActivity = (MainActivity) MainActivity.activity;

        Log.d("setting", "FirebaseAuth:start");
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        Log.d("setting", "FirebaseAuth:end");
        // [END initialize_auth

        button_settings_logout = findViewById(R.id.button_settings_logout);
        button_settings_logout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                mainActivity.finish();
                finish();
            }
        });

        button_settings_raspberry = findViewById(R.id.button_settings_raspberryChange);
        button_settings_raspberry.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                raspberryChange();
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void raspberryChange() {
        startActivity(new Intent(getApplicationContext(), RaspberryChangeActivity.class));
    }
}
