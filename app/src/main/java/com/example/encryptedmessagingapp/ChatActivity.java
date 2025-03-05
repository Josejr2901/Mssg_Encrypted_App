//package com.example.encryptedmessagingapp;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.encryptedmessagingapp.adapters.ChatAdapter;
//import com.example.encryptedmessagingapp.models.Message;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ChatActivity extends AppCompatActivity {
//
//    private FirebaseAuth mAuth;
//    private FirebaseUser currentUser;
//    private FirebaseFirestore db;
//    private String recipientEmail;
//    private RecyclerView chatRecyclerView;
//    private ChatAdapter chatAdapter;
//    private ArrayList<Message> messageList = new ArrayList<>();
//    private EditText messageInput;
//    private Button sendButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//        // Initialize Firebase
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//        db = FirebaseFirestore.getInstance();
//
//        // Get recipient email from intent
//        recipientEmail = getIntent().getStringExtra("recipientEmail");
//
//        // UI Elements
//        chatRecyclerView = findViewById(R.id.chatRecyclerView);
//        messageInput = findViewById(R.id.messageInput);
//        sendButton = findViewById(R.id.sendButton);
//
//        // RecyclerView setup
//        chatAdapter = new ChatAdapter(this, messageList, currentUser.getEmail());
//        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        chatRecyclerView.setAdapter(chatAdapter);
//
//        // Load messages from Firebase
//        loadChatMessages();
//
//        // Send message button
//        sendButton.setOnClickListener(view -> sendMessage());
//    }
//
//    // Load chat messages in real-time
//    private void loadChatMessages() {
//        CollectionReference chatRef = db.collection("chats")
//                .document(currentUser.getEmail() + "_" + recipientEmail)
//                .collection("messages");
//
//        chatRef.addSnapshotListener((queryDocumentSnapshots, error) -> {
//            if (error != null) return;
//            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                if (dc.getType() == DocumentChange.Type.ADDED) {
//                    Message message = dc.getDocument().toObject(Message.class);
//                    messageList.add(message);
//                }
//            }
//            chatAdapter.notifyDataSetChanged();
//            chatRecyclerView.scrollToPosition(messageList.size() - 1);
//        });
//    }
//
//    // Send a message
//    private void sendMessage() {
//        String text = messageInput.getText().toString().trim();
//        if (text.isEmpty()) return;
//
//        String senderEmail = currentUser.getEmail();
//        String receiverEmail = recipientEmail;
//
//        // ✅ Ensure chat ID is the same for both users
//        String chatId1 = senderEmail + "_" + receiverEmail;
//        String chatId2 = receiverEmail + "_" + senderEmail;
//        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;
//
//        Map<String, Object> message = new HashMap<>();
//        message.put("sender", senderEmail);
//        message.put("receiver", receiverEmail);
//        message.put("message", text);
//        message.put("timestamp", System.currentTimeMillis());
//
//        // ✅ Debugging: Print chatId to console
//        Log.d("FirestoreDebug", "Attempting to send message to chatId: " + chatId);
//
//        // ✅ Store message in Firestore
//        db.collection("chats")
//                .document(chatId)
//                .collection("messages")
//                .add(message)
//                .addOnSuccessListener(documentReference -> {
//                    messageInput.setText("");  // Clear input field after sending
//                    Toast.makeText(ChatActivity.this, "Message Sent!", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("FirestoreError", "Error sending message: " + e.getMessage());
//                    Toast.makeText(ChatActivity.this, "Error sending message: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//
//        // ✅ Store chat metadata in "chatList" for easy access
//        Map<String, Object> chatInfo = new HashMap<>();
//        chatInfo.put("chatId", chatId);
//        chatInfo.put("user1", senderEmail);
//        chatInfo.put("user2", receiverEmail);
//        chatInfo.put("lastMessage", text);
//        chatInfo.put("timestamp", System.currentTimeMillis());
//
//        db.collection("chatList").document(chatId).set(chatInfo);
//    }
//
//
//}


