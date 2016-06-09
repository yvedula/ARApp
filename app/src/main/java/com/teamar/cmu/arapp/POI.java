package com.teamar.cmu.arapp;

/**
 * Created by tijingwang on 6/8/16.
 */
public class POI {
    private String poiName;
    private int id;
    private String location;
    private String description;
    private ARContent[] arList;

    public POI(int id)
    {
        this.id = id;
    }

    public int getPoiID()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getPoiName()
    {
        return poiName;
    }

    public void setPoiName(String poiName)
    {
        this.poiName = poiName;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public ARContent[] getArList()
    {
        return arList;
    }

    public void setArList(ARContent[] arList)
    {
        this.arList = arList;
    }
}
