package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Bitmap���绺�棬����
 */
public class BitmapNetworkCache extends BitmapCacheDefaultImpl  {

	/**
	 * �������ñ��ػ���
	 */
	private BitmapLocalCahe mBitmapLocalCahe;

	/**
	 * �����ʵ������
	 */
	private static final BitmapNetworkCache mThis = new BitmapNetworkCache();

	
	/**
	 * ���ش����ʵ������
	 * @return �����ʵ������
	 */
	public static BitmapNetworkCache getInstance() {

		return mThis;

	}
	
	/**
	 * �������󣬲�Ҫ�����µ�ʵ������
	 */
	private BitmapNetworkCache() {}

	
	/**
	 * ����Ҫ�����Ĳ�������ʼ��BitmapLocalCahe
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);
		
		//��ȡBitmapLocalCahe
		mBitmapLocalCahe = BitmapLocalCahe.getInstance();
	}
	
	/**
	 * �������л�ȡBitmap��д�����ػ��棬�ٶ����ڴ淵��
	 * 
	 * @return ����true,����ֵ�����۳ɹ���񶼻���ʾͼƬ
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url) {
		
		setParams(imageView, url);
		
		// ����AsyncTaskִ������ͼƬ����ʾ
		new GetBitmapTask().execute();

		return true;
	}

	/**
	 * ���̻߳������ͼƬ
	 */
	class GetBitmapTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {// ���߳�
			return getCache();
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�

		};

		@Override
		protected void onPostExecute(Boolean result) {// ���߳�

			if (!result.booleanValue()) {// ��ȡʧ��
				showErrorBitmap();
			} else {// �ɹ���ȡ
				// ���ñ��ػ��������
				if(!mBitmapLocalCahe.displayBitmap(mImageView, mURL)){
					showErrorBitmap();
				}
			}

		};

	}

	/**
	 * ������������ͼƬ�����ñ��ػ���
	 * 
	 * @return �����Ƿ�ɹ�
	 */
	@Override
	public boolean getCache() {

		LogUtils.i("BitmapNetworkCache", "�������ж�ȡCache");
		
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
				connection.disconnect();// ��Ҫ���ǶϿ�
			}

		}
		return false;

	}

	/**
	 * �������÷���������
	 */
	@Override
	public <T> void setCache(T v) {
		throw new RuntimeException("Do not call this method:setCache() in "
				+ this.getClass().getName());
	}

}
