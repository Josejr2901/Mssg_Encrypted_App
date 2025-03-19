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
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        TextView recipientEmailText = findViewById(R.id.recipientEmailText);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Set recipient email in the header
        recipientEmailText.setText("Chatting with: " + recipientEmail);

        // RecyclerView setup
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(this, messageList, currentUser.getEmail());
        chatRecyclerView.setAdapter(chatAdapter);

        // Load messages from Firebase
        loadChatMessages();

        // Send message button
        sendButton.setOnClickListener(view -> sendMessage());
    }


//    // Load chat messages in real-time and auto-scroll to the latest message
//    private void loadChatMessages() {
//        String chatId1 = currentUser.getEmail() + "_" + recipientEmail;
//        String chatId2 = recipientEmail + "_" + currentUser.getEmail();
//        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;
//
//        CollectionReference chatRef = db.collection("chats")
//                .document(chatId)
//                .collection("messages");
//
//        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
//                .addSnapshotListener((queryDocumentSnapshots, error) -> {
//                    if (error != null) {
//                        Log.e("FirestoreError", "Error loading messages: " + error.getMessage());
//                        return;
//                    }
//
//                    boolean isFirstLoad = messageList.isEmpty();
//
//                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                        if (dc.getType() == DocumentChange.Type.ADDED) {
//                            Message message = dc.getDocument().toObject(Message.class);
//                            messageList.add(message);
//                        }
//                    }
//
//                    chatAdapter.notifyDataSetChanged();
//
//                    // Scroll to the latest message if it's the first load OR if a new message is received
//                    if (isFirstLoad || queryDocumentSnapshots.getDocumentChanges().size() > 0) {
//                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
//                    }
//                });
//    }

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

                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "No messages found.");
                        return;
                    }

                    boolean isFirstLoad = messageList.isEmpty();
                    HashSet<String> existingMessageIds = new HashSet<>();

                    // ✅ Store already loaded messages to prevent duplicates
                    for (Message msg : messageList) {
                        existingMessageIds.add(msg.getTimestamp() + "_" + msg.getSender());
                    }

                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Message message = dc.getDocument().toObject(Message.class);

                            // ✅ Skip messages that already exist
                            String messageKey = message.getTimestamp() + "_" + message.getSender();
                            if (existingMessageIds.contains(messageKey)) {
                                continue;
                            }

                            // ✅ Ignore messages that were deleted by this user
                            List<String> deletedBy = (List<String>) dc.getDocument().get("deletedBy");
                            if (deletedBy != null && deletedBy.contains(currentUser.getEmail())) {
                                continue;
                            }

                            messageList.add(message);
                            existingMessageIds.add(messageKey);
                        }
                    }

                    chatAdapter.notifyDataSetChanged();

                    // ✅ Auto-scroll only if it's the first load OR if a new message arrives
                    if (isFirstLoad || queryDocumentSnapshots.getDocumentChanges().size() > 0) {
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    public void deleteMessage(Message message) {
        String chatId1 = currentUser.getEmail() + "_" + recipientEmail;
        String chatId2 = recipientEmail + "_" + currentUser.getEmail();
        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

        db.collection("chats").document(chatId)
                .collection("messages").document(message.getMessageId())
                .update("message", "[Message deleted]", "icon", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Message deleted for both users");
                    Toast.makeText(ChatActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error deleting message", e));
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        String senderEmail = currentUser.getEmail();
        String receiverEmail = recipientEmail;

        // ✅ Ensure consistent chat ID for both users
        String chatId1 = senderEmail + "_" + receiverEmail;
        String chatId2 = receiverEmail + "_" + senderEmail;
        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

        // ✅ Generate Firestore document reference to get a unique message ID
        DocumentReference messageRef = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(); // Firestore generates a unique message ID

        String messageId = messageRef.getId(); // Retrieve Firestore-generated ID

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("messageId", messageId);
        messageData.put("sender", senderEmail);
        messageData.put("receiver", receiverEmail);
        messageData.put("message", text);
        messageData.put("timestamp", System.currentTimeMillis());
        messageData.put("deletedBy", new ArrayList<String>()); // ✅ Empty list for tracking deletions

        // ✅ Optimistically add message to UI
        messageList.add(new Message(messageId, senderEmail, receiverEmail, text, System.currentTimeMillis(), new ArrayList<>()));
        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
        messageInput.setText(""); // Clear input field

        // ✅ Store message in Firestore
        messageRef.set(messageData)
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error sending message: " + e.getMessage());
                    Toast.makeText(ChatActivity.this, "Error sending message", Toast.LENGTH_LONG).show();
                });

        // ✅ Update chat preview for both users
        updateChatList(chatId, senderEmail, receiverEmail, text);
        updateChatList(chatId, receiverEmail, senderEmail, text);
    }



    private void updateChatList(String chatId, String user1, String user2, String lastMessage) {
        Map<String, Object> chatListData = new HashMap<>();
        chatListData.put("users", new ArrayList<>(Arrays.asList(user1, user2)));
        chatListData.put("user1", user1);
        chatListData.put("user2", user2);
        chatListData.put("lastMessage", lastMessage);
        chatListData.put("timestamp", System.currentTimeMillis());

        db.collection("chatList").document(chatId)
                .set(chatListData, SetOptions.merge()) // ✅ Use merge to avoid overwriting existing data
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "ChatList updated for " + user1))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to update chatList for " + user1, e));
    }

}
