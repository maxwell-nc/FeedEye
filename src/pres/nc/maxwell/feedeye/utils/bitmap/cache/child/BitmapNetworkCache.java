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

	/**
	 * �Ƿ�ʹ���Զ�ѹ����������Ҫ�Զ������������setCompressOptions����
	 */
	private boolean isAutoCompress = true;

	/**
	 * ������С
	 */
	private int mSampleSize;

	/**
	 * ��ɫ����
	 */
	private Bitmap.Config mConfig;


	/**
	 * ��ʼ������
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ��URL
	 */
	public BitmapNetworkCache(ImageView imageView, String url) {
		super(imageView, url);
	}

	/**
	 * ����ѹ��ѡ�ʹ�ô˷�������Ĭ�ϲ�ʹ���Զ�ѹ��
	 * 
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 * @return ����this��������ʽ����
	 */
	public BitmapNetworkCache setCompressOptions(int sampleSize,
			Bitmap.Config config) {

		// ȡ���Զ�ѹ��
		isAutoCompress = false;

		this.mSampleSize = sampleSize;
		this.mConfig = config;

		return this;
	}

	/**
	 * �첽����������Bitmap����ʾ
	 */
	@Override
	public void displayBitmap() {

		LogUtils.i("BitmapNetworkCache", "�������ж�ȡBitmap");

		// ����AsyncTaskִ������ͼƬ
		new GetBitmapTask().execute();

	};

	/**
	 * ���̻߳������ͼƬ
	 */
	class GetBitmapTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {// ���߳�

			mImageView.setTag(mURL);// ImageView��URL�󶨣���ֹ������ʾ����ͼƬ

			return getCache();
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�

		};

		@Override
		protected void onPostExecute(Bitmap result) {// ���߳�

			// ��ʾͼƬ
			if (result != null) {

				String tagURL = (String) mImageView.getTag();
				if (mURL.equals(tagURL)) {// ����Ƿ�Ϊ��Ҫ��ʾ��ImageView
					mImageView.setImageBitmap(result);
				}

			} else {

				// ��ʾ�޷�����ͼƬ
				mImageView.setImageResource(mErrorImageResId);
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

				if (isAutoCompress) {// �Զ�ѹ��ͼƬ

					return new BitmapCompressUtils(inputStream)
							.CompressBitmapInputStream(
									mImageView.getMeasuredHeight(),
									mImageView.getMeasuredWidth());
				} else {// �ֶ�ѹ��ͼƬ

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
				connection.disconnect();// ��Ҫ���ǶϿ�
			}

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
