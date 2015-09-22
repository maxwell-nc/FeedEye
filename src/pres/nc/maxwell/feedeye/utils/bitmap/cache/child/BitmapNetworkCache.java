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
 * Bitmap网络缓存
 */
public class BitmapNetworkCache extends BitmapCache<Void> {

	/**
	 * 用于设置本地缓存
	 */
	private BitmapLocalCahe mBitmapLocalCahe;

	/**
	 * 加载错误时显示的图片
	 */
	protected int mErrorImageResId = R.drawable.img_load_error;

	/**
	 * 设置加载错误时显示的图片
	 * 
	 * @param errorImageResId
	 *            加载错误时显示的图片
	 */
	public void setErrorImageResId(int errorImageResId) {
		this.mErrorImageResId = errorImageResId;
	}

	/**
	 * 初始化参数
	 * 
	 * @param bitmapLocalCahe
	 *            用于设置本地缓存
	 */
	public BitmapNetworkCache(BitmapLocalCahe bitmapLocalCahe) {

		super(bitmapLocalCahe.getImageView(), bitmapLocalCahe.getURL());
		this.mBitmapLocalCahe = bitmapLocalCahe;

	}

	/**
	 * 显示无法加载图片
	 */
	private void showErrorBitmap(){
		mImageView.setImageResource(mErrorImageResId);
	}	
	
	/**
	 * 从网络中获取Bitmap，写到本地缓存，再读入内存返回
	 * 
	 * @return 返回true,无用值，无论成功与否都会显示图片
	 */
	@Override
	public boolean displayBitmap() {
		
		// 开启AsyncTask执行下载图片并显示
		new GetBitmapTask().execute();

		return true;
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

			if (!result.booleanValue()) {// 获取失败
				showErrorBitmap();
			} else {// 成功获取
				// 调用本地缓存对象处理
				if(!mBitmapLocalCahe.displayBitmap()){
					showErrorBitmap();
				}
			}

		};

	}

	/**
	 * 从网络中下载图片，设置本地缓存
	 * 
	 * @return 返回是否成功
	 */
	@Override
	protected boolean getCache() {

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
	public void setCache(Void v) {
		throw new RuntimeException("Do not call this method:setCache() in "
				+ this.getClass().getName());
	}

}
