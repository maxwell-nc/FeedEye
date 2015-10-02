package pres.nc.maxwell.feedeye.engine;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;
import android.text.TextUtils;
import android.util.Xml;

/**
 * ���Ľ�������֧��XML��ʽ
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
	public String encodingString;

	/**
	 * RSS���ͣ�item��ǩ��
	 */
	public int mItemCount;

	/**
	 * ATOM���ͣ�entry��ǩ��
	 */
	public int mEntryCount;

	/**
	 * ��ɽ���XML������
	 */
	private OnFinishedParseXMLListener mOnFinishedParseXMLListener;

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

		getXML();
	}

	/**
	 * ��ɽ���XML������
	 */
	public interface OnFinishedParseXMLListener {
		public void onFinishedParseXMLBaseInfo(boolean result);
	}

	/**
	 * ������ɽ���XML�ļ�����
	 * 
	 * @param onFinishedParseXMLListener
	 *            ������
	 */
	public void setOnFinishedParseXMLListener(
			OnFinishedParseXMLListener onFinishedParseXMLListener) {
		this.mOnFinishedParseXMLListener = onFinishedParseXMLListener;
	}

	/**
	 * �������ȡXML
	 */
	private void getXML() {

		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {//���߳�
				parseXMLBaseInfo(inputStream);
			}

			@Override
			public void onSuccess() {//���߳�
				if (mOnFinishedParseXMLListener != null) {
					mOnFinishedParseXMLListener
							.onFinishedParseXMLBaseInfo(true);
				}

			}

			@Override
			public void onFailure() {//���߳�
				if (mOnFinishedParseXMLListener != null) {
					mOnFinishedParseXMLListener
							.onFinishedParseXMLBaseInfo(false);
				}
			}

		});

		httpUtils.Connect(mFeedUrl, 10000, 10000);
	}

	/**
	 * ����XML������������Ϣ
	 * 
	 * @param inputStream
	 *            XML������
	 */
	private void parseXMLBaseInfo(InputStream inputStream) {

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inputStream, encodingString);
			int eventType = parser.getEventType();

			// ͳ�ƹ���
			mItemCount = 0;
			mEntryCount = 0;

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

				// �������
				if ("item".equals(parser.getName())) {// ATOM
					mItemCount++;
				}
				if ("entry".equals(parser.getName())) {// RSS
					mEntryCount++;
				}

				// TODO��ͼ��

				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
