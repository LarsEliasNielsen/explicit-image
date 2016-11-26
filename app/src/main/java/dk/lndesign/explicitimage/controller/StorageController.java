/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.controller;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class StorageController {

    private static final String LOG_TAG = StorageController.class.getSimpleName();

    private StorageReference mStorageRef;

    public StorageController() {
        mStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://explicit-image.appspot.com");
//                .child("upload");
//                .child(getFormattedDate("yyyy-MM-dd", new Date()));
    }

    public interface UploadCallback<StorageMetadata> {
        void onProgress(double progress);
        void onPause();
        void onFailure(@NonNull Exception exception);
        void onSuccess(@NonNull StorageMetadata metadata);
    }

    public void uploadImage(Uri imageUri, final UploadCallback<StorageMetadata> callback) {
        // TODO: Build a better naming convention for image files.
        StorageReference newUploadRef = mStorageRef
                .child("upload")
                .child(imageUri.getLastPathSegment());
        UploadTask uploadTask = newUploadRef.putFile(
                imageUri,
                new StorageMetadata.Builder().setContentType("image/jpeg").build()
        );

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                callback.onProgress(progress);

                Log.i(LOG_TAG, "Image upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onPause();

                Log.i(LOG_TAG, "Image upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onFailure(exception);

                Log.e(LOG_TAG, "Image upload failed, try again.");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageMetadata storageMetadata = taskSnapshot.getMetadata();
                if (storageMetadata != null) {
                    callback.onSuccess(storageMetadata);
                } else {
                    callback.onFailure(new Exception("No image metadata found"));

                    Log.e(LOG_TAG, "No image metadata found.");
                }
            }
        });
    }
}
