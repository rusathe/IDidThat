package edu.cmu.sv.app17.models;

public class Rating {

    String id = null;

    Integer rating,avgRating;
    String placeID;

    public Rating(Integer rating, Integer avgRating, String placeID) {
        this.rating = rating;
        this.avgRating = avgRating;
        this.placeID = placeID;
    }
    public void setId(String id) {
        this.id = id;
    }
}
