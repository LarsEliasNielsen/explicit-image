/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import dk.lndesign.explicitimage.view.PagerFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Step 1: Log in.
 * Step 2: Choose image from device.
 * Step 3: Annotate image.
 * Step 4: Upload image to remote storage.
 * Step 5: Add image upload entry in database.
 */
public class UploadFragment extends PagerFragment {

    private static final String LOG_TAG = UploadFragment.class.getSimpleName();
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String KEY_FRAGMENT_TITLE = "fragment_title";

    private Context mContext;

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

    public static UploadFragment newInstance(String title) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle(1);
        args.putString(KEY_FRAGMENT_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTitle() {
        return getArguments().getString(KEY_FRAGMENT_TITLE, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        mContext = view.getContext();

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

        mUserText = (TextView) view.findViewById(R.id.user_text);
        mLoginButton = (Button) view.findViewById(R.id.log_in_button);
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

        mImageView = (ImageView) view.findViewById(R.id.image_view);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        Button getImageButton = (Button) view.findViewById(R.id.get_image_button);
        getImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset image, progress bar and image annotation.
                mBitmap = null;
                mImageUri = null;
                progressBar.setProgress(0);
                mResponse = null;

                // http://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), RESULT_LOAD_IMAGE);
            }
        });

        final Button annotateImageButton = (Button) view.findViewById(R.id.annotate_image_button);
        annotateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap != null) {
                    mVisionController.annotateImage(mBitmap, new VisionController.LoadingCallback<VisionResultWrapper>() {
                        @Override
                        public void onDataLoaded(VisionResultWrapper result) {
                            if (result.getResponses() != null && result.getResponses().get(0) != null) {
                                mResponse = result.getResponses().get(0);
                                Toast.makeText(mContext, "Image annotation retrieved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Image annotation not found", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext, "Image annotation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(LOG_TAG, "No image selected");
                }
            }
        });

        Button uploadImageButton = (Button) view.findViewById(R.id.upload_image_button);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    if (mResponse != null) {
                        if (mImageUri != null) {
                            // Create a key for a new image in database and use its unique key for storage.
                            final String key = mDatabaseController.getNewImageChildKey();

                            mStorageController.uploadImage(key, mImageUri, new StorageController.UploadCallback<StorageMetadata>() {
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
                                        // TODO: Disabled safe search restriction for testing purposes.
                                        ExplicitImage explicitImage = new ExplicitImage(
                                                metadata.getPath(),
                                                metadata.getDownloadUrl() != null ? metadata.getDownloadUrl().toString() : null,
                                                DateTimeUtils.getFormattedDate(DateTimeUtils.FORMAT_FULL, metadata.getUpdatedTimeMillis()),
                                                mResponse.getLabelAnnotations(),
                                                mResponse.getSafeSearchAnnotation(),
//                                                ExplicitImageUtil.getSafeSearchRestricted(mResponse.getSafeSearchAnnotation()),
                                                true,
                                                mAuth.getCurrentUser().getUid(),
                                                mAuth.getCurrentUser().getEmail()
                                        );

                                        mDatabaseController.pushExplicitImage(key, explicitImage);

//                                        // Reset image annonation.
//                                        mResponse = null;
                                    } else {
                                        Log.e(LOG_TAG, "User not logged in, cannot upload image to storage");
                                    }
                                    Toast.makeText(mContext, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Attach authentication listener.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
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
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            // Detach authentication listener.
            mAuth.removeAuthStateListener(mAuthListener);
        }

        mVisionController.cancelLoading();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            Log.i(LOG_TAG, "Image uri: " + mImageUri.toString());

            try {
                mBitmap = new UserImage(mImageUri, mContext.getContentResolver()).getBitmap();

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
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithEmail: onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithEmail: failed", task.getException());
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
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
