/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model.vision.response;

import java.util.List;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class AnnotateImageResponse {

    private List<EntityAnnotation> labelAnnotations;
    private SafeSearchAnnotation safeSearchAnnotation;

    public List<EntityAnnotation> getLabelAnnotations() {
        return labelAnnotations;
    }

    public SafeSearchAnnotation getSafeSearchAnnotation() {
        return safeSearchAnnotation;
    }
}
