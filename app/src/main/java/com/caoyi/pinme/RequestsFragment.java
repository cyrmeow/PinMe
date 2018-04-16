package com.caoyi.pinme;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caoyi.pinme.models.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    View mFragmentView;
    RecyclerView mRequestsList;

    FirebaseRecyclerAdapter<Users, RequestViewHolder> mRecyclerViewAdapter;

    DatabaseReference mRequestReference;
    DatabaseReference mUsersReference;
    FirebaseUser mCurrentUser;




    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestsList = mFragmentView.findViewById(R.id.requests_list);
        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUser.getUid());
        mRequestReference.keepSynced(true);
        mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersReference.keepSynced(true);

        return mFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mRequestReference.orderByKey();
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();

        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<Users, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Users model) {

                final String uid = getRef(position).getKey();

                mUsersReference.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        holder.setName(name);
                        holder.setStatus(status);
                        holder.setImage(image);

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("uid", uid);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new RequestViewHolder(view);
            }
        };

        mRequestsList.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRecyclerViewAdapter.stopListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.users_single_name);
            userName.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatus = mView.findViewById(R.id.users_single_status);
            userStatus.setText(status);
        }

        public void setImage(String image) {
            CircleImageView userImage = mView.findViewById(R.id.users_single_image);
            Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(userImage);
        }
    }

}
