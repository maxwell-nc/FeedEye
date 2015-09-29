package pres.nc.maxwell.feedeye.engine;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;
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
	 * ���뷽ʽ
	 */
	private String encodingString;

	/**
	 * �����Ķ��ĵ�ַ
	 * 
	 * @param feedUrl
	 *            ���ĵ�ַ
	 * @param encodingString
	 *            ���뷽ʽ
	 */
	public void parseUrl(String feedUrl, String encodingString) {
		this.mFeedUrl = feedUrl;
		this.encodingString = encodingString;
		new ParseTask().execute();
	}

	public interface OnFinishedParseXMLListener {
		public void onFinishedParseXML(boolean result);
	}

	private OnFinishedParseXMLListener mOnFinishedParseXMLListener;

	public void setOnFinishedParseXMLListener(
			OnFinishedParseXMLListener onFinishedParseXMLListener) {
		this.mOnFinishedParseXMLListener = onFinishedParseXMLListener;
	}

	class ParseTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {// ���߳�

			return getXML();
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean result) {// ���߳�

			if (mOnFinishedParseXMLListener != null) {
				mOnFinishedParseXMLListener.onFinishedParseXML(result);
			}

			super.onPostExecute(result);
		}

	}

	/**
	 * �������ȡXML
	 * @return �Ƿ�ɹ���ȡXML
	 */
	private boolean getXML() {
		
		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {
			
			@Override
			public void onSuccess(InputStream inputStream) {
				parseXML(inputStream);
			}
			
			@Override
			public void onFailure() {
				
			}
		});
		
		return httpUtils.Connect(mFeedUrl, 10000, 10000);
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
			parser.setInput(inputStream, encodingString);
			int eventType = parser.getEventType();

			// ���Ͻ���
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType != XmlPullParser.START_TAG
						|| parser.getName() == null) {
					eventType = parser.next();
					continue;
				}

				// LogUtils.w("FeedXMLParser", parser.getName() );

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

	}

}
