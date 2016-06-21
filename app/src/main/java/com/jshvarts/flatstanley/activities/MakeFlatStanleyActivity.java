package com.jshvarts.flatstanley.activities;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jshvarts.flatstanley.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeFlatStanleyActivity extends AppCompatActivity {

    public static final String PHOTO_URI = "photoUri";

    private static final String TAG = "MakeFlatStanleyActivity";

    private static final String FILE_PROVIDER_AUTHORITY = "com.jshvarts.flatstanley.fileprovider";

    @BindView(R.id.flatStanleyImage)
    protected ImageView flatStanleyImageView;

    @BindView(R.id.attractionImage)
    protected ImageView attractionImageView;

    @BindView(R.id.target_layout)
    protected FrameLayout targetLayout;

    @BindView(R.id.source_layout)
    protected LinearLayout sourceLayout;

    @BindView(R.id.attractionCaption)
    protected EditText attractionCaption;

    @BindView(R.id.addAttractionCaption)
    protected Button addCaptionButton;

    private float posX;
    private float posY;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_flat_stanley);

        ButterKnife.bind(this);

        photoUri = getIntent().getParcelableExtra(PHOTO_URI);
        if (photoUri != null) {
            Log.d(TAG, "photoUri is not empty");
            displayPic();
        } else {
            Log.e(TAG, "photoUri is empty");
            attractionImageView.setImageResource(R.drawable.attraction);
        }

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData clipData = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(clipData, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE); // we are dragging the shadow
                return true;
            }
        };
        flatStanleyImageView.setOnLongClickListener(longClickListener);

        View.OnDragListener dragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction())
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d(TAG, "Action is DragEvent.ACTION_DRAG_STARTED");

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENTERED");

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(TAG, "Action is DragEvent.ACTION_DRAG_EXITED");

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d(TAG, "Action is DragEvent.ACTION_DRAG_LOCATION");

                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENDED. DragEvent.getResult() " + event.getResult());
                        if (!event.getResult()) {
                            Log.d(TAG, "Drag was not successful.");
                            flatStanleyImageView.setVisibility(View.VISIBLE);
                        }
                        return true;

                    case DragEvent.ACTION_DROP:
                        Log.d(TAG, "Action is DragEvent.ACTION_DROP");

                        ViewGroup draggedImageParentViewLayout = (ViewGroup) flatStanleyImageView.getParent();
                        Log.d(TAG, "draggedImageParentViewLayout: " + draggedImageParentViewLayout.getId());
                        Log.d(TAG, "targetLayout: " + targetLayout.getId());

                        View view = (View) event.getLocalState();
                        posX = event.getX();
                        posY = event.getY();
                        view.setX(posX-(view.getWidth()/2));
                        view.setY(posY-(view.getWidth()/2));
                        if (draggedImageParentViewLayout != targetLayout) {
                            draggedImageParentViewLayout.removeView(flatStanleyImageView);
                            targetLayout.addView(flatStanleyImageView);
                        }
                        sourceLayout.invalidate();
                        targetLayout.invalidate();
                        return true;

                    default:
                        break;
                }
                return false;
            }
        };
        targetLayout.setOnDragListener(dragListener);
    }

    @OnClick(R.id.addAttractionCaption)
    protected void handleAddCaptionButtonClick() {
        Log.d(TAG, "Begin handleAddCaptionButtonClick");
        final String attractionTitle = attractionCaption.getText().toString().trim();
        Log.d(TAG, "attraction caption: " + attractionTitle);

        if (TextUtils.isEmpty(attractionTitle)) {
            Log.d(TAG, "Caption is empty. Nothing to add.");
            return;
        }

        InputStream is = loadAttractionBitmap();
        if (is == null) {
            return;
        }
        Bitmap attractionBitmap = BitmapFactory.decodeStream(is);
        Bitmap bitmapOverlay = Bitmap.createBitmap(attractionBitmap.getWidth(), attractionBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapOverlay);
        canvas.drawBitmap(attractionBitmap, new Matrix(), null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(72);

        Rect bounds = new Rect();
        paint.getTextBounds(attractionTitle, 0, attractionTitle.length(), bounds);
        int boundsWidth = bounds.width() + 50; // padded by fixed amount of 50

        int x = bitmapOverlay.getWidth() - boundsWidth;
        int y = bitmapOverlay.getHeight() - 50;

        canvas.drawText(attractionTitle, x, y, paint);

        attractionImageView.setImageBitmap(bitmapOverlay);
        attractionImageView.invalidate();

        Log.d(TAG, "End handleAddCaptionButtonClick");
    }

    @OnClick(R.id.resetButton)
    protected void handleResetButtonClick() {
        Log.d(TAG, "Begin handleResetButtonClick");

        Intent intent = getIntent();
        finish();
        startActivity(intent);

        Log.d(TAG, "End handleResetButtonClick");
    }

    @OnClick(R.id.shareButton)
    protected void handleShareButtonClick() {
        Log.d(TAG, "Begin handleShareButtonClick");

        InputStream is = loadAttractionBitmap();
        if (is == null) {
            return;
        }
        Bitmap attractionBitmap = BitmapFactory.decodeStream(is);
        Bitmap flatStanleyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.reddit);

        Bitmap bitmapOverlay = Bitmap.createBitmap(attractionBitmap.getWidth(), attractionBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapOverlay);
        canvas.drawBitmap(attractionBitmap, new Matrix(), null);
        canvas.drawBitmap(flatStanleyBitmap, posX, posY, null);

        try {
            storeProcessedBitmap(bitmapOverlay);
        } catch (IOException e) {
            Log.d(TAG, "Unable to store the new combined image. " + e);
        }

        Intent shareActivityIntent = new Intent(this, ShareFlatStanleyActivity.class);
        shareActivityIntent.putExtra(ShareFlatStanleyActivity.PHOTO_URI, photoUri);
        startActivity(shareActivityIntent);

        Log.d(TAG, "End handleShareButtonClick");
    }

    private void storeProcessedBitmap(Bitmap bitmap) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "ExternalFilesDir: " + storageDir.getAbsolutePath());

        File file = new File(storageDir, imageFileName + ".jpg");
        OutputStream outputStream = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
        outputStream.flush();
        outputStream.close();

        photoUri = FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY,
                file);
    }

    private InputStream loadAttractionBitmap() {
        InputStream is;
        try {
            is = getContentResolver().openInputStream(photoUri);
        } catch (IOException e) {
            Log.d(TAG, "Unable to open photo for uri " + photoUri);
            Toast.makeText(MakeFlatStanleyActivity.this, "Unable to open photo.", Toast.LENGTH_LONG).show();
            return null;
        }
        return is;
    }
    private void displayPic() {
        Picasso.with(this).setIndicatorsEnabled(true);
        Picasso.with(this).load(photoUri).into(attractionImageView);
    }
}
