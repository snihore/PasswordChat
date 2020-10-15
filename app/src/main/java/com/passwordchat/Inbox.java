package com.passwordchat;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Inbox {

    private String title;
    private String url;
    private Bitmap image;

    private List<Message> messages;

    private long inboxTimestamp;

    public Inbox() {
    }

    public Inbox(String title) {
        this.title = title;
        this.messages = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message){
        if(message == null){
            return;
        }
        this.messages.add(message);
    }

    public long getInboxTimestamp() {
        return inboxTimestamp;
    }

    public void setInboxTimestamp(long inboxTimestamp) {
        this.inboxTimestamp = inboxTimestamp;
    }
}
