package cmpe295.sjsu.edu.mapsio.util;

import com.google.android.gms.location.places.Place;

public interface ICurrentLocationService {

    void onCurrentLocationReceived(Place currentPlace);
}
