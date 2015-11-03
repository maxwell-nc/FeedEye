package pres.nc.maxwell.feedeye.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;

/**
 * HTTP连接工具类，建议一个连接使用单独的对象
 */
public class HTTPUtils {

	/**
	 * 连接监听
	 */
	private OnConnectListener onConnectListener;

	/**
	 * 连接的线程
	 */
	private ConnectTask mCurrentTask;

	/**
	 * 任务线程池
	 */
	private ExecutorService mThreadPool;

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
		 * 在onConnect方法之后，处理成功，运行在主线程
		 */
		public void onSuccess();

		/**
		 * 在onConnect方法之后，处理失败，运行在主线程
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
	 * @param connectTimeout
	 *            连接超时毫秒数
	 * @param readTimeout
	 *            读取超时毫秒数
	 * @param threadPool
	 *            自定义线程池
	 */
	public void connect(String url, int connectTimeout, int readTimeout,
			ExecutorService threadPool) {

		mThreadPool = threadPool;
		mCurrentTask = new ConnectTask();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mCurrentTask.executeOnExecutor(threadPool, new ConnectInfo(url,
					connectTimeout, readTimeout));
		} else {
			mCurrentTask.execute(new ConnectInfo(url,
					connectTimeout, readTimeout));
		}
	}

	/**
	 * 取消连接
	 */
	public void disconnet() {

		if (mCurrentTask != null) {
			mCurrentTask.cancel(true);
		}

		if (mThreadPool != null) {
			mThreadPool.shutdownNow();
		}

	}

	/**
	 * 异步连接任务
	 */
	class ConnectTask extends AsyncTask<ConnectInfo, Void, Boolean> {

		@Override
		protected Boolean doInBackground(ConnectInfo... params) {// 子线程

			HttpURLConnection connection = null;
			try {

				String fixUrl = params[0].url;

				if (fixUrl.startsWith("https://")) {
					// 信任所有HTTPS连接
					HttpsURLConnection
							.setDefaultHostnameVerifier(new HostnameVerifier() {
								public boolean verify(String string,
										SSLSession ssls) {
									return true;
								}
							});
					connection = null;
					connection = (HttpsURLConnection) new URL(fixUrl)
							.openConnection();
				} else {// 非HTTPS
					connection = null;

					if (fixUrl.startsWith("//")) {// 如： //xxx.com/xx
						fixUrl = "http:" + fixUrl;
					}

					connection = (HttpURLConnection) new URL(fixUrl)
							.openConnection();
				}

				connection.setConnectTimeout(params[0].connectTimeout);
				connection.setReadTimeout(params[0].readTimeout);

				connection
						.setRequestProperty(
								"User-Agent",
								"Mozilla/5.0 (Linux; U; Android ;) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
				connection.setRequestMethod("GET");
				connection.connect();

				LogUtils.i("HTTPUtils", "Link:" + fixUrl);
				LogUtils.i("HTTPUtils",
						"ResponseCode:" + connection.getResponseCode());
				LogUtils.i("HTTPUtils",
						"ResponseCode:" + connection.getResponseMessage());

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
		protected void onPostExecute(Boolean result) {// 主线程

			if (onConnectListener != null) {

				if (result.booleanValue()) {// 成功接收
					onConnectListener.onSuccess();
				} else {// 接收失败
					onConnectListener.onFailure();
				}

			}
		}

	}

	/**
	 * 把HTML格式文本转成无标签文本，图片标签转换为"[图片]")
	 * 
	 * @param html
	 *            HTML格式文本，不修改原来的String对象
	 * @param isTrim
	 *            是否去除空格
	 * @param imgLinks
	 *            获得的图片链接集合，不需要可以传入null
	 * @return 无标签文本，新的String对象
	 */
	public static String html2Text(String html, boolean isTrim,
			final ArrayList<String> imgLinks) {

		String text = null;

		if (imgLinks != null) {

			text = Html.fromHtml(html, new ImageGetter() {

				public Drawable getDrawable(String source) {

					if ("src".equals(source) || TextUtils.isEmpty(source)) {
						imgLinks.add("无法识别的图片地址");
					} else {
						imgLinks.add(source);
					}

					return null;
				}

			}, null).toString();

		} else {
			text = Html.fromHtml(html).toString();
		}

		// 判断是否去除空格
		if (isTrim) {
			text = text.trim();
		}

		// 不需要图片链接
		if (imgLinks == null) {
			text = text.replace("\ufffc", "[图片]");
			return text;
		}

		// 替换图片obj
		for (String link : imgLinks) {

			if (TextUtils.isEmpty(link)) {
				text = text.replace("\ufffc", "[图片]");
			} else {
				text = text.replace("\ufffc", "[图片：" + link + "]");
			}

		}

		return text;
	}

	public static String[] html2Texts(String html,
			final ArrayList<String> imgLinks) {

		String longText = null;

		if (imgLinks != null) {

			longText = Html.fromHtml(html, new ImageGetter() {

				public Drawable getDrawable(String source) {

					if ("src".equals(source) || TextUtils.isEmpty(source)) {
						imgLinks.add("无法识别的图片地址");
					} else {
						imgLinks.add(source);
					}

					return null;
				}

			}, null).toString();

		} else {
			longText = Html.fromHtml(html).toString();
		}

		longText = longText.replace("\ufffc", "\ufffc\u0020");// 添加空格，防止两个图片连续出现的问题
		String[] textFragments = longText.split("\ufffc");

		return textFragments;
	}
}
