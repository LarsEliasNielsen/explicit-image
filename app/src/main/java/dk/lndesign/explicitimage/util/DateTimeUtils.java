package dk.lndesign.explicitimage.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Lars Nielsen <larn@tv2.dk>
 */
public class DateTimeUtils {

    public static final String FORMAT_FULL = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static String getFormattedDate(String dateFormat, long timeStamp) {
        return getFormattedDate(dateFormat, new Date(timeStamp));
    }

    public static String getFormattedDate(String dateFormat, Date date) {
        DateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return sdf.format(date);
    }
}
