package cmpe295.sjsu.edu.mapsio.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.controller.adapter.RecommendationsViewAdapter;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;

// https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java
// https://gist.github.com/ccabanero/6996756
// https://github.com/arimorty/floatingsearchview

public class GoogleMapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnPoiClickListener, GoogleMap.OnMapLongClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private GoogleMap googleMap;
    // provides access to Google's database of local place and business information.
    private GeoDataClient mGeoDataClient;
    // provides quick access to the device's current place, and offers the opportunity to report the
    // location of the device at a particular place.
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private boolean mLocationPermissionGranted;

    //current place where the device is located
    private Place currentPlace;

    private static final String KEY_LOCATION = "location";

    private ArrayList<String> Number;
    private View ChildView;
    private int RecyclerViewItemPosition;
    private Marker marker;
    private Map<String, LocationMarkerModel> markerMap;
    private FloatingSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init local marker dictionary/hashmap
        markerMap = new HashMap<>();

        setContentView(R.layout.activity_main);
        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Prompt the user for permission.
        getLocationPermission();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // init map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Save search text in the search bar
        final StringBuffer searchText = new StringBuffer();

        // init drawer layout
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // init navigation menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Search Bar
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);

        //Listener for search text changes
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                searchText.delete(0, searchText.length());
                searchText.append(newQuery);
            }
        });


        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Log.d("Search Bar","onSuggestionClicked");

            }

            @Override
            public void onSearchAction(String currentQuery) {
                googleMap.clear();
                search(searchText.toString());
            }
        });

        //Listener for clicks on the mic in the search bar
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                Log.d("Mic clicked", item.getTitle().toString());
                startVoiceRecognition();
            }

        });

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String pic = getIntent().getStringExtra("profile_url");

        View header = navigationView.getHeaderView(0);

        TextView nameTextView = (TextView) header.findViewById(R.id.username_textView);
        nameTextView.setText(name);
        TextView emailTextView = (TextView) header.findViewById(R.id.email_textView);
        emailTextView.setText(email);
        ImageView profilePicImageView = (ImageView) header.findViewById(R.id.account_imageView);
        Picasso.with(this).load(pic).into(profilePicImageView);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recommendations_recyclerView);
        // Adding items to RecyclerView.
        AddItemsToRecyclerViewArrayList();

        RecommendationsViewAdapter RecyclerViewHorizontalAdapter = new RecommendationsViewAdapter(Number);
        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);

        // Adding on item click listener to RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(GoogleMapsActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    // Toast.makeText(GoogleMapsActivity.this, "tap up", Toast.LENGTH_LONG).show();
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting clicked value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);

                    // Showing clicked item value on screen using toast message.
                    Toast.makeText(GoogleMapsActivity.this, Number.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

        });

    }

    // function to add items in RecyclerView.
    public void AddItemsToRecyclerViewArrayList() {

        Number = new ArrayList<>();
        Number.add("ONEAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaa");
        Number.add("TWO");
        Number.add("THREE");
        Number.add("FOUR");
        Number.add("FIVE");
        Number.add("SIX");
        Number.add("SEVEN");
        Number.add("EIGHT");
        Number.add("NINE");
        Number.add("TEN");
    }

    public void search(final String searchQuery) {
        //If location permission is not granted try getting it
        if (!mLocationPermissionGranted) {
            getLocationPermission();
            getDeviceLocation();
        }

        Task<AutocompletePredictionBufferResponse> results;

        if (currentPlace != null) {

            //this is just taking the center
            LatLng latLng = new LatLng(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude);
            LatLngBounds latlngBounds = new LatLngBounds(latLng, latLng);

            results = mGeoDataClient.getAutocompletePredictions(searchQuery, latlngBounds, null);
        } else {

            results = mGeoDataClient.getAutocompletePredictions(searchQuery, null, null);
        }

        results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {

            @Override
            public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {

                if (task.isSuccessful() && task.getResult() != null) {

                    //Holder for the place Ids
                    ArrayList<String> placeIdList = new ArrayList<>();
                    AutocompletePredictionBufferResponse response = task.getResult();

                    for (AutocompletePrediction prediction : response) {
                        placeIdList.add(prediction.getPlaceId());
                        Log.d("Predicted places: ", prediction.getPrimaryText(null).toString());

                    }

                    markPlaces(placeIdList, searchQuery);
                    response.release();
                }
            }
        });

    }

    private void markPlaces(ArrayList<String> placeIdList, String searchQuery) {

        //save the place whose name matches the search query
        Place mostLikelyPlaceByName = null;
        //save the first place in the list
        Place firstPlace = null;

        for (int i = 0; i < placeIdList.size(); i++) {
            Task<PlaceBufferResponse> result = mGeoDataClient.getPlaceById(placeIdList.get(i));

            while (!result.isComplete()) {} // hack to make things synchronous

            PlaceBufferResponse response = result.getResult();
            Place tempPlace = response.get(0);

            //save the first place
            if (i == 0) {
                firstPlace = response.get(0).freeze();
            }

            //save the place if the name of the place matches the search query
            if (searchQuery.equalsIgnoreCase(tempPlace.getName().toString())) {
                mostLikelyPlaceByName = response.get(0).freeze();
            }

            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.position(tempPlace.getLatLng());

            markerOptions.title(tempPlace.getName().toString());

            if (googleMap != null) {
                googleMap.addMarker(markerOptions);
            }

            response.release();
        }

        if (mostLikelyPlaceByName == null) {
            mostLikelyPlaceByName = firstPlace;
        }

        if (currentPlace != null) {

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            // 100 miles -> 160934 meters
            // 50 miles -> 80467.2
            LatLngBounds bounds = toBounds(currentPlace.getLatLng(), 80467.2);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width - 200, height - 700, 10);

            if (googleMap != null) {
                googleMap.animateCamera(cu);
            }
        } else {

            Log.d("markPlaces : ", "Current Place is null");
            if (googleMap != null && mostLikelyPlaceByName != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mostLikelyPlaceByName.getLatLng(), 10));
            }
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

            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // TODO: setMyLocationEnabled(true);
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnPoiClickListener(this);

        googleMap.setOnMarkerClickListener(this);

        googleMap.setOnMapLongClickListener(this);

        if (mLocationPermissionGranted) {
            enableMyLocation();
        } else {
            disableMyLocation();
        }
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        LocationMarkerModel locationMarkerModel = markerMap.get(marker.getId());
        if (locationMarkerModel.getPlaceId() != null) {

            Task<PlaceBufferResponse> result = mGeoDataClient.getPlaceById(locationMarkerModel.getPlaceId());
            result.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                    if (task.isSuccessful() && task.getResult() != null) {
                        PlaceBufferResponse response = task.getResult();
                        Place currPlace = response.get(0);

                        response.release();
                    }
                }
            });

        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        // Clears the previously touched position
        if (googleMap != null)
            googleMap.clear();

        // Creating a marker
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(poi.latLng)
                .title(poi.name + " : " + poi.placeId));

        markerMap.put(marker.getId(), new LocationMarkerModel(poi.name, poi.latLng, poi.placeId));

        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
