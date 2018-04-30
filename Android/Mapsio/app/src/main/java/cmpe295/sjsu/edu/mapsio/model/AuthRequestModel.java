package cmpe295.sjsu.edu.mapsio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nilamdeka on 3/13/18.
 */

public class AuthRequestModel {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("authCode")
    @Expose
    private String authCode;

    public AuthRequestModel() {
    }

    public AuthRequestModel(String id, String authCode) {
        this.id = id;
        this.authCode = authCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

}
