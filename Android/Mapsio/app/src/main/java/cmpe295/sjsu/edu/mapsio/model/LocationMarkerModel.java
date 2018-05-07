package cmpe295.sjsu.edu.mapsio.model;

import com.google.android.gms.maps.model.LatLng;

public class LocationMarkerModel {

    private String address;
    private String imageURL;
    private boolean isFavorite = false;
    private LatitudeLongitude latLng;
    private String name;
    private String placeId;
    private float rating;
    private boolean isPoi = false;

    public LocationMarkerModel() {
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId, boolean isPoi) {
        this.name = name;
        this.latLng = new LatitudeLongitude(latLng.latitude, latLng.longitude);
        this.placeId = placeId;
        this.isPoi = isPoi;
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId, String address, boolean isFavorite) {
        this.name = name;
        this.latLng = new LatitudeLongitude(latLng.latitude, latLng.longitude);
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LatLng getLatLng() {
        return new LatLng(latLng.latitide, latLng.longitude);
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = new LatitudeLongitude(latLng.latitude, latLng.longitude);
    }

    public boolean isPoi() {
        return isPoi;
    }

    public void setPoi(boolean poi) {
        isPoi = poi;
    }


    private class LatitudeLongitude {

        LatitudeLongitude() {
        }

        LatitudeLongitude(double latitide, double longitude) {
            this.latitide = latitide;
            this.longitude = longitude;
        }

        private double latitide;
        private double longitude;

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

    }

}
