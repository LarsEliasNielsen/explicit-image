/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import dk.lndesign.explicitimage.BuildConfig;
import dk.lndesign.explicitimage.ExplicitApplication;
import dk.lndesign.explicitimage.model.vision.request.AnnotateImageRequest;
import dk.lndesign.explicitimage.model.vision.request.Feature;
import dk.lndesign.explicitimage.model.vision.request.Image;
import dk.lndesign.explicitimage.model.vision.request.VisionRequestWrapper;
import dk.lndesign.explicitimage.model.vision.response.VisionResultWrapper;
import dk.lndesign.explicitimage.service.VisionService;
import dk.lndesign.explicitimage.util.Base64Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class VisionController {

    private static final String LOG_TAG = VisionController.class.getSimpleName();

    private VisionService mService;

    private Call<VisionResultWrapper> mAnnotationCall;

    public VisionController() {
        mService = ExplicitApplication.getInstance().getVisionService();
    }

    public interface LoadingCallback<T> {
        void onDataLoaded(T data);
        void onFailed();
    }

    /**
     * Annotate image with Google Vision API.
     * @param drawable Drawable to annotate.
     * @param callback Response callback.
     */
    public void annotateImage(Drawable drawable, final LoadingCallback<VisionResultWrapper> callback) {
        mAnnotationCall = mService.annotateImage(
                buildVisionRequest(drawable),
                BuildConfig.KEY
        );

        mAnnotationCall.enqueue(new Callback<VisionResultWrapper>() {
            @Override
            public void onResponse(Call<VisionResultWrapper> call, Response<VisionResultWrapper> response) {
                if (call.isCanceled() || response.body() == null) {
                    handleError("Image annotation", response, call, null);
                    callback.onFailed();
                } else {
                    callback.onDataLoaded(response.body());
                }

                mAnnotationCall = null;
            }

            @Override
            public void onFailure(Call<VisionResultWrapper> call, Throwable t) {
                handleError("Image annotation", null, call, t);

                if (!call.isCanceled()) {
                    callback.onFailed();
                }

                mAnnotationCall = null;
            }
        });
    }

    /**
     * Annotate image with Google Vision API.
     * @param bitmap Bitmap to annotate.
     * @param callback Response callback.
     */
    public void annotateImage(Bitmap bitmap, final LoadingCallback<VisionResultWrapper> callback) {
        mAnnotationCall = mService.annotateImage(
                buildVisionRequest(bitmap),
                BuildConfig.KEY
        );

        mAnnotationCall.enqueue(new Callback<VisionResultWrapper>() {
            @Override
            public void onResponse(Call<VisionResultWrapper> call, Response<VisionResultWrapper> response) {
                if (call.isCanceled() || response.body() == null) {
                    handleError("Image annotation", response, call, null);
                    callback.onFailed();
                } else {
                    callback.onDataLoaded(response.body());
                }

                mAnnotationCall = null;
            }

            @Override
            public void onFailure(Call<VisionResultWrapper> call, Throwable t) {
                handleError("Image annotation", null, call, t);

                if (!call.isCanceled()) {
                    callback.onFailed();
                }

                mAnnotationCall = null;
            }
        });
    }

    /**
     * Build Vision request for image annotation using Vision label detection and safe search.
     * Image annotation request body contains image and feature request for safe search and label detection.
     * @param drawable Drawable to include in request.
     * @return Vision request wrapper for request body.
     */
    private VisionRequestWrapper buildVisionRequest(Drawable drawable) {
        String encodedImage = Base64Utils.drawableToBase64(drawable);
        Image image = new Image(encodedImage);
        List<Feature> features = Arrays.asList(new Feature(Feature.TYPE_SAFE_SEARCH_DETECTION, 1), new Feature(Feature.TYPE_LABEL_DETECTION, 10));
        return new VisionRequestWrapper(Arrays.asList(new AnnotateImageRequest(image, features)));
    }

    /**
     * Build Vision request for image annotation using Vision label detection and safe search.
     * Image annotation request body contains image and feature request for safe search and label detection.
     * @param bitmap Bitmap in include in request.
     * @return Vision request wrapper for request body.
     */
    private VisionRequestWrapper buildVisionRequest(Bitmap bitmap) {
        String encodedImage = Base64Utils.bitmapToBase64(bitmap);
        Image image = new Image(encodedImage);
        List<Feature> features = Arrays.asList(new Feature(Feature.TYPE_SAFE_SEARCH_DETECTION, 1), new Feature(Feature.TYPE_LABEL_DETECTION, 10));
        return new VisionRequestWrapper(Arrays.asList(new AnnotateImageRequest(image, features)));
    }

    /**
     * Handles error from request.
     *
     * @param identification Some unique string identifying the request.
     * @param response       Response of the call, can be null.
     * @param call           Call that failed.
     * @param throwable      Exception that was thrown.
     */
    private void handleError(@NonNull String identification, @Nullable Response response,
                             @NonNull Call call, @Nullable Throwable throwable) {
        if (call.isCanceled()) {
            Log.w(LOG_TAG, identification + " request was canceled");
        } else {
            if (response != null) {
                Log.e(LOG_TAG, identification + " request failed with: " + response.message() + ", code: " + response.code());
            } else {
                Log.e(LOG_TAG, identification + " request failed");
            }

            Log.e(LOG_TAG, "Url: " + call.request().url(), throwable);
        }
    }

    /**
     * Cancel any ongoing loading.
     */
    public void cancelLoading(){
        if (mAnnotationCall != null) {
            mAnnotationCall.cancel();
            mAnnotationCall = null;
        }
    }
}
