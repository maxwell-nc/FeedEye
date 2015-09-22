package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.widget.ImageView;

/**
 * Bitmap����������
 * @param <T> ��һ����������
 */
public abstract class BitmapCache<T> {

	/**
	 * ��Ҫ��ʾ��ImageView
	 */
	protected ImageView mImageView;

	/**
	 * Ҫ��ʾͼƬ��URL
	 */
	protected String mURL;

	/**
	 * ��ȡ��Ҫ��ʾ��ImageView
	 * @return ��Ҫ��ʾ��ImageView
	 */
	public ImageView getImageView() {
		return mImageView;
	}
	
	/**
	 * ��ȡҪ��ʾͼƬ��URL
	 * @return Ҫ��ʾͼƬ��URL
	 */
	public String getURL() {
		return mURL;
	}

	/**
	 * ��ʼ����Ա����������ʹ�ã���ImageView��URL
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ��URL
	 */
	protected BitmapCache(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
		mImageView.setTag(mURL);// ImageView��URL�󶨣���ֹ������ʾ����ͼƬ
	}

	/**
	 * ��ʾBitmap�����ⲿ����
	 * 
	 * @return �����Ƿ�ɹ�
	 */
	public abstract boolean displayBitmap();

	/**
	 * ��ȡ���棬��������һ�����棬�ڲ�����
	 * 
	 * @return �����Ƿ�ɹ���ȡ
	 */
	protected abstract boolean getCache();

	/**
	 * ���滺��,����һ������������
	 * 
	 * @param t
	 *            ����
	 */
	public abstract void setCache(T t);

}
