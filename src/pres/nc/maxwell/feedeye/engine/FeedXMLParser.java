package pres.nc.maxwell.feedeye.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLParseUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLParseUtils.OnParseListener;
import android.os.AsyncTask;
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
	private String mEncodingString;

	/**
	 * ������Ϣ����
	 */
	private FeedXMLBaseInfo mBaseInfo;

	/**
	 * ������Ϣ����
	 */
	private ArrayList<FeedXMLContentInfo> mContentInfoList;

	/**
	 * Http���ӹ��������
	 */
	private HTTPUtils mHttpUtils;

	/**
	 * ��ɽ���XML������
	 */
	private OnFinishParseXMLListener mOnFinishParseListener;

	/**
	 * ��ɽ���XML������
	 */
	public interface OnFinishParseXMLListener {
		public void onFinishParseBaseInfo(boolean result,
				FeedXMLBaseInfo baseInfo);
		public void onFinishParseContent(boolean result,
				ArrayList<FeedXMLContentInfo> contentInfos);
	}

	/**
	 * ������ɽ���XML�ļ�����
	 * 
	 * @param onFinishParseXMLListener
	 *            ������
	 */
	public void setOnFinishedParseXMLListener(
			OnFinishParseXMLListener onFinishParseXMLListener) {
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

			this.mBaseInfo = new FeedXMLBaseInfo();

			getXMLBaseInfo();

		} else {

			this.mContentInfoList = new ArrayList<FeedXMLContentInfo>();

			getXMLContentInfo();
		}

	}

	/**
	 * ��������XML��
	 * 
	 * @param localStream
	 *            ����XML��
	 * @param encodingString
	 *            ���뷽ʽ
	 * @param parseType
	 *            �������� ����ѡ��{@link FeedXMLParser#TYPE_PARSE_BASE_INFO}����
	 *            {@link FeedXMLParser#TYPE_PARSE_CONTENT}
	 */
	public void parse(InputStream localStream, String encodingString,
			int parseType) {

		this.mEncodingString = encodingString;

		// �жϽ�������
		if (parseType == TYPE_PARSE_BASE_INFO) {

			// TODO��

		} else {
			this.mContentInfoList = new ArrayList<FeedXMLContentInfo>();
			getXMLContentInfo(localStream);
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
					mOnFinishParseListener.onFinishParseBaseInfo(true,
							mBaseInfo);
				}

			}

			@Override
			public void onFailure() {// ���߳�
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseBaseInfo(false,
							mBaseInfo);
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

		XMLParseUtils xmlUtils = new XMLParseUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			@Override
			public void onGetName(XmlPullParser parser, String name,
					int eventType) throws XmlPullParserException, IOException {

				// LogUtils.w("FeedXMLParser", name);

				// ���XML����
				if (TextUtils.isEmpty(mBaseInfo.type)) {

					if ("rss".equals(name)) {// rss����
						mBaseInfo.type = FeedXMLBaseInfo.TYPE_RSS;
					} else if ("feed".equals(name)) {// atom����
						mBaseInfo.type = FeedXMLBaseInfo.TYPE_ATOM;
					}

				}

				// ���XML����
				if (TextUtils.isEmpty(mBaseInfo.title)) {

					if ("title".equals(name)) {// ����
						mBaseInfo.title = parser.nextText();
					}

				}

				// ���XMLʱ��
				if (mBaseInfo.time == null) {

					if ("updated".equals(name)) {// ATOM

						String timeString = TimeUtils.LoopToTransTime(parser
								.nextText());

						mBaseInfo.time = TimeUtils.string2Timestamp(timeString);

					} else if ("pubDate".equals(name)) {// RSS

						String timeString = TimeUtils.LoopToTransTime(parser
								.nextText());

						mBaseInfo.time = TimeUtils.string2Timestamp(timeString);
					}

				}

				// ���XML��Ҫ
				if (TextUtils.isEmpty(mBaseInfo.summary)) {

					if ("subtitle".equals(name)) {// ATOM
						mBaseInfo.summary = parser.nextText();
					} else if ("description".equals(name)) {// RSS
						mBaseInfo.summary = parser.nextText();
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
	 * ���ؽ�����������
	 */
	class getLocalContentInfoTask extends AsyncTask<InputStream, Void, Void> {

		@Override
		protected Void doInBackground(InputStream... params) {// ���߳�
			parseXMLContent(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {// ���߳�

			if (mOnFinishParseListener != null) {
				mOnFinishParseListener.onFinishParseContent(true,
						mContentInfoList);
			}

		}

	}

	/**
	 * �ӱ��ؽ���XML�����ݲ�����
	 * 
	 * @param localStream
	 */
	private void getXMLContentInfo(InputStream localStream) {
		new getLocalContentInfoTask().execute(localStream);
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
					mOnFinishParseListener.onFinishParseContent(true,
							mContentInfoList);
				}

			}

			@Override
			public void onFailure() {// ���߳�
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseContent(false,
							mContentInfoList);
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
		mContentInfoList.clear();

		XMLParseUtils xmlUtils = new XMLParseUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			boolean startRssItemFlag = false;
			FeedXMLContentInfo contentInfo = null;

			@Override
			public void onGetName(XmlPullParser parser, String name,
					int eventType) throws XmlPullParserException, IOException {

				// ----------------RSS---------------

				if ("item".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS���ݿ�ʼ
					contentInfo = new FeedXMLContentInfo();
					startRssItemFlag = true;
				}

				if (startRssItemFlag == true
						&& "title".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS���ݱ���
					contentInfo.title = parser.nextText();
				}

				if (startRssItemFlag == true
						&& "description".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS��������
					contentInfo.description = parser.nextText();
				}

				if (startRssItemFlag == true
						&& "pubDate".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS��������
					contentInfo.pubDate = parser.nextText();
				}

				if (startRssItemFlag == true && "item".equals(parser.getName())
						&& eventType == XmlPullParser.END_TAG) {// RSS���ݽ���
					mContentInfoList.add(contentInfo);
					startRssItemFlag = false;
				}

				// ----------------ATOM---------------

				if ("entry".equals(parser.getName())) {// TODO:ATOM

				}

			}

			@Override
			public boolean isOnlyParseStartTag() {
				return false;
			}

			@Override
			public boolean isInterruptParse(XmlPullParser parser) {
				return false;
			}

		});

		xmlUtils.parseStream(inputStream, mEncodingString);

	}

}
