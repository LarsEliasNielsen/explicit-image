/**
 * Copyright (C) 2016 TV 2 Danmark A/S.
 */
package dk.lndesign.explicitimage.model.vision.response;

import java.util.List;

/**
 * Created by larn on 15/11/2016.
 *
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class VisionResultWrapper {

    private List<AnnotateImageResponse> responses;

    public List<AnnotateImageResponse> getResponses() {
        return responses;
    }
}
