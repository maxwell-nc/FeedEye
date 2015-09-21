package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import pres.nc.maxwell.feedeye.R;
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
	 * ���ش���ʱ��ʾ��ͼƬ
	 */
	protected int mErrorImageResId = R.drawable.img_load_error;
	
	/**
	 * ���ü��ش���ʱ��ʾ��ͼƬ
	 * @param errorImageResId ���ش���ʱ��ʾ��ͼƬ
	 */
	public void setErrorImageResId(int errorImageResId) {
		this.mErrorImageResId = errorImageResId;
	}

	
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
	
	/**
	 * ����һ�����ܻ���
	 */
	//protected abstract void  receiveCache();
}
