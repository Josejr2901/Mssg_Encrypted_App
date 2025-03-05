////package com.example.encryptedmessagingapp.adapters;
////
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.TextView;
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////import com.example.encryptedmessagingapp.R;
////import com.example.encryptedmessagingapp.models.Message;
////import java.util.ArrayList;
////import java.util.List;
////
////public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
////
////    private static final int VIEW_TYPE_SENT = 1;
////    private static final int VIEW_TYPE_RECEIVED = 2;
////
////    private List<Message> messageList;
////    private String currentUserEmail;
////
////    public ChatAdapter(List<Message> messageList, String currentUserEmail) {
////        this.messageList = messageList;
////        this.currentUserEmail = currentUserEmail;
////    }
////
////    @Override
////    public int getItemViewType(int position) {
////        Message message = messageList.get(position);
////        return message.getSender().equals(currentUserEmail) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
////    }
////
////    @NonNull
////    @Override
////    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view;
////        if (viewType == VIEW_TYPE_SENT) {
////            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
////            return new SentMessageViewHolder(view);
////        } else {
////            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
////            return new ReceivedMessageViewHolder(view);
////        }
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
////        Message chat = messageList.get(position);
////
////        holder.itemView.setOnLongClickListener(v -> {
////            new AlertDialog.Builder(holder.itemView.getContext())
////                    .setTitle("Delete Chat")
////                    .setMessage("Are you sure you want to delete this chat?")
////                    .setPositiveButton("Delete", (dialog, which) -> {
////                        if (context instanceof MainActivity) {
////                            ((MainActivity) context).deleteChat(chat.getReceiver());
////                        }
////                    })
////                    .setNegativeButton("Cancel", null)
////                    .show();
////            return true;
////        });
////
////        if (holder instanceof SentMessageViewHolder) {
////            ((SentMessageViewHolder) holder).bind(chat);
////        } else {
////            ((ReceivedMessageViewHolder) holder).bind(chat);
////        }
////    }
////
////
////    @Override
////    public int getItemCount() {
////        return messageList.size();
////    }
////
////    public void addMessage(Message message) {
////        messageList.add(message);
////        notifyItemInserted(messageList.size() - 1);
////    }
////
////    // ViewHolder for sent messages
////    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
////        TextView sentMessageText;
////
////        SentMessageViewHolder(View itemView) {
////            super(itemView);
////            sentMessageText = itemView.findViewById(R.id.sentMessageText);
////        }
////
////        void bind(Message message) {
////            sentMessageText.setText(message.getMessage());
////        }
////    }
////
////    // ViewHolder for received messages
////    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
////        TextView receivedMessageText;
////
////        ReceivedMessageViewHolder(View itemView) {
////            super(itemView);
////            receivedMessageText = itemView.findViewById(R.id.receivedMessageText);
////        }
////
////        void bind(Message message) {
////            receivedMessageText.setText(message.getMessage());
////        }
////    }
////}
//
//package com.example.encryptedmessagingapp.adapters;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.encryptedmessagingapp.MainActivity;
//import com.example.encryptedmessagingapp.R;
//import com.example.encryptedmessagingapp.models.Message;
//import java.util.List;
//
//public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static final int VIEW_TYPE_SENT = 1;
//    private static final int VIEW_TYPE_RECEIVED = 2;
//
//    private Context context;
//    private List<Message> messageList;
//    private String currentUserEmail;
//
//    public ChatAdapter(Context context, List<Message> messageList, String currentUserEmail) {
//        this.context = context;
//        this.messageList = messageList;
//        this.currentUserEmail = currentUserEmail;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Message message = messageList.get(position);
//        return message.getSender().equals(currentUserEmail) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == VIEW_TYPE_SENT) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
//            return new SentMessageViewHolder(view);
//        } else {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
//            return new ReceivedMessageViewHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Message message = messageList.get(position);
//
//        // Handle long press to delete chat
//        holder.itemView.setOnLongClickListener(v -> {
//            new AlertDialog.Builder(holder.itemView.getContext())
//                    .setTitle("Delete Chat")
//                    .setMessage("Are you sure you want to delete this chat?")
//                    .setPositiveButton("Delete", (dialog, which) -> {
//                        if (context instanceof MainActivity) {
//                            ((MainActivity) context).deleteChat(message.getReceiver());
//                        }
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .show();
//            return true;
//        });
//
//        if (holder instanceof SentMessageViewHolder) {
//            ((SentMessageViewHolder) holder).bind(message);
//        } else {
//            ((ReceivedMessageViewHolder) holder).bind(message);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return messageList.size();
//    }
//
//    public void addMessage(Message message) {
//        messageList.add(message);
//        notifyItemInserted(messageList.size() - 1);
//    }
//
//    // ViewHolder for sent messages
//    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
//        TextView sentMessageText;
//
//        SentMessageViewHolder(View itemView) {
//            super(itemView);
//            sentMessageText = itemView.findViewById(R.id.sentMessageText);
//        }
//
//        void bind(Message message) {
//            sentMessageText.setText(message.getMessage());
//        }
//    }
//
//    // ViewHolder for received messages
//    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
//        TextView receivedMessageText;
//
//        ReceivedMessageViewHolder(View itemView) {
//            super(itemView);
//            receivedMessageText = itemView.findViewById(R.id.receivedMessageText);
//        }
//
//        void bind(Message message) {
//            receivedMessageText.setText(message.getMessage());
//        }
//    }
//}


package com.example.encryptedmessagingapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.encryptedmessagingapp.MainActivity;
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

        // Handle long press to delete chat
        holder.itemView.setOnLongClickListener(v -> {
            if (context instanceof MainActivity) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Delete Chat")
                        .setMessage("Are you sure you want to delete this chat?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            ((MainActivity) context).deleteChat(message.getReceiver());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            }
            return false;
        });

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    // ViewHolder for sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessageText;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            sentMessageText = itemView.findViewById(R.id.sentMessageText);
        }

        void bind(Message message) {
            sentMessageText.setText(message.getMessage());
        }
    }

    // ViewHolder for received messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessageText;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            receivedMessageText = itemView.findViewById(R.id.receivedMessageText);
        }

        void bind(Message message) {
            receivedMessageText.setText(message.getMessage());
        }
    }
}
