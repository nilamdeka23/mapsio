package cmpe295.sjsu.edu.mapsio.controller;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.controller.adapter.RecommendationsViewAdapter;

public class GoogleMapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnPoiClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap googleMap;

    private ArrayList<String> Number;
    private View ChildView;
    private int RecyclerViewItemPosition;

    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // init drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // init navigation menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

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
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();




        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        // Clears the previously touched position
        if (googleMap != null)
            googleMap.clear();

        // Creating a marker
        marker = googleMap.addMarker(new MarkerOptions()
                .position(pointOfInterest.latLng)
                .title(pointOfInterest.name + " : " + pointOfInterest.placeId));

        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(pointOfInterest.latLng));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
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


}
