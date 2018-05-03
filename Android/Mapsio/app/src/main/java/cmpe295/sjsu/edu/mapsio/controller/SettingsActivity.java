package cmpe295.sjsu.edu.mapsio.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by nilamdeka on 2/21/18.
 */

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_detail);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        RelativeLayout signOutLayout = (RelativeLayout) findViewById(R.id.sign_out_layout);
        RelativeLayout deactivateAccountLayout = (RelativeLayout) findViewById(R.id.deactivate_account_layout);

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

    }

    private void signOut() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        } else {

            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle(getString(R.string.sign_out))
                .setMessage(getString(R.string.confirm))
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
                })
                .show();
    }

    private void deactivateAccount(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        } else {

            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle(getString(R.string.deactivate_account))
                .setMessage(getString(R.string.confirm))
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

}
