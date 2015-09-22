package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.widget.ImageView;

/**
 * BitmapCache的默认实现，用于快速实现BitmapCache
 */
public class BitmapCacheDefaultImpl implements BitmapCache {

	/**
	 * 需要显示的ImageView
	 */
	public ImageView mImageView;

	/**
	 * 要显示图片的URL
	 */
	public String mURL;

	/**
	 * 获取需要显示的ImageView
	 * 
	 * @return 需要显示的ImageView
	 */
	public ImageView getImageView() {
		return mImageView;
	}

	/**
	 * 获取要显示图片的URL
	 * 
	 * @return 要显示图片的URL
	 */
	public String getURL() {
		return mURL;
	}

	/**
	 * 设置要解析的参数
	 * 
	 * @param imageView
	 *            需要显示图片的ImageView
	 * @param url
	 *            要显示图片的网址
	 */
	public void setParams(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
	}

	/**
	 * 默认实现方法
	 * 
	 * @param imageView
	 *            需要显示图片的ImageView
	 * @param url
	 *            要显示图片的网址
	 * @return 返回假
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url) {
		setParams(imageView, url);
		return false;
	}

	/**
	 * 默认实现方法
	 * 
	 * @return 返回假
	 */
	@Override
	public boolean getCache() {
		return false;
	}

	/**
	 * 默认实现方法,没操作
	 * 
	 * @param t
	 */
	@Override
	public <T> void setCache(T t) {

	}

}
