package cmpe295.sjsu.edu.mapsio.model;

import com.google.android.gms.maps.model.LatLng;

public class LocationMarkerModel {

    private String address;
    private String imageURL;
    private boolean isFavorite;
    private double latitide;
    private double longitude;
    private String name;
    private String placeId;
    private float rating;

    public LocationMarkerModel() {
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId) {
        this.name = name;
        this.latitide = latLng.latitude;
        this.longitude = latLng.longitude;
        this.placeId = placeId;
    }

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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

    public double getLatitide() {
        return latitide;
    }

    public void setLatitide(double latitide) {
        this.latitide = latitide;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

}
