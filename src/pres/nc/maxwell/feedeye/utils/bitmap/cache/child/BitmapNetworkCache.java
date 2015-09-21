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
 * Bitmap���绺��
 */
public class BitmapNetworkCache extends BitmapCache {

	public BitmapNetworkCache(ImageView imageView, String url) {
		super(imageView, url);
	}

	/**
	 * �첽����������Bitmap����ʾ
	 */
	@Override
	public void displayBitmap() {

		LogUtils.i("BitmapNetworkCache", "�������ж�ȡBitmap");
		
		//����AsyncTaskִ������ͼƬ
		new GetBitmapTask().execute();

	};

	/**
	 * ���̻߳������ͼƬ
	 */
	class GetBitmapTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {// ���߳�
			return getCache();
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�

		};

		@Override
		protected void onPostExecute(Bitmap result) {// ���߳�
			
			//��ʾͼƬ
			if (result != null) {
				mImageView.setImageBitmap(result);
			} else {
				LogUtils.w("BitmapNetworkCacheUtils", "Bitmap can not get!!");
			}
			
		};

	}

	
	/**
	 * ������������ͼƬ
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

				// �ֶ�ѹ��ͼƬ
				// return new BitmapCompressUtils(inputStream)
				// .CompressBitmapInputStream(16,
				// Bitmap.Config.RGB_565);

				// �Զ�ѹ��ͼƬ
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

		// ��ʱ���߻�ȡʧ�ܷ���null
		return null;
	}

	/**
	 * ���绺�治��Ҫ����
	 */
	@Override
	protected void setCache() {
		// no implementation
	}

}
