package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapLocalCahe;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapMemoryCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapNetworkCache;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Bitmap三级缓存工具类
 */
public class BitmapCacheUtils {

	/**
	 * 内存缓存对象
	 */
	private BitmapMemoryCache mBitmapMemoryCache;

	/**
	 * 本地缓存对象
	 */
	private BitmapLocalCahe mBitmapLocalCahe;

	/**
	 * 网络缓存对象
	 */
	private BitmapNetworkCache mBitmapNetworkCache;

	public BitmapCacheUtils() {
		mBitmapMemoryCache = BitmapMemoryCache.getInstance();
		mBitmapLocalCahe = BitmapLocalCahe.getInstance();
		mBitmapNetworkCache = BitmapNetworkCache.getInstance();
	}

	public void displayBitmap(ImageView imageView, String url) {

	
		if (!mBitmapMemoryCache.displayBitmap(imageView, url)) {// 内存中没有缓存

			LogUtils.i("BitmapCacheUtils", "内存中没有缓存");

			if (!mBitmapLocalCahe.displayBitmap(imageView, url)) {// 本地中没有缓存

				LogUtils.i("BitmapCacheUtils", "本地中没有缓存");

				mBitmapNetworkCache.displayBitmap(imageView, url);// 永真，网络无法获取则显示错误图片

			}

		}

	}

	public void displayBitmap(ImageView imageView, String url, int sampleSize,
			Bitmap.Config config) {

		// 手动设置压缩选项
		mBitmapMemoryCache.setCompressOptions(sampleSize, config);

		displayBitmap(imageView, url);

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
