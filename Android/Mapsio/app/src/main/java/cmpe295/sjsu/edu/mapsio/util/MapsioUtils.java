package cmpe295.sjsu.edu.mapsio.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.Locale;

import cmpe295.sjsu.edu.mapsio.R;

public class MapsioUtils {

    private static MapsioUtils instance = null;

    private MapsioUtils() {
    }

    public static MapsioUtils getInstance() {
        if (instance == null) {
            instance = new MapsioUtils();
        }
        return instance;
    }

    private GeoDataClient geoDataClient;

    public void setGeoDataClient(GeoDataClient geoDataClient) {
        this.geoDataClient = geoDataClient;
    }

    // Request photos and metadata for the specified place.
    public void getPhotos(String placeId, final IPlacePhoto iPlacePhoto) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = geoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {

            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    if (photoMetadataBuffer != null && photoMetadataBuffer.getCount() != 0) {
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0).freeze();
                        // Get the attribution text.
                        CharSequence attribution = photoMetadata.getAttributions();
                        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                iPlacePhoto.onDownloadCallback(photo.getBitmap());
                            }
                        });
                    }
                    photoMetadataBuffer.release();
                }
            }
        });
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    public void displayInfoDialog(Context context, int title, int message){
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
        } else {

            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle(title)
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    //start navigation from current location to the selected destination using Google Maps
    public void startNavigation(LatLng destination, Context context) {

        Place currentPlace = LocationUtils.getInstance().getCurrPlace();
        if (currentPlace != null) {

            String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f",
                    destination.latitude, destination.longitude);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);

        } else {
            displayInfoDialog(context, R.string.info_dialog_curr_loc_title, R.string.info_dialog_curr_loc_message);
        }

    }

}
