package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.widget.ImageView;

/**
 * Bitmap缓存管理基类
 * @param <T> 上一级缓存类型
 */
public abstract class BitmapCache<T> {

	/**
	 * 需要显示的ImageView
	 */
	protected ImageView mImageView;

	/**
	 * 要显示图片的URL
	 */
	protected String mURL;

	/**
	 * 获取需要显示的ImageView
	 * @return 需要显示的ImageView
	 */
	public ImageView getImageView() {
		return mImageView;
	}
	
	/**
	 * 获取要显示图片的URL
	 * @return 要显示图片的URL
	 */
	public String getURL() {
		return mURL;
	}

	/**
	 * 初始化成员变量给子类使用，绑定ImageView和URL
	 * 
	 * @param imageView
	 *            需要显示的ImageView
	 * @param url
	 *            要显示图片的URL
	 */
	protected BitmapCache(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
		mImageView.setTag(mURL);// ImageView与URL绑定，防止重用显示错误图片
	}

	/**
	 * 显示Bitmap，给外部调用
	 * 
	 * @return 返回是否成功
	 */
	public abstract boolean displayBitmap();

	/**
	 * 获取缓存，并设置下一级缓存，内部调用
	 * 
	 * @return 返回是否成功获取
	 */
	protected abstract boolean getCache();

	/**
	 * 保存缓存,由上一级缓存对象调用
	 * 
	 * @param t
	 *            缓存
	 */
	public abstract void setCache(T t);

}
