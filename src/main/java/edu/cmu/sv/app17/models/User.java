package edu.cmu.sv.app17.models;


public class User {
    String id = null;
    String userName;
    String emailAddress;
    String password;
    public User(String userName, String emailAddress, String password) {

        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
    }
    public void setId(String id) {
        this.id = id;
    }
}
