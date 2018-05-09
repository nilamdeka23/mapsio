package cmpe295.sjsu.edu.mapsio.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.controller.adapter.FavoritesListAdapter;
import cmpe295.sjsu.edu.mapsio.controller.listener.RecyclerTouchListener;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.service.MapsioService;
import cmpe295.sjsu.edu.mapsio.util.LocationUtils;
import cmpe295.sjsu.edu.mapsio.util.MapsioUtils;
import cmpe295.sjsu.edu.mapsio.util.RecyclerItemTouchHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        FavoritesListAdapter.FavDirectionsClickListener, FavoritesListAdapter.FavLocationClickListener {

    private List<LocationMarkerModel> favoriteLocations = new ArrayList<>();
    private FavoritesListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private MapsioService mapsioService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.fav_coordinator_layout);
        mAdapter = new FavoritesListAdapter(this, favoriteLocations);
        mapsioService = MapsioService.Factory.create(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fav_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
//                final LocationMarkerModel favoriteLocation = favoriteLocations.get(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);

        prepareFavListData();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            final String name = favoriteLocations.get(viewHolder.getAdapterPosition()).getName();
            // backup of removed item for undo purpose
            final LocationMarkerModel deletedLocation = favoriteLocations.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            // delete request
            Call<List<LocationMarkerModel>> deleteFavoriteCall = mapsioService.deleteFavorite(userId, name);

            deleteFavoriteCall.enqueue(new Callback<List<LocationMarkerModel>>() {
                @Override
                public void onResponse(Call<List<LocationMarkerModel>> call, Response<List<LocationMarkerModel>> response) {
                    // TODO: remove hardcoded string
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, name + R.string.fav_loc_removed_msg, Snackbar.LENGTH_LONG);
                    snackbar.setAction(getString(R.string.undo).toUpperCase(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // add request
                            Call<List<LocationMarkerModel>> addFavoriteCall = mapsioService.addFavorite(userId, deletedLocation);

                            addFavoriteCall.enqueue(new Callback<List<LocationMarkerModel>>() {

                                @Override
                                public void onResponse(Call<List<LocationMarkerModel>> call, Response<List<LocationMarkerModel>> response) {
                                    // undo is selected, restore the deleted item
                                    mAdapter.restoreItem(deletedLocation, deletedIndex);
                                }

                                @Override
                                public void onFailure(Call<List<LocationMarkerModel>> call, Throwable t) {
                                    // TODO: handle failure
                                }

                            });
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }

                @Override
                public void onFailure(Call<List<LocationMarkerModel>> call, Throwable t) {
                    // TODO: handle failure
                }

            });

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(LocationUtils.getInstance().getGoogleMap()!=null){

            if(!LocationUtils.getInstance().ismLocationPermissionGranted()) {
                // Prompt the user for permission.
                LocationUtils.getInstance().getLocationPermission(this);
            }else{
                //get the device location. this also takes care of displaying the button
                LocationUtils.getInstance().getDeviceLocation();
            }
        }
    }

    private void prepareFavListData() {
        // get user id from local cache
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id","");

        Call<List<LocationMarkerModel>> recommendedLocationsCall = mapsioService.getFavorites(userId);

        recommendedLocationsCall.enqueue(new Callback<List<LocationMarkerModel>>() {
            @Override
            public void onResponse(Call<List<LocationMarkerModel>> call, Response<List<LocationMarkerModel>> response) {
                // TODO: show loading indicator
                favoriteLocations.addAll(response.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<LocationMarkerModel>> call, Throwable t) {
                // TODO: show empty list and appropriate message
            }

        });

    }

    @Override
    public void onDirectionClick(LocationMarkerModel locationMarkerModel) {

        MapsioUtils.getInstance().startNavigation(locationMarkerModel, userId, FavoritesActivity.this);
    }

    @Override
    public void onLocationClick(LocationMarkerModel locationMarkerModel) {
        Intent intent = new Intent(FavoritesActivity.this, GoogleMapsActivity.class);
        intent.putExtra("location_data", locationMarkerModel);
        //this is invoked so that the current favorites activity is destroyed and when on GoogleMapsActivity,
        // favorites activty doesn't appear on bac
        finish();
        startActivity(intent);
    }

}
