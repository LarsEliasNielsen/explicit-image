/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * @author Lars Nielsen <lars@lndesign.dk>.
 */
public class Base64Utils {

    /**
     * Convert an encoded base 64 image to a bitmap.
     * @param encodedImage Encoded image.
     * *
     * @return Bitmap from encoded image.
     */
    public static Bitmap base64ToBitmap(String encodedImage) {
        // Decode image string.
        byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);
        // Create bitmap from decoded image.
        return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    }

    /**
     * Convert a drawable into an encoded base 64 image.
     * @param drawable Drawable.
     * @return Encoded image.
     */
    public static String drawableToBase64(Drawable drawable) {
        // Convert to bitmap.
        Bitmap bitmap = Base64Utils.drawableToBitmap(drawable);
        // Get bitmap bytes.
        byte[] imageBytes = Base64Utils.imageToByteArray(bitmap);
        // Get encoded image.
        return base64Image(imageBytes);
    }

    /**
     * Convert a bitmap into en encoded base 64 image.
     * @param bitmap Bitmap.
     * @return Encoded image.
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        // Get bitmap bytes.
        byte[] imageBytes = Base64Utils.imageToByteArray(bitmap);
        // Get encoded image.
        return base64Image(imageBytes);
    }

    /**
     * Convert bitmap image to byte array.
     * @param image Bitmap image.
     * *
     * @return Byte array of bitmap image.
     */
    private static byte[] imageToByteArray(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Convert a byte array from a image to a encoded base 64 image.
     * @param imageByteArray Image byte array.
     * *
     * @return Encoded image.
     */
    private static String base64Image(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    /**
     * Convert a drawable into a bitmap.
     * @param drawable Drawable.
     * *
     * @return Bitmap.
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
