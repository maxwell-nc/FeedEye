package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

public abstract class BitmapCache {

	/**
	 *  需要显示的ImageView
	 */
	protected ImageView mImageView;
	
	/**
	 * 要显示图片的URL
	 */
	protected String mURL;

	
	/**
	 * 初始化成员变量给子类使用
	 * @param imageView 需要显示的ImageView
	 * @param url 要显示图片的URL
	 */
	public BitmapCache(ImageView imageView, String url) {
		this.mImageView = imageView;
		this.mURL = url;
	}

	/**
	 * 从Cache中显示Bitmap,给外部使用
	 */
	public abstract void displayBitmap();

	/**
	 * 获取缓存，内部实现
	 */
	protected abstract Bitmap getCache();

	/**
	 * 设置缓存，内部实现
	 */
	protected abstract void setCache();
}
