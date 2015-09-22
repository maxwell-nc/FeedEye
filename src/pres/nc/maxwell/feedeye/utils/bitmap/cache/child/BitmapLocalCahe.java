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
import android.os.Environment;

/**
 * Bitmap本地缓存
 */
public class BitmapLocalCahe extends BitmapCache<InputStream> {

	/**
	 * 用于设置内存缓存
	 */
	private BitmapMemoryCache mBitmapMemoryCache;

	/**
	 * 本地缓存的文件名
	 */
	private String mFileName;

	/**
	 * 初始化参数
	 * 
	 * @param bitmapMemoryCache
	 *            用于设置内存缓存
	 */
	public BitmapLocalCahe(BitmapMemoryCache bitmapMemoryCache) {

		super(bitmapMemoryCache.getImageView(), bitmapMemoryCache.getURL());
		this.mBitmapMemoryCache = bitmapMemoryCache;

	}

	/**
	 * 从本地中获取Bitmap，写到内存缓存，再读入显示
	 */
	@Override
	public boolean displayBitmap() {

		return getCache();
	}

	/**
	 * 从本地中寻找Cache，有则写到内存并显示，没则返回false
	 * 
	 * @return 返回是否成功
	 */
	@Override
	protected boolean getCache() {

		LogUtils.i("BitmapLocalCahe", "从本地中读取Cache");

		File cacheFile = getCacheFile();
		if (cacheFile.exists()) {// 本地缓存存在

			//设置内存缓存
			mBitmapMemoryCache.setCache(cacheFile);
			
			//从内存中显示
			if (!mBitmapMemoryCache.displayBitmap()) {
				
				return false;
				
			} else {
				
				return true;
				
			}

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
	public void setCache(InputStream bitmapNetworkStream) {

		LogUtils.i("BitmapLocalCahe", "设置本地缓存");

		BufferedOutputStream bufferedOutputStream = getBufferedOutputStream();

		try {

			// 写本地缓存
			IOUtils.writeStream(bitmapNetworkStream, bufferedOutputStream);

		} finally {// 关闭流

			IOUtils.closeQuietly(bitmapNetworkStream);
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
		File sdFile = Environment.getExternalStorageDirectory();

		// 判断SD卡容量,未实现
		// long freeSpace = sdFile.getFreeSpace();

		String savePath = sdFile.getAbsolutePath() + "/FeedEyeCache";

		// 如果文件夹不存在, 创建文件夹
		File saveDir = new File(savePath);

		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}

		LogUtils.w("BitmapLocalCahe", savePath);

		// 以URL的MD5值为文件名
		mFileName = MD5Utils.getMD5String(getURL());

		File file = new File(savePath, mFileName);

		return file;
	}

}
