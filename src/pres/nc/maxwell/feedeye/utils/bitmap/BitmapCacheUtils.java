package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapNetworkCache;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Bitmap三级缓存工具类
 */
public class BitmapCacheUtils {

	public void displayBitmap(ImageView imageView, String url) {

		// 内存缓存
		// 本地缓存
		// 网路缓存
		new BitmapNetworkCache(imageView, url).displayBitmap();

	}

	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			Bitmap bitmap) {

		imageView.setImageBitmap(bitmap);
		displayBitmap(imageView, url);

	}

	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			Drawable drawable) {

		imageView.setImageDrawable(drawable);
		displayBitmap(imageView, url);

	}

	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			int resId) {

		imageView.setImageResource(resId);
		displayBitmap(imageView, url);

	}
}
