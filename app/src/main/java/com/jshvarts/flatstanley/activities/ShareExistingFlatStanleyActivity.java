package com.jshvarts.flatstanley.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    @BindView(R.id.postcardImage)
    protected ImageView postcardImageView;

    private ShareActionProvider shareActionProvider;

    private Uri photoUri;

    private Firebase firebase;

    private long itemId;

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.share_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        Intent shareIntent = createShareIntent();
        if (shareIntent == null) {
            return false;
        }

        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    private Intent createShareIntent() {
        if (photoUri == null) {
            Log.e(TAG, "photoUri is not available for sharing");
            return null;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        shareIntent.setType("image/png");
        return shareIntent;
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

            photoUri = Uri.parse(path);
            InputStream inputStream;
            try {
                inputStream = getContentResolver().openInputStream(photoUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                postcardImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Unable to open photo for uri " + photoUri);
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
