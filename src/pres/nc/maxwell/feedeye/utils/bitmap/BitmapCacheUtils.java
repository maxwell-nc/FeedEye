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

	
	public void displayBitmap(ImageView imageView, String url) {

		// 内存缓存
		BitmapMemoryCache bitmapMemoryCache = new BitmapMemoryCache(imageView,
				url);
		
		//手动设置压缩选项
		//bitmapMemoryCache.setCompressOptions(16, Bitmap.Config.RGB_565);
		
		if (!bitmapMemoryCache.displayBitmap()) {//内存中没有缓存

			LogUtils.i("BitmapCacheUtils", "内存中没有缓存");
			
			// 本地缓存
			BitmapLocalCahe bitmapLocalCahe = new BitmapLocalCahe(
					bitmapMemoryCache);

			if (!bitmapLocalCahe.displayBitmap()) {//本地中没有缓存

				LogUtils.i("BitmapCacheUtils", "本地中没有缓存");
				
				// 网路缓存
				BitmapNetworkCache bitmapNetworkCache = new BitmapNetworkCache(
						bitmapLocalCahe);
				bitmapNetworkCache.displayBitmap();//永真，网络无法获取则显示错误图片

			}

		}

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
