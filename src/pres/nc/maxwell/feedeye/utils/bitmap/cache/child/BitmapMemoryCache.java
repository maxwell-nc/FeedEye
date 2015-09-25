package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.File;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCompressUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * Bitmap�ڴ滺��
 */
public class BitmapMemoryCache extends BitmapCacheDefaultImpl {

	/**
	 * ImageView�ĸ�
	 */
	private int mImageViewHeight;

	/**
	 * ImageView�Ŀ�
	 */
	private int mImageViewWidth;

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

	/**
	 * LruCache,���Bitmap�ļ���
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * �����ʵ������
	 */
	private static final BitmapMemoryCache mThis = new BitmapMemoryCache();

	/**
	 * ���ش����ʵ������
	 * 
	 * @return �����ʵ������
	 */
	public static BitmapMemoryCache getInstance() {

		return mThis;

	}

	/**
	 * �������󣬲�Ҫ�����µ�ʵ������
	 */
	private BitmapMemoryCache() {

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
	 * ����Ҫ�����Ĳ���
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);
	}

	/**
	 * ��LruCache�л�ȡBitmap��ʾ
	 * 
	 * @return �Ƿ�ɹ���ȡBitmap
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url) {

		setParams(imageView, url);
		mImageView.setTag(mURL);// ImageView��URL�󶨣���ֹ������ʾ����ͼƬ

		return getCache();
	}

	/**
	 * ��ȡ�ڴ滺��
	 * 
	 * @return �����Ƿ�ɹ���ȡ
	 */
	@Override
	public boolean getCache() {
		LogUtils.i("BitmapMemoryCache", "���ڴ��ж�ȡCache");

		Bitmap bitmapCache = mMemoryCache.get(mURL);

		if (bitmapCache != null) {//�л���
			String tagURL = (String) mImageView.getTag();

			// LogUtils.i("BitmapMemoryCache", "tagURL" + tagURL);
			// LogUtils.i("BitmapMemoryCache", "mURL" + mURL);

			if (mURL.equals(tagURL)) {// ����Ƿ�Ϊ��Ҫ��ʾ��ImageView
				mImageView.setImageBitmap(bitmapCache);
				return true;
			}else {//wrong tag
				mImageView.setTag(null);
				return false;
			}
			
		}else {//û�л���
			
			return false;
		}
	}

	/**
	 * �����ڴ滺�棬�ɱ��ػ���ʵ���������
	 * 
	 * @param bitmapFile
	 *            Bitmap�ļ�����
	 */
	@Override
	public <T> void setCache(T bitmapFile) {

		if (!(bitmapFile instanceof File)) {
			return;
		}

		LogUtils.i("BitmapMemoryCache", "�����ڴ滺��");

		
		// ����ImageView���ֿ��
		mImageView.measure(0, 0);//ʹ��onLayoutListener������������
		mImageViewHeight = mImageView.getMeasuredHeight();
		mImageViewWidth = mImageView.getMeasuredWidth();

		Bitmap bitmapCache = null;

		if (isAutoCompress) {// �Զ�ѹ��ͼƬ
			bitmapCache = new BitmapCompressUtils(
					(File) bitmapFile).CompressBitmapFile(
					mImageViewHeight, mImageViewWidth);
		} else {// �ֶ�ѹ��ͼƬ
			bitmapCache = new BitmapCompressUtils(
					(File) bitmapFile).CompressBitmapFile(
					mSampleSize, mConfig);
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
