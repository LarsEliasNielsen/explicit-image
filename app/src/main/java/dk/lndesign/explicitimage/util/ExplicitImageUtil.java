package dk.lndesign.explicitimage.util;

import dk.lndesign.explicitimage.model.vision.response.SafeSearchAnnotation;

/**
 * @author Lars Nielsen <larn@tv2.dk>
 */
public class ExplicitImageUtil {

    /**
     * Get safe search restriction for image {@link SafeSearchAnnotation}.
     * @param safeSearch Image safe search from Vision API.
     * @return True if no restrictions were violated, false otherwise.
     */
    public static boolean getSafeSearchRestricted(SafeSearchAnnotation safeSearch) {
        boolean adultRestriction = SafeSearchAnnotation.LIKELIHOOD_VERY_UNLIKELY.equals(safeSearch.getAdult()) ||
                SafeSearchAnnotation.LIKELIHOOD_UNLIKELY.equals(safeSearch.getAdult());
        boolean spoofRestriction = SafeSearchAnnotation.LIKELIHOOD_VERY_UNLIKELY.equals(safeSearch.getSpoof()) ||
                SafeSearchAnnotation.LIKELIHOOD_UNLIKELY.equals(safeSearch.getSpoof());
        boolean medicalRestriction = SafeSearchAnnotation.LIKELIHOOD_VERY_UNLIKELY.equals(safeSearch.getMedical()) ||
                SafeSearchAnnotation.LIKELIHOOD_UNLIKELY.equals(safeSearch.getMedical());
        boolean violenceRestriction = SafeSearchAnnotation.LIKELIHOOD_VERY_UNLIKELY.equals(safeSearch.getViolence()) ||
                SafeSearchAnnotation.LIKELIHOOD_UNLIKELY.equals(safeSearch.getViolence());

        return (adultRestriction && spoofRestriction && medicalRestriction && violenceRestriction);
    }
}
