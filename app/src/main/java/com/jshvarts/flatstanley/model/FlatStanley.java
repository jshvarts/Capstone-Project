package com.jshvarts.flatstanley.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlatStanley {
    private String id;
    private String imageData;
    private String caption;
    private String timestamp;

    public FlatStanley() {
        // dummy constructor to enable Jackson deserialization by Firebase
    }

    public FlatStanley(String imageData, String caption, String timestamp) {
        this.imageData = imageData;
        this.caption = caption;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public String getImageData() {
        return imageData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
