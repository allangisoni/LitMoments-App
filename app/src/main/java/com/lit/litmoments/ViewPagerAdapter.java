package com.lit.litmoments;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
   // private Integer [] images = {R.drawable.ic_simpson2, R.drawable.ic_simpson2};
   private List<ViewPagerImages> images;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    //private ImageView mImageView;
    ZoomageView imageView;

    public ViewPagerAdapter(Context context, List<ViewPagerImages> images ) {

        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images==null?0:images.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_viewpager, null);
         imageView = (ZoomageView) view.findViewById(R.id.imageView);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());

        Picasso.with(context).load(images.get(position).getJournalImagePath()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_travelbg)
                .error(R.drawable.ic_offline).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(images.get(position).getJournalImagePath()).placeholder(R.drawable.ic_travelbg)
                        .error(R.drawable.ic_offline).into(imageView, new Callback() {
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




    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
