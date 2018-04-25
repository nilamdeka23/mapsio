package cmpe295.sjsu.edu.mapsio.controller;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cmpe295.sjsu.edu.mapsio.R;

// https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java
//https://gist.github.com/ccabanero/6996756
public class GoogleMapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // will be needed later
    private GoogleMap googleMap;
    // provides access to Google's database of local place and business information.
    private GeoDataClient mGeoDataClient;
    // provides quick access to the device's current place, and offers the opportunity to report the
    // location of the device at a particular place.
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    //current place where the device is located
    private Place currentPlace;

    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Prompt the user for permission.
        getLocationPermission();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final StringBuffer searchText = new StringBuffer();

        FloatingSearchView mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                // TODO: null check on map and map lifecycle methods
                Log.d("Search Text Changed","Map cleared");
                //googleMap.clear();
                //Log.d("Search text: oldQuery: ", oldQuery);
                //Log.d("Search text: newQuery: ", newQuery);
                //search(newQuery);
                searchText.delete(0, searchText.length()) ;
                searchText.append(newQuery);
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                    Log.d("Search Bar:" ,"Search icon clicked");
                    Log.d("Search text:",searchText.toString());
                    googleMap.clear();
                    search(searchText.toString());
            }
        });

        //mSearchView.attachNavigationDrawerToMenuButton(drawer);
        //this is for hamburger icon
        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {

                    @Override
                    public void onMenuOpened() {
                        Log.d("Search Bar", "Opened");


                    }

                    @Override
                    public void onMenuClosed() {
                        Log.d("Search Bar", "Closed");
                    }
                });

    }



    public void search(String searchQuery) {
        Log.d("Search text", searchQuery);

        //getLocationPermission();
        //getDeviceLocation();


        /*Task<AutocompletePredictionBufferResponse> results =
                mGeoDataClient.getAutocompletePredictions(searchQuery,
                        new LatLngBounds(new LatLng(32.715738, -117.161084),
                                new LatLng(38.581572, -121.494400)), null);*/

        Task<AutocompletePredictionBufferResponse> results = null;
        if(currentPlace!=null) {
            LatLng latLng = new LatLng(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude);
            LatLngBounds latlngBounds = new LatLngBounds(latLng,latLng);


            results =
                    mGeoDataClient.getAutocompletePredictions(searchQuery,
                            latlngBounds, null);

            results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {

                @Override
                public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {

                    if(task.isSuccessful() && task.getResult()!=null) {

                        //Holder for the place Ids
                        ArrayList<String> placeIdList = new ArrayList<>();

                        AutocompletePredictionBufferResponse response = task.getResult();

                        for (AutocompletePrediction prediction : response) {
                            placeIdList.add(prediction.getPlaceId());
                            Log.d("Predicted places: ", prediction.getPrimaryText(null).toString());

                        }

                        markPlaces(placeIdList);
                        response.release();

                    }
                }
            });

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlace.getLatLng()));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(6));


        }else{
            //TODO : things to do if the current location is not known
            Log.d("Search" , "Current Place is null");


        }
    }


    private void markPlaces(ArrayList<String> placeIdList) {

        for (String placeId : placeIdList) {

            Task<PlaceBufferResponse> result = mGeoDataClient.getPlaceById(placeId);
            result.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {


                    if(task.isSuccessful() && task.getResult()!=null) {
                        PlaceBufferResponse response = task.getResult();
                        Place currPlace = response.get(0);

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(currPlace.getLatLng());
                        //markerOptions.title(currPlace.getAddress().toString());
                        markerOptions.title(currPlace.getName().toString());


                        // TODO: null check on map and map lifecycle methods
                        if(googleMap!=null) {
                            googleMap.addMarker(markerOptions);
                        }

                        response.release();
                    }
                }
            });

        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //this ID needs to be renamed to favorites
        if (id == R.id.nav_camera) {
            //Log.d("Navigation menu:" ,"Favorites clicked");

            Intent intent = new Intent(GoogleMapsActivity.this, FavoritesActivity.class);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        /*} else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {*/

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Prompt the user for permission.
        getLocationPermission();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }else{

                    //TODO : Snackbar for permission denial
                }
            }
        }
        // TODO: inform user of permission need
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

            if (mLocationPermissionGranted) {

                Task<PlaceLikelihoodBufferResponse> locationResult = mPlaceDetectionClient.getCurrentPlace(new PlaceFilter());

                locationResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {

                        if(task.isSuccessful() && task.getResult() != null){

                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            float maxLikelyVal = 0.0F;

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                if(placeLikelihood.getLikelihood() > maxLikelyVal){

                                    maxLikelyVal = placeLikelihood.getLikelihood();
                                    currentPlace = placeLikelihood.getPlace().freeze();

                                }

                            }

                            likelyPlaces.release();
                            Log.d("CURRENT LOCATION", "Current location found.");
                        }else{
                            Log.d("CURRENT LOCATION", "Current location is null. Using defaults.");
                            Log.e("CURRENT LOCATION", "Exception: %s", task.getException());

                        }

                    }
                });


                //Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
              /*  locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Log.d("CURRENT LOCATION", "Current location is found.");
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

}
