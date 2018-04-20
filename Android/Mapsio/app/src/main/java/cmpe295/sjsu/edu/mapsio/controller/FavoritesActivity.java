package cmpe295.sjsu.edu.mapsio.controller;

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
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.listener.FavoritesListAdapter;
import cmpe295.sjsu.edu.mapsio.listener.RecyclerItemTouchHelper;
import cmpe295.sjsu.edu.mapsio.listener.RecyclerTouchListener;
import cmpe295.sjsu.edu.mapsio.model.MyGooglePlaces;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, FavoritesListAdapter.FavDirectionsClickListener, FavoritesListAdapter.FavLocationClickListener {

    private List<MyGooglePlaces> favPlaceList = new ArrayList<>();
    private FavoritesListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.fav_coordinator_layout);
        mAdapter = new FavoritesListAdapter(this, favPlaceList);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fav_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                final MyGooglePlaces place = favPlaceList.get(position);
                Log.i("Position Clicked : ", position + "");
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);

        prepareFavListData();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = favPlaceList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final MyGooglePlaces deletedItem = favPlaceList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void prepareFavListData() {

        MyGooglePlaces places = new MyGooglePlaces("Nepenthe", "Restaurant");
        favPlaceList.add(places);

        places = new MyGooglePlaces("SJPL", "Library");
        favPlaceList.add(places);

        places = new MyGooglePlaces("SJSU", "School");
        favPlaceList.add(places);

        places = new MyGooglePlaces("Lees Sandwich", "Restaurant");
        favPlaceList.add(places);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDirectionClick(MyGooglePlaces myGooglePlace) {
        Log.i("Directions CLicked : ", "Directions Clicked ");
    }

    @Override
    public void onLocationClick(MyGooglePlaces myGooglePlace) {
        Log.i("Locations CLicked : ", "Locations Clicked ");
    }

}
