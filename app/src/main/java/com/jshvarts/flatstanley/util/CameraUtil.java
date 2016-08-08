package com.jshvarts.flatstanley.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class CameraUtil {
    public static boolean checkCameraAvailability(Context context) {
        PackageManager pm = context.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }

        return false;
    }
}
