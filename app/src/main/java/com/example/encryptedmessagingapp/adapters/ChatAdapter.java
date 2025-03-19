package com.example.encryptedmessagingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.encryptedmessagingapp.ChatActivity;
import com.example.encryptedmessagingapp.R;
import com.example.encryptedmessagingapp.models.Message;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<Message> messageList;
    private String currentUserEmail;

    public ChatAdapter(Context context, List<Message> messageList, String currentUserEmail) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserEmail = currentUserEmail;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.getSender().equals(currentUserEmail) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            setMessageAppearance(sentHolder.sentMessageText, message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            setMessageAppearance(receivedHolder.receivedMessageText, message);
        }

        // ✅ Handle long-press for message deletion
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setMessage("Are you sure you want to delete this message for both users?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (context instanceof ChatActivity) {
                            ((ChatActivity) context).deleteMessage(message);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    // ✅ Method to handle message appearance (deleted message style)
    private void setMessageAppearance(TextView messageTextView, Message message) {
        if (message.getMessage() == null || message.getMessage().equals("[Message deleted]")) {
            messageTextView.setText("[Message deleted]");
            messageTextView.setTypeface(null, Typeface.ITALIC);
            messageTextView.setTextColor(Color.GRAY);
        } else {
            messageTextView.setText(message.getMessage());
            messageTextView.setTypeface(null, Typeface.NORMAL);
            //messageTextView.setTextColor(Color.BLACK);
        }
    }

    // ✅ ViewHolder for sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessageText;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            sentMessageText = itemView.findViewById(R.id.sentMessageText);
        }
    }

    // ✅ ViewHolder for received messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessageText;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            receivedMessageText = itemView.findViewById(R.id.receivedMessageText);
        }
    }
}
