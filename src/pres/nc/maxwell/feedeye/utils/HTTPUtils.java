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
 * HTTP���ӹ�����
 */
public class HTTPUtils {

	/**
	 * ���Ӽ���
	 */
	private OnConnectListener onConnectListener;

	/**
	 * ���Ӽ�����
	 */
	public interface OnConnectListener {

		/**
		 * ���ӳɹ������ش���200ʱ�����õķ���
		 * 
		 * @param inputStream
		 *            ���ص�������
		 */
		public void onSuccess(InputStream inputStream);

		/**
		 * ʧ�ܺ���
		 */
		public void onFailure();
	}

	/**
	 * �������Ӽ�����
	 * 
	 * @param onConnectListener
	 *            ���Ӽ�����
	 */
	public void setOnConnectListener(OnConnectListener onConnectListener) {
		this.onConnectListener = onConnectListener;
	}

	/**
	 * ���캯�����������Ӽ�����
	 * 
	 * @param onConnectListener
	 *            ���Ӽ�����
	 */
	public HTTPUtils(OnConnectListener onConnectListener) {
		this.onConnectListener = onConnectListener;
	}
	
	/**
	 * ����HTTP��HTTPS
	 * 
	 * @param url
	 *            ��ַ
	 * @param ConnectTimeout
	 *            ���ӳ�ʱ������
	 * @param ReadTimeout
	 *            ��ȡ��ʱ������
	 * @return �Ƿ�ɹ�����
	 */
	public boolean Connect(String url, int ConnectTimeout, int ReadTimeout) {

		HttpURLConnection connection = null;
		try {

			if (url.startsWith("https://")) {
				// ��������HTTPS����
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
				} else {//TODO:����������
				
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
		
		if (onConnectListener != null) {
			onConnectListener.onFailure();
		}
		
		return false;

	}

}
