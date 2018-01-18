package com.lzw.my3dviewpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

/**
 * 在这里测试一下，看看效果
 */
public class MainActivity extends AppCompatActivity {
    //这里的图片从百度图片中下载，图片规格是960*640
    private static final int[] drawableIds = new int[]{R.mipmap.ic1,R.mipmap.ic2, R.mipmap.ic3,
            R.mipmap.ic4, R.mipmap.ic5, R.mipmap.ic6, R.mipmap.ic7, R.mipmap.ic8,
            R.mipmap.ic9, R.mipmap.ic10, R.mipmap.ic11, R.mipmap.ic12, R.mipmap.ic13};
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private MyPagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new MyPagerAdapter(drawableIds,this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true,new RotationPageTransformer());
        mViewPager.setOffscreenPageLimit(2);//设置预加载的数量，这里设置了2,会预加载中心item左边两个Item和右边两个Item
        mViewPager.setPageMargin(10);//设置两个Page之间的距离



    }
}
