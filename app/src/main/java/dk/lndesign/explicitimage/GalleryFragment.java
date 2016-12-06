/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

/**
 * @author Lars Nielsen <lars@lndesign.dk>.
 */
public class GalleryFragment extends Fragment {

    private DatabaseController mDatabase = new DatabaseController();

    private Context mContext;

    private GalleryRecyclerAdapter mRecycleAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mContext = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecycleAdapter = new GalleryRecyclerAdapter();
        recyclerView.setAdapter(mRecycleAdapter);
        if (CompatibilityUtil.isTablet(mContext) || CompatibilityUtil.isLandscape(mContext)) {
            // Tablet layout.
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            // Phone layout.
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateImages();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UploadFragment.class);
                startActivity(intent);
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