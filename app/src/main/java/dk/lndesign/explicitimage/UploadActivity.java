/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageMetadata;

import java.io.IOException;
import java.util.Locale;

import dk.lndesign.explicitimage.controller.DatabaseController;
import dk.lndesign.explicitimage.controller.StorageController;
import dk.lndesign.explicitimage.controller.VisionController;
import dk.lndesign.explicitimage.model.ExplicitImage;
import dk.lndesign.explicitimage.model.UserImage;
import dk.lndesign.explicitimage.model.vision.response.AnnotateImageResponse;
import dk.lndesign.explicitimage.model.vision.response.EntityAnnotation;
import dk.lndesign.explicitimage.model.vision.response.VisionResultWrapper;
import dk.lndesign.explicitimage.util.DateTimeUtils;
import dk.lndesign.explicitimage.util.ExplicitImageUtil;

/**
 * Step 1: Log in.
 * Step 2: Choose image from device.
 * Step 3: Annotate image.
 * Step 4: Upload image to remote storage.
 * Step 5: Add image upload entry in database.
 */
public class UploadActivity extends AppCompatActivity {

    private static final String LOG_TAG = UploadActivity.class.getSimpleName();
    private static final int RESULT_LOAD_IMAGE = 1;

    private AnnotateImageResponse mResponse;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private VisionController mVisionController = new VisionController();
    private StorageController mStorageController = new StorageController();
    private DatabaseController mDatabaseController = new DatabaseController();

    private ImageView mImageView;
    private TextView mUserText;
    private Button mLoginButton;

    private Bitmap mBitmap;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Setup authentication.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged: signed_out");
                }
            }
        };

        mUserText = (TextView) findViewById(R.id.user_text);
        mLoginButton = (Button) findViewById(R.id.log_in_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    logOutUser();
                } else {
                    logInUser();
                }
            }
        });

        mImageView = (ImageView) findViewById(R.id.image_view);

        Button getImageButton = (Button) findViewById(R.id.get_image_button);
        getImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // http://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), RESULT_LOAD_IMAGE);
            }
        });

        final Button annotateImageButton = (Button) findViewById(R.id.annotate_image_button);
        annotateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap != null) {
                    mVisionController.annotateImage(mBitmap, new VisionController.LoadingCallback<VisionResultWrapper>() {
                        @Override
                        public void onDataLoaded(VisionResultWrapper result) {
                            if (result.getResponses() != null && result.getResponses().get(0) != null) {
                                mResponse = result.getResponses().get(0);
                                Toast.makeText(getApplicationContext(), "Image annotation retrieved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Image annotation not found", Toast.LENGTH_SHORT).show();
                            }
                            for (AnnotateImageResponse annotateImageResponse : result.getResponses()) {

                                if (annotateImageResponse.getSafeSearchAnnotation() != null) {
                                    Log.d(LOG_TAG, annotateImageResponse.getSafeSearchAnnotation().toString());
                                } else {
                                    Log.i(LOG_TAG, "No safe search available for this image");
                                }

                                if (annotateImageResponse.getLabelAnnotations() != null &&
                                        !annotateImageResponse.getLabelAnnotations().isEmpty()) {

                                    for (EntityAnnotation entityAnnotation : annotateImageResponse.getLabelAnnotations()) {
                                        Log.d(LOG_TAG, entityAnnotation.toString());
                                    }
                                } else {
                                    Log.i(LOG_TAG, "No label annotation available for this image");
                                }
                            }
                        }

                        @Override
                        public void onFailed() {
                            Log.e(LOG_TAG, "Image annotation failed");
                            Toast.makeText(getApplicationContext(), "Image annotation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(LOG_TAG, "No image selected");
                }
            }
        });

        Button uploadImageButton = (Button) findViewById(R.id.upload_image_button);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    if (mResponse != null) {
                        if (mImageUri != null) {
                            mStorageController.uploadImage(mImageUri, new StorageController.UploadCallback<StorageMetadata>() {
                                @Override
                                public void onProgress(double progress) {
                                    progressBar.setProgress((int) progress);
                                }

                                @Override
                                public void onPause() {}

                                @Override
                                public void onFailure(@NonNull Exception exception) {}

                                @Override
                                public void onSuccess(@NonNull StorageMetadata metadata) {
                                    if (mAuth.getCurrentUser() != null) {
                                        ExplicitImage explicitImage = new ExplicitImage(
                                                metadata.getPath(),
                                                metadata.getDownloadUrl() != null ? metadata.getDownloadUrl().toString() : null,
                                                DateTimeUtils.getFormattedDate(DateTimeUtils.FORMAT_FULL, metadata.getUpdatedTimeMillis()),
                                                mResponse.getLabelAnnotations(),
                                                mResponse.getSafeSearchAnnotation(),
                                                ExplicitImageUtil.getSafeSearchRestricted(mResponse.getSafeSearchAnnotation()),
                                                mAuth.getCurrentUser().getUid(),
                                                mAuth.getCurrentUser().getEmail()
                                        );

                                        mDatabaseController.pushExplicitImage(explicitImage);
                                    } else {
                                        Log.e(LOG_TAG, "User not logged in, cannot upload image to storage");
                                    }
                                    Toast.makeText(getApplicationContext(), "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.e(LOG_TAG, "No image found, select image from device before uploading.");
                        }
                    } else {
                        Log.e(LOG_TAG, "No image annotation response found, annotate image before uploading.");
                    }
                } else {
                    Log.e(LOG_TAG, "User id not logged in, you must log in to upload image.");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Attach authentication listener.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAuth.getCurrentUser() != null) {
            mUserText.setText(
                    String.format(Locale.ENGLISH, "User: %s", mAuth.getCurrentUser().getUid()));
            mLoginButton.setText("Log out");
        } else {
            mUserText.setText("User: N/A");
            mLoginButton.setText("Log in");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            // Detach authentication listener.
            mAuth.removeAuthStateListener(mAuthListener);
        }

        mVisionController.cancelLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            Log.i(LOG_TAG, "Image uri: " + mImageUri.toString());

            try {
                mBitmap = new UserImage(mImageUri, getContentResolver()).getBitmap();

                if (mImageView != null) {
                    mImageView.setImageBitmap(mBitmap);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Could not get selected image", e);
                e.printStackTrace();
            }
        }
    }

    private void logInUser() {
        /*
         * Logging user set in explicitimage.properties
         */
        mAuth.signInWithEmailAndPassword(BuildConfig.USER, BuildConfig.PASSWORD)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithEmail: onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithEmail: failed", task.getException());
                            Toast.makeText(UploadActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            mUserText.setText(
                                    String.format(Locale.ENGLISH, "User: %s", task.getResult().getUser().getUid()));
                            mLoginButton.setText("Log out");
                        }
                    }
                });
    }

    private void logOutUser() {
        mAuth.signOut();
        mUserText.setText("User: N/A");
        mLoginButton.setText("Log in");
    }
}
