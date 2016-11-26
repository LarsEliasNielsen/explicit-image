/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model.vision.request;

import java.util.List;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class VisionRequestWrapper {

    private List<AnnotateImageRequest> requests;

    public VisionRequestWrapper(List<AnnotateImageRequest> requests) {
        this.requests = requests;
    }

    public List<AnnotateImageRequest> getRequests() {
        return requests;
    }
}
