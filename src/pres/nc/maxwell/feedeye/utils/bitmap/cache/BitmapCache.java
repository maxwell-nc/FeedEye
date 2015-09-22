package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.widget.ImageView;

/**
 * Bitmap缓存管理基类
 */
public interface BitmapCache {

	/**
	 * 设置要解析的参数
	 * 
	 * @param imageView
	 *            需要显示图片的ImageView
	 * @param url
	 *            要显示图片的网址
	 */
	public abstract void setParams(ImageView imageView, String url);

	/**
	 * 显示Bitmap，给外部调用
	 * 
	 * @param imageView
	 *            需要显示图片的ImageView
	 * @param url
	 *            要显示图片的URL
	 * @return 返回是否成功
	 */
	public abstract boolean displayBitmap(ImageView imageView, String url);

	/**
	 * 获取缓存，并设置下一级缓存，内部调用
	 * 
	 * @return 返回是否成功获取
	 */
	public abstract boolean getCache();

	/**
	 * 保存缓存,由上一级缓存对象调用
	 * 
	 * @param <T>
	 *            上一级缓存类型
	 * 
	 * @param t
	 *            缓存
	 */
	public abstract <T> void setCache(T t);

}
