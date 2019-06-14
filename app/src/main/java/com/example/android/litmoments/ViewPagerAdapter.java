package com.example.android.litmoments;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
   // private Integer [] images = {R.drawable.ic_simpson2, R.drawable.ic_simpson2};
   private List<ViewPagerImages> images;

    public ViewPagerAdapter(Context context, List<ViewPagerImages> images ) {

        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_viewpager, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        Picasso.with(context).load(images.get(position).getJournalImagePath()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_mesut)
                .error(R.drawable.ic_journalfinal3).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(images.get(position).getJournalImagePath()).placeholder(R.drawable.ic_mesut)
                        .error(R.drawable.ic_journalfinal3).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.v("Picasso","Could not fetch image");

                    }
                });

            }
        });

        //imageView.setImageResource(images[position]);


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}