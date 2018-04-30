package cmpe295.sjsu.edu.mapsio.model;

import com.google.android.gms.maps.model.LatLng;

public class LocationMarkerModel {

    private String address;
    private String imageURL;
    private boolean isFavorite;
    private LatLng latLng;
    private String name;
    private String placeId;

    public LocationMarkerModel() {
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId) {
        this.name = name;
        this.latLng = latLng;
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


    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

}
