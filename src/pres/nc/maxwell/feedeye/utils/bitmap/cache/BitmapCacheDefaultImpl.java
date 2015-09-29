package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import pres.nc.maxwell.feedeye.R;
import android.widget.ImageView;

/**
 * BitmapCache的默认实现，用于快速实现BitmapCache
 */
public class BitmapCacheDefaultImpl implements BitmapCache {

	/**
	 * 加载错误时显示的图片
	 */
	private int mErrorImageResId = R.drawable.img_load_error;

	/**
	 * 设置加载错误时显示的图片
	 * 
	 * @param errorImageResId
	 *            加载错误时显示的图片
	 */
	public void setErrorImageResId(int errorImageResId) {
		this.mErrorImageResId = errorImageResId;
	}

	/**
	 * 显示无法加载图片
	 */
	public void showErrorBitmap() {
		mImageView.setImageResource(mErrorImageResId);
	}

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
	 * 设置要解析的参数,默认实现方法
	 * 
	 * @param imageView
	 *            需要显示图片的ImageView
	 * @param url
	 *            要显示图片的网址
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
	}

	/**
	 * 显示Bitmap，给外部调用,默认实现方法
	 * 
	 * @param imageView
	 *            需要显示图片的ImageView
	 * @param url
	 *            要显示图片的网址
	 * @param cache
	 *            需要的上级缓存
	 * @return 返回假
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url,
			BitmapCache cache) {
		setParams(imageView, url);
		return false;
	}

	/**
	 * 获取缓存，并设置下一级缓存，内部调用,默认实现方法
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
