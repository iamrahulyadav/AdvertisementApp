package com.projects.owner.camlocation.model;

/**
 * Created by Sanawal Alvi on 11/4/2017.
 */

public class MediaModel {
    String id, url, type, lat, lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public MediaModel(String id, String url, String type, String lat, String lng)
    {
        this.id = id;
        this.url = url;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
    }
}
