package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * 图片缓存工具类（使用三级缓存）
 * 
 * @see BitmapThreeLevelsCache
 */
public class BitmapCacheUtils {

	/**
	 * 默认的加载中的资源图片id
	 */
	private static final int LOAD_RESOURCE_ID = R.drawable.listview_refresh_rotate;

	/**
	 * 默认的加载错误的资源图片id
	 */
	private static final int ERROR_RESOURCE_ID = R.drawable.img_load_error;

	/**
	 * 简单的显示图片，使用网络缓存、默认加载中加载失败图片，自动压缩图片
	 * 
	 * @param context
	 *            上下文
	 * @param imageView
	 *            要显示图片的控件
	 * @param url
	 *            要显示图片的地址（支持本地图片和网络图片）
	 * @see BitmapCacheUtils#displayBitmap(Context, ImageView, String, boolean,
	 *      int, int, int, android.graphics.Bitmap.Config)
	 */
	public static void displayBitmap(Context context, ImageView imageView,
			String url) {

		displayBitmap(context, imageView, url, true, -1, -1, -1, null);
	}

	/**
	 * 显示图片
	 * 
	 * @param context
	 *            上下文
	 * @param imageView
	 *            要显示图片的控件
	 * @param url
	 *            要显示图片的地址（支持本地图片和网络图片）
	 * @param sampleSize
	 *            采样大小（-1为不使用）
	 * @param config
	 *            颜色配置（null为不使用）
	 * @param isEnableNetworkCache
	 *            是否使用网络缓存
	 * @param loadResId
	 *            加载中的资源图片id（-1为不使用）
	 * @param errorResId
	 *            加载失败的资源图片id（-1为不使用）
	 * @see BitmapCacheUtils#displayBitmap(Context, ImageView, String)
	 */
	public static void displayBitmap(Context context, ImageView imageView,
			String url, boolean isEnableNetworkCache, int loadResId,
			int errorResId, int sampleSize, Bitmap.Config config) {

		// 设置加载中的图片
		if (loadResId != -1) {
			imageView.setImageResource(loadResId);
		} else {
			imageView.setImageResource(LOAD_RESOURCE_ID);
		}

		// 设置加载失败的图片
		Bitmap errBitmap = null;

		if (errorResId != -1) {
			errBitmap = BitmapFactory.decodeResource(context.getResources(),
					errorResId);
		} else {
			errBitmap = BitmapFactory.decodeResource(context.getResources(),
					ERROR_RESOURCE_ID);
		}

		// 设置压缩比例
		if (sampleSize == -1 || config == null) {// 自动压缩

			new BitmapThreeLevelsCache(imageView, url, errBitmap,
					isEnableNetworkCache).displayBitmap();

		} else {// 自定义压缩属性
			new BitmapThreeLevelsCache(imageView, url, errBitmap,
					isEnableNetworkCache)
					.setCompressOptions(sampleSize, config).displayBitmap();
		}

	}
}
