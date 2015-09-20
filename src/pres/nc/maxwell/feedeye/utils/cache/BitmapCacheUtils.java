package pres.nc.maxwell.feedeye.utils.cache;

import android.widget.ImageView;

/**
 * Bitmap三级缓存工具类
 */
public class BitmapCacheUtils {

	public void displayBitmap(ImageView imageView, String url) {

		// 内存缓存
		// 本地缓存
		// 网路缓存
		new BitmapNetworkCacheUtils().displayBitmapAsyncFromNetwork(imageView,
				url);

	}
}
