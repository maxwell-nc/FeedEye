package pres.nc.maxwell.feedeye.engine;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;
import pres.nc.maxwell.feedeye.utils.XMLUtils;
import pres.nc.maxwell.feedeye.utils.XMLUtils.OnParseListener;
import android.text.TextUtils;

/**
 * ������Ϣ��������֧������XML
 */
public class FeedXMLParser {

	/**
	 * ���ĵ�ַ
	 */
	private String mFeedUrl;

	/**
	 * ���뷽ʽ
	 */
	public String mEncodingString;

	/**
	 * ������Ϣ����
	 */
	public BaseInfo mBaseInfo;

	/**
	 * ������Ϣ����
	 */
	public ContentInfo mContentInfo;

	/**
	 * ������Ϣ
	 */
	public static class BaseInfo {

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

	}

	/**
	 * ������Ϣ
	 */
	public static class ContentInfo {

		/**
		 * ��Ϣ����
		 */
		public int mContentCount;

	}

	/**
	 * Http���ӹ��������
	 */
	private HTTPUtils mHttpUtils;

	/**
	 * ��ɽ���XML������
	 */
	private OnFinishParseListener mOnFinishParseListener;

	/**
	 * ��ɽ���XML������
	 */
	public interface OnFinishParseListener {
		public void onFinishParseBaseInfo(boolean result);
		public void onFinishParseContent(boolean result);
	}

	/**
	 * Ĭ�ϵ�XML������ɼ�������ʲô������
	 */
	public class OnFinishParseDefaultListener implements OnFinishParseListener {

		@Override
		public void onFinishParseBaseInfo(boolean result) {
		}

		@Override
		public void onFinishParseContent(boolean result) {
		}

	}

	/**
	 * ������ɽ���XML�ļ�����
	 * 
	 * @param onFinishParseXMLListener
	 *            ������
	 */
	public void setOnFinishedParseXMLListener(
			OnFinishParseListener onFinishParseXMLListener) {
		this.mOnFinishParseListener = onFinishParseXMLListener;
	}

	/**
	 * ֻ����������Ϣ
	 * 
	 * @see #parse(String, String, int)
	 */
	public static final int TYPE_PARSE_BASE_INFO = 1;

	/**
	 * ��������
	 * 
	 * @see #parse(String, String, int)
	 */
	public static final int TYPE_PARSE_CONTENT = 2;

	/**
	 * �����Ķ��ĵ�ַ
	 * 
	 * @param feedUrl
	 *            ���ĵ�ַ
	 * @param encodingString
	 *            ���뷽ʽ
	 * @param parseType
	 *            �������ͣ���ѡ��{@link FeedXMLParser#TYPE_PARSE_BASE_INFO}����
	 *            {@link FeedXMLParser#TYPE_PARSE_CONTENT}
	 */
	public void parse(String feedUrl, String encodingString, int parseType) {

		this.mFeedUrl = feedUrl;
		this.mEncodingString = encodingString;

		// �жϽ�������
		if (parseType == TYPE_PARSE_BASE_INFO) {

			this.mBaseInfo = new BaseInfo();

			getXMLBaseInfo();
		} else {

			this.mContentInfo = new ContentInfo();

			getXMLContentInfo();
		}

	}

	/**
	 * ȡ������XML
	 */
	public void cancelParse() {

		if (mHttpUtils != null) {
			mHttpUtils.Disconnet();
		}

	}

	/**
	 * �������ȡXML�Ļ�����Ϣ������
	 */
	private void getXMLBaseInfo() {

		mHttpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {// ���߳�
				parseXMLBaseInfo(inputStream);
			}

			@Override
			public void onSuccess() {// ���߳�
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseBaseInfo(true);
				}

			}

			@Override
			public void onFailure() {// ���߳�
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseBaseInfo(false);
				}
			}

		});

		mHttpUtils.Connect(mFeedUrl, 10000, 10000);
	}

	/**
	 * ����XML������������Ϣ
	 * 
	 * @param inputStream
	 *            XML������
	 */
	private void parseXMLBaseInfo(InputStream inputStream) {

		XMLUtils xmlUtils = new XMLUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			@Override
			public void onGetName(XmlPullParser parser, String name)
					throws XmlPullParserException, IOException {

				// LogUtils.w("FeedXMLParser", name);

				// ���XML����
				if (TextUtils.isEmpty(mBaseInfo.mFeedType)) {

					if ("rss".equals(name)) {// rss����
						mBaseInfo.mFeedType = "RSS";
					} else if ("feed".equals(name)) {// atom����
						mBaseInfo.mFeedType = "ATOM";
					}

				}

				// ���XML����
				if (TextUtils.isEmpty(mBaseInfo.mFeedTitle)) {

					if ("title".equals(name)) {// ����
						mBaseInfo.mFeedTitle = parser.nextText();
					}

				}

				// ���XMLʱ��
				if (TextUtils.isEmpty(mBaseInfo.mFeedTime)) {

					if ("updated".equals(name)) {// ATOM
						mBaseInfo.mFeedTime = parser.nextText();
					} else if ("pubDate".equals(name)) {// RSS
						mBaseInfo.mFeedTime = parser.nextText();
					}

				}

				// ���XML��Ҫ
				if (TextUtils.isEmpty(mBaseInfo.mFeedSummary)) {

					if ("subtitle".equals(name)) {// ATOM
						mBaseInfo.mFeedSummary = parser.nextText();
					} else if ("description".equals(name)) {// RSS
						mBaseInfo.mFeedSummary = parser.nextText();
					}

				}
			}

			@Override
			public boolean isOnlyParseStartTag() {
				return true;
			}

			@Override
			public boolean isInterruptParse(XmlPullParser parser) {

				// ��һ��Ԫ���˳�����,���ؽ���ȫ��
				if ("item".equals(parser.getName())) {// RSS
					return true;
				}
				if ("entry".equals(parser.getName())) {// ATOM
					return true;
				}

				return false;
			}
		});

		xmlUtils.parseStream(inputStream, mEncodingString);

	}

	/**
	 * �������ȡXML�����ݲ�����
	 */
	private void getXMLContentInfo() {

		mHttpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {// ���߳�
				parseXMLContent(inputStream);

			}

			@Override
			public void onSuccess() {// ���߳�
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseContent(true);
				}

			}

			@Override
			public void onFailure() {// ���߳�
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseContent(false);
				}
			}
		});

		mHttpUtils.Connect(mFeedUrl, 15000, 15000);
	}

	/**
	 * ����XML����������
	 * 
	 * @param inputStream
	 *            XML������
	 */
	private void parseXMLContent(InputStream inputStream) {

		// ��������
		mContentInfo.mContentCount = 0;

		XMLUtils xmlUtils = new XMLUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			@Override
			public void onGetName(XmlPullParser parser, String name)
					throws XmlPullParserException, IOException {

				if ("item".equals(parser.getName())) {// RSS
					mContentInfo.mContentCount++;
				}
				if ("entry".equals(parser.getName())) {// ATOM
					mContentInfo.mContentCount++;
				}

			}

			@Override
			public boolean isOnlyParseStartTag() {
				return true;
			}

			@Override
			public boolean isInterruptParse(XmlPullParser parser) {
				return false;
			}
		});

		xmlUtils.parseStream(inputStream, mEncodingString);

	}

}
