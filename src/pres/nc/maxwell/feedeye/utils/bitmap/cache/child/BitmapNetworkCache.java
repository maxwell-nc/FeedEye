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

	public BitmapNetworkCache(ImageView imageView, String url) {
		super(imageView, url);
	}

	/**
	 * 异步从网络下载Bitmap并显示
	 */
	@Override
	public void displayBitmap() {

		LogUtils.i("BitmapNetworkCache", "从网络中读取Bitmap");
		
		//开启AsyncTask执行下载图片
		new GetBitmapTask().execute();

	};

	/**
	 * 子线程获得网络图片
	 */
	class GetBitmapTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {// 子线程
			return getCache();
		}

		@Override
		protected void onProgressUpdate(Void... values) {// 主线程

		};

		@Override
		protected void onPostExecute(Bitmap result) {// 主线程
			
			//显示图片
			if (result != null) {
				mImageView.setImageBitmap(result);
			} else {
				LogUtils.w("BitmapNetworkCacheUtils", "Bitmap can not get!!");
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

				// 手动压缩图片
				// return new BitmapCompressUtils(inputStream)
				// .CompressBitmapInputStream(16,
				// Bitmap.Config.RGB_565);

				// 自动压缩图片
				return new BitmapCompressUtils(inputStream)
						.CompressBitmapInputStream(
								mImageView.getMeasuredHeight(),
								mImageView.getMeasuredWidth());

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
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
