package cmpe295.sjsu.edu.mapsio.model;

import com.google.android.gms.maps.model.LatLng;

public class LocationMarkerModel {

    private String address;
    private boolean isFavorite = false;
    private LatLngModel latLng;
    private String name;
    private String placeId;
    private float rating;
    private boolean isPoi = false;

    public LocationMarkerModel() {
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId, boolean isPoi) {
        this.name = name;
        this.latLng = new LatLngModel(latLng.latitude, latLng.longitude);
        this.placeId = placeId;
        this.isPoi = isPoi;
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId, String address, boolean isFavorite) {
        this.name = name;
        this.latLng = new LatLngModel(latLng.latitude, latLng.longitude);
        this.placeId = placeId;
        this.address = address;
        this.isFavorite = isFavorite;
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

}
