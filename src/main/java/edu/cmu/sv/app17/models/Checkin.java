package edu.cmu.sv.app17.models;

public class Checkin {
    String id = null;
    String placeID;
    String userID;
    String placeName;
    Boolean isExisting;
    Integer rating;

    public Checkin(String placeID, String userID, String placeName, Boolean isExisting,
                   Integer rating){
        this.placeID = placeID;
        this.userID = userID;
        this.placeName = placeName;
        this.isExisting = isExisting;
        this.rating = rating;
    }

    public void setId(String id) {this.id = id;}

}
