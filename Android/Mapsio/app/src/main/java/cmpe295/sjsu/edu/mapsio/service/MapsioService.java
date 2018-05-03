package cmpe295.sjsu.edu.mapsio.service;

import android.content.Context;

import java.util.List;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.model.AuthRequestModel;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.model.PlaceDetailRequestModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MapsioService {

    @POST("/api/users/authCode")
    Call<AuthRequestModel> register(@Body AuthRequestModel authRequest);

    @POST("/api/place/detail")
    Call<LocationMarkerModel> getPlaceDetail(@Body PlaceDetailRequestModel placeDetailRequest);

    @GET("/api/trips")
    Call<List<LocationMarkerModel>> getRecommendedLocations(@Query("userId") String userId);

    @GET("/api/favorites")
    Call<List<LocationMarkerModel>> getFavorites(@Query("userId") String userId);


    class Factory {
        public static MapsioService create(Context context) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.server_base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(MapsioService.class);
        }
    }

}
