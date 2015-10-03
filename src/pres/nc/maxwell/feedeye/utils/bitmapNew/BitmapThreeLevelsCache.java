package pres.nc.maxwell.feedeye.utils.bitmapNew;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCompressUtils;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Bitmap的三级缓存控制，包含控制网络、本地和内存缓存
 */
public class BitmapThreeLevelsCache {

	/**
	 * 需要显示的ImageView
	 */
	public ImageView mImageView;

	/**
	 * 要显示图片的URL
	 */
	public String mURL;

	/**
	 * 加载失败时加载的图片
	 */
	public Bitmap mErrBitmap;

	/**
	 * 是否使用自动压缩参数，若要自定参数，请调用setCompressOptions方法
	 */
	private boolean isAutoCompress = true;

	/**
	 * 采样大小
	 */
	private int mSampleSize;

	/**
	 * 颜色配置
	 */
	private Bitmap.Config mConfig;
	

	/**
	 * 是否开启网络缓存
	 */
	private boolean mIsEnableNetworkCache = true;

	/**
	 * 每次使用请创建新的对象
	 * 
	 * @param imageView
	 *            需要显示的ImageView
	 * @param url
	 *            要显示图片的URL
	 */
	public BitmapThreeLevelsCache(ImageView imageView, String url,
			Bitmap errBitmap,boolean isEnableNetworkCache) {
		this.mImageView = imageView;
		this.mURL = url;
		this.mErrBitmap = errBitmap;
		this.mIsEnableNetworkCache = isEnableNetworkCache;
		mImageView.setTag(mURL);
	}

	public void displayBitmap() {

		if (isLocalFile()) {// 本地图片

			LogUtils.i("BitmapThreeLevelsCache", "L1:本地图片，直接存取内存缓存");
			if (!getMemoryCache()) {// 1.获取内存缓存

				File file = new File(mURL);
				setMemoryCache(file);// 写到内存缓存中
				getMemoryCache();// 重新从内存缓存中读取

			}

		} else {// 网络图片

			LogUtils.i("BitmapThreeLevelsCache", "L1:获取内存缓存");
			if (!getMemoryCache()) {// 1.获取内存缓存

				LogUtils.i("BitmapThreeLevelsCache", "L2:获取本地缓存");
				if (!getLocalCache()) {// 2.获取本地缓存

					if (mIsEnableNetworkCache) {//开启网络缓存
						
						LogUtils.i("BitmapThreeLevelsCache", "L3:获取网路缓存");
						getNetworkCache();// 3.获取网络缓存
						
					}else {//不使用网络缓存
						
						LogUtils.i("BitmapThreeLevelsCache", "L2:不使用网络缓存");
						showErrorBitmap();
						
					}

				}

			}
		}

	}

	/**
	 * 判断是否为本地文件
	 * 
	 * @return 若是本地文件返回真，否则返回假
	 */
	private boolean isLocalFile() {

		if (mURL.startsWith("/")) {// 如： /sdcard/xxx/xxx.jpg
			return true;
		}

		return false;
	}

	private void setMemoryCache(File bitmapFile) {

		// 解析File对象
		Bitmap bitmapCache = decodeFile(bitmapFile);

		if (bitmapCache != null) {
			// 加入内存缓存
			BitmapLruCacheDispatcher.getInstance().getmMemoryCache()
					.put(mURL, bitmapCache);
		}

	}

	private void setLocalCache(InputStream bitmapNetworkStream) {
		BufferedOutputStream bufferedOutputStream = getBufferedOutputStream();

		try {

			// 写本地缓存
			IOUtils.writeStream(bitmapNetworkStream, bufferedOutputStream);

		} finally {// 关闭流

			IOUtils.closeQuietly(bitmapNetworkStream);
			IOUtils.closeQuietly(bufferedOutputStream);

		}
	}

	@SuppressWarnings("unused")
	private void setNetworkCache() {

		throw new RuntimeException(
				"Do not call this method:setNetworkCache() in "
						+ this.getClass().getName());

	}

