/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage;

import android.os.Bundle;
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

    DatabaseController mDatabase = new DatabaseController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final GalleryRecyclerAdapter recyclerAdapter = new GalleryRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mDatabase.getExplicitImages(new DatabaseController.LoadingCallback<List<ExplicitImage>>() {
            @Override
            public void onDataChange(List<ExplicitImage> images) {
                recyclerAdapter.clearItems();
                recyclerAdapter.updateItems(images);
            }

            @Override
            public void onCancelled() {

            }
        });
    }
}
