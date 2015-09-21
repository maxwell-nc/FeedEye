package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import pres.nc.maxwell.feedeye.R;
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
	 * 加载错误时显示的图片
	 */
	protected int mErrorImageResId = R.drawable.img_load_error;
	
	/**
	 * 设置加载错误时显示的图片
	 * @param errorImageResId 加载错误时显示的图片
	 */
	public void setErrorImageResId(int errorImageResId) {
		this.mErrorImageResId = errorImageResId;
	}

	
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
	
	/**
	 * 从上一级接受缓存
	 */
	//protected abstract void  receiveCache();
}
