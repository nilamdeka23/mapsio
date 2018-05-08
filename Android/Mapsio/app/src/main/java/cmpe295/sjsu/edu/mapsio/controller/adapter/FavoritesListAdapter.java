package cmpe295.sjsu.edu.mapsio.controller.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.util.IPlacePhoto;
import cmpe295.sjsu.edu.mapsio.util.MapsioUtils;

/**
 * Created by laddu on 3/12/18.
 */

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.MyViewHolder> {
    private Context context;
    private List<LocationMarkerModel> favoriteList;
    private FavDirectionsClickListener directionsClickListener;
    private FavLocationClickListener locClickListener;

    public interface FavDirectionsClickListener {
        void onDirectionClick(LocationMarkerModel favoriteLocation);
    }

    public interface FavLocationClickListener {
        void onLocationClick(LocationMarkerModel favoriteLocation);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView locationName, locationAddress;
        public LinearLayout favDirectionsLayout, favLocationLayout;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;
        public ImageView locationImage;
        public RatingBar locationRating;

        public MyViewHolder(View view) {
            super(view);
            locationName = (TextView) view.findViewById(R.id.location_name);
            locationAddress = (TextView) view.findViewById(R.id.location_address);
            viewForeground =(LinearLayout) view.findViewById(R.id.view_foreground);
            viewBackground =(RelativeLayout) view.findViewById(R.id.view_background);
            favLocationLayout = (LinearLayout) view.findViewById(R.id.fav_location_layout);
            favDirectionsLayout = (LinearLayout) view.findViewById(R.id.fav_direction_layout);
            locationImage = (ImageView) view.findViewById(R.id.location_image);
            locationRating = (RatingBar) view.findViewById(R.id.location_rating);
        }
    }

    public FavoritesListAdapter(Context context, List<LocationMarkerModel> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.directionsClickListener = (FavDirectionsClickListener) context;
        this.locClickListener = (FavLocationClickListener) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorites, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final LocationMarkerModel favoriteLocation = favoriteList.get(position);

        MapsioUtils.getInstance().getPhotos(favoriteLocation.getPlaceId(), new IPlacePhoto() {

            public void onDownloadCallback(Bitmap bitmap) {

                holder.locationImage.setImageBitmap(bitmap);
            }
        });

        holder.locationName.setText(favoriteLocation.getName());
        holder.locationAddress.setText(favoriteLocation.getAddress());
        holder.locationRating.setRating(favoriteLocation.getRating());
        holder.favDirectionsLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                directionsClickListener.onDirectionClick(favoriteLocation);

            }
        });

        holder.favLocationLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                locClickListener.onLocationClick(favoriteLocation);
            }

        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public void removeItem(int position) {
        favoriteList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(LocationMarkerModel favoriteLocation, int position) {
        favoriteList.add(position, favoriteLocation);
        // notify item added by position
        notifyItemInserted(position);
    }

}
