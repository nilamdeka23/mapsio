package cmpe295.sjsu.edu.mapsio.service;

import android.content.Context;

import java.util.List;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.model.AuthRequestModel;
import cmpe295.sjsu.edu.mapsio.model.LatLngModel;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.model.TripRequestModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MapsioService {

    @POST("/api/users/authCode")
    Call<AuthRequestModel> register(@Body AuthRequestModel authRequest);

    @POST("/api/locations")
    Call<LocationMarkerModel> getPlaceDetail(@Body LatLngModel placeDetailRequest);

    @POST("api/users/recommendations")
    Call<List<LocationMarkerModel>> getRecommendedLocations(@Query("userId") String userId,
                                                            @Body LocationMarkerModel locationMarkerModel);

    // TODO: remove this dummy request
    @POST("api/users/recommendation")
    Call<List<LocationMarkerModel>> getRecommendedLocation(@Query("userId") String userId,
                                                           @Body LocationMarkerModel locationMarkerModel);

    @GET("/api/favorites")
    Call<List<LocationMarkerModel>> getFavorites(@Query("userId") String userId);

    @POST("/api/favorites")
    Call<List<LocationMarkerModel>> addFavorite(@Query("userId") String userId, @Body LocationMarkerModel locationMarkerModel);

    @DELETE("/api/favorites")
    Call<List<LocationMarkerModel>> deleteFavorite(@Query("userId") String userId, @Query("locationName") String name);

    @POST("/api/trips")
    Call<TripRequestModel> registerTrip(@Query("userId") String userId, @Body TripRequestModel tripRequestModel);


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
