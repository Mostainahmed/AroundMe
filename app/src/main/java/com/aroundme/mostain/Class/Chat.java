package com.aroundme.mostain.Class;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Chat {
    private String chatId;
    private String sender;
    private String receiver;
    private String senderUid;
    private String receiverUid;
    private String message;
    private String fileurl;
    private String imageUrl;
    private long timestamp;
    private boolean isread;
    private int type;

    public Chat() {
    }

    // Text Message
    public Chat(int type, String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp, boolean isread
           // , String chatId
    ) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
        this.isread = isread;
        //this.chatId = chatId;
    }

    // Audio Message
    public Chat(int type, String sender, String receiver, String senderUid, String receiverUid, long timestamp, boolean isread, String fileurl
           // , String chatId
    ) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.fileurl = fileurl;
        this.timestamp = timestamp;
        this.isread = isread;
        //this.chatId = chatId;
    }

    // Audio Message
    public Chat(int type, String sender, String receiver, String senderUid, String receiverUid, long timestamp, String imageUrl , boolean isread
           // , String chatId
    ) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.isread = isread;
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
