/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.app.Application;

import dk.lndesign.explicitimage.service.VisionService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Lars Nielsen <lars@lndesign.dk>.
 */
public class ExplicitApplication extends Application {

    private static ExplicitApplication instance;
    private VisionService mVisionService;

    public static ExplicitApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://vision.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mVisionService = retrofit.create(VisionService.class);
    }

    public VisionService getVisionService() {
        return mVisionService;
    }
}
