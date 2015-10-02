package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.widget.ImageView;

/**
 * Bitmap本地缓存
 */
public class BitmapLocalCahe extends BitmapCacheDefaultImpl {

	/**
	 * 本地缓存的文件名
	 */
	private String mFileName;

	/**
	 * 设置要解析的参数
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);
	}

	/**
	 * 从本地中获取Bitmap，写到内存缓存，再读入显示
	 * 
	 * @param cache
	 *            接收BitmapMemoryCache对象
	 * @return 返回是否成功获取本地缓存
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url,
			BitmapCache cache) {

		setParams(imageView, url);
		return getCache();
	}

	/**
	 * 完成本地缓存获取的监听器实例对象
	 */
	private OnFinishedGetLocalCacheListener onFinishedListener;

	/**
	 * 完成本地缓存获取的监听器
	 */
	public interface OnFinishedGetLocalCacheListener {
		public void onFinishedGetLocalCache(ImageView imageView, String url,
				File cacheFile);
	}

	/**
	 * 提供外部调用的设置完成本地缓存获取的监听器方法
	 * 
	 * @param listener
	 *            监听器
	 */
	public void setOnFinishedGetLocalCacheListener(
			OnFinishedGetLocalCacheListener listener) {
		this.onFinishedListener = listener;
	}

	/**
	 * 从本地中寻找Cache，有则写到内存并显示，没则返回false
	 * 
	 * @return 返回是否成功
	 */
	@Override
	public boolean getCache() {

		LogUtils.i("BitmapLocalCahe", "从本地中读取Cache");

		File cacheFile = getCacheFile();
		if (cacheFile.exists()) {// 本地缓存存在

			// 调用外部方法来完成处理结果
			if (onFinishedListener != null) {
				onFinishedListener.onFinishedGetLocalCache(mImageView, mURL, cacheFile);
			}
			return true;

		} else {// 本地缓存不存在
			return false;
		}

	}

	/**
	 * 设置本地缓存，由网络下载后调用
	 * 
	 * @param bitmapNetworkStream
	 *            网络流
	 */
	@Override
	public <T> void setCache(T bitmapNetworkStream) {

		if (!(bitmapNetworkStream instanceof InputStream)) {
			return;
		}

		LogUtils.i("BitmapLocalCahe", "设置本地缓存");

		BufferedOutputStream bufferedOutputStream = getBufferedOutputStream();

		try {

			// 写本地缓存
			IOUtils.writeStream((InputStream) bitmapNetworkStream,
					bufferedOutputStream);

		} finally {// 关闭流

			IOUtils.closeQuietly((InputStream) bitmapNetworkStream);
			IOUtils.closeQuietly(bufferedOutputStream);

		}

	}

	/**
	 * 返回用于保存图片的输出流
	 * 
	 * @return 用于保存图片的输出流
	 */
	private BufferedOutputStream getBufferedOutputStream() {

		File file = getCacheFile();

		// 创建用于保存图片的输出流
		BufferedOutputStream bufferedOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

		return bufferedOutputStream;
	}

	/**
	 * 返回Cache保存的File对象
	 * 
	 * @return 要操作的File对象
	 */
	private File getCacheFile() {

		// 判断SD卡容量,未实现
		// File sdFile = Environment.getExternalStorageDirectory();
		// long freeSpace = sdFile.getFreeSpace();

		// 以URL的MD5值为文件名
		mFileName = MD5Utils.getMD5String(getURL());

		File file = IOUtils.getFileInSdcard("/FeedEyeCache", mFileName);

		return file;
	}

}
