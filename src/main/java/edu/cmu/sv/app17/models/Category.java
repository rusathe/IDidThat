package edu.cmu.sv.app17.models;

public class Category {
    String id = null;
    String categoryName;

    public Category(String categoryName){
        this.categoryName = categoryName;
    }

    public void setId(String id) {this.id = id;}
}
