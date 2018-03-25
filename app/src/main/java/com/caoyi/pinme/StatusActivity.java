package com.caoyi.pinme;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatusText;
    private Button mSaveButton;

    // Firebase
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Progress Dialogue
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


        mStatusText = findViewById(R.id.status_input);
        mStatusText.getEditText().setText(getIntent().getStringExtra("old_status"));

        mSaveButton = findViewById(R.id.status_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {



             mProgress = new ProgressDialog(StatusActivity.this);
             mProgress.setTitle("Saving changes...");
             mProgress.show();

             String status = mStatusText.getEditText().getText().toString();
             mUserDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {

                     if (task.isSuccessful()) {

                         mProgress.dismiss();
                         finish();

                     } else {

                         mProgress.hide();
                         Toast.makeText(getApplicationContext(), "Failed to save status chages!", Toast.LENGTH_LONG).show();

                     }

                 }
             });

         }
        });



    }
}
