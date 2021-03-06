package cmpe295.sjsu.edu.mapsio.controller.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;
import cmpe295.sjsu.edu.mapsio.util.IPlacePhoto;
import cmpe295.sjsu.edu.mapsio.util.MapsioUtils;

public class RecommendationsViewAdapter extends RecyclerView.Adapter<RecommendationsViewAdapter.MyView> {

    private List<LocationMarkerModel> list;
    private Context context;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView recommendationTitle;
        public ImageView recommendationImage;


        public MyView(View view) {
            super(view);

            recommendationTitle = (TextView) view.findViewById(R.id.recommendation_title);
            recommendationImage = (ImageView) view.findViewById(R.id.recommendation_image);
        }
    }

    public RecommendationsViewAdapter(List<LocationMarkerModel> horizontalList, Context context) {
        this.list = horizontalList;
        this.context = context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_item, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        MapsioUtils.getInstance().getPhotos(list.get(position).getPlaceId(), new IPlacePhoto() {

            public void onDownloadCallback(Bitmap bitmap) {

                holder.recommendationImage.setImageBitmap(bitmap);
            }
        });

        holder.recommendationTitle.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}