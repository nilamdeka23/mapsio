package cmpe295.sjsu.edu.mapsio.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        /*setContentView(R.layout.activity_settings);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button _signout = (Button) findViewById(R.id.signout);
        Button _revoke = (Button) findViewById(R.id.revoke);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String imagePath = getIntent().getStringExtra("profile_url");

        TextView _name = (TextView) findViewById(R.id.account_title);
        _name.setText(name);
        TextView _email = (TextView) findViewById(R.id.account_email);
        _email.setText(email.toUpperCase());
        ImageView _pic = (ImageView) findViewById(R.id.account_image);
        Picasso.with(this).load(imagePath).into(_pic);*/

        RelativeLayout sign_out_section = (RelativeLayout) findViewById(R.id.sign_out_section);
        RelativeLayout deactivate_section = (RelativeLayout) findViewById(R.id.deactivate_section);


        sign_out_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutAccount();

            }
        });

        deactivate_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactivateAccount();
            }
        });

     /*   _signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        if (status.isSuccess()) {

                            Intent intent = new Intent(SettingsActivity.this, GoogleSigninActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(SettingsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        _revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        if (status.isSuccess()) {

                            Intent intent = new Intent(SettingsActivity.this, GoogleSigninActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(SettingsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });*/

    }


    private void signoutAccount(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                if (status.isSuccess()) {

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
                        // TODO : check what happens
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deactivateAccount(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Deactivate Account")
                .setMessage("Are you sure you want to deactivate account?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                if (status.isSuccess()) {

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
                        // TODO : check what happens
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(SettingsActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();

    }

}
