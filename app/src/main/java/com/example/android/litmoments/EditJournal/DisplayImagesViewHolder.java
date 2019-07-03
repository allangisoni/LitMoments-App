package com.example.android.litmoments.EditJournal;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.android.litmoments.EditJournal.DisplayImagesAdapter;
import com.example.android.litmoments.EditJournal.DisplayImagesModel;
import com.example.android.litmoments.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DisplayImagesViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivJournalPhoto;
    public CheckBox cbaddJournalCheck;

    public DisplayImagesViewHolder(View itemView) {
        super(itemView);
        ivJournalPhoto = itemView.findViewById(R.id.ivJournalPhoto);
        cbaddJournalCheck = itemView.findViewById(R.id.chJournalCheck);

    }

   /** public void bind(final DisplayImagesModel photoitem, final DisplayImagesAdapter.OnItemClickListener listener) {

         Picasso.with(itemView.getContext()).load(photoitem.getJournalImageView()).networkPolicy(NetworkPolicy.OFFLINE).into(ivJournalPhoto, new Callback() {
             @Override
             public void onSuccess() {

             }

             @Override
             public void onError() {
                 Picasso.with(itemView.getContext()).load(new File (photoitem.getJournalImageView())).networkPolicy(NetworkPolicy.OFFLINE).into(ivJournalPhoto, new Callback() {
                     @Override
                     public void onSuccess() {

                     }

                     @Override
                     public void onError() {

                     }
                 });
             }
         });

          //   Picasso.with(itemView.getContext()).load(new File(photoitem.getJournalImageView())).networkPolicy(NetworkPolicy.OFFLINE).into(ivJournalPhoto);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(photoitem);

                //Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                //intent.putExtra("movieDetails", movizitem);
                //itemView.getContext().startActivity(intent);
            }
        });
    }  **/
}
