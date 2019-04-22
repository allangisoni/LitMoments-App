package com.example.android.litmoments;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @BindView(R.id.addjournal_fab)  FloatingActionButton addJournalFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseApp.initializeApp(getApplicationContext());


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(MainActivity.this, "Current user is "+" "+ mAuth.getCurrentUser().getDisplayName()+" ", Toast.LENGTH_SHORT).show();
        }

        addJournalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddJournalEntry.class));
            }
        });

    }
}
