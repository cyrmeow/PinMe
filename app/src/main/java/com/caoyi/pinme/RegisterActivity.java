package com.caoyi.pinme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private TextInputLayout mName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateButton;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    // Database
    private DatabaseReference mDatabase;

    // Progress Dialog
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.reg_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        mName = findViewById(R.id.reg_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateButton = findViewById(R.id.reg_create_button);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    mRegProgress.setTitle("Welcome to PinMe, " + name + "!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    createAccount(name, email, password);

                }


            }
        });


    }

    private void createAccount(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("name", name);
                            map.put("status", "I love PinMe");
                            map.put("image", "default");
//                            map.put("image_thumbnail", "default_thumbnail");

                            mDatabase.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Log.d(TAG, "createUserWithEmail:success");
                                    mRegProgress.dismiss();

                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });





                        } else {

                            mRegProgress.hide();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }
}
