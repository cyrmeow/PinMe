package com.caoyi.pinme;


import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caoyi.pinme.models.Chats;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
public class ChatsFragment extends Fragment {

    View mFragmentView;
    RecyclerView mChatsList;
    FirebaseRecyclerAdapter<Chats, ChatsViewHolder> mAdapter;

    FirebaseUser mCurrentUser;
    DatabaseReference mMessagesReference;
    DatabaseReference mChatsReference;
    DatabaseReference mUsersReference;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_chats, container, false);

        mChatsList = mFragmentView.findViewById(R.id.chats_list);
        mChatsList.setHasFixedSize(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mChatsReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrentUser.getUid());
        mChatsReference.keepSynced(true);
        mMessagesReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        mMessagesReference.keepSynced(true);
        mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersReference.keepSynced(true);

        return mFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mChatsReference.orderByChild("timestamp");
        FirebaseRecyclerOptions<Chats> options = new FirebaseRecyclerOptions.Builder<Chats>().setQuery(query, Chats.class).build();
        mAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Chats model) {
                final String uid = getRef(position).getKey();

                mMessagesReference.child(mCurrentUser.getUid()).child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Query lastMsgQuery = mMessagesReference.child(mCurrentUser.getUid()).child(uid).limitToLast(1);
//                        Log.i("LAST_QUERY", lastMsgQuery.toString());

                        lastMsgQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                String msg = dataSnapshot.child("message").getValue().toString();
                                holder.setMessage(msg);

                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mUsersReference.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        holder.setName(name);
                        holder.setImage(image);

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("uid", uid);
                                chatIntent.putExtra("username", name);
                                startActivity(chatIntent);

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
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);

                return new ChatsViewHolder(view);
            }
        };

        mChatsList.setAdapter(mAdapter);
        mAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.users_single_name);
            userName.setText(name);
        }

        public void setMessage(String message) {
            TextView userStatus = mView.findViewById(R.id.users_single_status);
            userStatus.setText(message);
        }

        public void setImage(String image) {
            CircleImageView userImage = mView.findViewById(R.id.users_single_image);
            Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(userImage);
        }
    }
}
