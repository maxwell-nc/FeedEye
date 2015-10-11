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
	 * 编码方式
	 */
	public String mEncodingString;

	/**
	 * 基本信息集合
	 */
	public FeedXMLBaseInfo mBaseInfo;

	/**
	 * 内容信息集合
	 */
	public ArrayList<FeedXMLContentInfo> mContentInfoList;

	/**
	 * Http连接工具类对象
	 */
	private HTTPUtils mHttpUtils;

	/**
	 * 完成解析XML监听器
	 */
	private OnFinishParseListener mOnFinishParseListener;

	/**
	 * 完成解析XML监听器
	 */
	public interface OnFinishParseListener {
		public void onFinishParseBaseInfo(boolean result);
		public void onFinishParseContent(boolean result);
	}

	/**
	 * 默认的XML解析完成监听器，什么都不做
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
	 * 只解析基本信息
	 * 
	 * @see #parse(String, String, int)
	 */
	public static final int TYPE_PARSE_BASE_INFO = 1;

	/**
	 * 解析内容
	 * 
	 * @see #parse(String, String, int)
	 */
	public static final int TYPE_PARSE_CONTENT = 2;

	/**
	 * 解析的订阅地址
	 * 
	 * @param feedUrl
	 *            订阅地址
	 * @param encodingString
	 *            编码方式
	 * @param parseType
	 *            解析类型，可选：{@link FeedXMLParser#TYPE_PARSE_BASE_INFO}或者
	 *            {@link FeedXMLParser#TYPE_PARSE_CONTENT}
	 */
	public void parse(String feedUrl, String encodingString, int parseType) {

		this.mFeedUrl = feedUrl;
		this.mEncodingString = encodingString;

		// 判断解析类型
		if (parseType == TYPE_PARSE_BASE_INFO) {

			this.mBaseInfo = new FeedXMLBaseInfo();

			getXMLBaseInfo();

		} else {

			this.mContentInfoList = new ArrayList<FeedXMLContentInfo>();

			getXMLContentInfo();
		}

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
	 * 从网络读取XML的基本信息并解析
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
			public void onGetName(XmlPullParser parser, String name,
					int eventType) throws XmlPullParserException, IOException {

				// LogUtils.w("FeedXMLParser", name);

				// 检查XML类型
				if (TextUtils.isEmpty(mBaseInfo.feedType)) {

					if ("rss".equals(name)) {// rss类型
						mBaseInfo.feedType = "RSS";
					} else if ("feed".equals(name)) {// atom类型
						mBaseInfo.feedType = "ATOM";
					}

				}

				// 检查XML标题
				if (TextUtils.isEmpty(mBaseInfo.feedTitle)) {

					if ("title".equals(name)) {// 标题
						mBaseInfo.feedTitle = parser.nextText();
					}

				}

				// 检查XML时间
				if (TextUtils.isEmpty(mBaseInfo.feedTime)) {

					if ("updated".equals(name)) {// ATOM
						mBaseInfo.feedTime = parser.nextText();
					} else if ("pubDate".equals(name)) {// RSS
						mBaseInfo.feedTime = parser.nextText();
					}

				}

				// 检查XML概要
				if (TextUtils.isEmpty(mBaseInfo.feedSummary)) {

					if ("subtitle".equals(name)) {// ATOM
						mBaseInfo.feedSummary = parser.nextText();
					} else if ("description".equals(name)) {// RSS
						mBaseInfo.feedSummary = parser.nextText();
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

	/**
	 * 从网络读取XML的内容并解析
	 */
	private void getXMLContentInfo() {

		mHttpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {// 子线程
				parseXMLContent(inputStream);

			}

			@Override
			public void onSuccess() {// 主线程
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseContent(true);
				}

			}

			@Override
			public void onFailure() {// 主线程
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseContent(false);
				}
			}
		});

		mHttpUtils.Connect(mFeedUrl, 15000, 15000);
	}

	/**
	 * 解析XML输入流内容
	 * 
	 * @param inputStream
	 *            XML输入流
	 */
	private void parseXMLContent(InputStream inputStream) {

		// 计数清零
		mContentInfoList.clear();

		XMLUtils xmlUtils = new XMLUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			boolean startRssItemFlag = false;
			FeedXMLContentInfo contentInfo = null;
			
			@Override
			public void onGetName(XmlPullParser parser, String name,
					int eventType) throws XmlPullParserException, IOException {

				//----------------RSS---------------
				
				if ("item".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS内容开始
					contentInfo = new FeedXMLContentInfo();
					startRssItemFlag = true;
				}
				
				if (startRssItemFlag == true && "title".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS内容标题
					contentInfo.title = parser.nextText();
				}
				
				if (startRssItemFlag == true && "description".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS内容描述
					contentInfo.description = parser.nextText();
				}

				if (startRssItemFlag == true && "item".equals(parser.getName())
						&& eventType == XmlPullParser.END_TAG) {// RSS内容结束
					mContentInfoList.add(contentInfo);
					startRssItemFlag = false;
				}
				
				
				//----------------ATOM---------------
				
				if ("entry".equals(parser.getName())) {// ATOM
					
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
