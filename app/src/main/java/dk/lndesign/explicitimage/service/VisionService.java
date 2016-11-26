/**
 * Copyright (C) 2016 TV 2 Danmark A/S.
 */
package dk.lndesign.explicitimage.service;

import dk.lndesign.explicitimage.model.vision.request.VisionRequestWrapper;
import dk.lndesign.explicitimage.model.vision.response.VisionResultWrapper;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by larn on 15/11/2016.
 *
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public interface VisionService {
    @POST("/v1/images:annotate")
    Call<VisionResultWrapper> annotateImage(@Body VisionRequestWrapper body, @Query("key") String key);
}
