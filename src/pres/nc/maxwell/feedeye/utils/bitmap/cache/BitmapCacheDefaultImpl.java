package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import pres.nc.maxwell.feedeye.R;
import android.widget.ImageView;

/**
 * BitmapCache��Ĭ��ʵ�֣����ڿ���ʵ��BitmapCache
 */
public class BitmapCacheDefaultImpl implements BitmapCache {

	/**
	 * ���ش���ʱ��ʾ��ͼƬ
	 */
	private int mErrorImageResId = R.drawable.img_load_error;

	/**
	 * ���ü��ش���ʱ��ʾ��ͼƬ
	 * 
	 * @param errorImageResId
	 *            ���ش���ʱ��ʾ��ͼƬ
	 */
	public void setErrorImageResId(int errorImageResId) {
		this.mErrorImageResId = errorImageResId;
	}

	/**
	 * ��ʾ�޷�����ͼƬ
	 */
	public void showErrorBitmap() {
		mImageView.setImageResource(mErrorImageResId);
	}

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
	 * ����Ҫ�����Ĳ���,Ĭ��ʵ�ַ���
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾͼƬ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ����ַ
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
	}

	/**
	 * ��ʾBitmap�����ⲿ����,Ĭ��ʵ�ַ���
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾͼƬ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ����ַ
	 * @param cache
	 *            ��Ҫ���ϼ�����
	 * @return ���ؼ�
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url,
			BitmapCache cache) {
		setParams(imageView, url);
		return false;
	}

	/**
	 * ��ȡ���棬��������һ�����棬�ڲ�����,Ĭ��ʵ�ַ���
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
