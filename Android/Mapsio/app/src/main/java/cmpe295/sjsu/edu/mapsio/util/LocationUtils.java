package cmpe295.sjsu.edu.mapsio.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import cmpe295.sjsu.edu.mapsio.model.LatLngModel;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.service.MapsioService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class LocationUtils{

    private static LocationUtils instance = null;

    private LocationUtils() {
    }

    public static LocationUtils getInstance() {
        if (instance == null) {
            instance = new LocationUtils();
        }
        return instance;
    }

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;

    private LocationMarkerModel currPlace;
    private GoogleMap googleMap;
    private PlaceDetectionClient placeDetectionClient;
    private ICurrentLocationService currentLocationService;
    private FusedLocationProviderClient fusedLocationProviderClient;



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
            getDeviceLocation(activity);
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

    public void getDeviceLocation(final AppCompatActivity activity) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

            if (mLocationPermissionGranted && fusedLocationProviderClient!=null) {
                /*fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d("Current device latitude",""+location.getLatitude());
                            // Get place details from the web service and update the currPlace object
                            updateCurrentLocation(applicationContext, location);
                        }

                    }
                });*/
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(activity,new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if(task.isComplete() && task.isSuccessful()){
                            Location location = task.getResult();
                            if(location!=null) {
                                updateCurrentLocation(activity, location);
                                enableMyLocation(activity);
                                //TODO: discuss with Nilam as now currPlace will be updated asynchrnously
                                currentLocationService.onCurrentLocationReceived(currPlace);
                            }
                        }

                    }
                });

               /* Task<PlaceLikelihoodBufferResponse> locationResult = placeDetectionClient.getCurrentPlace(new PlaceFilter());

                locationResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {

                        currPlace = null;

                        if (task.isSuccessful() && task.getResult() != null) {

                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            float maxLikelyVal = 0.0F;

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                if (placeLikelihood.getLikelihood() > maxLikelyVal) {

                                    maxLikelyVal = placeLikelihood.getLikelihood();
                                    currPlace = placeLikelihood.getPlace().freeze();
                                }

                            }
                            currentLocationService.onCurrentLocationReceived(currPlace);
                            enableMyLocation();

                            likelyPlaces.release();
                            Log.d("CURRENT LOCATION", "Current location found.");
                            Log.d(" Current Place Check", "Current location populated.");

                        } else {
                            Log.d("CURRENT LOCATION", "Current location is null. Using defaults.");
                            Log.e("CURRENT LOCATION", "Exception: %s", task.getException());

                        }

                    }
                });*/

            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    public void enableMyLocation(final AppCompatActivity activity) {
        if (googleMap != null) {

            //update current location when the MyLocationButton is clicker
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Log.d("My Location Button :", "clicked");
                    Log.d("My Location Button :", "If permission granted :" + mLocationPermissionGranted);
                    // Get the current location of the device and set the position of the map.
                    if(mLocationPermissionGranted) {
                        LocationUtils.getInstance().getDeviceLocation(activity);
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

    public void updateCurrentLocation(Context context, Location location) {

        MapsioService mapsioService = MapsioService.Factory.create(context);
        Call<LocationMarkerModel> placeDetailRequestCall = mapsioService.getPlaceDetail(new LatLngModel(location.getLatitude(),
                location.getLongitude()));

        placeDetailRequestCall.enqueue(new Callback<LocationMarkerModel>() {
            @Override
            public void onResponse(Call<LocationMarkerModel> call, Response<LocationMarkerModel> response) {
                Log.d("RESPONSE", "RESPONSE" + response.toString());
                LocationMarkerModel updatedLocationLocation = response.body();
                if(updatedLocationLocation!=null) {
                    Log.d("Place Id of curr loc",""+updatedLocationLocation.getPlaceId());
                    currPlace = updatedLocationLocation;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(currPlace.getLatLng()));
                }
            }

            @Override
            public void onFailure(Call<LocationMarkerModel> call, Throwable t) {
                Log.d("FAILURE", "FAILURE" + t.toString());
            }

        });
    }

    /* GETTERS AND SETTERS */
    public boolean ismLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }

    public void setmLocationPermissionGranted(boolean mLocationPermissionGranted) {
        this.mLocationPermissionGranted = mLocationPermissionGranted;
    }

    public LocationMarkerModel getCurrPlace() {
        return currPlace;
    }

    public void setCurrPlace(LocationMarkerModel currPlace) {
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

    public ICurrentLocationService getCurrentLocationService() {
        return currentLocationService;
    }

    public void setCurrentLocationService(Context context) {
        this.currentLocationService = (ICurrentLocationService) context;
    }

    public FusedLocationProviderClient getFusedLocationProviderClient() {
        return fusedLocationProviderClient;
    }

    public void setFusedLocationProviderClient(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }



}
