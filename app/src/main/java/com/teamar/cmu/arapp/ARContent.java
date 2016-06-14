package com.teamar.cmu.arapp;

/**
 * Created by tijingwang on 6/8/16.
 */
public class ARContent {


    private String name;
    private String description;
    private String artistID;
    private int id;

    public ARContent(int id) {
        this.id = id;
    }

    public String getARName() {
        return name;
    }

    public void setARName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }


    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
}
