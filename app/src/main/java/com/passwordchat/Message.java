package com.passwordchat;

public class Message {

    private String msg;
    private boolean isEncrypted;
    private long timestamp;

    public Message(String msg, long timestamp) {
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public void encrypt(String key) {

        this.isEncrypted = true;

        //Encrypt Code
        try {

            this.msg = Security.encrypt(this.msg, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void decrypt(String key){

        this.isEncrypted = false;

        //Decrypt Code
        try {
            this.msg = Security.decrypt(this.msg, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
