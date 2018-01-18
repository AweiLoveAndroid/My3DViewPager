package com.lzw.my3dviewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/4/15.
 */
public class MyPagerAdapter extends PagerAdapter {
    private int[] mBitmapIds;
    private Context mContext;
    private LruCache<Integer,Bitmap> mCache;

    public MyPagerAdapter(int[] data,Context context){
        this.mBitmapIds = data;
        this.mContext = context;

        //以下两个变量是做缓存处理
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory * 3 / 8;  //缓存区的大小
        mCache = new LruCache<Integer, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();  //返回Bitmap的大小
            }
        };
    }

    @Override
    public int getCount() {
        return mBitmapIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv);
        //imageView.setImageResource(mBitmapIds[position]);
        //new LoadBitmapTask(imageView).execute(mBitmapIds[position]);
        loadBitmapIntoTarget(mBitmapIds[position],imageView);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    /**
     * 看有没有缓存，有就开启异步加载，没有就直接加载图片
     * @param id
     * @param imageView
     */
    public void loadBitmapIntoTarget(Integer id, ImageView imageView){
    	//真正开发中是要做三级缓存处理的，这里都是用的本地图片，就没有做处理。
    	//如果你想试试，可以在tomcat里面放几个图片，试试从服务器获取图片，然后去做三级缓存处理
    	//我这里简化操作，只简洁的说一下基本的思路
        //首先尝试从内存缓存中获取是否有对应id的Bitmap
        Bitmap bitmap = mCache.get(id);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else {
            //如果没有则开启异步任务去加载
            new LoadBitmapTask(imageView).execute(id);
        }
    }


    //计算图片大小
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height >= reqHeight || width > reqWidth){
            while ((height / (2 * inSampleSize)) >= reqHeight
                    && (width / (2 * inSampleSize)) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    //dp转换成px
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 异步加载图片
     */
    private class LoadBitmapTask extends AsyncTask<Integer,Void,Bitmap> {
        private ImageView imageView;

        public LoadBitmapTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            //这样做没有做缓存处理，加载大量大图容易出现OOM
            //Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),params[0]);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;     //1、inJustDecodeBounds置为true，此时只加载图片的宽高信息
            BitmapFactory.decodeResource(mContext.getResources(),params[0],options);
            options.inSampleSize = calculateInSampleSize(options,
                    dp2px(mContext,240),
                    dp2px(mContext,360));          //2、根据ImageView的宽高计算所需要的采样率
            options.inJustDecodeBounds = false;    //3、inJustDecodeBounds置为false，正常加载图片
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),params[0],options);
            //把加载好的Bitmap放进LruCache内
            mCache.put(params[0],bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }


    }
}
