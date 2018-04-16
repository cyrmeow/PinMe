package com.caoyi.pinme;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.caoyi.pinme.adapters.MessageAdapter;
import com.caoyi.pinme.models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mUsersReference;
    private DatabaseReference mChatsReference;
    private DatabaseReference mMessageReference;
    private FirebaseUser mCurrentUser;
    private String mWithUserId;
    private String mWithUserName;

    private TextView mChatInput;
    private ImageButton mMoreButton;
    private ImageButton mSendButton;
    private RecyclerView mMessagesList;

    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mWithUserId = getIntent().getStringExtra("uid");
        mWithUserName = getIntent().getStringExtra("username");


        mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mWithUserName);

        mChatInput = findViewById(R.id.chat_input_text);
        mMoreButton = findViewById(R.id.chat_more_button);
        mSendButton = findViewById(R.id.chat_send_button);

        mAdapter = new MessageAdapter(messageList);

        mMessagesList = findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter );
        




        mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersReference.keepSynced(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mChatsReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        mChatsReference.keepSynced(true);
        // create entry in chat table if not exist
        mChatsReference.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mWithUserId)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put(mCurrentUser.getUid() + "/" + mWithUserId, chatAddMap);
                    chatUserMap.put(mWithUserId + "/" + mCurrentUser.getUid(), chatAddMap);

                    mChatsReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {

                                Log.d("CHAT_LOG", databaseError.getMessage());

                            }
                        }
                    });

                } else {


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mMessageReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        mMessageReference.keepSynced(true);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();

            }

        });

        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CharSequence[] options = new CharSequence[]{"send image", "send location"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                return;
                            case 1:
                                Intent mapsIntent = new Intent(ChatActivity.this, LocationSelectActivity.class);
                                mapsIntent.putExtra("current_uid", mCurrentUser.getUid());
                                mapsIntent.putExtra("with_uid", mWithUserId);
                                startActivity(mapsIntent);
                                return;
                            default:
                                return;
                        }
                    }
                });
                builder.show();*/

                Intent mapsIntent = new Intent(ChatActivity.this, LocationSelectActivity.class);
                mapsIntent.putExtra("current_uid", mCurrentUser.getUid());
                mapsIntent.putExtra("with_uid", mWithUserId);
                startActivity(mapsIntent);
            }
        });

        loadMessages();

    }

    private void loadMessages() {

        DatabaseReference conversationRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrentUser.getUid()).child(mWithUserId);
        conversationRef.keepSynced(true);
        conversationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                messageList.add(message);
                mAdapter.notifyDataSetChanged();

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

    private void sendMessage() {

        String msg = mChatInput.getText().toString();

        if (!TextUtils.isEmpty(msg)) {


            String currentUserRef = mCurrentUser.getUid() + "/" + mWithUserId;
            String withUserRef = mWithUserId + "/" + mCurrentUser.getUid();

            Map msgMap = new HashMap();
            msgMap.put("message", msg);
            msgMap.put("type", "text");
            msgMap.put("timestamp", ServerValue.TIMESTAMP);
            msgMap.put("from", mCurrentUser.getUid());


            String pushId = FirebaseDatabase.getInstance().getReference().child("Messages")
                    .child(mCurrentUser.getUid()).child(mWithUserId).push().getKey();



            Map msgUserMap = new HashMap();
            msgUserMap.put(currentUserRef + "/" + pushId, msgMap);
            msgUserMap.put(withUserRef + "/" + pushId, msgMap);

            mChatInput.setText("");


            mMessageReference.updateChildren(msgUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("MESSAGE_LOG", databaseError.getMessage());
                    }
                }
            });

        }

    }
}
