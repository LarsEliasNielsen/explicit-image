/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import dk.lndesign.explicitimage.adapter.PagerAdapter;
import dk.lndesign.explicitimage.view.PagerFragment;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class MainActivity extends AppCompatActivity {

    PagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<PagerFragment> pagerFragments = new ArrayList<>();
        pagerFragments.add(GalleryFragment.newInstance("Gallery"));
        pagerFragments.add(UploadFragment.newInstance("Upload"));

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), pagerFragments);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
