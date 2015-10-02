package pres.nc.maxwell.feedeye.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.os.AsyncTask;

/**
 * HTTP连接工具类
 */
public class HTTPUtils {

	/**
	 * 连接监听
	 */
	private OnConnectListener onConnectListener;

	/**
	 * 连接监听器，封装了Handler，运行在主线程
	 */
	public interface OnConnectListener {

		/**
		 * （返回代码200时）调用的方法，运行在子线程
		 * 
		 * @param inputStream返回的输入流
		 */
		public void onConnect(InputStream inputStream);

		/**
		 * 处理成功，运行在主线程
		 */
		public void onSuccess();

		/**
		 * 失败后处理，运行在主线程
		 */
		public void onFailure();
	}

	/**
	 * 设置连接监听器
	 * 
	 * @param onConnectListener
	 *            连接监听器
	 */
	public void setOnConnectListener(OnConnectListener onConnectListener) {
		this.onConnectListener = onConnectListener;
	}

	/**
	 * 构造函数，传入连接监听器
	 * 
	 * @param onConnectListener
	 *            连接监听器
	 */
	public HTTPUtils(OnConnectListener onConnectListener) {
		this.onConnectListener = onConnectListener;
	}

	/**
	 * 存储连接信息
	 */
	public class ConnectInfo {

		public ConnectInfo(String url, int connectTimeout, int readTimeout) {
			this.url = url;
			this.connectTimeout = connectTimeout;
			this.readTimeout = readTimeout;
		}

		public String url;
		public int connectTimeout;
		public int readTimeout;
		
	}
	
	/**
	 * 连接HTTP和HTTPS
	 * 
	 * @param url
	 *            地址
	 * @param ConnectTimeout
	 *            连接超时毫秒数
	 * @param ReadTimeout
	 *            读取超时毫秒数
	 */
	public void Connect(String url, int connectTimeout, int readTimeout) {
		new ConnectTask().execute(new ConnectInfo(url, connectTimeout,
				readTimeout));
	}

	/**
	 * 异步连接任务
	 */
	class ConnectTask extends AsyncTask<ConnectInfo, Void, Boolean> {

		@Override
		protected Boolean doInBackground(ConnectInfo... params) {// 子线程

			HttpURLConnection connection = null;
			try {

				if (params[0].url.startsWith("https://")) {
					// 信任所有HTTPS连接
					HttpsURLConnection
							.setDefaultHostnameVerifier(new HostnameVerifier() {
								public boolean verify(String string,
										SSLSession ssls) {
									return true;
								}
							});
					connection = null;
					connection = (HttpsURLConnection) new URL(params[0].url)
							.openConnection();
				} else {//非HTTPS
					connection = null;
					connection = (HttpURLConnection) new URL(params[0].url)
							.openConnection();
				} 

				connection.setConnectTimeout(params[0].connectTimeout);
				connection.setReadTimeout(params[0].readTimeout);

				connection.setRequestMethod("GET");
				connection.connect();

				LogUtils.i("HTTPUtils",
						"ResponseCode:" + connection.getResponseCode());

				if (connection.getResponseCode() == 200) {

					// 调用监听器
					if (onConnectListener != null) {
						onConnectListener
								.onConnect(connection.getInputStream());
					}
					
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
		@Override
		protected void onPostExecute(Boolean result) {//主线程

			if (onConnectListener != null) {

				if (result.booleanValue()) {// 成功接收
					onConnectListener.onSuccess();
				} else {//接收失败
					onConnectListener.onFailure();
				}

			}
		}

		
	}
	

}
