/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model.vision.request;

import java.util.List;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class AnnotateImageRequest {

    private Image image;
    private List<Feature> features;

    public AnnotateImageRequest(Image image, List<Feature> features) {
        this.image = image;
        this.features = features;
    }

    public Image getImage() {
        return image;
    }

    public List<Feature> getFeatures() {
        return features;
    }
}
