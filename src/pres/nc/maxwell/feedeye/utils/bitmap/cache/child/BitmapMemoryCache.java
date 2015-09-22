package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.File;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCompressUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * Bitmap内存缓存
 */
public class BitmapMemoryCache extends BitmapCache<File> {

	/**
	 * 是否使用自动压缩参数，若要自定参数，请调用setCompressOptions方法
	 */
	private boolean isAutoCompress = true;

	/**
	 * 采样大小
	 */
	private int mSampleSize;

	/**
	 * 颜色配置
	 */
	private Bitmap.Config mConfig;

	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 初始化参数
	 * 
	 * @param imageView
	 *            需要显示的ImageView
	 * @param url
	 *            要显示的图片URL
	 */
	public BitmapMemoryCache(ImageView imageView, String url) {
		super(imageView, url);

		long maxCacheMemory = Runtime.getRuntime().maxMemory() / 8;// 设置最大Cache占用应用总内存1/8
		mMemoryCache = new LruCache<String, Bitmap>((int) maxCacheMemory) {

			/**
			 * 计算返回每一个Bitmap的占用的内存大小
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				
				int bytes = value.getRowBytes() * value.getHeight();
				LogUtils.e("BitmapMemoryCache", "图片大小" + bytes / 1024 + "KB");
				
				return bytes;
			}

		};

		LogUtils.e("BitmapMemoryCache", "内存缓存大小" + maxCacheMemory / 1024 + "KB");

	}

	/**
	 * 从LruCache中获取Bitmap显示
	 * 
	 * @return 是否成功获取Bitmap
	 */
	@Override
	public boolean displayBitmap() {

		return getCache();
	}

	@Override
	protected boolean getCache() {
		LogUtils.i("BitmapMemoryCache", "从内存中读取Cache");

		Bitmap bitmapCache = mMemoryCache.get(mURL);

		if (bitmapCache != null) {
			String tagURL = (String) mImageView.getTag();

			if (mURL.equals(tagURL)) {// 检查是否为需要显示的ImageView
				mImageView.setImageBitmap(bitmapCache);
				return true;
			}
		}

		return false;
	}

	@Override
	public void setCache(File bitmapFile) {
		LogUtils.i("BitmapMemoryCache", "设置内存缓存");

		Bitmap bitmapCache = null;

		if (isAutoCompress) {// 自动压缩图片
			bitmapCache = new BitmapCompressUtils(bitmapFile)
					.CompressBitmapFile(mImageView.getMeasuredHeight(),
							mImageView.getMeasuredWidth());
		} else {// 手动压缩图片
			bitmapCache = new BitmapCompressUtils(bitmapFile)
					.CompressBitmapFile(mSampleSize, mConfig);
		}

		if (bitmapCache != null) {
			String tagURL = (String) mImageView.getTag();
			if (mURL.equals(tagURL)) {// 检查是否为需要显示的ImageView

				// 加入内存缓存
				mMemoryCache.put(mURL, bitmapCache);
			}
		}

	}

	/**
	 * 设置压缩选项，使用此方法后则默认不使用自动压缩
	 * 
	 * @param sampleSize
	 *            采样大小
	 * @param config
	 *            颜色配置
	 * @return 返回this，方便链式调用
	 */
	public BitmapMemoryCache setCompressOptions(int sampleSize,
			Bitmap.Config config) {

		// 取消自动压缩
		isAutoCompress = false;

		this.mSampleSize = sampleSize;
		this.mConfig = config;

		return this;
	}

}
