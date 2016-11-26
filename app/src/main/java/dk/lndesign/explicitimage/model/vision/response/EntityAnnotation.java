/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model.vision.response;

import java.util.Locale;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class EntityAnnotation {

    private String description;
    private String mid;
    private double score;

    public String getDescription() {
        return description;
    }

    public String getMid() {
        return mid;
    }

    public double getScore() {
        return score;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%s { description: %s, score: %f }",
                this.getClass().getSimpleName(),
                description, score);
    }
}
