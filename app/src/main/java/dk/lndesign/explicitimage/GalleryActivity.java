/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import dk.lndesign.explicitimage.adapter.GalleryRecyclerAdapter;
import dk.lndesign.explicitimage.controller.DatabaseController;
import dk.lndesign.explicitimage.model.ExplicitImage;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class GalleryActivity extends AppCompatActivity {

    private DatabaseController mDatabase = new DatabaseController();

    private GalleryRecyclerAdapter mRecycleAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycleAdapter = new GalleryRecyclerAdapter();
        recyclerView.setAdapter(mRecycleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateImages();
            }
        });

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
