package com.jshvarts.flatstanley.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.model.FlatStanley;

import java.util.ArrayList;
import java.util.List;

public class BrowseFlatStanleysActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.browse_flat_stanleys);

        FlatStanley flatStanley1 = new FlatStanley(null, "new york", "friday");
        FlatStanley flatStanley2 = new FlatStanley(null, "Philly", "monday");
        List<FlatStanley> flatStanleys = new ArrayList();
        flatStanleys.add(flatStanley1);
        flatStanleys.add(flatStanley2);

        FlatStanleyAdapter flatStanleysAdapter = new FlatStanleyAdapter(this, flatStanleys);
        ListView listViewItems = (ListView) findViewById(R.id.listView);
        listViewItems.setAdapter(flatStanleysAdapter);
    }
}
