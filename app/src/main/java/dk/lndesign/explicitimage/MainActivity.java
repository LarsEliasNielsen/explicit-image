/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import dk.lndesign.explicitimage.adapter.PagerAdapter;
import dk.lndesign.explicitimage.view.PagerFragment;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Menu mMainMenu;
    PagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    /**
     * Update options menu log in button.
     * Will only affect menu item if menu is created.
     *
     * TODO: We should move log in button, so we don't need to update the options menu as often.
     */
    private void updateOptionLogInButton() {
        if (mMainMenu != null) {
            if (mAuth.getCurrentUser() != null) {
                mMainMenu.findItem(R.id.log_in_button).setIcon(R.drawable.ic_check_box_24dp);
            } else {
                mMainMenu.findItem(R.id.log_in_button).setIcon(R.drawable.ic_check_box_blank_24dp);
            }
        } else {
            Log.w(LOG_TAG, "Menu not found when trying to update options menu");
        }
    }

    private void logInUser() {
        /*
         * User set in explicitimage.properties
         */
        mAuth.signInWithEmailAndPassword(BuildConfig.USER, BuildConfig.PASSWORD)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithEmail: onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithEmail: failed", task.getException());
                        }
                        updateOptionLogInButton();
                    }
                });
    }

    private void logOutUser() {
        mAuth.signOut();
        updateOptionLogInButton();
    }
}
