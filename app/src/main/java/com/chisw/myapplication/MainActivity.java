package com.chisw.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE = 9001;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SignInButton signInButton;
    private GoogleApiClient apiClient;
    private Button logout;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        signInButton = findViewById(R.id.sign_in_button);
        logout = findViewById(R.id.logout);
        imageView = findViewById(R.id.imageView);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ");
    }

    public void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d(TAG, "onResult: LOGOUT");
            }
        });
    }

    private void handleResult(GoogleSignInResult signInResult) {
        if (signInResult.isSuccess()) {
            GoogleSignInAccount account = signInResult.getSignInAccount();
            if (account != null) {
                String name = account.getDisplayName();
                String email = account.getEmail();
                Uri photo_url = account.getPhotoUrl();
                if (photo_url != null) {
                    String img_url = photo_url.toString();
                    //Loading image from below url into imageView
                    Glide.with(this)
                            .load(img_url)
                            .into(imageView);
                }
                Toast.makeText(this, email, Toast.LENGTH_LONG).show();
                Log.d(TAG, "handleResult: INFO: " + name + " " + email);

            }
        } else {
            Log.e(TAG, "handleResult: Sync Failed");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(signInResult);
        }
    }
}
