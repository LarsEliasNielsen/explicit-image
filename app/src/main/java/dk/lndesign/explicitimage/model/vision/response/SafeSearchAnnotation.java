/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model.vision.response;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Model class for Google safe search.
 *
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class SafeSearchAnnotation {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ LIKELIHOOD_UNKNOWN, LIKELIHOOD_VERY_UNLIKELY, LIKELIHOOD_UNLIKELY, LIKELIHOOD_POSSIBLE, LIKELIHOOD_LIKELY, LIKELIHOOD_VERY_LIKELY })
    public @interface Likelihood {}

    public static final String LIKELIHOOD_UNKNOWN = "UNKNOWN";
    public static final String LIKELIHOOD_VERY_UNLIKELY = "VERY_UNLIKELY";
    public static final String LIKELIHOOD_UNLIKELY = "UNLIKELY";
    public static final String LIKELIHOOD_POSSIBLE = "POSSIBLE";
    public static final String LIKELIHOOD_LIKELY = "LIKELY";
    public static final String LIKELIHOOD_VERY_LIKELY = "VERY_LIKELY";

    private String adult;
    private String spoof;
    private String medical;
    private String violence;

    @Likelihood
    public String getAdult() {
        return adult;
    }

    @Likelihood
    public String getSpoof() {
        return spoof;
    }

    @Likelihood
    public String getMedical() {
        return medical;
    }

    @Likelihood
    public String getViolence() {
        return violence;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%s { adult: %s, spoof: %s, medical: %s, violence: %s }",
                this.getClass().getSimpleName(), adult, spoof, medical, violence);
    }
}
