package com.caoyi.pinme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URL;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;

    // Settings UI
    CircleImageView mImage;
    TextView mName;
    TextView mStatus;
    Button mStatusButton;
    Button mImageButton;

    // Firebase Storage
    private StorageReference mImageStorage;

    // Progress Dialog
    private ProgressDialog mProgress;

    private final int GALLERY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImage = findViewById(R.id.settings_image);
        mName = findViewById(R.id.settings_name);
        mStatus = findViewById(R.id.settings_status);
        mStatusButton = findViewById(R.id.settings_status_button);
        mImageButton = findViewById(R.id.settings_image_button);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("old_status", mStatus.getText().toString());
                startActivity(statusIntent);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            // Get the cropped image
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                // Show progress dialog
                mProgress = new ProgressDialog(SettingsActivity.this);
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setTitle("Uploading Profile Image...");
                mProgress.show();
                Uri resultUri = result.getUri();
                String fileName = mCurrentUser.getUid() + ".jpg";
                StorageReference filepath = mImageStorage.child("profile_images").child(fileName);

                // Upload image file to Firebase storage
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            String downloadPath = task.getResult().getDownloadUrl().toString();
                            mUserDatabase.child("image").setValue(downloadPath).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        mProgress.dismiss();
                                        Toast.makeText(SettingsActivity.this, "successfully uploaded profile image!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            mProgress.dismiss();
                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
