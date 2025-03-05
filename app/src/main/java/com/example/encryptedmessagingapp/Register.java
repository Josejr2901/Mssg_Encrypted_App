//package com.example.encryptedmessagingapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class Register extends AppCompatActivity {
//
//    private TextInputEditText editTextEmail, editTextPassword;
//    private Button buttonRegister;
//    private FirebaseAuth mAuth;
//    private ProgressBar progressBar;
//    private TextView textViewLogin;
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is already logged in
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            navigateToMain();
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        // UI References
//        editTextEmail = findViewById(R.id.email);
//        editTextPassword = findViewById(R.id.password);
//        buttonRegister = findViewById(R.id.button_register);
//        progressBar = findViewById(R.id.progressBar);
//        textViewLogin = findViewById(R.id.loginNow);
//
//        // Navigate to Login Page
//        textViewLogin.setOnClickListener(view -> {
//            startActivity(new Intent(Register.this, Login.class));
//            finish();
//        });
//
//        // Handle Register Button Click
//        buttonRegister.setOnClickListener(view -> registerUser());
//    }
//
//    // 🔹 Register Function
//    private void registerUser() {
//        String email = editTextEmail.getText().toString().trim();
//        String password = editTextPassword.getText().toString().trim();
//
//        // Input Validations
//        if (TextUtils.isEmpty(email)) {
//            editTextEmail.setError("Enter your email");
//            editTextEmail.requestFocus();
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            editTextPassword.setError("Enter your password");
//            editTextPassword.requestFocus();
//            return;
//        }
//        if (password.length() < 6) {
//            editTextPassword.setError("Password must be at least 6 characters");
//            editTextPassword.requestFocus();
//            return;
//        }
//
//        // Show Progress Bar
//        progressBar.setVisibility(View.VISIBLE);
//
//        // Firebase Registration
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    progressBar.setVisibility(View.GONE);
//                    if (task.isSuccessful()) {
//                        Toast.makeText(Register.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
//                        navigateToMain();
//                    } else {
//                        Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//    // 🔹 Navigate to Main Activity
//    private void navigateToMain() {
//        Intent intent = new Intent(Register.this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//}


package com.example.encryptedmessagingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView textViewLogin;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI References
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonRegister = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.loginNow);

        textViewLogin.setOnClickListener(view -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });

        buttonRegister.setOnClickListener(view -> registerUser());
    }

//    private void registerUser() {
//        String email = editTextEmail.getText().toString().trim();
//        String password = editTextPassword.getText().toString().trim();
//
//        if (TextUtils.isEmpty(email)) {
//            editTextEmail.setError("Enter your email");
//            editTextEmail.requestFocus();
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            editTextPassword.setError("Enter your password");
//            editTextPassword.requestFocus();
//            return;
//        }
//        if (password.length() < 6) {
//            editTextPassword.setError("Password must be at least 6 characters");
//            editTextPassword.requestFocus();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        // Register User in Firebase Authentication
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    progressBar.setVisibility(View.GONE);
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        if (user != null) {
//                            saveUserToFirestore(user);
//                        }
//                    } else {
//                        Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//    private void saveUserToFirestore(FirebaseUser user) {
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("email", user.getEmail());
//        userData.put("uid", user.getUid());
//
//        db.collection("users").document(user.getEmail())
//                .set(userData)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(Register.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
//                    navigateToMain();
//                })
//                .addOnFailureListener(e -> Toast.makeText(Register.this, "Failed to save user!", Toast.LENGTH_SHORT).show());
//    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Enter your email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter your password");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user);
                        }
                    } else {
                        Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ✅ Save User in Firestore After Registration
    private void saveUserToFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("uid", user.getUid());

        FirebaseFirestore.getInstance().collection("users")
                .document(user.getEmail())  // Store users with email as the document ID
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Register.this, "User added to Firestore!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Register.this, "Failed to save user!", Toast.LENGTH_SHORT).show());
    }


    private void navigateToMain() {
        Intent intent = new Intent(Register.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
