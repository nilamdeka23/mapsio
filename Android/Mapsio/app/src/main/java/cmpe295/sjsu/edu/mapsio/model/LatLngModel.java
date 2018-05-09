package cmpe295.sjsu.edu.mapsio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatLngModel implements Parcelable {

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

    protected LatLngModel(Parcel in) {

            latitude = Double.valueOf(in.readString());

            longitude = Double.valueOf( in.readString());

    }

    public static final Creator<LatLngModel> CREATOR = new Creator<LatLngModel>() {
        @Override
        public LatLngModel createFromParcel(Parcel in) {
            return new LatLngModel(in);
        }

        @Override
        public LatLngModel[] newArray(int size) {
            return new LatLngModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(String.valueOf(latitude));
        dest.writeString(String.valueOf(longitude));
    }


}
