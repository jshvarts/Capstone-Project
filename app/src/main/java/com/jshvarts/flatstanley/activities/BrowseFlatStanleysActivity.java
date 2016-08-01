package com.jshvarts.flatstanley.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jshvarts.flatstanley.Constants;
import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.model.FlatStanley;

import java.util.ArrayList;
import java.util.List;

public class BrowseFlatStanleysActivity extends AppCompatActivity {

    private static final String TAG = "BrowseFlatStanleys";

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.browse_flat_stanleys);

        final List<FlatStanley> flatStanleys = new ArrayList();

        firebase = new Firebase(Constants.getEntrytUri());
        firebase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot snapshot) {
               System.out.println("There are " + snapshot.getChildrenCount() + " items");
               for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                   FlatStanley flatStanley = dataSnapshot.getValue(FlatStanley.class);
                   Log.d(TAG, "imageData: " + flatStanley.getImageData());
                   Log.d(TAG, "caption: " + flatStanley.getCaption());
                   Log.d(TAG, "timestamp: " + flatStanley.getTimestamp());
                   flatStanley.setId(dataSnapshot.getKey());
                   flatStanleys.add(flatStanley);
               }

               FlatStanleyAdapter flatStanleysAdapter = new FlatStanleyAdapter(BrowseFlatStanleysActivity.this, flatStanleys);
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
}
