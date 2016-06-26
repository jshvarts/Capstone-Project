package com.jshvarts.flatstanley.activities;

import android.Manifest;
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

import com.google.common.base.Preconditions;
import com.jshvarts.flatstanley.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

public class ChoosePicActivity extends AppCompatActivity {
    private static final String TAG = "ChoosePicActivity";
    private static final int REQUEST_CHOOSE_PHOTO = 5;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 6;

    @BindView(R.id.pic_picked)
    protected ImageView picPickedImageView;

    private Uri origPhotoUri;
    private String optimizedPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.choose_pic);

        dispatchPickPictureIntent();
        //firePhotoPickerIntent();
        optimizePic();
        displayPic();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        //super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Log.d(TAG, "onActivityResult: requestCode: " + requestCode +", resultCode: " + resultCode);

        switch(requestCode) {
            case REQUEST_CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    Log.d(TAG, "REQUEST_CHOOSE_PHOTO, RESULT_OK. uri " + imageReturnedIntent.getData());
                    origPhotoUri = imageReturnedIntent.getData();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo!
                    Log.e(TAG, "Permission to read storage denied!");
                }
                return;
            }
        }
    }

    private void dispatchPickPictureIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_STORAGE);
                }
            } else {
                Log.d(TAG, "will read from storage. permission previously granted.");
                firePhotoPickerIntent();
            }
        }
    }

    private void optimizePic() {
        Preconditions.checkNotNull(origPhotoUri, "origPhotoUri cannot be null");
        Bitmap bitmap;
        try {
            bitmap = decodeAndScalePic();
            createImageFile();
            storeProcessedBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Failed to decode and scale pic: " + e);
            return;
        }
        if (bitmap == null) {
            return;
        }
    }

    private void storeProcessedBitmap(Bitmap bitmap) throws IOException {
        Preconditions.checkNotNull(bitmap, "bitmap cannot be null");
        OutputStream outputStream;
        Log.d(TAG, "Will write to optimizedPhotoPath: " + optimizedPhotoPath);
        File file = new File(optimizedPhotoPath); // we will overwrite the existing file with this optimized one.
        outputStream = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        outputStream.flush();
        outputStream.close();

        // Deal with WRITE_EXTERNAL_STORAGE permission on Marshmallow if you need to store the image to gallery
        //MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
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
        optimizedPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void displayPic() {
        Preconditions.checkNotNull(optimizedPhotoPath, "optimizedPhotoPath cannot be null");
        Picasso.with(this).setIndicatorsEnabled(true);
        Picasso.with(this).load(optimizedPhotoPath).into(picPickedImageView);
        picPickedImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Decodes image and scales it to reduce memory consumption
     */
    private Bitmap decodeAndScalePic() throws IOException {
        Preconditions.checkNotNull(origPhotoUri, "origPhotoUri cannot be null");
        Bitmap outBitmap;

        File file = new File(origPhotoUri.getPath());

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), o);

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

        if (o.outWidth <= targetWidth && o.outHeight <= targetHeight) {
            // Return image as is without any additional scaling
            Bitmap origBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), null);
            outBitmap = Bitmap.createBitmap(origBitmap, 0, 0, o.outWidth, o.outHeight);
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

        Bitmap scaledBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), scaleOptions);
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
    }

    private void firePhotoPickerIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CHOOSE_PHOTO);
    }
}
