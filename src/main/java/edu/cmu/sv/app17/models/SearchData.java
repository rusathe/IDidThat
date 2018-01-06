package edu.cmu.sv.app17.models;


public class SearchData {
    String id = null;
    String placeLocation;
    String placeType;
    public SearchData( String placeLocation, String placeType) {
        this.placeLocation = placeLocation;
        this.placeType = placeType;
    }
    public void setId(String id) {
        this.id = id;
    }
}
