package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCompressUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Bitmap网络缓存
 */
public class BitmapNetworkCache extends BitmapCache {

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
	 * 初始化参数
	 * 
	 * @param imageView
	 *            需要显示的ImageView
	 * @param url
	 *            要显示图片的URL
	 */
	public BitmapNetworkCache(ImageView imageView, String url) {
		super(imageView, url);
	}

	/**
	 * 设置压缩选项，使用此方法后则默认不使用自动压缩
	 * 
	 * @param sampleSize
	 *            采样大小
	 * @param config
	 *            颜色配置
	 * @return 返回this，方便链式调用
	 */
	public BitmapNetworkCache setCompressOptions(int sampleSize,
			Bitmap.Config config) {

		// 取消自动压缩
		isAutoCompress = false;

		this.mSampleSize = sampleSize;
		this.mConfig = config;

		return this;
	}

	/**
	 * 异步从网络下载Bitmap并显示
	 */
	@Override
	public void displayBitmap() {

		LogUtils.i("BitmapNetworkCache", "从网络中读取Bitmap");

		// 开启AsyncTask执行下载图片
		new GetBitmapTask().execute();

	};

	/**
	 * 子线程获得网络图片
	 */
	class GetBitmapTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {// 子线程

			mImageView.setTag(mURL);// ImageView与URL绑定，防止重用显示错误图片

			return getCache();
		}

		@Override
		protected void onProgressUpdate(Void... values) {// 主线程

		};

		@Override
		protected void onPostExecute(Bitmap result) {// 主线程

			// 显示图片
			if (result != null) {

				String tagURL = (String) mImageView.getTag();
				if (mURL.equals(tagURL)) {// 检查是否为需要显示的ImageView
					mImageView.setImageBitmap(result);
				}

			} else {

				// 显示无法加载图片
				mImageView.setImageResource(mErrorImageResId);
			}

		};

	}

	/**
	 * 从网络中下载图片
	 */
	@Override
	protected Bitmap getCache() {

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(mURL).openConnection();

			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			connection.setRequestMethod("GET");
			connection.connect();

			if (connection.getResponseCode() == 200) {

				InputStream inputStream = new BufferedInputStream(
						connection.getInputStream());

				if (isAutoCompress) {// 自动压缩图片

					return new BitmapCompressUtils(inputStream)
							.CompressBitmapInputStream(
									mImageView.getMeasuredHeight(),
									mImageView.getMeasuredWidth());
				} else {// 手动压缩图片

					return new BitmapCompressUtils(inputStream)
							.CompressBitmapInputStream(mSampleSize, mConfig);

				}

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();// 不要忘记断开
			}

		}

		// 超时或者获取失败返回null
		return null;
	}

	/**
	 * 网络缓存不需要设置
	 */
	@Override
	protected void setCache() {
		// no implementation
	}

}
