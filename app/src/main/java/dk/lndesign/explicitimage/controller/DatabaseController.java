/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
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
}
