package com.jshvarts.flatstanley.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jshvarts.flatstanley.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LobbyActivity extends AppCompatActivity {

    @BindView(R.id.takePic)
    protected Button takePicButton;

    @BindView(R.id.browse)
    protected Button browseButton;

    @BindView(R.id.adView)
    protected AdView adView;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lobby);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
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