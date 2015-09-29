package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Bitmap网络缓存
 */
public class BitmapNetworkCache extends BitmapCacheDefaultImpl {

	/**
	 * 用于设置本地缓存
	 */
	private BitmapLocalCahe mBitmapLocalCahe;

	/**
	 * 设置要解析的参数
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);
	}

	/**
	 * 从网络中获取Bitmap，写到本地缓存，再读入内存返回
	 * 
	 * @param cache
	 *            接收BitmapLocalCahe对象
	 * @return 返回true,无用值，无论成功与否都会显示图片
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url,
			BitmapCache cahe) {

		// 获取BitmapLocalCahe
		mBitmapLocalCahe = (BitmapLocalCahe) cahe;

		setParams(imageView, url);

		// 开启AsyncTask执行下载图片并显示
		new GetBitmapTask().execute();

		return true;
	}

	/**
	 * 完成网络缓存获取的监听器实例对象
	 */
	private OnFinishedGetNetworkCacheListener onFinishedListener;

	/**
	 * 完成网络缓存获取的监听器
	 */
	public interface OnFinishedGetNetworkCacheListener {
		public void onFinishedGetNetworkCache(ImageView imageView, String url,
				boolean result);
	}

	/**
	 * 提供外部调用的设置完成网络缓存获取的监听器方法
	 * @param listener 监听器
	 */
	public void setOnFinishedGetNetworkCache(
			OnFinishedGetNetworkCacheListener listener) {
		this.onFinishedListener = listener;
	}

	/**
	 * 子线程获得网络图片
	 */
	class GetBitmapTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {// 子线程
			return getCache();
		}

		@Override
		protected void onProgressUpdate(Void... values) {// 主线程

		};

		@Override
		protected void onPostExecute(Boolean result) {// 主线程

			//调用外部方法来完成处理结果
			if (onFinishedListener != null) {
				onFinishedListener.onFinishedGetNetworkCache(mImageView, mURL,
						result);
			}

		};

	}

	/**
	 * 从网络中下载图片，设置本地缓存
	 * 
	 * @return 返回是否成功
	 */
	@Override
	public boolean getCache() {

		LogUtils.i("BitmapNetworkCache", "从网络中读取Cache");

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(mURL).openConnection();

			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			connection.setRequestMethod("GET");
			connection.connect();

			if (connection.getResponseCode() == 200) {

				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						connection.getInputStream());

				mBitmapLocalCahe.setCache(bufferedInputStream);

				return true;
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

		return false;

	}

	/**
	 * 不能设置服务器缓存
	 */
	@Override
	public <T> void setCache(T v) {
		throw new RuntimeException("Do not call this method:setCache() in "
				+ this.getClass().getName());
	}

}
