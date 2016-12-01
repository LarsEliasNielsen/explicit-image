/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.controller;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.lndesign.explicitimage.model.ExplicitImage;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class DatabaseController {

    private static final String LOG_TAG = DatabaseController.class.getSimpleName();

    private DatabaseReference mDatabaseRef;

    public DatabaseController() {
        mDatabaseRef = FirebaseDatabase.getInstance()
                .getReference();
//                .child("images");
    }

    public interface LoadingCallback<T> {
        void onDataChange(T data);
        void onCancelled();
    }

    public void pushExplicitImage(ExplicitImage explicitImage) {
        // Get new key and image values.
        String key = mDatabaseRef.child("images").push().getKey();
        Map<String, Object> postValues = explicitImage.toMap();

        // Update multiple references.
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/images/" + key, postValues);
        if (explicitImage.getUserId() != null) {
            childUpdates.put("/users/" + explicitImage.getUserId() + "/images/" + key, postValues);
        }

        mDatabaseRef.updateChildren(childUpdates);
    }

    public void getExplicitImages(final LoadingCallback<List<ExplicitImage>> callback) {
        Query imageQuery = mDatabaseRef.child("images")
                .limitToLast(100);

        imageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ExplicitImage> images = new ArrayList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // Image is only listed if no restrictions are violated.
                    if (postSnapshot.getValue(ExplicitImage.class).isListed()) {
                        images.add(0, postSnapshot.getValue(ExplicitImage.class));
                    }
                }
                callback.onDataChange(images);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Loading of images canceled", databaseError.toException());
                callback.onCancelled();
            }
        });
    }
}
