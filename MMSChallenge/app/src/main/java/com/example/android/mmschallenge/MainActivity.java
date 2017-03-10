/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mmschallenge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * This app enables user to choose an image from the Gallery
 * and launch an SMS messaging app to send the image to the
 * phone number of the user's choice.
 */

public class MainActivity extends AppCompatActivity {
    private static final int IMAGE_PICK = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Creates the activity, sets the view, and checks for Storage permission
     * in order to read external storage.
     *
     * @param savedInstanceState Instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermission();
    }

    /**
     * Checks whether the app has READ_EXTERNAL_STORAGE permission.
     */
    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, getString(R.string.permission_not_granted));
                // Permission not yet granted. Use requestPermissions().
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // Permission already granted. Enable the button.
                enablePicButton();
            }
        }
    }

    /**
     * Processes permission request codes.
     *
     * @param requestCode  The request code passed in requestPermissions()
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // For each permission, checks if it is granted or not.
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted. Enable the button.
                    enablePicButton();
                } else {
                    // Permission denied.
                    // Disable the functionality that depends on this permission.
                    Log.d(TAG, getString(R.string.failure_permission));
                    Toast.makeText(MainActivity.this, getString(R.string.failure_permission),
                            Toast.LENGTH_LONG).show();
                    // Disable the button.
                    disablePicButton();
                }
            }
        }
    }

    /**
     * Makes the Choose Picture button invisible so that it can't be used.
     */
    private void disablePicButton() {
        Toast.makeText(this, R.string.button_disabled, Toast.LENGTH_LONG).show();
        (findViewById(R.id.button_photo)).setVisibility(View.INVISIBLE);
    }

    /**
     * Makes the Choose Picture button visible so that it can be used.
     */
    private void enablePicButton() {
        (findViewById(R.id.button_photo)).setVisibility(View.VISIBLE);
    }

    /**
     *
     * On click of the Choose Picture button, enables the user to
     * choose an image from the Gallery.
     *
     * @param view View (Choose Picture button) that was clicked.
     */
    public void choosePic(View view) {
        // Choose a picture.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_PICK);
    }

    /**
     * Sets the image Uri and creates implicit intent with ACTION_SEND
     * to launch an app to send the image.
     */
    @Override
    protected void onActivityResult
    (int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == IMAGE_PICK) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, getString(R.string.picture_chosen));
                Uri mSelectedImage = imageReturnedIntent.getData();
                Log.d(TAG, "onActivityResult: " + mSelectedImage.toString());
                Intent smsIntent = new Intent(Intent.ACTION_SEND);
                smsIntent.putExtra(Intent.EXTRA_STREAM, mSelectedImage);
                smsIntent.setType("image/*");
                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                } else {
                    Log.d(TAG, "Can't resolve app for ACTION_SEND Intent.");
                }
            }
        }
    }

}