package com.example.encryptedmessagingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptedmessagingapp.adapters.ChatAdapter;
import com.example.encryptedmessagingapp.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String recipientEmail;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();
    private EditText messageInput;
    private Button sendButton;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Get recipient email from intent
        recipientEmail = getIntent().getStringExtra("recipientEmail");

        // UI Elements
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // RecyclerView setup
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // ✅ Ensures new messages appear at the bottom
        chatRecyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(this, messageList, currentUser.getEmail());
        chatRecyclerView.setAdapter(chatAdapter);

        // Load messages from Firebase
        loadChatMessages();

        // Send message button
        sendButton.setOnClickListener(view -> sendMessage());
    }

    // Load chat messages in real-time and auto-scroll to the latest message
    private void loadChatMessages() {
        String chatId1 = currentUser.getEmail() + "_" + recipientEmail;
        String chatId2 = recipientEmail + "_" + currentUser.getEmail();
        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

        CollectionReference chatRef = db.collection("chats")
                .document(chatId)
                .collection("messages");

        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error loading messages: " + error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Message message = dc.getDocument().toObject(Message.class);

                                // ✅ Avoid duplicate messages
                                if (!messageList.contains(message)) {
                                    messageList.add(message);
                                }
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1); // ✅ Auto-scroll to last message
                    }
                });
    }


    // Send a message
//    private void sendMessage() {
//        String text = messageInput.getText().toString().trim();
//        if (text.isEmpty()) return;
//
//        String senderEmail = currentUser.getEmail();
//        String receiverEmail = recipientEmail;
//
//        // ✅ Ensure chat ID is the same for both users
//        String chatId1 = senderEmail + "_" + receiverEmail;
//        String chatId2 = receiverEmail + "_" + senderEmail;
//        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;
//
//        Map<String, Object> message = new HashMap<>();
//        message.put("sender", senderEmail);
//        message.put("receiver", receiverEmail);
//        message.put("message", text);
//        message.put("timestamp", System.currentTimeMillis());
//
//        // ✅ Debugging: Print chatId to console
//        Log.d("FirestoreDebug", "Attempting to send message to chatId: " + chatId);
//
//        // ✅ Store message in Firestore
//        db.collection("chats")
//                .document(chatId)
//                .collection("messages")
//                .add(message)
//                .addOnSuccessListener(documentReference -> {
//                    messageInput.setText("");  // Clear input field after sending
//                    chatRecyclerView.scrollToPosition(messageList.size() - 1); // ✅ Auto-scroll after sending
//                    Toast.makeText(ChatActivity.this, "Message Sent!", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("FirestoreError", "Error sending message: " + e.getMessage());
//                    Toast.makeText(ChatActivity.this, "Error sending message: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//
//        // ✅ Store chat metadata in "chatList" for easy access
//        Map<String, Object> chatInfo = new HashMap<>();
//        chatInfo.put("chatId", chatId);
//        chatInfo.put("user1", senderEmail);
//        chatInfo.put("user2", receiverEmail);
//        chatInfo.put("lastMessage", text);
//        chatInfo.put("timestamp", System.currentTimeMillis());
//
//        db.collection("chatList").document(chatId).set(chatInfo);
//    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        String senderEmail = currentUser.getEmail();
        String receiverEmail = recipientEmail;

        // ✅ Ensure chat ID is the same for both users
        String chatId1 = senderEmail + "_" + receiverEmail;
        String chatId2 = receiverEmail + "_" + senderEmail;
        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

        Map<String, Object> message = new HashMap<>();
        message.put("sender", senderEmail);
        message.put("receiver", receiverEmail);
        message.put("message", text);
        message.put("timestamp", System.currentTimeMillis());

        // ✅ Instantly add message to UI before Firestore response
        Message newMessage = new Message(senderEmail, receiverEmail, text, System.currentTimeMillis());
        messageList.add(newMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    messageInput.setText("");  // Clear input field after sending
                    Log.d("FirestoreDebug", "Message sent successfully: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error sending message: " + e.getMessage());
                    Toast.makeText(ChatActivity.this, "Error sending message: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        // ✅ Store chat metadata in "chatList"
        Map<String, Object> chatInfo = new HashMap<>();
        chatInfo.put("chatId", chatId);
        chatInfo.put("user1", senderEmail);
        chatInfo.put("user2", receiverEmail);
        chatInfo.put("lastMessage", text);
        chatInfo.put("timestamp", System.currentTimeMillis());

        db.collection("chatList").document(chatId).set(chatInfo);
    }

}
