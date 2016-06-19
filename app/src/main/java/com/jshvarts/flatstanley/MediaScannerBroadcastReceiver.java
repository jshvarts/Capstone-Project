package com.jshvarts.flatstanley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MediaScannerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
            Log.d("MediaScanner", "ACTION_MEDIA_SCANNER_STARTED received");
        }
        if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
            Log.d("MediaScanner", "ACTION_MEDIA_SCANNER_FINISHED received");
        }
    }
}
