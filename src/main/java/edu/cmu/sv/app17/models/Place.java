package edu.cmu.sv.app17.models;

public class Place {

    String id = null;
    String placeName;
    String cityName;
    String placeCategoryType;
    Integer numberCheckins;
    Double avgRating;
    Integer latestRankingbyCategory;

    public Place(String placeName, String cityName, String placeCategoryType, Integer numberCheckins, Double avgRating,Integer latestRankingbyCategory) {
        this.placeName = placeName;
        this.cityName = cityName;
        this.placeCategoryType = placeCategoryType;
        this.numberCheckins = numberCheckins;
        this.avgRating = avgRating;
        this.latestRankingbyCategory = latestRankingbyCategory;
    }

    public void setId(String id) {
        this.id = id;
    }
}
