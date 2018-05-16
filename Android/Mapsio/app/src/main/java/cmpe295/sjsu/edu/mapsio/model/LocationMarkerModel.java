package cmpe295.sjsu.edu.mapsio.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;

public class LocationMarkerModel implements SearchSuggestion {

    private String address;
    private boolean isFavorite = false;
    private LatLngModel latLng;
    private String name;
    private String placeId;
    private float rating;
    private boolean isPoi = false;

    public LocationMarkerModel() {
    }

    public LocationMarkerModel(String placeId, String name, String address){
        this.placeId = placeId;
        this.name = name;
        this.address = address;
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId, boolean isPoi) {
        this.name = name;
        this.latLng = new LatLngModel(latLng.latitude, latLng.longitude);
        this.placeId = placeId;
        this.isPoi = isPoi;
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId, String address, float rating) {
        this.name = name;
        this.latLng = new LatLngModel(latLng.latitude, latLng.longitude);
        this.placeId = placeId;
        this.address = address;
        this.rating = rating;
    }

    protected LocationMarkerModel(Parcel in) {
        address = in.readString();
        isFavorite = Boolean.valueOf(in.readString());
        latLng = in.readParcelable(LatLngModel.class.getClassLoader());
        name = in.readString();
        placeId = in.readString();
        rating = in.readFloat();
        isPoi = Boolean.valueOf(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(Boolean.toString(isFavorite));
        dest.writeParcelable(latLng,flags);
        dest.writeString(name);
        dest.writeString(placeId);
        dest.writeFloat(rating);
        dest.writeString(Boolean.toString(isPoi));

    }
    public static final Creator<LocationMarkerModel> CREATOR = new Creator<LocationMarkerModel>() {
        @Override
        public LocationMarkerModel createFromParcel(Parcel in) {
            return new LocationMarkerModel(in);
        }

        @Override
        public LocationMarkerModel[] newArray(int size) {
            return new LocationMarkerModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LatLng getLatLng() {
        return new LatLng(latLng.getLatitude(), latLng.getLongitude());
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = new LatLngModel(latLng.latitude, latLng.longitude);
    }

    public boolean isPoi() {
        return isPoi;
    }

    public void setPoi(boolean poi) {
        isPoi = poi;
    }

    @Override
    public String getBody() {

        String dispStr = "<p><font size=\"3\" color=\"white\">"+name+"</font><br/><font size=\"1\" color=\"grey\">"+address+"</font></p>";
        return Html.fromHtml(dispStr).toString();
    }
}
