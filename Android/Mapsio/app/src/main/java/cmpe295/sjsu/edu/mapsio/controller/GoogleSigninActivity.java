package cmpe295.sjsu.edu.mapsio.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;

import cmpe295.sjsu.edu.mapsio.R;
import cmpe295.sjsu.edu.mapsio.model.AuthRequestModel;
import cmpe295.sjsu.edu.mapsio.service.MapsioService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nilamdeka on 2/21/18.
 */

public class GoogleSigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleSigninActivity";
    private static final int SIGN_IN = 001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);
        //ID, email address and basic profile are included in DEFAULT_SING_IN.
        String serverClientId = getString(R.string.web_client_id);
        String calenderScope = getString(R.string.calender_scope);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(calenderScope))
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .requestProfile()
                .build();

        //Building a GoogleApiClient with access to the Google Sign-in API and options
        //specified by googleSignInOptions
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //Customizing Sign In With Google Button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setScopes(googleSignInOptions.getScopeArray());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, SIGN_IN);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);

        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            final String accountName = acct.getDisplayName();
            final String accountEmail = acct.getEmail();
            final Uri accountPic = acct.getPhotoUrl();
            final String userId = acct.getId();
            String authCode = acct.getServerAuthCode();

            MapsioService mapsioService = MapsioService.Factory.create(this);
            Call<AuthRequestModel> authRequestCall = mapsioService.register(new AuthRequestModel(userId,
                    authCode));

            authRequestCall.enqueue(new Callback<AuthRequestModel>() {

                @Override
                public void onResponse(Call<AuthRequestModel> call, Response<AuthRequestModel> response) {
                    Toast.makeText(GoogleSigninActivity.this, "SignIn Auth Success " +
                            response.toString(), Toast.LENGTH_SHORT).show();

                    // store data as local cache
                    storeUserIdInSharedPreferences(userId);

                    Intent intent = new Intent(GoogleSigninActivity.this, GoogleMapsActivity.class);
//                  Intent intent = new Intent(GoogleSigninActivity.this, SettingsActivity.class);
                    intent.putExtra("name", accountName);
                    intent.putExtra("email", accountEmail);
                    if (accountPic != null) {
                        intent.putExtra("profile_url", accountPic.toString());
                    }

                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<AuthRequestModel> call, Throwable t) {

                    Toast.makeText(GoogleSigninActivity.this, "SignIn Auth Fail " +
                            t.toString(), Toast.LENGTH_SHORT).show();
                }

            });

        } else {
            // TODO: come up with a better message
            Toast.makeText(GoogleSigninActivity.this, "SignIn Failure " + result.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUserIdInSharedPreferences(String userId) {

        SharedPreferences sharedPreferences = getSharedPreferences("user_data",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", userId);
        editor.apply();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Loading....");
            progressDialog.setIcon(R.drawable.google_icon);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO: come up with a better message
        Toast.makeText(GoogleSigninActivity.this, "SignIn Connection Failure " +
                connectionResult.toString(), Toast.LENGTH_SHORT).show();
    }

}
