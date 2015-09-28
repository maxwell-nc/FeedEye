package pres.nc.maxwell.feedeye.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Xml;

/**
 * 订阅解析器，支持XML格式
 * 
 * @author Forest
 * 
 */
public class FeedXMLParser {

	/**
	 * 订阅地址
	 */
	private String mFeedUrl;

	/**
	 * 订阅类型
	 */
	public String mFeedType;

	/**
	 * 订阅标题
	 */
	public String mFeedTitle;

	/**
	 * 订阅时间
	 */
	public String mFeedTime;

	/**
	 * 订阅概要
	 */
	public String mFeedSummary;

	/**
	 * 解析的订阅地址
	 * 
	 * @param feedUrl
	 *            订阅地址
	 */
	public void parseUrl(String feedUrl) {
		this.mFeedUrl = feedUrl;

		new ParseTask().execute();
	}

	public interface OnFinishedParseXMLListener {
		public void onFinishedParseXML();
	}

	private OnFinishedParseXMLListener mOnFinishedParseXMLListener;
	
	public void setOnFinishedParseXMLListener(
			OnFinishedParseXMLListener onFinishedParseXMLListener) {
		this.mOnFinishedParseXMLListener = onFinishedParseXMLListener;
	}

	class ParseTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// 子线程
			getXML();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {// 主线程
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {// 主线程

			super.onPostExecute(result);
		}

	}

	private void getXML() {

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(mFeedUrl).openConnection();

			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			connection.setRequestMethod("GET");
			connection.connect();

			if (connection.getResponseCode() == 200) {

				

				parseXML(connection.getInputStream());

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			/*if (connection != null) {
				connection.disconnect();// 不要忘记断开
			}*/

		}
	}

	/**
	 * 解析XML输入流
	 * 
	 * @param inputStream
	 *            XML输入流
	 */
	private void parseXML(InputStream inputStream) {

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inputStream, "utf-8");
			int eventType = parser.getEventType();

			// 不断解析
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType != XmlPullParser.START_TAG || parser.getName()==null) {
					eventType = parser.next();
					continue;
				}
				
				//LogUtils.w("FeedXMLParser", parser.getName() );
				
				// 检查XML类型
				if (TextUtils.isEmpty(mFeedType)) {
					
					if ("rss".equals(parser.getName())) {// rss类型
						mFeedType = "RSS";
					} else if (parser.getName() == "feed") {// atom类型
						mFeedType = "ATOM";
					}
					
				}

				// 检查XML标题
				if (TextUtils.isEmpty(mFeedTitle)) {

					if ("title".equals(parser.getName())) {// 标题
						mFeedTitle = parser.nextText();
					}
					
				}

				// 检查XML时间
				if (TextUtils.isEmpty(mFeedTime)) {

					if ("updated".equals(parser.getName())) {// ATOM
						mFeedTime = parser.nextText();
					}
					if ("pubDate".equals(parser.getName())) {// RSS
						mFeedTime = parser.nextText();
					}
					
				}

				// 检查XML概要
				if (TextUtils.isEmpty(mFeedSummary)) {

					if ("subtitle".equals(parser.getName())) {// ATOM
						mFeedSummary = parser.nextText();
					}
					if ("description".equals(parser.getName())) {// RSS
						mFeedSummary = parser.nextText();
					}
					
				}

				// TODO：图标、数量

				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mOnFinishedParseXMLListener!=null) {
			mOnFinishedParseXMLListener.onFinishedParseXML();
		}
		
	}

}
