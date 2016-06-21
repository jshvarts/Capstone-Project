package com.jshvarts.flatstanley.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.jshvarts.flatstanley.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareFlatStanleyActivity extends AppCompatActivity {

    private static final String TAG = "ShareFlatStanleyActiv";

    public static final String PHOTO_URI = "photoUri";

    @BindView(R.id.postcardImage)
    protected ImageView postcardImageView;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_flat_stanley);

        ButterKnife.bind(this);

        photoUri = getIntent().getParcelableExtra(PHOTO_URI);
        if (photoUri != null) {
            Log.d(TAG, "photoUri is not empty");
            Log.d(TAG, "photoUri: " + photoUri.getPath());
            displayPic();
        } else {
            Log.e(TAG, "photoUri is empty");
            postcardImageView.setImageResource(R.drawable.attraction);
        }
    }

    private void displayPic() {
        Picasso.with(this).setIndicatorsEnabled(true);
        Picasso.with(this).load(photoUri).into(postcardImageView);
    }
}
