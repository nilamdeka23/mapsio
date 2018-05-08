package cmpe295.sjsu.edu.mapsio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatLngModel {

    @SerializedName("lat")
    @Expose
    private Double latitude;

    @SerializedName("lng")
    @Expose
    private Double longitude;

    public LatLngModel() {
    }

    public LatLngModel(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
