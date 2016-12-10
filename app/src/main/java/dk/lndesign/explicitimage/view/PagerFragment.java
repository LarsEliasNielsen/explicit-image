/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.view;

import android.support.v4.app.Fragment;

/**
 * Titled fragment for viewpager with tab layout.
 *
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class PagerFragment extends Fragment {

    /**
     * Get fragment title.
     * Override this method to return the desired title used for creating a fragment tab.
     * @return Title of fragment
     */
    public String getTitle() {
        return null;
    }
}
