package cmpe295.sjsu.edu.mapsio.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import cmpe295.sjsu.edu.mapsio.R;

public class LocationUtils{

    private static LocationUtils locationUtils = new LocationUtils();

    private Place currPlace;

    private boolean mLocationPermissionGranted = false;

    private GoogleMap googleMap;

    private PlaceDetectionClient placeDetectionClient;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private LocationUtils(){}

    public static LocationUtils getInstance() {
        return locationUtils;
    }


    public void getLocationPermission(AppCompatActivity activity) {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mLocationPermissionGranted = false;
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            currPlace = null;
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }



    public void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

            if (mLocationPermissionGranted && placeDetectionClient!=null) {

                Task<PlaceLikelihoodBufferResponse> locationResult = placeDetectionClient.getCurrentPlace(new PlaceFilter());

                locationResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {

                        Place currentPlace = null;

                        if (task.isSuccessful() && task.getResult() != null) {

                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            float maxLikelyVal = 0.0F;

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                if (placeLikelihood.getLikelihood() > maxLikelyVal) {

                                    maxLikelyVal = placeLikelihood.getLikelihood();
                                    currentPlace = placeLikelihood.getPlace().freeze();

                                }

                            }

                            LocationUtils.getInstance().setCurrPlace(currentPlace);
                            enableMyLocation();

                            likelyPlaces.release();
                            Log.d("CURRENT LOCATION", "Current location found.");
                            Log.d(" Current Place Check", "Current location populated.");

                        } else {
                            Log.d("CURRENT LOCATION", "Current location is null. Using defaults.");
                            Log.e("CURRENT LOCATION", "Exception: %s", task.getException());

                        }

                    }
                });

            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    public void enableMyLocation() {

        if (googleMap != null) {

            //update current location when the MyLocationButton is clicker
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Log.d("My Location Button :", "clicked");
                    Log.d("My Location Button :", "If permission granted :" + mLocationPermissionGranted);
                    // Get the current location of the device and set the position of the map.
                    if(mLocationPermissionGranted) {
                        LocationUtils.getInstance().getDeviceLocation();
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }else{
                        googleMap.setMyLocationEnabled(false);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                    return false;
                }
            });
        }
    }

    public boolean ismLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }

    public void setmLocationPermissionGranted(boolean mLocationPermissionGranted) {
        this.mLocationPermissionGranted = mLocationPermissionGranted;
    }

    public Place getCurrPlace() {
        return currPlace;
    }

    public void setCurrPlace(Place currPlace) {
        this.currPlace = currPlace;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }


    public PlaceDetectionClient getPlaceDetectionClient() {
        return placeDetectionClient;
    }

    public void setPlaceDetectionClient(PlaceDetectionClient placeDetectionClient) {
        this.placeDetectionClient = placeDetectionClient;
    }
}
