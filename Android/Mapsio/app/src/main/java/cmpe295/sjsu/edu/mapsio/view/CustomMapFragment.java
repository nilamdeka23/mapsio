package cmpe295.sjsu.edu.mapsio.view;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapFragment extends SupportMapFragment {

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View mapView = super.onCreateView(layoutInflater, viewGroup, bundle);
        // get the button view
        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        // convert dp to px
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float logicalDensity = displayMetrics.density;
        int rightMargin = (int) Math.ceil(20 * logicalDensity);
        int bottomMargin = (int) Math.ceil(130 * logicalDensity);

        // add layout constaints
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position the right button
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_END, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, rightMargin, bottomMargin);

        return mapView;
    }
}
