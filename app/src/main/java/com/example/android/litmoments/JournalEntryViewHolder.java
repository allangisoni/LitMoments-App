package com.example.android.litmoments;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class JournalEntryViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivaddJournalPhoto;
    public CheckBox  cbaddJournalCheck;
    private  List<JournalPhotoModel> items,selected;
    JournalEntryAdapater journalEntryAdapater;

    public JournalEntryViewHolder(View itemView) {
        super(itemView);
        ivaddJournalPhoto = itemView.findViewById(R.id.ivJournalPhoto);
        cbaddJournalCheck = itemView.findViewById(R.id.chJournalCheck);

    }

   /** public void bind(final JournalPhotoModel photoitem, final JournalEntryAdapater.OnItemClickListener listener) {

        Picasso.with(itemView.getContext()).load(photoitem.getJournalImage()).into(ivJournalPhoto);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(photoitem);

               /* if (selected.contains(photoitem)) {
                    selected.remove(photoitem);
                    unhighlightView(itemView);
                } else {
                    selected.add(photoitem);
                    highlightView(itemView);
                }

                receiver.onClickAction();
               */

                //Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                //intent.putExtra("movieDetails", movizitem);
                //itemView.getContext().startActivity(intent);
            }


     //   });

     /*   if (selected.contains(photoitem)) {
            highlightView(itemView);
        } else {
            unhighlightView(itemView);
        }*/
 //   }

  //  }


