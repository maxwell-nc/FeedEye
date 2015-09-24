package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.os.Environment;
import android.widget.ImageView;

/**
 * Bitmap本地缓存
 */
public class BitmapLocalCahe extends BitmapCacheDefaultImpl {

	/**
	 * 用于设置内存缓存
	 */
	private BitmapMemoryCache mBitmapMemoryCache;

	/**
	 * 本地缓存的文件名
	 */
	private String mFileName;

	/**
	 * 此类的实例对象
	 */
	private static final BitmapLocalCahe mThis = new BitmapLocalCahe();

	/**
	 * 返回此类的实例对象
	 * 
	 * @return 此类的实例对象
	 */
	public static BitmapLocalCahe getInstance() {

		return mThis;

	}

	/**
	 * 单例对象，不要创建新的实例对象
	 */
	private BitmapLocalCahe() {

	}

	/**
	 * 设置要解析的参数，初始化BitmapLocalCahe
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);

		// 获取BitmapMemoryCache
		mBitmapMemoryCache = BitmapMemoryCache.getInstance();
	}

	/**
	 * 从本地中获取Bitmap，写到内存缓存，再读入显示
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url) {
		setParams(imageView, url);
		return getCache();
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

			// 设置内存缓存
			mBitmapMemoryCache.setCache(cacheFile);

			// 从内存中显示
			if (!mBitmapMemoryCache.displayBitmap(mImageView, mURL)) {

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
		File sdFile = Environment.getExternalStorageDirectory();

		// 判断SD卡容量,未实现
		// long freeSpace = sdFile.getFreeSpace();

		String savePath = sdFile.getAbsolutePath() + "/FeedEyeCache";

		// 如果文件夹不存在, 创建文件夹
		File saveDir = new File(savePath);

		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}

		// 以URL的MD5值为文件名
		mFileName = MD5Utils.getMD5String(getURL());

		File file = new File(savePath, mFileName);

		return file;
	}

}
