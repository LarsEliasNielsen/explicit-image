/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model.vision.request;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class Feature {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ TYPE_UNSPECIFIED, TYPE_LABEL_DETECTION, TYPE_SAFE_SEARCH_DETECTION })
    public @interface FeatureType {}

    public static final String TYPE_UNSPECIFIED = "TYPE_UNSPECIFIED";
    public static final String TYPE_LABEL_DETECTION = "LABEL_DETECTION";
    public static final String TYPE_SAFE_SEARCH_DETECTION = "SAFE_SEARCH_DETECTION";

    @FeatureType private String type;
    private int maxResults;

    public Feature(@FeatureType String type, int maxResults) {
        this.type = type;
        this.maxResults = maxResults;
    }

    @FeatureType
    public String getType() {
        return type;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%s { type: %s, maxResults: %d }",
                this.getClass().getSimpleName(), type, maxResults);
    }
}
