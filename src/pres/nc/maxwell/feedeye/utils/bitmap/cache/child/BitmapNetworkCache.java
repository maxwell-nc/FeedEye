package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import android.os.AsyncTask;

/**
 * Bitmap���绺��
 */
public class BitmapNetworkCache extends BitmapCache<Void> {

	/**
	 * �������ñ��ػ���
	 */
	private BitmapLocalCahe mBitmapLocalCahe;

	/**
	 * ���ش���ʱ��ʾ��ͼƬ
	 */
	protected int mErrorImageResId = R.drawable.img_load_error;

	/**
	 * ���ü��ش���ʱ��ʾ��ͼƬ
	 * 
	 * @param errorImageResId
	 *            ���ش���ʱ��ʾ��ͼƬ
	 */
	public void setErrorImageResId(int errorImageResId) {
		this.mErrorImageResId = errorImageResId;
	}

	/**
	 * ��ʼ������
	 * 
	 * @param bitmapLocalCahe
	 *            �������ñ��ػ���
	 */
	public BitmapNetworkCache(BitmapLocalCahe bitmapLocalCahe) {

		super(bitmapLocalCahe.getImageView(), bitmapLocalCahe.getURL());
		this.mBitmapLocalCahe = bitmapLocalCahe;

	}

	/**
	 * ��ʾ�޷�����ͼƬ
	 */
	private void showErrorBitmap(){
		mImageView.setImageResource(mErrorImageResId);
	}	
	
	/**
	 * �������л�ȡBitmap��д�����ػ��棬�ٶ����ڴ淵��
	 * 
	 * @return ����true,����ֵ�����۳ɹ���񶼻���ʾͼƬ
	 */
	@Override
	public boolean displayBitmap() {
		
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
				if(!mBitmapLocalCahe.displayBitmap()){
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
	protected boolean getCache() {

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
	public void setCache(Void v) {
		throw new RuntimeException("Do not call this method:setCache() in "
				+ this.getClass().getName());
	}

}
