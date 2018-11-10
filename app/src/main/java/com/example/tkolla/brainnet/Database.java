package com.example.tkolla.brainnet;

public class Database {

    private String UserName;
    private String UserId;

    public Database() {

    }

    public Database(String UserId, String UserName){
        this.UserName = UserName;
        this.UserId = UserId;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserName() {
        return UserName;
    }
}
