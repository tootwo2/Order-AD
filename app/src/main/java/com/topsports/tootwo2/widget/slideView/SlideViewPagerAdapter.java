package com.topsports.tootwo2.widget.slideView;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by tootwo2 on 15/11/3.
 */
public class SlideViewPagerAdapter extends PagerAdapter{

    //放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;

    public  SlideViewPagerAdapter(List<ImageView> imageViewsList){
        this.imageViewsList=imageViewsList;
    }


    @Override
    public Object instantiateItem(View container, int position) {
        ImageView imageView = imageViewsList.get(position);

        ((ViewPager)container).addView(imageViewsList.get(position));
        return imageViewsList.get(position);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager)container).removeView(imageViewsList.get(position));
    }


    @Override
    public int getCount() {
        return imageViewsList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