//        MapsioService mapsioService = MapsioService.Factory.create(this);
//        Call<LocationMarkerModel> placeDetailRequestCall = mapsioService.getPlaceDetail(new PlaceDetailRequestModel(latLng.latitude,
//                latLng.longitude));
//
//        placeDetailRequestCall.enqueue(new Callback<LocationMarkerModel>() {
//            @Override
//            public void onResponse(Call<LocationMarkerModel> call, Response<LocationMarkerModel> response) {
//                Log.d("RESPONSE", "RESPONSE" + response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<LocationMarkerModel> call, Throwable t) {
//                Log.d("FAILURE", "FAILURE" + t.toString());
//            }
//
//        });

        // Clears the previously touched position
        if (googleMap != null)
            googleMap.clear();

        // Creating a marker
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(latLng.latitude + " : " + latLng.longitude));

        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
            enableMyLocation();
        } else {
            mLocationPermissionGranted = false;
            disableMyLocation();
            currentPlace = null;
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
                    enableMyLocation();
                } else {
                    mLocationPermissionGranted = false;
                    disableMyLocation();
                    currentPlace = null;
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

                        if (task.isSuccessful() && task.getResult() != null) {

                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            float maxLikelyVal = 0.0F;

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                if (placeLikelihood.getLikelihood() > maxLikelyVal) {

                                    maxLikelyVal = placeLikelihood.getLikelihood();
                                    currentPlace = placeLikelihood.getPlace().freeze();

                                }

                            }

                            likelyPlaces.release();
                            Log.d("CURRENT LOCATION", "Current location found.");
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
    private void enableMyLocation() {

        if(googleMap!=null) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    private void disableMyLocation() {

        if(googleMap!=null) {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }


    final int VOICE_SEARCH_CODE = 3012;

    public void startVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        intent.putExtra("android.speech.extra.PROMPT", "Speak Now");
        this.startActivityForResult(intent, VOICE_SEARCH_CODE);
    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_SEARCH_CODE && resultCode == RESULT_OK) {
            ArrayList < String > matches = data
                    .getStringArrayListExtra("android.speech.extra.RESULTS");
            if(mSearchView!=null) {

                mSearchView.setSearchText(matches.get(0));
                search(matches.get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
