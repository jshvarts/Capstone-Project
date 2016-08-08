package com.jshvarts.flatstanley.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.jshvarts.flatstanley.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LobbyActivity extends AppCompatActivity {

    @BindView(R.id.takePic)
    protected Button takePicButton;

    @BindView(R.id.browse)
    protected Button browseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lobby);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.takePic)
    protected void handleTakePicButtonClick() {
        Intent takePicIntent = new Intent(this, TakePicActivity.class);
        startActivity(takePicIntent);
    }

    @OnClick(R.id.browse)
    protected void handleBrowsePicsButtonClick() {
        Intent takePicIntent = new Intent(this, BrowseFlatStanleysActivity.class);
        startActivity(takePicIntent);
    }
}