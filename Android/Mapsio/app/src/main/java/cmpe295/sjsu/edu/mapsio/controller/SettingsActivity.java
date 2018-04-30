package cmpe295.sjsu.edu.mapsio.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        Picasso.with(this).load(imagePath).into(_pic);

        _signout.setOnClickListener(new View.OnClickListener() {
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
        });

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(SettingsActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();

    }

}
