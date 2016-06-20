package com.jshvarts.flatstanley.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.util.CameraUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TakePicActivity extends AppCompatActivity {
    private static final String TAG = "TakePicActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;
    private static final String FILE_PROVIDER_AUTHORITY = "com.jshvarts.flatstanley.fileprovider";

    @BindView(R.id.pic_taken)
    protected ImageView picTakenImageView;

    private String currentPhotoPath;

    private Uri contentUri;

    private Intent takePictureIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.take_pic);

        ButterKnife.bind(this);

        if (!CameraUtil.checkCameraAvailability(this)) {
            Log.d(TAG, "Device has no camera");
            return;
        }

        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "error saving pic: " + photoFile);
            }

            if (photoFile != null) {
                contentUri = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.CAMERA)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {
                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.CAMERA},
                                PERMISSIONS_REQUEST_CAMERA);
                    }
                } else {
                    Log.d(TAG, "will take picture. permission previously granted.");
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addPicToGallery();
            displayPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo!
                    Log.e(TAG, "Permission to use camera denied!");
                }
                return;
            }
        }
    }

    @OnClick(R.id.retakePicButton)
    protected void handleRetakePicButtonClick() {
        Log.d(TAG, "Begin handleRetakePicButtonClick");

        Intent intent = getIntent();
        finish();
        startActivity(intent);

        Log.d(TAG, "End handleRetakePicButtonClick");
    }

    @OnClick(R.id.addFlatStanley)
    protected void handleAddFlatStanleyButtonClick() {
        Log.d(TAG, "Begin handleAddFlatStanleyButtonClick");

        Intent detailIntent = new Intent(this, MakeFlatStanleyActivity.class);
        detailIntent.putExtra(MakeFlatStanleyActivity.PHOTO_PATH, currentPhotoPath);
        startActivity(detailIntent);

        Log.d(TAG, "End handleAddFlatStanleyButtonClick");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "ExternalFilesDir: " + storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addPicToGallery() {
        // TODO revisit as it does not work on Marshmallow
        Log.d(TAG, "sending broadcast to add image to gallery.");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void displayPic() {
        Bitmap bitmap;
        try {
            bitmap = decodeAndScalePic();
        } catch (IOException e) {
            Log.e(TAG, "Failed to decode and scale pic: " + e);
            return;
        }
        if (bitmap == null) {
            return;
        }
        picTakenImageView.setImageBitmap(bitmap);
        picTakenImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Decodes image and scales it to reduce memory consumption
     */
    private Bitmap decodeAndScalePic() throws IOException {
        Bitmap outBitmap;

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, o);

        Log.d(TAG, "width: " + o.outWidth);
        Log.d(TAG, "height: " + o.outHeight);

        if (o.outWidth == -1 || o.outHeight == -1) {
            Log.e(TAG, "Unable to load bitmap. retry taking a pic.");
            return null;
        }

        // preserve the orientation (portrait vs landscape)
        ExifInterface exif = new ExifInterface(currentPhotoPath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        Matrix m = new Matrix();
        if (orientation == 3) {
            m.postRotate(180);
        } else if (orientation == 6) {
            m.postRotate(90);
        } else if (orientation == 8) {
            m.postRotate(270);
        }

        // The new size we want to scale to ensure memory usage is optimal
        int targetWidth;
        int targetHeight;
        if (o.outHeight > o.outWidth) {
            targetWidth = getResources().getInteger(R.integer.pic_width_px);
            targetHeight = getResources().getInteger(R.integer.pic_height_px);
        } else if (o.outHeight == o.outWidth) {
            targetWidth = targetHeight = getResources().getInteger(R.integer.pic_width_px);
        } else {
            targetWidth = getResources().getInteger(R.integer.pic_width_px);
            targetHeight = getResources().getInteger(R.integer.pic_height_px);
        }

        Log.d(TAG, "targetWidth: " + targetWidth);
        Log.d(TAG, "targetHeight: " + targetHeight);

        if (o.outWidth <= targetWidth && o.outHeight <= targetHeight) {
            // Return image as is without any additional scaling
            Bitmap origBitmap = BitmapFactory.decodeFile(currentPhotoPath, null);
            outBitmap = Bitmap.createBitmap(origBitmap, 0, 0, o.outWidth, o.outHeight, m, true);
            origBitmap.recycle();

            return outBitmap;
        }

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while(o.outWidth / scale / 2 >= targetWidth &&
                o.outHeight / scale / 2 >= targetHeight) {
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
        scaleOptions.inSampleSize = scale;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(currentPhotoPath, scaleOptions);
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), m, true);
    }
}
