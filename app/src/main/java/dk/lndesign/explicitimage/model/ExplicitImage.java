/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.lndesign.explicitimage.model.vision.response.EntityAnnotation;
import dk.lndesign.explicitimage.model.vision.response.SafeSearchAnnotation;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
@IgnoreExtraProperties
public class ExplicitImage {

    private String imagePath;
    private String downloadPath;
    private String imageUpdated;
    private List<EntityAnnotation> entityAnnotations;
    private SafeSearchAnnotation safeSearchAnnotation;
    private boolean listed;
    private String userId;
    private String userEmail;

    public ExplicitImage() {}

    public ExplicitImage(String imagePath,
                         String downloadPath,
                         String imageUpdated,
                         List<EntityAnnotation> entityAnnotations,
                         SafeSearchAnnotation safeSearchAnnotation,
                         boolean listed,
                         String userId,
                         String userEmail) {
        this.imagePath = imagePath;
        this.downloadPath = downloadPath;
        this.imageUpdated = imageUpdated;
        this.entityAnnotations = entityAnnotations;
        this.safeSearchAnnotation = safeSearchAnnotation;
        this.listed = listed;
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public String getImageUpdated() {
        return imageUpdated;
    }

    public List<EntityAnnotation> getEntityAnnotations() {
        return entityAnnotations;
    }

    public SafeSearchAnnotation getSafeSearchAnnotation() {
        return safeSearchAnnotation;
    }

    public boolean isListed() {
        return listed;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("imagePath", imagePath);
        result.put("downloadPath", downloadPath);
        result.put("imageUpdated", imageUpdated);
        result.put("entityAnnotations", entityAnnotations);
        result.put("safeSearchAnnotation", safeSearchAnnotation);
        result.put("listed", listed);
        result.put("userId", userId);
        result.put("userEmail", userEmail);

        return result;
    }
}
