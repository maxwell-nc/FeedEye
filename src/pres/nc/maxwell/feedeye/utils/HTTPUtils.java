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
 * HTTP���ӹ�����
 */
public class HTTPUtils {

	/**
	 * ���Ӽ���
	 */
	private OnConnectListener onConnectListener;

	/**
	 * ���Ӽ���������װ��Handler�����������߳�
	 */
	public interface OnConnectListener {

		/**
		 * �����ش���200ʱ�����õķ��������������߳�
		 * 
		 * @param inputStream���ص�������
		 */
		public void onConnect(InputStream inputStream);

		/**
		 * ����ɹ������������߳�
		 */
		public void onSuccess();

		/**
		 * ʧ�ܺ������������߳�
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
	 * �洢������Ϣ
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
	 * ����HTTP��HTTPS
	 * 
	 * @param url
	 *            ��ַ
	 * @param ConnectTimeout
	 *            ���ӳ�ʱ������
	 * @param ReadTimeout
	 *            ��ȡ��ʱ������
	 */
	public void Connect(String url, int connectTimeout, int readTimeout) {
		new ConnectTask().execute(new ConnectInfo(url, connectTimeout,
				readTimeout));
	}

	/**
	 * �첽��������
	 */
	class ConnectTask extends AsyncTask<ConnectInfo, Void, Boolean> {

		@Override
		protected Boolean doInBackground(ConnectInfo... params) {// ���߳�

			HttpURLConnection connection = null;
			try {

				if (params[0].url.startsWith("https://")) {
					// ��������HTTPS����
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
				} else {//��HTTPS
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

					// ���ü�����
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
					connection.disconnect();// ��Ҫ���ǶϿ�
				}
			}

			return false;

		}
		@Override
		protected void onPostExecute(Boolean result) {//���߳�

			if (onConnectListener != null) {

				if (result.booleanValue()) {// �ɹ�����
					onConnectListener.onSuccess();
				} else {//����ʧ��
					onConnectListener.onFailure();
				}

			}
		}

		
	}
	

}
