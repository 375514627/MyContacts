package com.example.a8.newcontacts.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by A8 on 2016/5/12.
 */
public class ImageLoad {

    public static Bitmap loadFromResouce() {
        new AsyncTask<Void, Void, Bitmap>(){

            @Override
            protected void onPostExecute(Bitmap bitmap) {
            }

            @Override
            protected Bitmap doInBackground(Void... params) {


                return null;
            }
        }.execute();
        return null;
    }

    //图片二次采样 显示到添加联系人 测试

    public static Bitmap compressBitmap(String iamgeFilePath, int width, int height) {

        Log.d("TAG", "理想的宽高 ： width = " + width + "   height = " + height);

        //图片的二次采样
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只会去加载图片的宽高
        //如果options.inJustDecodeBounds=true;解码这个图片的结果为null。只会去加载这个图片的宽高

        int imageWidth = options.outWidth;//得到图片的真实宽度---2000
        int imageHeight = options.outHeight;//得到图片的真实高度---1200
        Log.d("TAG", "真实的宽高 ： imageWidth = " + imageWidth + "   imageHeight = " + imageHeight);
        //计算图片缩放的倍数
        int scale = 1;

        // Math.ceil---->向前取整数。如：2.3=>3。7.1=>8（为了让得到的比例强转）
        int scaleX = (int) Math.ceil(1.0 * imageWidth / width);
        int scaleY = (int) Math.ceil(1.0 * imageHeight / height);
        if (scaleX > 1 || scaleY > 1) {//只有X或Y的缩放比例大于1才需要去比较缩放
            scale = scaleX > scaleY ? scaleX : scaleY;
        }

        options.inJustDecodeBounds = false;//取消只加载图片的宽高
        options.inSampleSize = scale;//设置图片的缩放比例
        options.inPreferredConfig = Bitmap.Config.RGB_565;//设置图片解码的质量

        final Bitmap bitmap = BitmapFactory.decodeFile(iamgeFilePath, options);

        Log.d("TAG", "最终的宽高 ： width = " + bitmap.getWidth() + "   height = " + bitmap.getHeight());

        return bitmap;
    }

}
