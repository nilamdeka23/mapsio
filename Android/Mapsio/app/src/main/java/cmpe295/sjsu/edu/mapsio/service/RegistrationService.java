package cmpe295.sjsu.edu.mapsio.service;

import cmpe295.sjsu.edu.mapsio.model.RegistrationRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by nilamdeka on 3/13/18.
 */

public interface RegistrationService {

    @POST("/alerts")
    Call<ResponseModel> register(@Body RegistrationRequest registrationRequest);
}