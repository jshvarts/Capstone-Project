package com.jshvarts.flatstanley.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jshvarts.flatstanley.R;

public class SpringboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.springboard);

        setSpringboardListView(new ArrayAdapter<>(this,
                R.layout.springboard_item,
                getResources().getStringArray(R.array.springboardItems)));
    }

    private void setSpringboardListView(ArrayAdapter<String> adapter) {
        ListView springBoardListView = (ListView) findViewById(R.id.springboardListView);
        springBoardListView.setAdapter(adapter);
        springBoardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivityByClassName(i,
                        getResources().getStringArray(R.array.springboardActivityClassNameArray));
            }
        });
    }

    private void startActivityByClassName(int index, String[] classNameArray) {
        if (index < 0 || index >= classNameArray.length) {
            Log.e(getClass().getSimpleName(), "Unknown class index.");
            return;
        }

        try {
            Class clazz = Class.forName(classNameArray[index]);
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Log.e(getClass().getSimpleName(), "Unknown class name: " + e.getMessage());
        }
    }
}
