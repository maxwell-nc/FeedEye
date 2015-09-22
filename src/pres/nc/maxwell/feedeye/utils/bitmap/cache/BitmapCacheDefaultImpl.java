package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.widget.ImageView;

/**
 * BitmapCache��Ĭ��ʵ�֣����ڿ���ʵ��BitmapCache
 */
public class BitmapCacheDefaultImpl implements BitmapCache {

	/**
	 * ��Ҫ��ʾ��ImageView
	 */
	public ImageView mImageView;

	/**
	 * Ҫ��ʾͼƬ��URL
	 */
	public String mURL;

	/**
	 * ��ȡ��Ҫ��ʾ��ImageView
	 * 
	 * @return ��Ҫ��ʾ��ImageView
	 */
	public ImageView getImageView() {
		return mImageView;
	}

	/**
	 * ��ȡҪ��ʾͼƬ��URL
	 * 
	 * @return Ҫ��ʾͼƬ��URL
	 */
	public String getURL() {
		return mURL;
	}

	/**
	 * ����Ҫ�����Ĳ���
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾͼƬ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ����ַ
	 */
	public void setParams(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
	}

	/**
	 * Ĭ��ʵ�ַ���
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾͼƬ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ����ַ
	 * @return ���ؼ�
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url) {
		setParams(imageView, url);
		return false;
	}

	/**
	 * Ĭ��ʵ�ַ���
	 * 
	 * @return ���ؼ�
	 */
	@Override
	public boolean getCache() {
		return false;
	}

	/**
	 * Ĭ��ʵ�ַ���,û����
	 * 
	 * @param t
	 */
	@Override
	public <T> void setCache(T t) {

	}

}
