package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedInputStream;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Bitmap���绺��
 */
public class BitmapNetworkCache extends BitmapCacheDefaultImpl {

	/**
	 * ��ǰ���绺�����
	 */
	private BitmapNetworkCache mThisBitmapNetworkCache;

	/**
	 * ��ʼ��
	 */
	public BitmapNetworkCache() {

		mThisBitmapNetworkCache = this;

	}

	/**
	 * �������ñ��ػ���
	 */
	private BitmapLocalCahe mBitmapLocalCahe;

	/**
	 * ����Ҫ�����Ĳ���
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);
	}

	/**
	 * �������л�ȡBitmap��д�����ػ��棬�ٶ����ڴ淵��
	 * 
	 * @param cache
	 *            ����BitmapLocalCahe����
	 * @return ����true,����ֵ�����۳ɹ���񶼻���ʾͼƬ
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url,
			BitmapCache cahe) {

		// ��ȡBitmapLocalCahe
		mBitmapLocalCahe = (BitmapLocalCahe) cahe;

		setParams(imageView, url);
		
		// ����AsyncTaskִ������ͼƬ����ʾ
		new GetBitmapTask().execute();

		return true;
	}

	/**
	 * ������绺���ȡ�ļ�����ʵ������
	 */
	private OnFinishedGetNetworkCacheListener onFinishedListener;

	/**
	 * ������绺���ȡ�ļ�����
	 */
	public interface OnFinishedGetNetworkCacheListener {
		public void onFinishedGetNetworkCache(BitmapNetworkCache thisCache,
				ImageView imageView, String url, boolean result);
	}

	/**
	 * �ṩ�ⲿ���õ�����������绺���ȡ�ļ���������
	 * 
	 * @param listener
	 *            ������
	 */
	public void setOnFinishedGetNetworkCache(
			OnFinishedGetNetworkCacheListener listener) {
		this.onFinishedListener = listener;
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

			// �����ⲿ��������ɴ�����
			if (onFinishedListener != null) {
				onFinishedListener.onFinishedGetNetworkCache(
						mThisBitmapNetworkCache, mImageView, mURL, result);
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

		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onSuccess(InputStream inputStream) {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						inputStream);

				mBitmapLocalCahe.setCache(bufferedInputStream);
			}

			@Override
			public void onFailure() {

			}
		});

		return httpUtils.Connect(mURL, 5000, 5000);
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
