package com.jshvarts.flatstanley.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.common.base.Preconditions;
import com.jshvarts.flatstanley.Constants;
import com.jshvarts.flatstanley.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareFlatStanleyActivity extends AppCompatActivity {

    private static final String TAG = "ShareFlatStanleyActiv";

    public static final String PHOTO_URI_EXTRA = "photoUri";

    public static final String CAPTION_TEXT_EXTRA = "caption";

    private Firebase firebase;

    @BindView(R.id.postcardImage)
    protected ImageView postcardImageView;

    @BindView(R.id.shareButton)
    protected Button shareButton;

    private Uri photoUri;

    private String captionText;

    private Date now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_flat_stanley);

        ButterKnife.bind(this);

        now = getUriTimestamp();
        firebase = new Firebase(Constants.getEntrytUri(now));

        photoUri = getIntent().getParcelableExtra(PHOTO_URI_EXTRA);
        captionText = getIntent().getStringExtra(CAPTION_TEXT_EXTRA);
        if (photoUri != null) {
            Log.d(TAG, "photoUri: " + photoUri.getPath());
            Log.d(TAG, "captionText: " + captionText);

            displayPic();

        } else {
            Log.e(TAG, "photoUri is empty");
        }
    }

    @OnClick(R.id.shareButton)
    protected void handleShareButtonClick() {
        storeInFirebase();
    }

    private void displayPic() {
        Picasso.with(this).setIndicatorsEnabled(true);
        Picasso.with(this).load(photoUri).into(postcardImageView);
    }

    private void storeInFirebase() {
        Target bitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                handleLoadedBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                // do nothing
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // do nothing
            }
        };

        Picasso.with(this).load(photoUri).into(bitmapTarget);
    }

    private void handleLoadedBitmap(Bitmap bitmap) {
        Preconditions.checkNotNull(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String base64Image = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               reportShareSuccess();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
               // do nothing
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               // do nothing
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
               // do nothing
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
               // do nothing
            }
        });

        firebase.child(Constants.IMAGE_DATA_FIELD).setValue(base64Image);
        firebase.child(Constants.CAPTION_FIELD).setValue(TextUtils.isEmpty(captionText) ? "" : captionText);
        firebase.child(Constants.TIMESTAMP_FIELD).setValue(getDisplayTimestamp());
    }

    /**
     * Messages user about the success and disables the share button.
     */
    private void reportShareSuccess() {
        Toast.makeText(ShareFlatStanleyActivity.this, getText(R.string.shared_message_text),
                Toast.LENGTH_SHORT).show();
        shareButton.setText(getText(R.string.shared_button_text));
        shareButton.setEnabled(false);
    }

    private Date getUriTimestamp() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US);
        return cal.getTime();
    }

    private String getDisplayTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy hh:mm:ssa");
        return dateFormat.format(now);
    }
}
