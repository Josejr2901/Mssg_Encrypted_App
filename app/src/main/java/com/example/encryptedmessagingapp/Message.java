package com.example.encryptedmessagingapp.models;

import java.util.List;

public class Message {
    private String messageId; // Unique Firestore ID for the message
    private String sender;
    private String receiver;
    private String message;
    private long timestamp;
    private List<String> deletedBy; // Tracks users who deleted the message

    // Default constructor (needed for Firebase)
    public Message() {}

    // Constructor for creating a new message
    public Message(String messageId, String sender, String receiver, String message, long timestamp, List<String> deletedBy) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.deletedBy = deletedBy;
    }

    // Getter and Setter for messageId
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    // Getter and Setter for sender
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // Getter and Setter for receiver
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and Setter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getter and Setter for deletedBy list
    public List<String> getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(List<String> deletedBy) {
        this.deletedBy = deletedBy;
    }
}
