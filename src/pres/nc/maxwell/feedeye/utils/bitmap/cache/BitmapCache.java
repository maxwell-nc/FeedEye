package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

public abstract class BitmapCache {

	/**
	 *  ��Ҫ��ʾ��ImageView
	 */
	protected ImageView mImageView;
	
	/**
	 * Ҫ��ʾͼƬ��URL
	 */
	protected String mURL;

	
	/**
	 * ��ʼ����Ա����������ʹ��
	 * @param imageView ��Ҫ��ʾ��ImageView
	 * @param url Ҫ��ʾͼƬ��URL
	 */
	public BitmapCache(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
	}

	/**
	 * ��Cache����ʾBitmap,���ⲿʹ��
	 */
	public abstract void displayBitmap();

	/**
	 * ��ȡ���棬�ڲ�ʵ��
	 */
	protected abstract Bitmap getCache();

	/**
	 * ���û��棬�ڲ�ʵ��
	 */
	protected abstract void setCache();
}
