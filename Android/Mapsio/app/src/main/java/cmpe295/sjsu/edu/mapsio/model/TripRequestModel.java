package cmpe295.sjsu.edu.mapsio.model;


import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

public class TripRequestModel {

    private LocationMarkerModel startLocation;
    private LocationMarkerModel endLocation;
    private String journeyTime;
    private String journeyDate;
    private transient Context context;

    public TripRequestModel() {

    }

    public TripRequestModel(LocationMarkerModel startLocation, LocationMarkerModel endLocation, Context context) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        AndroidThreeTen.init(context);
        this.journeyDate = LocalDate.now().toString() + ", " + LocalDate.now().getDayOfWeek();
        this.journeyTime = LocalTime.now().toString();
    }

    public LocationMarkerModel getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LocationMarkerModel startLocation) {
        this.startLocation = startLocation;
    }

    public LocationMarkerModel getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationMarkerModel endLocation) {
        this.endLocation = endLocation;
    }

    public String getJourneyTime() {
        return journeyTime;
    }

    public void setJourneyTime(String journeyTime) {
        this.journeyTime = journeyTime;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

}
