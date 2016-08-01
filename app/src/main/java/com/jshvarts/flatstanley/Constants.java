package com.jshvarts.flatstanley;

import java.util.Date;

public class Constants {
    public static final String FIREBASE_URL = "https://flat-stanley.firebaseio.com";
    public static final String IMAGE_DATA_FIELD = "imageData";
    public static final String CAPTION_FIELD = "caption";
    public static final String TIMESTAMP_FIELD = "timestamp";

    public static String getEntrytUri(Date date) {
        if (date != null) {
            return FIREBASE_URL + "/items/" + date.getTime();
        } else {
            return FIREBASE_URL + "/items";
        }
    }
}
