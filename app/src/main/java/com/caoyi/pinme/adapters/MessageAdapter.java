package com.caoyi.pinme.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caoyi.pinme.ChatActivity;
import com.caoyi.pinme.R;
import com.caoyi.pinme.models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by A.C. on 3/24/18.
 */

//public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Messages> mMessagesList;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsersRef;

    public MessageAdapter(List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_self_layout, parent, false);
                return new MessageSelfViewHolder(v1);
            default:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
                return new MessageViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Messages msg = mMessagesList.get(position);
        String from = msg.getFrom();
        String message = msg.getMessage();

        if (holder.getItemViewType() == 0) {
            MessageViewHolder vh0 = (MessageViewHolder) holder;
            vh0.mMessageText.setText(message);
            Linkify.addLinks(vh0.mMessageText, Linkify.ALL);

        } else {
            MessageSelfViewHolder vh1 = (MessageSelfViewHolder) holder;
            vh1.mMessageText.setText(message);
            Linkify.addLinks(vh1.mMessageText, Linkify.ALL);
        }
    }


    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages msg = mMessagesList.get(position);
        if (msg.getFrom().equals(mCurrentUser.getUid())) {
            if (msg.getType().equals("text")) {
                return 1;
            } else if (msg.getType().equals("location")) {
                return 3;
            }
        } else {
            if (msg.getType().equals("text")) {
                return 0;
            } else if (msg.getType().equals("location")) {
                return 2;
            }
        }
        return 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView mMessageText;
        CircleImageView mProfileImage;


        public MessageViewHolder(View itemView) {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.message_text_layout);
            mProfileImage = itemView.findViewById(R.id.message_image_layout);
//            Linkify.addLinks(mMessageText, Linkify.ALL);
        }
    }

    public static class MessageSelfViewHolder extends RecyclerView.ViewHolder {

        TextView mMessageText;
        CircleImageView mProfileImage;


        public MessageSelfViewHolder(View itemView) {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.message_text_layout);
            mProfileImage = itemView.findViewById(R.id.message_image_layout);
//            Linkify.addLinks(mMessageText, Linkify.ALL);
        }
    }

    public static class MapSelfViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MapSelfViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
    }
}
