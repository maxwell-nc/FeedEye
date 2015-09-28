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
 * ���Ľ�������֧��XML��ʽ
 * 
 * @author Forest
 * 
 */
public class FeedXMLParser {

	/**
	 * ���ĵ�ַ
	 */
	private String mFeedUrl;

	/**
	 * ��������
	 */
	public String mFeedType;

	/**
	 * ���ı���
	 */
	public String mFeedTitle;

	/**
	 * ����ʱ��
	 */
	public String mFeedTime;

	/**
	 * ���ĸ�Ҫ
	 */
	public String mFeedSummary;

	/**
	 * �����Ķ��ĵ�ַ
	 * 
	 * @param feedUrl
	 *            ���ĵ�ַ
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
		protected Void doInBackground(Void... params) {// ���߳�
			getXML();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {// ���߳�

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
				connection.disconnect();// ��Ҫ���ǶϿ�
			}*/

		}
	}

	/**
	 * ����XML������
	 * 
	 * @param inputStream
	 *            XML������
	 */
	private void parseXML(InputStream inputStream) {

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inputStream, "utf-8");
			int eventType = parser.getEventType();

			// ���Ͻ���
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType != XmlPullParser.START_TAG || parser.getName()==null) {
					eventType = parser.next();
					continue;
				}
				
				//LogUtils.w("FeedXMLParser", parser.getName() );
				
				// ���XML����
				if (TextUtils.isEmpty(mFeedType)) {
					
					if ("rss".equals(parser.getName())) {// rss����
						mFeedType = "RSS";
					} else if (parser.getName() == "feed") {// atom����
						mFeedType = "ATOM";
					}
					
				}

				// ���XML����
				if (TextUtils.isEmpty(mFeedTitle)) {

					if ("title".equals(parser.getName())) {// ����
						mFeedTitle = parser.nextText();
					}
					
				}

				// ���XMLʱ��
				if (TextUtils.isEmpty(mFeedTime)) {

					if ("updated".equals(parser.getName())) {// ATOM
						mFeedTime = parser.nextText();
					}
					if ("pubDate".equals(parser.getName())) {// RSS
						mFeedTime = parser.nextText();
					}
					
				}

				// ���XML��Ҫ
				if (TextUtils.isEmpty(mFeedSummary)) {

					if ("subtitle".equals(parser.getName())) {// ATOM
						mFeedSummary = parser.nextText();
					}
					if ("description".equals(parser.getName())) {// RSS
						mFeedSummary = parser.nextText();
					}
					
				}

				// TODO��ͼ�ꡢ����

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
