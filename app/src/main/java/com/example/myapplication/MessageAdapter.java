package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;



    public class MessageAdapter extends RecyclerView.Adapter<com.example.myapplication.MessageAdapter.MyViewHolder> {

        private Context context;

        public MessageAdapter(Context context) {
            this.context = context;
            this.messageModelList = new ArrayList<>();
        }

        private static final int VIEW_TYPE_SENT = 0;
        private static final int VIEW_TYPE_RECEIVED = 1;

        public void add(MessageModel messageModel) {
            messageModelList.add(messageModel);
            notifyDataSetChanged();
        }

        public void clear() {
            messageModelList.clear();
            notifyDataSetChanged();
        }

        private List<MessageModel> messageModelList;


        @NonNull
        @Override
        public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
             View view;
            if (viewType == VIEW_TYPE_SENT) {
                 view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_sent, parent, false);

            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_received, parent, false);

            }
            return new MyViewHolder(view,viewType);


            // View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row,parent,false);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
            MessageModel messageModel = messageModelList.get(position);
            if (messageModel.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                if (holder.textViewSentMessage != null) {  // ✅ Prevents NullPointerException
                    holder.textViewSentMessage.setText(messageModel.getMessage());
                    holder.textViewSentMessage.setVisibility(View.VISIBLE);
                }
                if (holder.textViewReceivedMessage != null) {
                    holder.textViewReceivedMessage.setVisibility(View.GONE);
                }
            } else {
                if (holder.textViewReceivedMessage != null) {  // ✅ Prevents NullPointerException
                    holder.textViewReceivedMessage.setText(messageModel.getMessage());
                    holder.textViewReceivedMessage.setVisibility(View.VISIBLE);
                }
                if (holder.textViewSentMessage != null) {
                    holder.textViewSentMessage.setVisibility(View.GONE);
                }

            }
        }

        @Override
        public int getItemCount() {
            return messageModelList.size();
        }

        public List<MessageModel> getMessageModelList() {

            return messageModelList;
        }


        @Override
        public int getItemViewType(int position) {

            if (messageModelList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                return VIEW_TYPE_SENT;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewSentMessage, textViewReceivedMessage;


            public MyViewHolder(@NonNull View itemView, int viewType) {
                super(itemView);

                textViewSentMessage = itemView.findViewById(R.id.textViewSendMessage);
                textViewReceivedMessage = itemView.findViewById(R.id.textViewReceivedMessage);
                if (viewType == MessageAdapter.VIEW_TYPE_SENT) {
                    textViewSentMessage = itemView.findViewById(R.id.textViewSendMessage);
                } else {
                    textViewReceivedMessage = itemView.findViewById(R.id.textViewReceivedMessage);
                }
                if (textViewSentMessage == null && textViewReceivedMessage == null) {
                    Log.e("MessageAdapter", "Error: Both TextViews are null!");
                }
            }
        }
    }

