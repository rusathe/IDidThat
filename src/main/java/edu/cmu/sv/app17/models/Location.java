package edu.cmu.sv.app17.models;

public class Location {
    String id = null;
    String locName;
    String cityName;
    String stateName;
    String countryName;
    Boolean isSuburb;
    Number numberCheckin;

    public Location(String locName, String cityName, String stateName, String countryName,
                    Boolean isSuburb, Number numberCheckin){

        this.locName = locName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.countryName = countryName;
        this.isSuburb = isSuburb;
        this.numberCheckin = numberCheckin;

    }

    public void setId(String id) {this.id = id;}

}
