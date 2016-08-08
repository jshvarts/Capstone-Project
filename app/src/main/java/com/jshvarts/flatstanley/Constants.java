package com.jshvarts.flatstanley;

public class Constants {
    public static final String FIREBASE_URL = "https://flat-stanley.firebaseio.com";
    public static final String IMAGE_DATA_FIELD = "imageData";
    public static final String CAPTION_FIELD = "caption";
    public static final String TIMESTAMP_FIELD = "timestamp";

    public static String getEntrytUri(long time) {
        return FIREBASE_URL + "/items/" + time;
    }

    public static String getEntrytUri() {
        return FIREBASE_URL + "/items";
    }
}
