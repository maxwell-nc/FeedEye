package pres.nc.maxwell.feedeye.utils.bitmap;

import java.io.File;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapLocalCahe;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapLocalCahe.OnFinishedGetLocalCacheListener;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapMemoryCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapNetworkCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapNetworkCache.OnFinishedGetNetworkCacheListener;
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
	 * 是否开启网络缓存
	 */
	private boolean mIsEnableNetworkCache = true;

	/**
	 * 初始化三级缓存对象，默认使用网络缓存
	 */
	public BitmapCacheUtils() {
		this(true);
	}

	/**
	 * 初始化三级缓存对象
	 * 
	 * @param isEnableNetworkCache
	 *            是否使用网络缓存
	 */
	public BitmapCacheUtils(boolean isEnableNetworkCache) {

		this.mIsEnableNetworkCache = isEnableNetworkCache;

		// 内存缓存对象
		mBitmapMemoryCache = new BitmapMemoryCache();

	}

	/**
	 * 从缓存中获取Bitmap并显示在ImageView中
	 * 
	 * @param imageView
	 *            要显示的ImageView对象
	 * @param url
	 *            要显示的图片URL
	 */
	public void displayBitmap(ImageView imageView, String url) {
		

		
		// 注意：网络缓存会创建新的子线程，不要复用对象，否则出现部分图片无法加载问题
		BitmapNetworkCache networkCache = null;

		// 本地缓存对象
		final BitmapLocalCahe localCahe = new BitmapLocalCahe();
	
		
		if (mIsEnableNetworkCache) {
			// 网络缓存对象
			networkCache = new BitmapNetworkCache();
		} else {
			LogUtils.i("BitmapCacheUtils", "不使用网络缓存");
		}

		
		// 防止空指针
		if (imageView == null) {
			return;
		}
		
		imageView.setTag(url);// ImageView与URL绑定，防止重用显示错误图片
		
		if (networkCache != null) {
			// 设置完成读取网路缓存的处理
			networkCache
					.setOnFinishedGetNetworkCache(new OnFinishedGetNetworkCacheListener() {

						@Override
						public void onFinishedGetNetworkCache(
								pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapNetworkCache thisCache,
								ImageView imageView, String url, boolean result) {

							if (!result) {// 获取失败
								thisCache.showErrorBitmap();
							} else {// 成功获取
								// 调用本地缓存对象处理
								if (!localCahe.displayBitmap(imageView,
										url, mBitmapMemoryCache)) {
									thisCache.showErrorBitmap();
								}
							}

						}
					});
		}
		
		
		localCahe.setOnFinishedGetLocalCacheListener(new OnFinishedGetLocalCacheListener() {
			
			@Override
			public void onFinishedGetLocalCache(ImageView imageView, String url, File cacheFile) {
				// 设置内存缓存
				mBitmapMemoryCache.setCache(cacheFile);


				// 从内存中显示
				mBitmapMemoryCache.displayBitmap(imageView, url, null);

			
			}
		});

		// 1.读取内存缓存
		if (!mBitmapMemoryCache.displayBitmap(imageView, url, null)) {// 内存中没有缓存

			LogUtils.i("BitmapCacheUtils", "内存中没有缓存");

			// 2.读取本地缓存
			if (!localCahe.displayBitmap(imageView, url,
					mBitmapMemoryCache)) {// 本地中没有缓存

				LogUtils.i("BitmapCacheUtils", "本地中没有缓存");

				if (mIsEnableNetworkCache) {
					// 3.读取网络缓存
					networkCache
							.displayBitmap(imageView, url, localCahe);// 永真，网络无法获取则显示错误图片
				} else {// 不使用网络缓存且本地缓存不存在
					localCahe.showErrorBitmap();
				}
			}

		}
	}

	/**
	 * 从缓存中获取Bitmap并显示在ImageView中,带自定义的压缩选项
	 * 
	 * @param imageView
	 *            要显示的ImageView对象
	 * @param url
	 *            要显示的图片URL
	 * @param sampleSize
	 *            采样大小
	 * @param config
	 *            颜色配置
	 */
	public void displayBitmap(ImageView imageView, String url, int sampleSize,
			Bitmap.Config config) {

		// 手动设置压缩选项
		mBitmapMemoryCache.setCompressOptions(sampleSize, config);

		displayBitmap(imageView, url);

	}

	/**
	 * 从缓存中获取Bitmap并显示在ImageView中,带加载中图片
	 * 
	 * @param imageView
	 *            要显示的ImageView对象
	 * @param url
	 *            要显示的图片URL
	 * @param t
	 *            加载时显示的图片
	 */
	public <T> void displayBitmap(ImageView imageView, String url, T t) {

		if (t instanceof Bitmap) {

			imageView.setImageBitmap((Bitmap) t);

		} else if (t instanceof Drawable) {

			imageView.setImageDrawable((Drawable) t);

		} else if (t instanceof Integer) {// ResourceID

			imageView.setImageResource((Integer) t);

		} else {// T is a Illegal Type

			throw new RuntimeException(this.getClass().getName()
					+ "T is not a Bitmap type");

		}

		displayBitmap(imageView, url);
	}

	/**
	 * 从缓存中获取Bitmap并显示在ImageView中,带自定义的压缩选项和设置自定义的加载中图片
	 * 
	 * @param imageView
	 *            要显示的ImageView对象
	 * @param url
	 *            要显示的图片URL
	 * @param sampleSize
	 *            采样大小
	 * @param config
	 *            颜色配置
	 * @param t
	 *            加载时显示的图片
	 */
	public <T> void displayBitmap(ImageView imageView, String url,
			int sampleSize, Bitmap.Config config, T t) {

		// 手动设置压缩选项
		mBitmapMemoryCache.setCompressOptions(sampleSize, config);

		displayBitmap(imageView, url, t);

	}

}
