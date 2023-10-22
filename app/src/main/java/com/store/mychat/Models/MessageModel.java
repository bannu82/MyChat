package com.store.mychat.Models;

public class MessageModel {
    private String messageId;
    private String senderId ,receiverId;
    private String message;
    private long timestamp;
    private  String messageType;
    private String url;
//    private boolean isSeen;

    public MessageModel() {
        // Default constructor required for Firebase
    }

    public MessageModel(String messageId, String senderId, String message, long timestamp , String receiverId ) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.receiverId = receiverId;
    }

    public MessageModel(String messageId, String senderId, String message, long timestamp , String receiverId , String url){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.receiverId = receiverId;
        this.url = url;
    }


    // Getters and setters for the fields

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
//    public boolean isSeen() {
//        return isSeen;
//    }
//
//    public void setSeen(boolean seen) {
//        isSeen = seen;
//    }
}
