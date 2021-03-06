/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.lndesign.explicitimage.ImageViewerActivity;
import dk.lndesign.explicitimage.R;
import dk.lndesign.explicitimage.model.ExplicitImage;
import dk.lndesign.explicitimage.model.vision.response.EntityAnnotation;
import dk.lndesign.explicitimage.model.vision.response.SafeSearchAnnotation;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        ImageView mImageView;
        TextView mImagePath;
        View mImageLabelContainer;
        TextView mImageLabels;
        View mSafeSearchContainer;
        TextView mSafeSearchAdult;
        TextView mSafeSearchSpoof;
        TextView mSafeSearchMedical;
        TextView mSafeSearchViolence;

        ViewHolder(View view) {
            super(view);
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mImageView = (ImageView) view.findViewById(R.id.image_view);
            mImagePath = (TextView) view.findViewById(R.id.image_path);
            mImageLabelContainer = view.findViewById(R.id.label_container);
            mImageLabels = (TextView) view.findViewById(R.id.image_labels);
            mSafeSearchContainer = view.findViewById(R.id.safe_search_container);
            mSafeSearchAdult = (TextView) view.findViewById(R.id.safe_search_adult);
            mSafeSearchSpoof = (TextView) view.findViewById(R.id.safe_search_spoof);
            mSafeSearchMedical = (TextView) view.findViewById(R.id.safe_search_medical);
            mSafeSearchViolence = (TextView) view.findViewById(R.id.safe_search_violence);
        }
    }

    private List<ExplicitImage> mItems = new ArrayList<>();

    /**
     * Sets the contents of the adapter to the new list of items and notifies the recycler view.
     *
     * @param items New list of items.
     */
    public void updateItems(@NonNull List<ExplicitImage> items) {
        mItems.clear();
        mItems.addAll(items);

        notifyDataSetChanged();
    }

    /**
     * Makes the adapter empty.
     */
    public void clearItems() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_image_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ExplicitImage image = mItems.get(position);
        final Context context = holder.itemView.getContext();

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewerActivity.class);
                intent.putExtra(ImageViewerActivity.IMAGE_URL, image.getDownloadPath());
                context.startActivity(intent);
            }
        });

        Glide.with(holder.itemView.getContext())
                .load(image.getDownloadPath())
                .into(holder.mImageView);

        holder.mImagePath.setText(image.getImagePath());

        if (image.getEntityAnnotations() != null) {
            StringBuilder labels = new StringBuilder();
            for (EntityAnnotation entityAnnotation : image.getEntityAnnotations()) {
                labels.append(String.format(Locale.ENGLISH,
                        context.getString(R.string.image_label_entity),
                        entityAnnotation.getDescription(),
                        entityAnnotation.getScore() * 100));
                labels.append(", ");
            }
            holder.mImageLabels.setText(labels.toString());
            holder.mImageLabelContainer.setVisibility(View.VISIBLE);
        } else {
            holder.mImageLabelContainer.setVisibility(View.GONE);
        }

        if (image.getSafeSearchAnnotation() != null) {
            holder.mSafeSearchAdult.setText(String.format(Locale.ENGLISH,
                    context.getString(R.string.safe_search),
                    getSafeSearchValue(image.getSafeSearchAnnotation().getAdult())));
            setSafeSearchTextColor(holder.mSafeSearchAdult, image.getSafeSearchAnnotation().getAdult());

            holder.mSafeSearchSpoof.setText(String.format(Locale.ENGLISH,
                    context.getString(R.string.safe_search),
                    getSafeSearchValue(image.getSafeSearchAnnotation().getSpoof())));
            setSafeSearchTextColor(holder.mSafeSearchSpoof, image.getSafeSearchAnnotation().getSpoof());

            holder.mSafeSearchMedical.setText(String.format(Locale.ENGLISH,
                    context.getString(R.string.safe_search),
                    getSafeSearchValue(image.getSafeSearchAnnotation().getMedical())));
            setSafeSearchTextColor(holder.mSafeSearchMedical, image.getSafeSearchAnnotation().getMedical());

            holder.mSafeSearchViolence.setText(String.format(Locale.ENGLISH,
                    context.getString(R.string.safe_search),
                    getSafeSearchValue(image.getSafeSearchAnnotation().getViolence())));
            setSafeSearchTextColor(holder.mSafeSearchViolence, image.getSafeSearchAnnotation().getViolence());

            holder.mSafeSearchContainer.setVisibility(View.VISIBLE);
        } else {
            holder.mSafeSearchContainer.setVisibility(View.GONE);
        }
    }

    private void setSafeSearchTextColor(TextView textView, @SafeSearchAnnotation.Likelihood String likelihood) {
        switch (likelihood) {
            case SafeSearchAnnotation.LIKELIHOOD_VERY_UNLIKELY:
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.warning_low_level));
                break;
            case SafeSearchAnnotation.LIKELIHOOD_UNLIKELY:
            case SafeSearchAnnotation.LIKELIHOOD_POSSIBLE:
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.warning_mid_level));
                break;
            case SafeSearchAnnotation.LIKELIHOOD_LIKELY:
            case SafeSearchAnnotation.LIKELIHOOD_VERY_LIKELY:
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.warning_high_level));
                break;
            case SafeSearchAnnotation.LIKELIHOOD_UNKNOWN:
                textView.setTextColor(Color.BLACK);
                break;
            default:
                break;
        }
    }

    private int getSafeSearchValue(@SafeSearchAnnotation.Likelihood String likelihood) {
        switch (likelihood) {
            case SafeSearchAnnotation.LIKELIHOOD_VERY_UNLIKELY:
                return 1;
            case SafeSearchAnnotation.LIKELIHOOD_UNLIKELY:
                return 2;
            case SafeSearchAnnotation.LIKELIHOOD_POSSIBLE:
                return 3;
            case SafeSearchAnnotation.LIKELIHOOD_LIKELY:
                return 4;
            case SafeSearchAnnotation.LIKELIHOOD_VERY_LIKELY:
                return 5;
            case SafeSearchAnnotation.LIKELIHOOD_UNKNOWN:
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
