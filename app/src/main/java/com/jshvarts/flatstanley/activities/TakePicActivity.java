package com.jshvarts.flatstanley.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.util.CameraUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakePicActivity extends AppCompatActivity {
    private static final String TAG = "TakePicActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;
    private static final String FILE_PROVIDER_AUTHORITY = "com.jshvarts.flatstanley.fileprovider";

    @BindView(R.id.take_pic_text)
    protected TextView takePicText;

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
            takePicText.setText("Device has no camera");
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
                    takePicText.setText("Permission to use camera denied!");
                }
                return;
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
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
        Log.d(TAG, "sending broadcast to add image to gallery.");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        Log.d(TAG, "scheme: " + contentUri.getScheme().equals("file"));
        Log.d(TAG, "action: " + Intent.ACTION_MEDIA_SCANNER_SCAN_FILE.equals(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
        Log.d(TAG, "path: " + contentUri.getPath());
        Log.d(TAG, "ExternalStorageDirectory: " + Environment.getExternalStorageDirectory().getPath());
    }

    private void displayPic() {
        // Get the dimensions of the View
        int targetW = 720;
        int targetH = 480;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Log.d(TAG, "start file decode");
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        Log.d(TAG, "end file decode");
        picTakenImageView.setImageBitmap(bitmap);
        picTakenImageView.setVisibility(View.VISIBLE);
    }
}
