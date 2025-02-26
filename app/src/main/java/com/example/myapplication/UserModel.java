package com.example.myapplication;

public class UserModel {
    String userID;
    String UserEmail;
    String UserName;
    String UserPassword;

    public UserModel(String userID, String userEmail, String userName, String userPassword) {
        this.userID = userID;
        UserEmail = userEmail;
        UserName = userName;
        UserPassword = userPassword;
    }

    public UserModel() {

    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public String getUserName() {
        return UserName;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }
}
