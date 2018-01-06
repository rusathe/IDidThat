package edu.cmu.sv.app17.models;


public class UserHistory {
    String id = null;
    String userName;
    String latest;
    String second;
    String third;
    public UserHistory(String userName,String latest, String second, String third) {
        this.userName = userName;
        this.latest = latest;
        this.second = second;
        this.third = third;
    }
    public void setId(String id) {
        this.id = id;
    }
}
