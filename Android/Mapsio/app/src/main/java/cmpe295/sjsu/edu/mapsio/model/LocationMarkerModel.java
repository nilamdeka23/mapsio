package cmpe295.sjsu.edu.mapsio.model;

import com.google.android.gms.maps.model.LatLng;

public class LocationMarkerModel {

    private String address;
    private String imageURL;
    private boolean isFavorite;
    private LatitudeLongitude latLng;
    private String name;
    private String placeId;
    private float rating;

    public LocationMarkerModel() {
    }

    public LocationMarkerModel(String name, LatLng latLng, String placeId) {
        this.name = name;
        this.latLng = new LatitudeLongitude(latLng.latitude, latLng.longitude);
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


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
