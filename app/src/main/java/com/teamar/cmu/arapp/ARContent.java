package com.teamar.cmu.arapp;

/**
 * Created by tijingwang on 6/8/16.
 */
public class ARContent {


    private String name;
    private String description;
    private String artistName;
    private int id;

    public ARContent(String name) {
        this.name = name;
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

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }


    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
}
