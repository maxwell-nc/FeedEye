package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapLocalCahe;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapMemoryCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapNetworkCache;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Bitmap�������湤����
 */
public class BitmapCacheUtils {

	/**
	 * �ڴ滺�����
	 */
	private BitmapMemoryCache mBitmapMemoryCache;

	/**
	 * ���ػ������
	 */
	private BitmapLocalCahe mBitmapLocalCahe;

	/**
	 * ���绺�����
	 */
	private BitmapNetworkCache mBitmapNetworkCache;

	/**
	 * �Ƿ������绺��
	 */
	private boolean mIsEnableNetworkCache =  true;

	
	/**
	 * ��ʼ���������浥������Ĭ��ʹ�����绺��
	 */
	public BitmapCacheUtils() {
		this(true);
	}
	
	/**
	 * ��ʼ���������浥������
	 * @param isEnableNetworkCache �Ƿ�ʹ�����绺��
	 */
	public BitmapCacheUtils(boolean isEnableNetworkCache) {

		this.mIsEnableNetworkCache = isEnableNetworkCache;

		// �ڴ滺�����
		mBitmapMemoryCache = BitmapMemoryCache.getInstance();
		// ���ػ������
		mBitmapLocalCahe = BitmapLocalCahe.getInstance();

		if (mIsEnableNetworkCache) {
			// ���绺�����
			mBitmapNetworkCache = BitmapNetworkCache.getInstance();
		}else{
			LogUtils.i("BitmapCacheUtils", "��ʹ�����绺��");
		}

	}

	/**
	 * �ӻ����л�ȡBitmap����ʾ��ImageView��
	 * 
	 * @param imageView
	 *            Ҫ��ʾ��ImageView����
	 * @param url
	 *            Ҫ��ʾ��ͼƬURL
	 */
	public void displayBitmap(ImageView imageView, String url) {

		if (imageView == null) {
			return;
		}

		// 1.��ȡ�ڴ滺��
		if (!mBitmapMemoryCache.displayBitmap(imageView, url)) {// �ڴ���û�л���

			LogUtils.i("BitmapCacheUtils", "�ڴ���û�л���");

			// 2.��ȡ���ػ���
			if (!mBitmapLocalCahe.displayBitmap(imageView, url)) {// ������û�л���

				LogUtils.i("BitmapCacheUtils", "������û�л���");

				if (mIsEnableNetworkCache) {
					// 3.��ȡ���绺��
					mBitmapNetworkCache.displayBitmap(imageView, url);// ���棬�����޷���ȡ����ʾ����ͼƬ
				}else {//��ʹ�����绺���ұ��ػ��治����
					mBitmapLocalCahe.showErrorBitmap();
				}
			}

		}

	}

	/**
	 * �ӻ����л�ȡBitmap����ʾ��ImageView��,���Զ����ѹ��ѡ��
	 * 
	 * @param imageView
	 *            Ҫ��ʾ��ImageView����
	 * @param url
	 *            Ҫ��ʾ��ͼƬURL
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 */
	public void displayBitmap(ImageView imageView, String url, int sampleSize,
			Bitmap.Config config) {

		// �ֶ�����ѹ��ѡ��
		mBitmapMemoryCache.setCompressOptions(sampleSize, config);

		displayBitmap(imageView, url);

	}

	/**
	 * �ӻ����л�ȡBitmap����ʾ��ImageView��,��������ͼƬ
	 * 
	 * @param imageView
	 *            Ҫ��ʾ��ImageView����
	 * @param url
	 *            Ҫ��ʾ��ͼƬURL
	 * @param t
	 *            ����ʱ��ʾ��ͼƬ
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
	 * �ӻ����л�ȡBitmap����ʾ��ImageView��,���Զ����ѹ��ѡ��������Զ���ļ�����ͼƬ
	 * 
	 * @param imageView
	 *            Ҫ��ʾ��ImageView����
	 * @param url
	 *            Ҫ��ʾ��ͼƬURL
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 * @param t
	 *            ����ʱ��ʾ��ͼƬ
	 */
	public <T> void displayBitmap(ImageView imageView, String url,
			int sampleSize, Bitmap.Config config, T t) {

		// �ֶ�����ѹ��ѡ��
		mBitmapMemoryCache.setCompressOptions(sampleSize, config);

		displayBitmap(imageView, url, t);

	}

}
