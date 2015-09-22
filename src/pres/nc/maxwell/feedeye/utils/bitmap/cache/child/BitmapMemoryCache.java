package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.File;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCompressUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * Bitmap�ڴ滺��
 */
public class BitmapMemoryCache extends BitmapCache<File> {

	/**
	 * �Ƿ�ʹ���Զ�ѹ����������Ҫ�Զ������������setCompressOptions����
	 */
	private boolean isAutoCompress = true;

	/**
	 * ������С
	 */
	private int mSampleSize;

	/**
	 * ��ɫ����
	 */
	private Bitmap.Config mConfig;

	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * ��ʼ������
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾ��ImageView
	 * @param url
	 *            Ҫ��ʾ��ͼƬURL
	 */
	public BitmapMemoryCache(ImageView imageView, String url) {
		super(imageView, url);

		long maxCacheMemory = Runtime.getRuntime().maxMemory() / 8;// �������Cacheռ��Ӧ�����ڴ�1/8
		mMemoryCache = new LruCache<String, Bitmap>((int) maxCacheMemory) {

			/**
			 * ���㷵��ÿһ��Bitmap��ռ�õ��ڴ��С
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				
				int bytes = value.getRowBytes() * value.getHeight();
				LogUtils.e("BitmapMemoryCache", "ͼƬ��С" + bytes / 1024 + "KB");
				
				return bytes;
			}

		};

		LogUtils.e("BitmapMemoryCache", "�ڴ滺���С" + maxCacheMemory / 1024 + "KB");

	}

	/**
	 * ��LruCache�л�ȡBitmap��ʾ
	 * 
	 * @return �Ƿ�ɹ���ȡBitmap
	 */
	@Override
	public boolean displayBitmap() {

		return getCache();
	}

	@Override
	protected boolean getCache() {
		LogUtils.i("BitmapMemoryCache", "���ڴ��ж�ȡCache");

		Bitmap bitmapCache = mMemoryCache.get(mURL);

		if (bitmapCache != null) {
			String tagURL = (String) mImageView.getTag();

			if (mURL.equals(tagURL)) {// ����Ƿ�Ϊ��Ҫ��ʾ��ImageView
				mImageView.setImageBitmap(bitmapCache);
				return true;
			}
		}

		return false;
	}

	@Override
	public void setCache(File bitmapFile) {
		LogUtils.i("BitmapMemoryCache", "�����ڴ滺��");

		Bitmap bitmapCache = null;

		if (isAutoCompress) {// �Զ�ѹ��ͼƬ
			bitmapCache = new BitmapCompressUtils(bitmapFile)
					.CompressBitmapFile(mImageView.getMeasuredHeight(),
							mImageView.getMeasuredWidth());
		} else {// �ֶ�ѹ��ͼƬ
			bitmapCache = new BitmapCompressUtils(bitmapFile)
					.CompressBitmapFile(mSampleSize, mConfig);
		}

		if (bitmapCache != null) {
			String tagURL = (String) mImageView.getTag();
			if (mURL.equals(tagURL)) {// ����Ƿ�Ϊ��Ҫ��ʾ��ImageView

				// �����ڴ滺��
				mMemoryCache.put(mURL, bitmapCache);
			}
		}

	}

	/**
	 * ����ѹ��ѡ�ʹ�ô˷�������Ĭ�ϲ�ʹ���Զ�ѹ��
	 * 
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 * @return ����this��������ʽ����
	 */
	public BitmapMemoryCache setCompressOptions(int sampleSize,
			Bitmap.Config config) {

		// ȡ���Զ�ѹ��
		isAutoCompress = false;

		this.mSampleSize = sampleSize;
		this.mConfig = config;

		return this;
	}

}
