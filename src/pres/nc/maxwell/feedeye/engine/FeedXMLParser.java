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
	public String mEncodingString;

	/**
	 * ��ɽ���XML������
	 */
	private OnFinishParseListener mOnFinishParseListener;

	/**
	 * Http���ӹ��������
	 */
	private HTTPUtils mHttpUtils;

	/**
	 * �����Ķ��ĵ�ַ
	 * 
	 * @param feedUrl
	 *            ���ĵ�ַ
	 * @param encodingString
	 *            ���뷽ʽ
	 */
	public void parse(String feedUrl, String encodingString) {
		this.mFeedUrl = feedUrl;
		this.mEncodingString = encodingString;

		getXMLBaseInfo();
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
	 * ��ɽ���XML������
	 */
	public interface OnFinishParseListener {
		public void onFinishParseBaseInfo(boolean result);
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
	 * �������ȡXML
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
			public void onGetName(XmlPullParser parser, String name) throws XmlPullParserException, IOException {

				//LogUtils.w("FeedXMLParser", name);
				
				// ���XML����
				if (TextUtils.isEmpty(mFeedType)) {

					if ("rss".equals(name)) {// rss����
						mFeedType = "RSS";
					} else if ("feed".equals(name)) {// atom����
						mFeedType = "ATOM";
					}

				}
				
				// ���XML����
				if (TextUtils.isEmpty(mFeedTitle)) {

					if ("title".equals(name)) {// ����
						mFeedTitle = parser.nextText();
					}

				}

				// ���XMLʱ��
				if (TextUtils.isEmpty(mFeedTime)) {

					if ("updated".equals(name)) {// ATOM
						mFeedTime = parser.nextText();
					} else if ("pubDate".equals(name)) {// RSS
						mFeedTime = parser.nextText();
					}

				}

				// ���XML��Ҫ
				if (TextUtils.isEmpty(mFeedSummary)) {

					if ("subtitle".equals(name)) {// ATOM
						mFeedSummary = parser.nextText();
					} else if ("description".equals(name)) {// RSS
						mFeedSummary = parser.nextText();
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
}
