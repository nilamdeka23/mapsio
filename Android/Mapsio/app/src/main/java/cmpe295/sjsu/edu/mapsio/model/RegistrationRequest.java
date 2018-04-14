package cmpe295.sjsu.edu.mapsio.model;

/**
 * Created by nilamdeka on 3/13/18.
 */

public class RegistrationRequest {

    private String webAuthToken;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String webAuthToken) {
        this.webAuthToken = webAuthToken;
    }

    public String getWebAuthToken() {
        return webAuthToken;
    }

}
