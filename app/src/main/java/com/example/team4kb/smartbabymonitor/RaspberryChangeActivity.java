package com.example.team4kb.smartbabymonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RaspberryChangeActivity extends AppCompatActivity {

    Button button_raspberry_change;
    TextView text_raspberry_previous;
    EditText text_raspberry_change;
    String string_raspberry_change;
    String userId;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_database]
    private FirebaseDatabase mDatabase;
    DatabaseReference myRef;
    // [END declare_database]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raspberry_change);

        button_raspberry_change = findViewById(R.id.button_raspberry_change_execute);
        button_raspberry_change.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFirebaseRaspberry();
            }
        });

        text_raspberry_previous = findViewById(R.id.field_raspberry_previous);
        text_raspberry_change = findViewById(R.id.field_raspberry_change);
        text_raspberry_change.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NONE) {
                    changeFirebaseRaspberry();
                }
                return true;
            }
        });

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START initialize_database]
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("users");
        // [END initialize_database]

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userId = mAuth.getUid();
                String text = dataSnapshot.child(userId).child("raspberry").getValue(String.class);
                text_raspberry_previous.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });
    }

    private void changeFirebaseRaspberry() {
        string_raspberry_change = text_raspberry_change.getText().toString();
        myRef.child(userId).child("raspberry").setValue(string_raspberry_change);
    }

}
