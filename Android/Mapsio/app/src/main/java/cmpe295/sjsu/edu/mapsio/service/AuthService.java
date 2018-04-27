package cmpe295.sjsu.edu.mapsio.service;

import cmpe295.sjsu.edu.mapsio.model.AuthRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by nilamdeka on 3/13/18.
 */

public interface AuthService {

    @POST("/api/users/authCode")
    Call<AuthRequest> register(@Body AuthRequest authRequest);
}