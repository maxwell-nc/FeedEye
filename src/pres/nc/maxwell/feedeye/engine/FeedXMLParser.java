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
 * 订阅信息解析器，支持网络XML
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
	 * 编码方式
	 */
	public String mEncodingString;

	/**
	 * 完成解析XML监听器
	 */
	private OnFinishParseListener mOnFinishParseListener;

	/**
	 * Http连接工具类对象
	 */
	private HTTPUtils mHttpUtils;

	/**
	 * 解析的订阅地址
	 * 
	 * @param feedUrl
	 *            订阅地址
	 * @param encodingString
	 *            编码方式
	 */
	public void parse(String feedUrl, String encodingString) {
		this.mFeedUrl = feedUrl;
		this.mEncodingString = encodingString;

		getXMLBaseInfo();
	}

	/**
	 * 取消解析XML
	 */
	public void cancelParse() {

		if (mHttpUtils != null) {
			mHttpUtils.Disconnet();
		}

	}

	/**
	 * 完成解析XML监听器
	 */
	public interface OnFinishParseListener {
		public void onFinishParseBaseInfo(boolean result);
	}

	/**
	 * 设置完成解析XML的监听器
	 * 
	 * @param onFinishParseXMLListener
	 *            监听器
	 */
	public void setOnFinishedParseXMLListener(
			OnFinishParseListener onFinishParseXMLListener) {
		this.mOnFinishParseListener = onFinishParseXMLListener;
	}

	/**
	 * 从网络读取XML
	 */
	private void getXMLBaseInfo() {

		mHttpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {// 子线程
				parseXMLBaseInfo(inputStream);
			}

			@Override
			public void onSuccess() {// 主线程
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseBaseInfo(true);
				}

			}

			@Override
			public void onFailure() {// 主线程
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseBaseInfo(false);
				}
			}

		});

		mHttpUtils.Connect(mFeedUrl, 10000, 10000);
	}

	/**
	 * 解析XML输入流基本信息
	 * 
	 * @param inputStream
	 *            XML输入流
	 */
	private void parseXMLBaseInfo(InputStream inputStream) {

		XMLUtils xmlUtils = new XMLUtils();

		
		xmlUtils.setOnParseListener(new OnParseListener() {
			
			@Override
			public void onGetName(XmlPullParser parser, String name) throws XmlPullParserException, IOException {

				//LogUtils.w("FeedXMLParser", name);
				
				// 检查XML类型
				if (TextUtils.isEmpty(mFeedType)) {

					if ("rss".equals(name)) {// rss类型
						mFeedType = "RSS";
					} else if ("feed".equals(name)) {// atom类型
						mFeedType = "ATOM";
					}

				}
				
				// 检查XML标题
				if (TextUtils.isEmpty(mFeedTitle)) {

					if ("title".equals(name)) {// 标题
						mFeedTitle = parser.nextText();
					}

				}

				// 检查XML时间
				if (TextUtils.isEmpty(mFeedTime)) {

					if ("updated".equals(name)) {// ATOM
						mFeedTime = parser.nextText();
					} else if ("pubDate".equals(name)) {// RSS
						mFeedTime = parser.nextText();
					}

				}

				// 检查XML概要
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
				
				// 第一个元素退出解析,不必解析全部
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
