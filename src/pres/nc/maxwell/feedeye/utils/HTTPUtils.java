package pres.nc.maxwell.feedeye.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * HTTP连接工具类
 */
public class HTTPUtils {

	/**
	 * 连接监听
	 */
	private OnConnectListener onConnectListener;

	/**
	 * 连接监听器
	 */
	public interface OnConnectListener {

		/**
		 * 连接成功（返回代码200时）调用的方法
		 * 
		 * @param inputStream
		 *            返回的输入流
		 */
		public void onSuccess(InputStream inputStream);

		/**
		 * 失败后处理
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
	 * 连接HTTP和HTTPS
	 * 
	 * @param url
	 *            地址
	 * @param ConnectTimeout
	 *            连接超时毫秒数
	 * @param ReadTimeout
	 *            读取超时毫秒数
	 * @return 是否成功连接
	 */
	public boolean Connect(String url, int ConnectTimeout, int ReadTimeout) {

		HttpURLConnection connection = null;
		try {

			if (url.startsWith("https://")) {
				// 信任所有HTTPS连接
				HttpsURLConnection
						.setDefaultHostnameVerifier(new HostnameVerifier() {
							public boolean verify(String string, SSLSession ssls) {
								return true;
							}
						});
				connection = null;
				connection = (HttpsURLConnection) new URL(url).openConnection();
			} else if (url.startsWith("http://")) {
				connection = null;
				connection = (HttpURLConnection) new URL(url).openConnection();
			}else {
				return false;
			}

			connection.setConnectTimeout(ConnectTimeout);
			connection.setReadTimeout(ReadTimeout);

			connection.setRequestMethod("GET");
			connection.connect();

			LogUtils.w("HTTPUtils",
					"ResponseCode:" + connection.getResponseCode());
			if (connection.getResponseCode() == 200) {

				if (onConnectListener != null) {
					onConnectListener.onSuccess(connection.getInputStream());
					return true;
				} else {//TODO:添加其他情况
				
				}
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
		
		if (onConnectListener != null) {
			onConnectListener.onFailure();
		}
		
		return false;

	}

}
