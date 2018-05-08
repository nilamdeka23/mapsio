package cmpe295.sjsu.edu.mapsio.util;

import cmpe295.sjsu.edu.mapsio.model.LocationMarkerModel;

public interface ICurrentLocationService {

    void onCurrentLocationReceived(LocationMarkerModel currentPlace);
}
