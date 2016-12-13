/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

import dk.lndesign.explicitimage.adapter.PagerAdapter;
import dk.lndesign.explicitimage.view.PagerFragment;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RESULT_SIGN_IN = 420;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Menu mMainMenu;
    PagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(LOG_TAG, "Unable to connect to Google API client: " + connectionResult.toString());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Setup authentication.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged: signed_out");
                }
                updateOptionLogInButton();
            }
        };

        List<PagerFragment> pagerFragments = new ArrayList<>();
        pagerFragments.add(GalleryFragment.newInstance("Gallery"));
        pagerFragments.add(UploadFragment.newInstance("Upload"));

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), pagerFragments);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMainMenu = menu;

        updateOptionLogInButton();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_in_button:
                if (mAuth.getCurrentUser() != null) {
                    // Log out.
                    logOutUser();
                } else {
                    // Log in.
                    logInUser();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(LOG_TAG, "Sign in with Google failed.");
            }
            updateOptionLogInButton();
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        // Attach authentication listener.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateOptionLogInButton();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            // Detach authentication listener.
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle: " + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Authenticated: " + task.getResult().getUser().getEmail(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Update options menu log in button.
     * Will only affect menu item if menu is created.
     *
     * TODO: We should move log in button, so we don't need to update the options menu as often.
     */
    private void updateOptionLogInButton() {
        if (mMainMenu != null) {
            if (mAuth.getCurrentUser() != null) {
                for (final UserInfo userInfo : mAuth.getCurrentUser().getProviderData()) {
                    // TODO: Figure out what to do with multiple images. We just pick the last we find.
                    if (userInfo.getPhotoUrl() != null) {
                        Glide.with(this).load(userInfo.getPhotoUrl()).into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                // TODO: Remove nasty casting.
                                Bitmap bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                                RoundedBitmapDrawable roundProfileImage = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                roundProfileImage.setCircular(true);

                                mMainMenu.findItem(R.id.log_in_button).setIcon(roundProfileImage);
                            }
                        });
                    }
                }
            } else {
                mMainMenu.findItem(R.id.log_in_button).setIcon(R.drawable.ic_account_circle_24dp);
            }
        } else {
            Log.w(LOG_TAG, "Menu not found when trying to update options menu");
        }
    }

    private void logInUser() {
        // Display Google login activity.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RESULT_SIGN_IN);
    }

    private void logOutUser() {
        mAuth.signOut();
        updateOptionLogInButton();
    }
}
