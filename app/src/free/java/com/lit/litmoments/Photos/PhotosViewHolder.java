package com.lit.litmoments.Photos;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lit.litmoments.DispJournal.DisplayJournal;
import com.lit.litmoments.DispJournal.ImageSliderActivity;
import com.lit.litmoments.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PhotosViewHolder  extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public PhotosViewHolder(View itemView) {
        super(itemView);
     imageView = itemView.findViewById(R.id.image);
    }

    public void bind(final PhotoModel photoModel, PhotosAdapter.OnItemClickListener listener){
        final int radius = 70;
        final int margin = 8;
       // final Transformation transformation = new Circl(radius, margin);
        Picasso picasso = Picasso.with(itemView.getContext());
        picasso.setIndicatorsEnabled(false);
        picasso.with(itemView.getContext()).load(photoModel.getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).
                placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline)
                .transform(new RoundedCornersTransformation(radius,margin)).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                picasso.with(itemView.getContext()).load(photoModel.getImageUrl()).
                        placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline)
                        .transform(new RoundedCornersTransformation(radius,margin)).into(imageView);
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(photoModel);
                Intent intent = new Intent(itemView.getContext(), ImageSliderActivity.class);
                ArrayList<String> arrimageFile = new ArrayList<>();
                arrimageFile.add(photoModel.getImageUrl());
                intent.putStringArrayListExtra("imagefiles", arrimageFile);
                itemView.getContext().startActivity(intent);
            }
        });

    }
}
