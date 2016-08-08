package com.jshvarts.flatstanley.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jshvarts.flatstanley.Constants;
import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.activities.adapters.SharedByOthersAdapter;
import com.jshvarts.flatstanley.model.FlatStanley;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jshvarts.flatstanley.data.MyPicsContract.CONTENT_URI;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_PATH;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_CAPTION;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_TIMESTAMP;

public class BrowseFlatStanleysActivity extends AppCompatActivity {

    private static final String TAG = "BrowseFlatStanleys";

    private Firebase firebase;

    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.browse_flat_stanleys);

        ButterKnife.bind(this);

        final List<FlatStanley> flatStanleys = new ArrayList();

        firebase = new Firebase(Constants.getEntrytUri());
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                System.out.println("There are " + snapshot.getChildrenCount() + " items");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FlatStanley flatStanley = dataSnapshot.getValue(FlatStanley.class);
                    //Log.d(TAG, "imageData: " + flatStanley.getImageData());
                    //Log.d(TAG, "caption: " + flatStanley.getCaption());
                    //Log.d(TAG, "timestamp: " + flatStanley.getTimestamp());
                    flatStanley.setId(dataSnapshot.getKey());
                    flatStanleys.add(flatStanley);
                }

                SharedByOthersAdapter flatStanleysAdapter = new SharedByOthersAdapter(BrowseFlatStanleysActivity.this, flatStanleys);
                ListView listViewItems = (ListView) findViewById(R.id.listView);
                listViewItems.setAdapter(flatStanleysAdapter);
                listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "id clicked: " + id);

                        Intent shareActivityIntent = new Intent(BrowseFlatStanleysActivity.this, ShareExistingFlatStanleyActivity.class);
                        shareActivityIntent.putExtra(ShareExistingFlatStanleyActivity.ITEM_ID_EXTRA, id);
                        startActivity(shareActivityIntent);

                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "The read failed: " + firebaseError.getMessage());
            }
        });
    }

    @OnClick(R.id.mine_only)
    protected void handleMyOnlyButtonClick() {
        String[] projection = new String[] {BaseColumns._ID, COLUMN_PATH, COLUMN_CAPTION, COLUMN_TIMESTAMP};

        Cursor c = getContentResolver().query(CONTENT_URI, projection, null, null, null);
        if (c.getCount() == 0) {
            c.close();
            Log.d(TAG, "no pics created by current user yet");
            Toast.makeText(BrowseFlatStanleysActivity.this, "No pics created by you yet.", Toast.LENGTH_SHORT).show();
        } else  {
            while(c.moveToNext()) {
                Log.d(TAG, "pic 0: " + c.getInt(0));
                Log.d(TAG, "pic 1: " + c.getString(1));
                Log.d(TAG, "pic 2: " + c.getString(2));
                Log.d(TAG, "pic 3: " + c.getString(3));
            }
            c.close();
        }
    }
}