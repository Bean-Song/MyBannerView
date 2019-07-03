package com.example.mybannerview;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BannerPagerAdapter extends PagerAdapter {
    Context context;
    private List<String> list;
    private List<View> bannerView = new ArrayList<>();

    /**
     * 构造函数主要获取两个对象
     * @param context banner 所在的上下文对象
     * @param list 图片的网址的集合
     */
    public BannerPagerAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }

    /**
     * 通过传来的图片的网络地址资源 加载图片
     * 图片大小为充满父控件
     */
    public void initViewList() {
        for (String s : list) {
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(s).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            bannerView.add(imageView);
        }
    }



    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(bannerView.get(position));
    }

    @Override
    public int getCount() {
        return bannerView.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        bannerView.get(position).setOnClickListener(v -> System.out.println(position + "<<<<<<<<<<<<<<<<<<<"));
        container.addView(bannerView.get(position));
        return bannerView.get(position);
    }

    /**
     * 添加一个view，在banner的最后一个元素，因为做到无限切换，首尾都多一个item，所以加的位置是size() - 1 的位置
     * @param view 添加的view
     */
    public void addView(View view) {
        if (view != null) {
            bannerView.add(bannerView.size() - 1, view);
        }
    }
}
