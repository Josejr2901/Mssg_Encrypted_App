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
import com.example.encryptedmessagingapp.utils.EncryptionUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;
import javax.crypto.SecretKey;

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

    private SecretKey currentAESKey;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        recipientEmail = getIntent().getStringExtra("recipientEmail");

        TextView recipientEmailText = findViewById(R.id.recipientEmailText);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(false); // disable until AES key is ready


        recipientEmailText.setText("Chatting with: " + recipientEmail);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(this, messageList, currentUser.getEmail());
        chatRecyclerView.setAdapter(chatAdapter);

        // Generate consistent chatId
        String chatId1 = currentUser.getEmail() + "_" + recipientEmail;
        String chatId2 = recipientEmail + "_" + currentUser.getEmail();
        chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

        initializeAESKey(); // 🔐 Load or create encryption key

        sendButton.setEnabled(true); // ✅ Ready to send
        sendButton.setOnClickListener(view -> sendMessage());

    }

private void initializeAESKey() {
    db.collection("keys").document(chatId).get().addOnSuccessListener(snapshot -> {
        if (snapshot.exists()) {
            String encodedKey = snapshot.getString("aesKey");
            currentAESKey = EncryptionUtils.decodeKey(encodedKey);
            Log.d("KeyDebug", "AES key loaded from Firestore");
        } else {
            try {
                currentAESKey = EncryptionUtils.generateAESKey();
                String encodedKey = EncryptionUtils.encodeKey(currentAESKey);
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("aesKey", encodedKey);
                db.collection("keys").document(chatId).set(keyMap);
                Log.d("KeyDebug", "New AES key generated and stored");
            } catch (Exception e) {
                Log.e("KeyError", "Failed to generate AES key", e);
                return;
            }
        }

        sendButton.setEnabled(true);
        sendButton.setOnClickListener(view -> sendMessage());
        loadChatMessages();
    });
}


    private void loadChatMessages() {
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error loading messages: " + error.getMessage());
                        return;
                    }

                    boolean isFirstLoad = messageList.isEmpty();
                    HashSet<String> existingMessageIds = new HashSet<>();
                    for (Message msg : messageList) {
                        existingMessageIds.add(msg.getTimestamp() + "_" + msg.getSender());
                    }

                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Message message = dc.getDocument().toObject(Message.class);
                            String messageKey = message.getTimestamp() + "_" + message.getSender();

                            if (existingMessageIds.contains(messageKey)) continue;

                            List<String> deletedBy = (List<String>) dc.getDocument().get("deletedBy");
                            if (deletedBy != null && deletedBy.contains(currentUser.getEmail())) continue;

                            try {
                                String decrypted = EncryptionUtils.decrypt(message.getMessage(), currentAESKey);
                                message.setMessage(decrypted);
                            } catch (Exception e) {
                                message.setMessage("[Encrypted]");
                                Log.e("DecryptError", "Failed to decrypt message", e);
                            }

                            messageList.add(message);
                        }
                    }

                    chatAdapter.notifyDataSetChanged();
                    if (isFirstLoad || queryDocumentSnapshots.getDocumentChanges().size() > 0) {
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        Log.d("SendDebug", "Send button clicked");

        String plainText = messageInput.getText().toString().trim();
        if (plainText.isEmpty()) return;

        // ✅ Check if AES key is ready
        if (currentAESKey == null) {
            Log.e("SendDebug", "AES key is STILL null at time of sending");
            Toast.makeText(this, "Key not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderEmail = currentUser.getEmail();
        String receiverEmail = recipientEmail;

        DocumentReference messageRef = db.collection("chats").document(chatId)
                .collection("messages").document();

        String messageId = messageRef.getId();
        long timestamp = System.currentTimeMillis();

        String encryptedMessage;
        try {
            encryptedMessage = EncryptionUtils.encrypt(plainText, currentAESKey);
        } catch (Exception e) {
            Log.e("EncryptError", "Encryption failed", e);
            Toast.makeText(this, "Encryption failed", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, Object> messageData = new HashMap<>();
        messageData.put("messageId", messageId);
        messageData.put("sender", senderEmail);
        messageData.put("receiver", receiverEmail);
        messageData.put("message", encryptedMessage);
        messageData.put("timestamp", timestamp);
        messageData.put("deletedBy", new ArrayList<String>());

        messageList.add(new Message(messageId, senderEmail, receiverEmail, plainText, timestamp, new ArrayList<>()));
        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
        messageInput.setText("");

        messageRef.set(messageData)
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error sending message: " + e.getMessage());
                    Toast.makeText(ChatActivity.this, "Error sending message", Toast.LENGTH_LONG).show();
                });

        updateChatList(chatId, senderEmail, receiverEmail, encryptedMessage);
        updateChatList(chatId, receiverEmail, senderEmail, encryptedMessage);

    }

    private void updateChatList(String chatId, String user1, String user2, String lastMessage) {
        Map<String, Object> chatListData = new HashMap<>();
        chatListData.put("users", Arrays.asList(user1, user2));
        chatListData.put("user1", user1);
        chatListData.put("user2", user2);
        chatListData.put("lastMessage", lastMessage);
        chatListData.put("timestamp", System.currentTimeMillis());

        db.collection("chatList").document(chatId)
                .set(chatListData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "ChatList updated"))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to update chatList", e));
    }

    public void deleteMessage(Message message) {
        db.collection("chats").document(chatId)
                .collection("messages").document(message.getMessageId())
                .update("message", "[Message deleted]", "icon", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Message deleted");
                    Toast.makeText(ChatActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error deleting message", e));
    }
}
