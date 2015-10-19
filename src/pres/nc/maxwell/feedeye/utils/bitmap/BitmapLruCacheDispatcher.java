package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 分配Bitmpa的Lru内存区，单例
 */
public class BitmapLruCacheDispatcher {

	/**
	 * 单例
	 */
	private final static BitmapLruCacheDispatcher dispatcher = new BitmapLruCacheDispatcher();
	

	/**
	 * LruCache,存放Bitmap的集合
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * @return LruCache
	 */
	public LruCache<String, Bitmap> getmMemoryCache() {
		return mMemoryCache;
	}
	
	/**
	 * 禁止创建实例对象，请不要使用反射创建实例
	 */
	private BitmapLruCacheDispatcher(){
	

		long maxCacheMemory = Runtime.getRuntime().maxMemory() / 8;// 设置最大Cache占用应用总内存1/8
		mMemoryCache = new LruCache<String, Bitmap>((int) maxCacheMemory) {

			/**
			 * 计算返回每一个Bitmap的占用的内存大小
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {

				//计算图片占用空间
				int bytes = value.getRowBytes() * value.getHeight();
				LogUtils.e("BitmapLruCacheDispatcher", "图片大小" + bytes / 1024 + "KB");

				return bytes;
			}

		};

		LogUtils.e("BitmapMemoryCache", "内存缓存大小" + maxCacheMemory / 1024 + "KB");
		
		
	}
	
	/**
	 * 获得实例对象
	 * @return 单例
	 */
	public static BitmapLruCacheDispatcher getInstance(){
		return dispatcher;
	}
	
	
}
