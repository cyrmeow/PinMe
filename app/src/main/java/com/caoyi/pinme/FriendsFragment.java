package com.caoyi.pinme;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caoyi.pinme.models.Friends;
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
public class FriendsFragment extends Fragment {

    private FirebaseRecyclerAdapter<Friends, FriendsViewHolder> mAdapter;
    private RecyclerView mFriendsList;

    private View mFragmentView;

    private DatabaseReference mFriendsRef;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsersRef;



    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mFragmentView.findViewById(R.id.friends_list);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser.getUid());
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return mFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mFriendsRef.orderByKey();
        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();


        mAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {


                final String friendId = getRef(position).getKey();

                mUsersRef.child(friendId).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        holder.setName(name);
                        holder.setStatus(status);
                        holder.setImage(image);

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Event for each item.
                                        if(i == 0){

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("uid", friendId);
                                            startActivity(profileIntent);

                                        }

                                        if(i == 1){

                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("uid", friendId);
                                            chatIntent.putExtra("username", name);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });

                                builder.show();
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
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new FriendsViewHolder(view);
            }
        };

        mFriendsList.setAdapter(mAdapter);
        mAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
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
