package cmpe295.sjsu.edu.mapsio.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.controller.adapter.RecommendationsViewAdapter;
import cmpe295.sjsu.edu.mapsio.model.LatLngModel;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.service.MapsioService;
import cmpe295.sjsu.edu.mapsio.util.ICurrentLocationService;
import cmpe295.sjsu.edu.mapsio.util.IPlacePhoto;
import cmpe295.sjsu.edu.mapsio.util.LocationUtils;
import cmpe295.sjsu.edu.mapsio.util.MapsioUtils;
import cmpe295.sjsu.edu.mapsio.view.CustomMapFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java
// https://gist.github.com/ccabanero/6996756
// https://github.com/arimorty/floatingsearchview

public class GoogleMapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnPoiClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, ICurrentLocationService {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int VOICE_SEARCH_CODE = 3012;

    private GoogleMap googleMap;
    // provides access to Google's database of local place and business information.
    private GeoDataClient geoDataClient;

    private ArrayList<LocationMarkerModel> recommendedLocations = new ArrayList<>();
    private Map<String, LocationMarkerModel> markerMap;

    private View childView;
    private View markerDescLayout;
    private RecyclerView recommendationsRecyclerView;
    private RecommendationsViewAdapter recommendationsViewAdapter;
    private FloatingSearchView searchView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init local marker dictionary/hashmap
        markerMap = new HashMap<>();
        // init marker description layout
        markerDescLayout = findViewById(R.id.marker_desc_layout);
        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        geoDataClient = Places.getGeoDataClient(this, null);
        MapsioUtils.getInstance().setGeoDataClient(geoDataClient);
        // provides quick access to the device's current place, and offers the opportunity to report the
        // location of the device at a particular place.
        PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        LocationUtils.getInstance().setPlaceDetectionClient(placeDetectionClient);
        LocationUtils.getInstance().setCurrentLocationService(this);

        // init map
        CustomMapFragment mapFragment = (CustomMapFragment) getSupportFragmentManager()
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
        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);

        //Listener for search text changes
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                searchText.delete(0, searchText.length());
                searchText.append(newQuery);
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String currentQuery) {
                // TODO: should we clear mMap too?
                googleMap.clear();
                search(searchText.toString(),GoogleMapsActivity.this);
            }
        });

        //Listener for clicks on the mic in the search bar
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                startVoiceRecognition();
            }

        });

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("user_name", "");
        String email = sharedPreferences.getString("email", "");
        String picURL = sharedPreferences.getString("profile_pic_url", "");
        userId = sharedPreferences.getString("user_id", "");

        View header = navigationView.getHeaderView(0);

        TextView nameTextView = (TextView) header.findViewById(R.id.username_textView);
        nameTextView.setText(name);
        TextView emailTextView = (TextView) header.findViewById(R.id.email_textView);
        emailTextView.setText(email);
        ImageView profilePicImageView = (ImageView) header.findViewById(R.id.account_imageView);
        // TODO: need to fix this
