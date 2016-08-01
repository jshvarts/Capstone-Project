package com.jshvarts.flatstanley.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import com.firebase.client.Firebase;
import com.jshvarts.flatstanley.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareExistingFlatStanleyActivity extends AppCompatActivity {

    private static final String TAG = "ShareExistingFlatStan";

    public static final String ITEM_ID_EXTRA = "itemId";

    private Firebase firebase;

    private long itemId;

    @BindView(R.id.postcardImage)
    protected ImageView postcardImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_existing_flat_stanley);

        ButterKnife.bind(this);

        itemId = getIntent().getLongExtra(ITEM_ID_EXTRA, 0);
        if (itemId > 0) {
            Log.d(TAG, "itemId: " + itemId);

            displayPic();

        } else {
            Log.e(TAG, "itemId is invalid");
        }
    }

    private void displayPic() {
        Picasso.with(this).setIndicatorsEnabled(true);
        //Picasso.with(this).load(photoUri).into(postcardImageView);
    }
}
