package com.example.mybannerview;


import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list.add("http://img4.imgtn.bdimg.com/it/u=2802552649,754334291&fm=214&gp=0.jpg");
        list.add("http://hbimg.huabanimg.com/06244f30abb918e06051e8d9a8e21d93d4ee8eff4390b-va2rB0_fw658");
        list.add("http://pic26.nipic.com/20121217/9252150_104156164000_2.jpg");
        list.add("http://pic18.nipic.com/20111207/6608733_123038684000_2.jpg");

        ViewPager viewPager = findViewById(R.id.banner);
        ViewGroup dotView = findViewById(R.id.in_ll);
        MyBanner banner = new MyBanner(this, list, viewPager);
        banner.initBanner(dotView);

        ImageView view = new ImageView(this);
        view.setImageResource(R.drawable.ic_launcher_background);
        view.setOnClickListener(v ->
                System.out.println("新添加的View的点击事件"));
        banner.addView(view);




    }

}
