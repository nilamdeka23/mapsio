package cmpe295.sjsu.edu.mapsio.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.util.LocationUtils;
import cmpe295.sjsu.edu.mapsio.util.MapsioUtils;

/**
 * Created by nilamdeka on 2/21/18.
 */

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private AlertDialog.Builder builder;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        RelativeLayout signOutLayout = (RelativeLayout) findViewById(R.id.sign_out_layout);
        RelativeLayout deactivateAccountLayout = (RelativeLayout) findViewById(R.id.deactivate_account_layout);
        RelativeLayout currentLocationLayout = (RelativeLayout) findViewById(R.id.current_loc_layout);

        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();
            }
        });

        deactivateAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deactivateAccount();
            }
        });

        currentLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableMyLocation();

            }
        });

    }

    private void signOut() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        } else {

            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle(getString(R.string.settings_sign_out))
                .setMessage(getString(R.string.settings_sign_out_confirm))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                if (status.isSuccess()) {
                                    // delete data from local application cache
                                    SharedPreferences sharedPreferences = getSharedPreferences("user_data",
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();

                                    Intent intent = new Intent(SettingsActivity.this, GoogleSigninActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    Toast.makeText(SettingsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();

    }

    private void deactivateAccount(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogTheme);
        } else {

            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle(getString(R.string.settings_deactivate_account))
                .setMessage(getString(R.string.settings_deactivate_confirm))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                if (status.isSuccess()) {
                                    // delete user data from local application cache
                                    SharedPreferences sharedPreferences = getSharedPreferences("user_data",
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();

                                    Intent intent = new Intent(SettingsActivity.this, GoogleSigninActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(SettingsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(SettingsActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private void enableMyLocation(){

        if(LocationUtils.getInstance().getCurrPlace()!=null){

            MapsioUtils.displayInfoDialog(this.getApplicationContext(), R.string.settings_curr_loc,R.string.curr_loc_alrdy_enabled_msg);

        }else {

            LocationUtils.getInstance().getLocationPermission(this);
            if(LocationUtils.getInstance().ismLocationPermissionGranted()){
                Toast.makeText(this, R.string.curr_loc_enabled_msg,Toast.LENGTH_LONG);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.getInstance().setmLocationPermissionGranted(true);
                    LocationUtils.getInstance().getGoogleMap().setMyLocationEnabled(true);
                    LocationUtils.getInstance().getGoogleMap().getUiSettings().setMyLocationButtonEnabled(true);
                    //TODO: this toast is not appearing
                    Toast.makeText(this, "Your current location is enabled.",Toast.LENGTH_LONG);

                } else {
                    LocationUtils.getInstance().setmLocationPermissionGranted(false);
                    LocationUtils.getInstance().getGoogleMap().setMyLocationEnabled(false);
                    LocationUtils.getInstance().getGoogleMap().getUiSettings().setMyLocationButtonEnabled(true);
                    LocationUtils.getInstance().setCurrPlace(null);
                    //TODO: this toast is not appearing
                    Toast.makeText(this, "Your current location is not enabled.",Toast.LENGTH_LONG);
                }
            }
        }

    }
}
