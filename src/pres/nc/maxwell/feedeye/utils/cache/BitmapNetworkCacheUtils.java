package pres.nc.maxwell.feedeye.utils.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pres.nc.maxwell.feedeye.utils.LogUtils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Bitmap网络缓存
 */
public class BitmapNetworkCacheUtils {

	private ImageView mImageView;
	
	
	/**
	 * 异步从网络下载Bitmap
	 * 
	 * @param url
	 *            要下载的图片网址
	 * @param viewHeight
	 *            传入View的高度
	 * @param viewWidth
	 *            传入View的宽度
	 * @return Bitmap对象
	 */
	public void displayBitmapAsyncFromNetwork(ImageView imageView, String url) {

		this.mImageView = imageView;
		new GetBitmapTask().execute(url,
				String.valueOf(mImageView.getMeasuredHeight()),
				String.valueOf(mImageView.getMeasuredWidth()));

	};

	/**
	 * 子线程获得网络图片
	 */
	class GetBitmapTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {// 子线程

			return downloadBitmap(params[0], Integer.parseInt(params[1]),
					Integer.parseInt(params[2]));
		}

		@Override
		protected void onProgressUpdate(Void... values) {// 主线程

		};

		@Override
		protected void onPostExecute(Bitmap result) {// 主线程
			if (result != null) {
				mImageView.setImageBitmap(result);
			} else {
				LogUtils.w("BitmapNetworkCacheUtils", "Bitmap can not get!!");
			}
		};

		/**
		 * 根据URL下载图片并压缩返回
		 * 
		 * @param url
		 *            下载图片的URL
		 * @param requiredHeight
		 *            实际传入View的高度
		 * @param requiredWidth
		 *            实际传入View的宽度
		 * @return
		 */
		private Bitmap downloadBitmap(String url, int requiredHeight,
				int requiredWidth) {
			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) new URL(url).openConnection();

				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);

				connection.setRequestMethod("GET");
				connection.connect();

				if (connection.getResponseCode() == 200) {

					// LogUtils.w("BitmapNetworkCacheUtils",
					// "ResponseCode 200");

					InputStream inputStream = connection.getInputStream();

					// LogUtils.w("BitmapNetworkCacheUtils",
					// inputStream == null ? "inputStream is null"
					// : "inputStream is not null");

					return new BitmapCompressUtils(inputStream)
							.CompressBitmapInputStream(16,
									Bitmap.Config.RGB_565);

					// 压缩图片
					// return new BitmapCompressUtils(inputStream)
					// .CompressBitmapInputStream(requiredHeight,
					// requiredWidth);

				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
			}

			return null;

		}
	}

}
