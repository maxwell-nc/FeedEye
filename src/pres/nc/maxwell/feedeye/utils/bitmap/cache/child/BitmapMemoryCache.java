package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.File;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCompressUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * Bitmap内存缓存
 */
public class BitmapMemoryCache extends BitmapCacheDefaultImpl {

	/**
	 * ImageView的高
	 */
	private int mImageViewHeight;

	/**
	 * ImageView的宽
	 */
	private int mImageViewWidth;

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

	/**
	 * LruCache,存放Bitmap的集合
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 此类的实例对象
	 */
	private static final BitmapMemoryCache mThis = new BitmapMemoryCache();

	/**
	 * 返回此类的实例对象
	 * 
	 * @return 此类的实例对象
	 */
	public static BitmapMemoryCache getInstance() {

		return mThis;

	}

	/**
	 * 单例对象，不要创建新的实例对象
	 */
	private BitmapMemoryCache() {

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
	 * 设置要解析的参数
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);
	}

	/**
	 * 从LruCache中获取Bitmap显示
	 * 
	 * @return 是否成功获取Bitmap
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url) {

		setParams(imageView, url);
		mImageView.setTag(mURL);// ImageView与URL绑定，防止重用显示错误图片

		return getCache();
	}

	/**
	 * 获取内存缓存
	 * 
	 * @return 返回是否成功获取
	 */
	@Override
	public boolean getCache() {
		LogUtils.i("BitmapMemoryCache", "从内存中读取Cache");

		Bitmap bitmapCache = mMemoryCache.get(mURL);

		if (bitmapCache != null) {//有缓存
			String tagURL = (String) mImageView.getTag();

			// LogUtils.i("BitmapMemoryCache", "tagURL" + tagURL);
			// LogUtils.i("BitmapMemoryCache", "mURL" + mURL);

			if (mURL.equals(tagURL)) {// 检查是否为需要显示的ImageView
				mImageView.setImageBitmap(bitmapCache);
				return true;
			}else {//wrong tag
				mImageView.setTag(null);
				return false;
			}
			
		}else {//没有缓存
			
			return false;
		}
	}

	/**
	 * 设置内存缓存，由本地缓存实例对象调用
	 * 
	 * @param bitmapFile
	 *            Bitmap文件对象
	 */
	@Override
	public <T> void setCache(T bitmapFile) {

		if (!(bitmapFile instanceof File)) {
			return;
		}

		LogUtils.i("BitmapMemoryCache", "设置内存缓存");

		
		// 测量ImageView布局宽高
		mImageView.measure(0, 0);//使用onLayoutListener会出现奇怪问题
		mImageViewHeight = mImageView.getMeasuredHeight();
		mImageViewWidth = mImageView.getMeasuredWidth();

		Bitmap bitmapCache = null;

		if (isAutoCompress) {// 自动压缩图片
			bitmapCache = new BitmapCompressUtils(
					(File) bitmapFile).CompressBitmapFile(
					mImageViewHeight, mImageViewWidth);
		} else {// 手动压缩图片
			bitmapCache = new BitmapCompressUtils(
					(File) bitmapFile).CompressBitmapFile(
					mSampleSize, mConfig);
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
