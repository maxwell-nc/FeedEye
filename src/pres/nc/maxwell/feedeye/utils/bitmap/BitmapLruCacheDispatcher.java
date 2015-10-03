package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * ����Bitmpa��Lru�ڴ���������
 */
public class BitmapLruCacheDispatcher {

	/**
	 * ����
	 */
	private final static BitmapLruCacheDispatcher dispatcher = new BitmapLruCacheDispatcher();
	

	/**
	 * LruCache,���Bitmap�ļ���
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * @return LruCache
	 */
	public LruCache<String, Bitmap> getmMemoryCache() {
		return mMemoryCache;
	}
	
	/**
	 * ��ֹ����ʵ�������벻Ҫʹ�÷��䴴��ʵ��
	 */
	private BitmapLruCacheDispatcher(){
	

		long maxCacheMemory = Runtime.getRuntime().maxMemory() / 8;// �������Cacheռ��Ӧ�����ڴ�1/8
		mMemoryCache = new LruCache<String, Bitmap>((int) maxCacheMemory) {

			/**
			 * ���㷵��ÿһ��Bitmap��ռ�õ��ڴ��С
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {

				//����ͼƬռ�ÿռ�
				int bytes = value.getRowBytes() * value.getHeight();
				LogUtils.e("BitmapLruCacheDispatcher", "ͼƬ��С" + bytes / 1024 + "KB");

				return bytes;
			}

		};

		LogUtils.e("BitmapMemoryCache", "�ڴ滺���С" + maxCacheMemory / 1024 + "KB");
		
		
	}
	
	/**
	 * ���ʵ������
	 * @return ����
	 */
	public static BitmapLruCacheDispatcher getInstance(){
		return dispatcher;
	}
	
	
}
