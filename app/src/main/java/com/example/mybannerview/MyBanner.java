package com.example.mybannerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * banner轮播图，
 * 1、能通过图片网络地址的list 组装成无限循环的轮播图
 * 2、可以添加一个view，将View对象添加到banner中
 * 需要在xml 中添加ViewPager的组件和Dots的容器
 * <p>
 * ##########################################################
 * <p>
 * <android.support.v4.view.ViewPager
 * android:id="@+id/banner"
 * android:layout_width="wrap_content"
 * android:layout_height="160dp"
 * app:layout_constraintBottom_toBottomOf="parent"
 * app:layout_constraintLeft_toLeftOf="parent"
 * app:layout_constraintRight_toRightOf="parent"
 * app:layout_constraintTop_toTopOf="parent" />
 * <p>
 * <p>
 * <LinearLayout
 * android:id="@+id/in_ll"
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * android:layout_marginStart="8dp"
 * android:layout_marginLeft="8dp"
 * android:layout_marginEnd="8dp"
 * android:layout_marginRight="8dp"
 * android:layout_marginTop="150dp"
 * android:orientation="horizontal"
 * app:layout_constraintEnd_toEndOf="parent"
 * app:layout_constraintStart_toStartOf="parent"
 * app:layout_constraintTop_toTopOf="@+id/banner">
 *
 * </LinearLayout>
 * <p>
 * #############################################################
 * <p>
 * Dots小圆点 用到两个 ImageResource 资源 （R.drawable.light_dot， R.drawable.gray_dot）
 */
public class MyBanner implements ViewPager.OnPageChangeListener {
    private Context context;
    private List<String> imageUrlList;//图片地址
    private ViewPager viewPager;//将显示轮播图的ViewPager组件
    private ViewGroup dotView;//小白点展示的容器
    private List<ImageView> dotList = new ArrayList<>();//小白点组件
    private BannerPagerAdapter adapter;
    private int bannerSize;
    private int scrollDuration = 500; //切换的动画时间
    private int scrollShowDuration = 2500; //切换的时间间隔 = 切换动画时间 + 展示时间

    /**
     * 构造器
     * @param context      添加banner的上下文
     * @param imageUrlList 图片网络地址的list
     * @param viewPager    在xml 中添加的的ViewPager对象
     */
    public MyBanner(Context context, List<String> imageUrlList, ViewPager viewPager) {
        this.context = context;
        this.imageUrlList = imageUrlList;
        this.viewPager = viewPager;
    }


    /**
     * 设置切换的动画时间
     * @param scrollDuration 单位是ms
     */
    public void setScrollDuration(int scrollDuration) {
        this.scrollDuration = scrollDuration;
    }

    /**
     * 设置切换的间隔时间，两次动画的间隔时间是切换的动画时间和展示时间之和
     * @param scrollShowDuration 单位 ms
     */
    public void setScrollShowDuration(int scrollShowDuration) {
        this.scrollShowDuration = scrollShowDuration;
    }

    /**
     * 初始化 banner
     *
     * @param dotView 小圆点的容器对象
     */
    public void initBanner(ViewGroup dotView) {
        bannerSize = imageUrlList.size();
        this.dotView = dotView;
        initImgList();
        adapter = new BannerPagerAdapter(context, imageUrlList);
        adapter.initViewList();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        //设置切换图片的时间
        ViewPagerScroller scroller = new ViewPagerScroller(context);
        scroller.setScrollDuration(scrollDuration);//切换过度的时间
        scroller.initViewPagerScroll(viewPager);

        initDots();

        //自动切换的时间控制，每隔一段时间发送一次消息，让banner切换
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(scrollShowDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
        viewPager.setCurrentItem(1);
    }

    /**
     * 图片网络地址的list 的处理，在首尾加一个，可以实现图片的无限循环
     */
    private void initImgList() {
        String s0 = imageUrlList.get(0);
        String sX = imageUrlList.get(imageUrlList.size() - 1);
        imageUrlList.add(0, sX);
        imageUrlList.add(s0);
    }

    /**
     * 添加小圆点，根据图片的数量判断添加圆点的数量
     */
    private void initDots() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 20, 0);
        for (int i = 1; i < imageUrlList.size() - 1; i++) {
            ImageView dot = new ImageView(context);
            dot.setImageResource(R.drawable.gray_dot);
            dotList.add(dot);
            dotView.addView(dot, layoutParams);
        }

    }

    /**
     * 实现无限切换，当选中最后一张是切换到第2张，当选中第一张时切换到倒数第二张，切换中无动画，视觉上产生无限循环的效果
     *
     * @param position             当前所在图片的position
     * @param positionOffset       移动的百分比
     * @param positionOffsetPixels 移出的偏移量
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0.0) {
            if (position == 0) {
                viewPager.setCurrentItem(bannerSize, false);
            } else if (position == bannerSize + 1) {
                viewPager.setCurrentItem(1, false);
            }
        }
    }

    /**
     * 根据选中的页面重置小圆点的图片样式，选中的position对应的小圆点变亮
     *
     * @param position 当前所在的图片的position
     */
    @Override
    public void onPageSelected(int position) {
        for (ImageView v : dotList) {
            v.setImageResource(R.drawable.gray_dot);
        }
        if (position > 0 && position <= dotList.size())
            dotList.get(position - 1).setImageResource(R.drawable.light_dot);
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }

    /**
     * 图片循环控制，当一定时间后让图片切换一次
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    };

    /**
     * 可以在banner的后面添加一个自定义的view
     *
     * @param view 添加的View
     */
    public void addView(View view) {
        adapter.addView(view);
        addDot();
        bannerSize++;
        adapter.notifyDataSetChanged();

    }

    /**
     * 添加View的时候使用，同时添加一个小圆点匹配View的数量
     */
    private void addDot() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 20, 0);
        ImageView dot = new ImageView(context);
        dot.setImageResource(R.drawable.gray_dot);
        dotList.add(dot);
        dotView.addView(dot, layoutParams);

    }


    /**
     * 内部类 设置切换时间
     */
    public class ViewPagerScroller extends Scroller {
        // 默认滑动速度
        private int mScrollDuration = 1000;

        /**
         * 设置速度速度
         *
         * @param duration
         */
        public void setScrollDuration(int duration) {
            this.mScrollDuration = duration;
        }

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }


        public void initViewPagerScroll(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
