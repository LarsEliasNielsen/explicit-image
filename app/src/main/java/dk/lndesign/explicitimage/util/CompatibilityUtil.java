package dk.lndesign.explicitimage.util;

import android.content.Context;
import android.content.res.Configuration;

/**
 * @author Lars Nielsen <larn@tv2.dk>
 */
public class CompatibilityUtil {

    private CompatibilityUtil() {}

    /**
     * Determine if the device is a tablet (i.e. it has a large screen).
     *
     * @param context The calling context.
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
