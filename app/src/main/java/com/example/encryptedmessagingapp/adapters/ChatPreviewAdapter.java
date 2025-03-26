package com.example.encryptedmessagingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptedmessagingapp.ChatActivity;
import com.example.encryptedmessagingapp.MainActivity;
import com.example.encryptedmessagingapp.R;
import com.example.encryptedmessagingapp.models.Message;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ChatViewHolder> {
    private Context context;
    private ArrayList<Message> chatList;
    private String currentUserEmail;

    public ChatPreviewAdapter(Context context, ArrayList<Message> chatList, String currentUserEmail) {
        this.context = context;
        this.chatList = chatList;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_preview_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message chatPreview = chatList.get(position);

        holder.chatPartnerText.setText(chatPreview.getReceiver());
        holder.lastMessageText.setText(chatPreview.getMessage());

        // ✅ Only show timestamp if it's a valid time (> 0)
        if (chatPreview.getTimestamp() > 0) {
            holder.lastMessageTimestamp.setText(formatTimestamp(chatPreview.getTimestamp()));
        } else {
            holder.lastMessageTimestamp.setText(""); // If no timestamp, keep empty
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("recipientEmail", chatPreview.getReceiver());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).showDeleteConfirmationDialog(chatPreview.getReceiver());
            }
            return true;
        });

    }



    private String formatTimestamp(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatPartnerText, lastMessageText, lastMessageTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatPartnerText = itemView.findViewById(R.id.chatPartnerText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
            lastMessageTimestamp = itemView.findViewById(R.id.lastMessageTimestamp);
        }
    }

}
