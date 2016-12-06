/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mItems = new ArrayList<>();

    public PagerAdapter(FragmentManager fm, List<Fragment> items) {
        super(fm);

        mItems = items;
    }

    @Override
    public Fragment getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Hi";
    }
}
