package com.jshvarts.flatstanley.activities;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.jshvarts.flatstanley.data.MyPicsContract;
import com.jshvarts.flatstanley.model.FlatStanley;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_PATH;

public class ShareExistingFlatStanleyActivity extends AppCompatActivity {

    private static final String TAG = "ShareExistingFlatStan";

    public static final String IS_PIC_LOCAL_EXTRA = "isLocal";
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
        boolean isLocal = getIntent().getBooleanExtra(IS_PIC_LOCAL_EXTRA, false);
        if (isLocal) {
            displayLocalPic();
        } else {
            displayRemotePic();
        }
    }

    private void displayLocalPic() {

        String[] projection = new String[] {COLUMN_PATH};

        Cursor c = getContentResolver().query(ContentUris.withAppendedId(MyPicsContract.CONTENT_URI, itemId), projection, null, null, null);
        if (c.getCount() == 0) {
            c.close();
            Log.d(TAG, "unable to load pic");
        } else {
            String path = null;
            while (c.moveToNext()) {
                path = c.getString(0);
            }
            c.close();

            Uri uri = Uri.parse(path);
            InputStream inputStream;
            try {
                inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                postcardImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Unable to open photo for uri " + uri);
            }
        }
    }

    private void displayRemotePic() {
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
