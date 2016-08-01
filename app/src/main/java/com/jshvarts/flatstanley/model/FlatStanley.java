package com.jshvarts.flatstanley.model;

import android.graphics.Bitmap;

public class FlatStanley {
    private final Bitmap bitmap;
    private final String caption;
    private final String timestamp;

    public FlatStanley(Bitmap bitmap, String caption, String timestamp) {
        this.bitmap = bitmap;
        this.caption = caption;
        this.timestamp = timestamp;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getCaption() {
        return caption;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
