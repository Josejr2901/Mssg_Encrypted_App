package com.example.encryptedmessagingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptedmessagingapp.adapters.ChatAdapter;
import com.example.encryptedmessagingapp.models.Message;
import com.example.encryptedmessagingapp.utils.EncryptionUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import android.util.Log;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.example.encryptedmessagingapp.adapters.ChatPreviewAdapter;

import javax.crypto.SecretKey;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView userEmailText;
    private Button newChatButton, logoutButton;
    private RecyclerView chatRecyclerView;
    private ChatPreviewAdapter chatAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();
    private String chatPartnerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // UI References
        userEmailText = findViewById(R.id.userEmail);
        newChatButton = findViewById(R.id.newChatButton);
        logoutButton = findViewById(R.id.logoutButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        if (currentUser != null) {
            userEmailText.setText("Logged in as: " + currentUser.getEmail());
        } else {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }

        // RecyclerView setup for chat previews
        chatAdapter = new ChatPreviewAdapter(this, messageList, currentUser.getEmail());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        loadChats();

        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        });

        newChatButton.setOnClickListener(view -> startNewChat());
    }


    private void startNewChat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter recipient's email");

        final EditText input = new EditText(this);
        input.setHint("Recipient's Email");
        builder.setView(input);

        builder.setPositiveButton("Start Chat", (dialog, which) -> {
            chatPartnerEmail = input.getText().toString().trim();

            if (chatPartnerEmail.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter a valid email!", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users").whereEqualTo("email", chatPartnerEmail).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // User exists, create or open chat
                            String chatId1 = currentUser.getEmail() + "_" + chatPartnerEmail;
                            String chatId2 = chatPartnerEmail + "_" + currentUser.getEmail();
                            String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

                            // Add chat to `chatList` so it appears in MainActivity
                            Map<String, Object> chatListData = new HashMap<>();
                            chatListData.put("users", new ArrayList<>(Arrays.asList(currentUser.getEmail(), chatPartnerEmail)));
                            chatListData.put("user1", currentUser.getEmail());
                            chatListData.put("user2", chatPartnerEmail);
                            chatListData.put("lastMessage", "");  // No messages yet
                            chatListData.put("timestamp", System.currentTimeMillis());

                            db.collection("chatList").document(chatId)
                                    .set(chatListData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Chat added to chatList successfully!");
                                        openChatWindow(chatPartnerEmail);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreError", "Failed to add chat to chatList", e);
                                        Toast.makeText(MainActivity.this, "Failed to start chat", Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            // User is not registered
                            Toast.makeText(MainActivity.this, "User not registered!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error checking user: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void loadChats() {
        db.collection("chatList")
                .whereArrayContains("users", currentUser.getEmail())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error loading chats", error);
                        return;
                    }

                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        Log.d("Firestore", "No chats found.");
                        messageList.clear();
                        chatAdapter.notifyDataSetChanged();
                        return;
                    }

                    messageList.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String chatPartner;

                        if (doc.contains("user1") && doc.contains("user2")) {
                            chatPartner = doc.getString("user1").equals(currentUser.getEmail())
                                    ? doc.getString("user2") : doc.getString("user1");
                        } else {
                            Log.e("FirestoreError", "Missing user1/user2 fields in document");
                            continue;
                        }

                        String lastMessageRaw = doc.contains("lastMessage") ? doc.getString("lastMessage") : "No messages yet";
                        Long timestamp = doc.contains("timestamp") ? doc.getLong("timestamp") : 0;

                        String chatId1 = currentUser.getEmail() + "_" + chatPartner;
                        String chatId2 = chatPartner + "_" + currentUser.getEmail();
                        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

                        db.collection("keys").document(chatId).get()
                                .addOnSuccessListener(keyDoc -> {
                                    String decryptedLastMessage = "[Encrypted]";

                                    if (keyDoc.exists()) {
                                        String encodedKey = keyDoc.getString("aesKey");
                                        try {
                                            SecretKey key = EncryptionUtils.decodeKey(encodedKey);
                                            decryptedLastMessage = EncryptionUtils.decrypt(lastMessageRaw, key);
                                        } catch (Exception e) {
                                            Log.e("PreviewDecrypt", "Failed to decrypt lastMessage", e);
                                        }
                                    }

                                    Message chatPreview = new Message(
                                            "", // messageId
                                            currentUser.getEmail(),
                                            chatPartner,
                                            decryptedLastMessage,
                                            timestamp,
                                            new ArrayList<>()
                                    );

                                    // ✅ Prevent duplicate chat previews
                                    boolean updated = false;
                                    for (int i = 0; i < messageList.size(); i++) {
                                        Message existing = messageList.get(i);
                                        if (existing.getReceiver().equals(chatPartner)) {
                                            messageList.set(i, chatPreview);
                                            updated = true;
                                            break;
                                        }
                                    }

                                    if (!updated) {
                                        messageList.add(chatPreview);
                                    }

                                    chatAdapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to fetch AES key", e));
                    }
                });
    }

    public void deleteChat(String recipientEmail) {
        if (currentUser == null) return;

        String chatId1 = currentUser.getEmail() + "_" + recipientEmail;
        String chatId2 = recipientEmail + "_" + currentUser.getEmail();
        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

        // Remove the chat from `chatList` for the current user
        db.collection("chatList").document(chatId)
                .update("users", FieldValue.arrayRemove(currentUser.getEmail()))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Chat removed for " + currentUser.getEmail()))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error deleting chat", e));

        // Mark all messages as deleted for the current user
        db.collection("chats").document(chatId).collection("messages")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().update("deletedBy", FieldValue.arrayUnion(currentUser.getEmail()));
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error marking messages as deleted", e));

        //  Refresh RecyclerView
        Toast.makeText(MainActivity.this, "Chat deleted", Toast.LENGTH_SHORT).show();
        loadChats();
    }



    private void openChatWindow(String recipientEmail) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("recipientEmail", recipientEmail);
        startActivity(intent);
    }

}
