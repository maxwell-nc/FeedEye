package pres.nc.maxwell.feedeye.engine;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfoBean;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
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
	public FeedXMLBaseInfoBean mBaseInfoBean;

	/**
	 * ������Ϣ����
	 */
	public FeedXMLContentInfo mContentInfoBean;


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

			this.mBaseInfoBean = new FeedXMLBaseInfoBean();

			getXMLBaseInfo();
		} else {

			this.mContentInfoBean = new FeedXMLContentInfo();

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
			
			String feedType = mBaseInfoBean.getFeedType();
			String feedTitle = mBaseInfoBean.getFeedTitle();
			String feedTime = mBaseInfoBean.getFeedTime();
			String feedSummary = mBaseInfoBean.getFeedSummary();

			@Override
			public void onGetName(XmlPullParser parser, String name)
					throws XmlPullParserException, IOException {
				
				
				// LogUtils.w("FeedXMLParser", name);

				// ���XML����
				if (TextUtils.isEmpty(feedType)) {

					if ("rss".equals(name)) {// rss����
						feedType = "RSS";
					} else if ("feed".equals(name)) {// atom����
						feedType = "ATOM";
					}

				}

				// ���XML����
				if (TextUtils.isEmpty(feedTitle)) {

					if ("title".equals(name)) {// ����
						feedTitle = parser.nextText();
					}

				}

				// ���XMLʱ��
				if (TextUtils.isEmpty(feedTime)) {

					if ("updated".equals(name)) {// ATOM
						feedTime = parser.nextText();
					} else if ("pubDate".equals(name)) {// RSS
						feedTime = parser.nextText();
					}

				}

				// ���XML��Ҫ
				if (TextUtils.isEmpty(feedSummary)) {

					if ("subtitle".equals(name)) {// ATOM
						feedSummary = parser.nextText();
					} else if ("description".equals(name)) {// RSS
						feedSummary = parser.nextText();
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
		mContentInfoBean.setContentCount(0);

		XMLUtils xmlUtils = new XMLUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			@Override
			public void onGetName(XmlPullParser parser, String name)
					throws XmlPullParserException, IOException {

				if ("item".equals(parser.getName())) {// RSS
					mContentInfoBean.setContentCount(mContentInfoBean.getContentCount()+1);
				}
				if ("entry".equals(parser.getName())) {// ATOM
					mContentInfoBean.setContentCount(mContentInfoBean.getContentCount()+1);
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
