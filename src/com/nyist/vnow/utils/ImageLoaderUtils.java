package com.nyist.vnow.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nyist.vnow.R;

/**
 * @author harry
 * @version Creat on 2014-6-27上午11:52:36
 */
public class ImageLoaderUtils
{
    private static DisplayImageOptions options = null;
    public static ImageLoader imageLoader = ImageLoader
            .getInstance();
    /*
     * 读取手机图片的DisplayImageOptions
     */
    public static DisplayImageOptions defaultPhotoOptions = ImageLoaderUtils
            .initDisplayImageOptions(R.drawable.pic_default_small,
                    R.drawable.pic_default_small,
                    R.drawable.pic_default_small);
    public static DisplayImageOptions roundedPhotoOptions = ImageLoaderUtils
            .initRoundedDisplayImageOptions(R.drawable.pic_default_small,
                    R.drawable.pic_default_small,
                    R.drawable.pic_default_small);

    public static DisplayImageOptions initDisplayImageOptions(
            int imageResOnLoading,
            int imageResForEmptyUri, int imageResOnFail) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(
                        imageResOnLoading)
                .showImageForEmptyUri(
                        imageResForEmptyUri)
                .showImageOnFail(
                        imageResOnFail)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

    public static DisplayImageOptions initRoundedDisplayImageOptions(
            int imageResOnLoading,
            int imageResForEmptyUri, int imageResOnFail) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(
                        imageResOnLoading)
                .showImageForEmptyUri(
                        imageResForEmptyUri)
                .showImageOnFail(
                        imageResOnFail)
                .cacheInMemory(true).displayer(new RoundedBitmapDisplayer(5))
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

    public static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener
    {
        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri,
                View view, Bitmap loadedImage)
        {
            if (loadedImage != null)
            {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages
                        .contains(imageUri);
                if (firstDisplay)
                {
                    FadeInBitmapDisplayer.animate(
                            imageView, 100);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
