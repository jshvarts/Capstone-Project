package com.jshvarts.flatstanley.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jshvarts.flatstanley.Constants;
import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.model.FlatStanley;

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
        firebase = new Firebase(Constants.getEntrytUri(itemId));
        firebase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot snapshot) {
               System.out.println("There are " + snapshot.getChildrenCount() + " items");
               FlatStanley flatStanley = snapshot.getValue(FlatStanley.class);
               Log.d(TAG, "imageData: " + flatStanley.getImageData());
               Log.d(TAG, "caption: " + flatStanley.getCaption());
               Log.d(TAG, "timestamp: " + flatStanley.getTimestamp());

               byte[] decodedBytes = Base64.decode(flatStanley.getImageData(),Base64.DEFAULT);
               Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
               postcardImageView.setImageBitmap(decodedBitmap);
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {
               Log.d(TAG, "The read failed: " + firebaseError.getMessage());
           }
        });
    }
}
