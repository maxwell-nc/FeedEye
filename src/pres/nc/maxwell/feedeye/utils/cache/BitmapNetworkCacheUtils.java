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
 * Bitmap���绺��
 */
public class BitmapNetworkCacheUtils {

	private ImageView mImageView;
	
	
	/**
	 * �첽����������Bitmap
	 * 
	 * @param url
	 *            Ҫ���ص�ͼƬ��ַ
	 * @param viewHeight
	 *            ����View�ĸ߶�
	 * @param viewWidth
	 *            ����View�Ŀ��
	 * @return Bitmap����
	 */
	public void displayBitmapAsyncFromNetwork(ImageView imageView, String url) {

		this.mImageView = imageView;
		new GetBitmapTask().execute(url,
				String.valueOf(mImageView.getMeasuredHeight()),
				String.valueOf(mImageView.getMeasuredWidth()));

	};

	/**
	 * ���̻߳������ͼƬ
	 */
	class GetBitmapTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {// ���߳�

			return downloadBitmap(params[0], Integer.parseInt(params[1]),
					Integer.parseInt(params[2]));
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�

		};

		@Override
		protected void onPostExecute(Bitmap result) {// ���߳�
			if (result != null) {
				mImageView.setImageBitmap(result);
			} else {
				LogUtils.w("BitmapNetworkCacheUtils", "Bitmap can not get!!");
			}
		};

		/**
		 * ����URL����ͼƬ��ѹ������
		 * 
		 * @param url
		 *            ����ͼƬ��URL
		 * @param requiredHeight
		 *            ʵ�ʴ���View�ĸ߶�
		 * @param requiredWidth
		 *            ʵ�ʴ���View�Ŀ��
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

					// ѹ��ͼƬ
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