	private boolean getMemoryCache() {

		Bitmap bitmapCache = BitmapLruCacheDispatcher.getInstance()
				.getmMemoryCache().get(mURL);

		if (bitmapCache != null) {// 有缓存
			String tagURL = (String) mImageView.getTag();

			// LogUtils.i("BitmapThreeLevelsCache", "tagURL" + tagURL);
			// LogUtils.i("BitmapThreeLevelsCache", "mURL" + mURL);

			if (mURL.equals(tagURL)) {// 检查是否为需要显示的ImageView
				mImageView.setImageBitmap(bitmapCache);
				return true;
			} else {// wrong tag
					// 不用处理
				return false;
			}

		} else {// 没有缓存

			return false;
		}

	}

	private boolean getLocalCache() {

		File cacheFile = getCacheFile();

		if (cacheFile.exists()) {// 本地缓存存在

			// 设置内存缓存
			setMemoryCache(cacheFile);

			// 重新读取缓存
			getMemoryCache();

			return true;

		} else {

			return false;

		}

	}

	private void getNetworkCache() {
		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {// 子线程
				// 设置本地缓存
				setLocalCache(inputStream);
				LogUtils.i("BitmapThreeLevelsCache", mURL);

			}

			@Override
			public void onSuccess() {// 主线程
				// 重新读取缓存
				getLocalCache();
			}

			@Override
			public void onFailure() {// 主线程

				// TODO：获取favicon失败
				String tagURL = (String) mImageView.getTag();
				if (mURL.equals(tagURL)) {// 检查是否为需要显示的ImageView

					showErrorBitmap();

				} else {// wrong tag

				}

			}

		});

		httpUtils.Connect(mURL, 10000, 10000);

	}

	/**
	 * 本地：返回用于保存图片的输出流
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
	 * 本地：返回Cache保存的File对象
	 * 
	 * @return 要操作的File对象
	 */
	private File getCacheFile() {

		// 判断SD卡容量,未实现
		// File sdFile = Environment.getExternalStorageDirectory();
		// long freeSpace = sdFile.getFreeSpace();

		// 以URL的MD5值为文件名
		String fileName = MD5Utils.getMD5String(mURL);

		File file = IOUtils.getFileInSdcard("/FeedEyeCache", fileName);

		return file;
	}

	/**
	 * 压缩：设置压缩选项，使用此方法后则默认不使用自动压缩
	 * 
	 * @param sampleSize
	 *            采样大小
	 * @param config
	 *            颜色配置
	 * @return 返回this，方便链式调用
	 */
	public BitmapThreeLevelsCache setCompressOptions(int sampleSize,
			Bitmap.Config config) {

		// 取消自动压缩
		isAutoCompress = false;

		this.mSampleSize = sampleSize;
		this.mConfig = config;

		return this;
	}

	/**
	 * 压缩：解析file对象并压缩Bitmap
	 * 
	 * @param bitmapFile
	 *            图片文件对象
	 * @return 压缩解析后的Bitmap
	 */
	private Bitmap decodeFile(File bitmapFile) {
		Bitmap bitmapCache = null;

		// 测量ImageView布局宽高
		mImageView.measure(0, 0);// 使用onLayoutListener会出现奇怪问题
		int viewHeight = mImageView.getMeasuredHeight();
		int viewWidth = mImageView.getMeasuredWidth();

		if (isAutoCompress) {// 自动压缩图片
			bitmapCache = new BitmapCompressUtils((File) bitmapFile)
					.CompressBitmapFile(viewHeight, viewWidth);
		} else {// 手动压缩图片
			bitmapCache = new BitmapCompressUtils((File) bitmapFile)
					.CompressBitmapFile(mSampleSize, mConfig);
		}

		return bitmapCache;
	}

	/**
	 * 失败：显示加载失败的的图片
	 */
	private void showErrorBitmap() {
		// 失败后设置内存缓存为加载失败的图片，防止多次访问网络
		if (mErrBitmap != null) {

			mImageView.setImageBitmap(mErrBitmap);
			// 加入内存缓存
			BitmapLruCacheDispatcher.getInstance()
					.getmMemoryCache().put(mURL, mErrBitmap);
		} else {
			LogUtils.i("BitmapThreeLevelsCache",
					"mErrBitmap is null");
		}
	}

}
