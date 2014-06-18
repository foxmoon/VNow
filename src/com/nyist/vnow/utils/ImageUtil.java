package com.nyist.vnow.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
/**
 * 
 * @author harry
 *
 */
public class ImageUtil {

	
	/**
	 * @param b
	 * @return true：bitmap可用 false:bitmap不可用
	 */
	public static boolean isAvailableBitmap(Bitmap b) {
		if (b != null && !b.isRecycled())
			return true;
		else
			return false;
	}
}
