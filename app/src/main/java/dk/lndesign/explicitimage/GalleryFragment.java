/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dk.lndesign.explicitimage.adapter.GalleryRecyclerAdapter;
import dk.lndesign.explicitimage.controller.DatabaseController;
import dk.lndesign.explicitimage.model.ExplicitImage;
import dk.lndesign.explicitimage.util.CompatibilityUtil;
import dk.lndesign.explicitimage.view.PagerFragment;

/**
 * @author Lars Nielsen <lars@lndesign.dk>.
 */
public class GalleryFragment extends PagerFragment {

    private static final String KEY_FRAGMENT_TITLE = "fragment_title";

    private DatabaseController mDatabase = new DatabaseController();

    private GalleryRecyclerAdapter mRecycleAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    public static GalleryFragment newInstance(String title) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle(1);
        args.putString(KEY_FRAGMENT_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTitle() {
        return getArguments().getString(KEY_FRAGMENT_TITLE, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        Context context = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecycleAdapter = new GalleryRecyclerAdapter();
        recyclerView.setAdapter(mRecycleAdapter);
        if (CompatibilityUtil.isTablet(context) || CompatibilityUtil.isLandscape(context)) {
            // Tablet layout.
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            // Phone layout.
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateImages();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateImages();
    }

    private void updateImages() {
        mDatabase.getExplicitImages(new DatabaseController.LoadingCallback<List<ExplicitImage>>() {
            @Override
            public void onDataChange(List<ExplicitImage> images) {
                mRecycleAdapter.clearItems();
                mRecycleAdapter.updateItems(images);

                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled() {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }
}
