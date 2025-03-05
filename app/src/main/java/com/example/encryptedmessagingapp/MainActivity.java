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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private TextView userEmailText;
    private Button newChatButton, logoutButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
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

        // RecyclerView setup
        chatAdapter = new ChatAdapter(this, messageList, currentUser.getEmail());
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

            // ✅ Check Firestore instead of FirebaseAuth
            db.collection("users").document(chatPartnerEmail).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // ✅ User exists, open chat
                            openChatWindow(chatPartnerEmail);
                        } else {
                            // ❌ User is not registered
                            Toast.makeText(MainActivity.this, "User not registered!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error checking user", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    private void loadChats() {
        db.collection("chatList")
                .whereArrayContains("users", currentUser.getEmail())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) return;

                    messageList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String chatPartner = doc.getString("user1").equals(currentUser.getEmail())
                                ? doc.getString("user2")
                                : doc.getString("user1");

                        String lastMessage = doc.getString("lastMessage");

                        Message chatPreview = new Message(currentUser.getEmail(), chatPartner, lastMessage, doc.getLong("timestamp"));
                        messageList.add(chatPreview);
                    }
                    chatAdapter.notifyDataSetChanged();
                });
    }


    private void openChatWindow(String recipientEmail) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("recipientEmail", recipientEmail);
        startActivity(intent);
    }

    // 🔹 Delete a chat when long-pressed
    public void deleteChat(String recipientEmail) {
        if (currentUser == null) return;

        String chatId1 = currentUser.getEmail() + "_" + recipientEmail;
        String chatId2 = recipientEmail + "_" + currentUser.getEmail();
        String chatId = chatId1.compareTo(chatId2) < 0 ? chatId1 : chatId2;

        // ✅ Delete the chat from Firestore
        db.collection("chatList").document(chatId).delete();
        db.collection("chats").document(chatId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Chat deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error deleting chat", Toast.LENGTH_SHORT).show());

        // ✅ Remove the chat from the RecyclerView
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getReceiver().equals(recipientEmail)) {
                messageList.remove(i);
                chatAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

}
