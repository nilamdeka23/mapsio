package cmpe295.sjsu.edu.mapsio.service;

import android.content.Context;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.model.AuthRequestModel;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.model.PlaceDetailRequestModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MapsioService {

    @POST("/api/users/authCode")
    Call<AuthRequestModel> register(@Body AuthRequestModel authRequest);

    @POST("/api/place/detail")
    Call<LocationMarkerModel> getPlaceDetail(@Body PlaceDetailRequestModel placeDetailRequest);

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
