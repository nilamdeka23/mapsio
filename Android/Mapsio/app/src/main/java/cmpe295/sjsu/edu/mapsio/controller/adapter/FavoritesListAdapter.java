package cmpe295.sjsu.edu.mapsio.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.model.MyGooglePlaces;

/**
 * Created by laddu on 3/12/18.
 */

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.MyViewHolder> {
    private Context context;
    private List<MyGooglePlaces> favList;
    private FavDirectionsClickListener directionsClickListener;
    private FavLocationClickListener locClickListener;


    public interface FavDirectionsClickListener {
        void onDirectionClick(MyGooglePlaces myGooglePlace);
    }

    public interface FavLocationClickListener {
        void onLocationClick(MyGooglePlaces myGooglePlace);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, category;
        public LinearLayout favDirectionsLayout, favLocationLayout;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            category = (TextView) view.findViewById(R.id.category);
            viewForeground =(LinearLayout) view.findViewById(R.id.view_foreground);
            viewBackground =(RelativeLayout) view.findViewById(R.id.view_background);
            favLocationLayout = (LinearLayout) view.findViewById(R.id.fav_location_layout);
            favDirectionsLayout = (LinearLayout) view.findViewById(R.id.fav_direction_layout);
        }
    }

    public FavoritesListAdapter(Context context, List<MyGooglePlaces> favList) {
        this.context = context;
        this.favList = favList;
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
        MyGooglePlaces place = favList.get(position);
        holder.name.setText(place.getName());
        holder.category.setText(place.getCategory());
        holder.favDirectionsLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                directionsClickListener.onDirectionClick(favList.get(holder.getAdapterPosition()));

            }
        });

        holder.favLocationLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                locClickListener.onLocationClick(favList.get(holder.getAdapterPosition()));
            }

        });
        //   holder.year.setText(movie.getYear());
        //TODO: check what the Glide library is doing here
        /*Glide.with(context)
                .load(item.getThumbnail())
                .into(holder.thumbnail); */
    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    public void removeItem(int position) {
        favList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(MyGooglePlaces item, int position) {
        favList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

}