//        Picasso.with(this).load(picURL).into(profilePicImageView);

        recommendationsRecyclerView = (RecyclerView) findViewById(R.id.recommendations_recyclerView);

        recommendationsViewAdapter = new RecommendationsViewAdapter(recommendedLocations, this);
        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendationsRecyclerView.setLayoutManager(HorizontalLayout);
        recommendationsRecyclerView.setAdapter(recommendationsViewAdapter);
        // Adding items to RecyclerView.
        AddItemsToRecyclerViewArrayList(LocationUtils.getInstance().getCurrPlace());

        // Adding on item click listener to RecyclerView.
        recommendationsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(GoogleMapsActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    // Toast.makeText(GoogleMapsActivity.this, "tap up", Toast.LENGTH_LONG).show();
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                childView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (childView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting clicked value.
                    int recyclerViewItemPosition = Recyclerview.getChildAdapterPosition(childView);
                    LocationMarkerModel selectedRecommendation = recommendedLocations.get(recyclerViewItemPosition);

                    showMarkerDescLayout(selectedRecommendation);
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
    public void AddItemsToRecyclerViewArrayList(Place currentPlace) {

        if (currentPlace != null) {
            // init request
            LocationMarkerModel currentLocation = new LocationMarkerModel(currentPlace.getName().toString(), currentPlace.getLatLng(),
                    currentPlace.getId(), currentPlace.getAddress().toString(), false);

            MapsioService mapsioService = MapsioService.Factory.create(this);
            Call<List<LocationMarkerModel>> recommendedLocationsCall = mapsioService.getRecommendedLocations(userId, currentLocation);

            recommendedLocationsCall.enqueue(new Callback<List<LocationMarkerModel>>() {
                @Override
                public void onResponse(Call<List<LocationMarkerModel>> call, Response<List<LocationMarkerModel>> response) {

                    recommendedLocations.addAll(response.body());
                    recommendationsViewAdapter.notifyDataSetChanged();
                    recommendationsRecyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<List<LocationMarkerModel>> call, Throwable t) {
                }

            });
        }

    }

    public void search(final String searchQuery, AppCompatActivity activity) {
        //If location permission is not granted try getting it
        if (!LocationUtils.getInstance().ismLocationPermissionGranted()) {
            // Prompt the user for permission.
            LocationUtils.getInstance().getLocationPermission(activity);
        }

        Task<AutocompletePredictionBufferResponse> results;
        Place currentPlace = LocationUtils.getInstance().getCurrPlace();
        if (currentPlace != null) {
            //this is just taking the center
            LatLng latLng = new LatLng(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude);
            LatLngBounds latlngBounds = new LatLngBounds(latLng, latLng);

            results = geoDataClient.getAutocompletePredictions(searchQuery, latlngBounds, null);
        } else {

            results = geoDataClient.getAutocompletePredictions(searchQuery, null, null);
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
                    }

                    markPlaces(placeIdList, searchQuery);
                    response.release();
                }
            }
        });

    }

    private void markPlaces(ArrayList<String> placeIdList, String searchQuery) {

        //clear the google map before marking new places
        googleMap.clear();
        //save the place whose name matches the search query
        Place mostLikelyPlaceByName = null;
        //save the first place in the list
        Place firstPlace = null;

        Place currentPlace = LocationUtils.getInstance().getCurrPlace();

        for (int i = 0; i < placeIdList.size(); i++) {
            Task<PlaceBufferResponse> result = geoDataClient.getPlaceById(placeIdList.get(i));

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
                Marker marker = googleMap.addMarker(markerOptions);
                markerMap.put(marker.getId(), new LocationMarkerModel(tempPlace.getName().toString(),
                        tempPlace.getLatLng(), tempPlace.getId(), false));
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
            LatLngBounds bounds = MapsioUtils.getInstance().toBounds(currentPlace.getLatLng(), 80467.2);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width - 200, height - 700, 12);

            if (googleMap != null) {
                googleMap.animateCamera(cu);
            }

        } else {

            if (googleMap != null && mostLikelyPlaceByName != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mostLikelyPlaceByName.getLatLng(), 13));
            }
        }

    }

    private void showMarkerDescLayout(final LocationMarkerModel locationObj) {
        final ImageView locationImageView = (ImageView) markerDescLayout.findViewById(R.id.location_imageView);
        // TODO: appropriate placeholder image
        locationImageView.setImageResource(R.mipmap.ic_launcher);
        TextView locationTitleTextView = (TextView) markerDescLayout.findViewById(R.id.location_title_textView);
        TextView locationDescTextView = (TextView) markerDescLayout.findViewById(R.id.location_desc_textView);
        Button favUnfavButton = (Button) markerDescLayout.findViewById(R.id.fav_unfav_button);
        Button getDirectionsButton = (Button) markerDescLayout.findViewById(R.id.get_directions_button);
        RatingBar ratingBar = (RatingBar) markerDescLayout.findViewById(R.id.ratingBar);

        MapsioUtils.getInstance().getPhotos(locationObj.getPlaceId(), new IPlacePhoto() {

            public void onDownloadCallback(Bitmap bitmap) {

                locationImageView.setImageBitmap(bitmap);
            }
        });

        getDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startNavigation(locationObj.getLatLng());
            }
        });

        favUnfavButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // init request
                MapsioService mapsioService = MapsioService.Factory.create(GoogleMapsActivity.this);
                Call<LocationMarkerModel> addFavoriteCall = mapsioService.addFavorite(userId, locationObj);

                addFavoriteCall.enqueue(new Callback<LocationMarkerModel>() {
                    @Override
                    public void onResponse(Call<LocationMarkerModel> call, Response<LocationMarkerModel> response) {
                        Log.d("RESPONSE", "RESPONSE + " + response.toString());
                    }

                    @Override
                    public void onFailure(Call<LocationMarkerModel> call, Throwable t) {
                        Log.d("FAILURE", "FAILURE + " + t.toString());
                    }

                });

            }
        });

        locationTitleTextView.setText(locationObj.getName());
        locationDescTextView.setText(locationObj.getAddress());
        ratingBar.setRating(locationObj.getRating());
        // hide recommendations view
        recommendationsRecyclerView.setVisibility(View.INVISIBLE);
        // render marker description visible
        markerDescLayout.setVisibility(View.VISIBLE);
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        intent.putExtra("android.speech.extra.PROMPT", "Speak Now");
        this.startActivityForResult(intent, VOICE_SEARCH_CODE);
    }

    //start navigation from current location to the selected destination using Google Maps
    private void startNavigation(LatLng destination) {

        Place currentPlace = LocationUtils.getInstance().getCurrPlace();

        if (currentPlace != null) {

            //String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude, destination.latitude, destination.longitude);
            String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", destination.latitude, destination.longitude);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);

        } else {

            MapsioUtils.displayInfoDialog(this, R.string.info_dialog_curr_loc_title,R.string.info_dialog_curr_loc_message);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //this ID needs to be renamed to favorites
        if (id == R.id.nav_favorites) {

            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_about_us) {

            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
            //finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        LocationMarkerModel markerObj = markerMap.get(marker.getId());
        if (!markerObj.isPoi() && markerObj.getPlaceId() != null) {

            Task<PlaceBufferResponse> result = geoDataClient.getPlaceById(markerObj.getPlaceId());
            result.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                    if (task.isSuccessful() && task.getResult() != null) {
                        PlaceBufferResponse response = task.getResult();
                        Place place = response.get(0).freeze();
                        LocationMarkerModel locationObj = new LocationMarkerModel(place.getName().toString(), place.getLatLng(),
                                place.getId(), place.getAddress().toString(), false);

                        showMarkerDescLayout(locationObj);

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
        // Clears the previously touched location
        if (googleMap != null)
            googleMap.clear();
        // clear local cache
        markerMap.clear();

        // Creating a marker
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(poi.latLng)
                .title(poi.name));
        // add to local cache
        markerMap.put(marker.getId(), new LocationMarkerModel(poi.name, poi.latLng, poi.placeId, true));

        Task<PlaceBufferResponse> result = geoDataClient.getPlaceById(poi.placeId);
        result.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                if (task.isSuccessful() && task.getResult() != null) {
                    PlaceBufferResponse response = task.getResult();
                    Place place = response.get(0).freeze();
                    LocationMarkerModel locationObj = new LocationMarkerModel(place.getName().toString(), place.getLatLng(),
                            place.getId(), place.getAddress().toString(), false);

                    showMarkerDescLayout(locationObj);

                    response.release();
                }
            }
        });

        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng));
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        MapsioService mapsioService = MapsioService.Factory.create(this);
        Call<LocationMarkerModel> placeDetailRequestCall = mapsioService.getPlaceDetail(new LatLngModel(latLng.latitude,
                latLng.longitude));

        placeDetailRequestCall.enqueue(new Callback<LocationMarkerModel>() {
            @Override
            public void onResponse(Call<LocationMarkerModel> call, Response<LocationMarkerModel> response) {

                LocationMarkerModel dropPin = response.body();
                // Clears the previously touched position
                if (googleMap != null)
                    googleMap.clear();

                // Creating a marker
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(dropPin.getName()));
                // add to local cache
                markerMap.put(marker.getId(), dropPin);

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }

            @Override
            public void onFailure(Call<LocationMarkerModel> call, Throwable t) {
            }

        });

    }

    @Override
    public void onCurrentLocationReceived(Place currentPlace) {
        // Adding items to RecyclerView.
        AddItemsToRecyclerViewArrayList(currentPlace);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // make recommendations view visible
        recommendationsRecyclerView.setVisibility(View.VISIBLE);
        // make marker description invisible
        markerDescLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        LocationUtils.getInstance().setGoogleMap(googleMap);
        googleMap.setOnPoiClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);
        if(!LocationUtils.getInstance().ismLocationPermissionGranted()) {
            // Prompt the user for permission.
            LocationUtils.getInstance().getLocationPermission(this);
        }
        LocationUtils.getInstance().enableMyLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_SEARCH_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra("android.speech.extra.RESULTS");

            if (searchView != null) {

                searchView.setSearchText(matches.get(0));
                search(matches.get(0),GoogleMapsActivity.this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handles the result of the request for location permissions. This has to be activity specific
     */
    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.getInstance().setmLocationPermissionGranted(true);
                    // Get the current location of the device and set the position of the map.
                    LocationUtils.getInstance().getDeviceLocation();

                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                } else {
                    LocationUtils.getInstance().setmLocationPermissionGranted(false);
                    LocationUtils.getInstance().setCurrPlace(null);

                    googleMap.setMyLocationEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            }
        }

    }

}
