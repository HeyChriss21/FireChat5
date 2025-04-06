package com.catania.firechat5;

public class MessageModel {
    public MessageModel() {
    }
    private String senderId, receiverId, message;

    public MessageModel(String senderId, String receiverId, String message) {
        this.senderId   = senderId;
        this.receiverId = receiverId;
        this.message    = message;
    }


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
