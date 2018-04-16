package com.caoyi.pinme;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caoyi.pinme.models.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUserReference;
    FirebaseRecyclerAdapter<Users, UsersViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserReference.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query usersQuery = mUserReference.orderByKey();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(usersQuery, Users.class).build();


        adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());

                final String uid = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("uid", uid);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new UsersViewHolder(view);
            }
        };

        mUsersList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {

            TextView mUserName = mView.findViewById(R.id.users_single_name);
            mUserName.setText(name);

        }

        public void setStatus(String status) {
            TextView mUserStatus = mView.findViewById(R.id.users_single_status);
            mUserStatus.setText(status);
        }

        public void setImage(String image) {
            CircleImageView mUserImage = mView.findViewById(R.id.users_single_image);
            Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mUserImage);
        }
    }


}


