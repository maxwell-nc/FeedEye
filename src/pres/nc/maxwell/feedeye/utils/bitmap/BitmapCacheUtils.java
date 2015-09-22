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

	public BitmapCacheUtils() {
		mBitmapMemoryCache = BitmapMemoryCache.getInstance();
		mBitmapLocalCahe = BitmapLocalCahe.getInstance();
		mBitmapNetworkCache = BitmapNetworkCache.getInstance();
	}

	public void displayBitmap(ImageView imageView, String url) {

	
		if (!mBitmapMemoryCache.displayBitmap(imageView, url)) {// �ڴ���û�л���

			LogUtils.i("BitmapCacheUtils", "�ڴ���û�л���");

			if (!mBitmapLocalCahe.displayBitmap(imageView, url)) {// ������û�л���

				LogUtils.i("BitmapCacheUtils", "������û�л���");

				mBitmapNetworkCache.displayBitmap(imageView, url);// ���棬�����޷���ȡ����ʾ����ͼƬ

			}

		}

	}

	public void displayBitmap(ImageView imageView, String url, int sampleSize,
			Bitmap.Config config) {

		// �ֶ�����ѹ��ѡ��
		mBitmapMemoryCache.setCompressOptions(sampleSize, config);

		displayBitmap(imageView, url);

	}

	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			Bitmap bitmap) {
		
		imageView.setImageBitmap(bitmap);
		displayBitmap(imageView, url);

	}

	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			Drawable drawable) {

		imageView.setImageDrawable(drawable);
		displayBitmap(imageView, url);

	}

	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			int resId) {

		imageView.setImageResource(resId);
		displayBitmap(imageView, url);

	}
}
