package pres.nc.maxwell.feedeye.engine;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;

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
	private String mEncodingString;

	/**
	 * 基本信息集合
	 */
	private FeedXMLBaseInfo mBaseInfo;

	/**
	 * 内容信息集合
	 */
	private ArrayList<FeedXMLContentInfo> mContentInfoList;

	/**
	 * Http连接工具类对象
	 */
	private HTTPUtils mHttpUtils;

	/**
	 * 完成解析XML监听器
	 */
	private OnFinishParseXMLListener mOnFinishParseListener;

	/**
	 * 完成解析XML监听器
	 * 
	 * @see SimpleOnFinishParseXMLListener
	 */
	public interface OnFinishParseXMLListener {
		public void onFinishParseBaseInfo(boolean result,
				FeedXMLBaseInfo baseInfo);
		public void onFinishParseContent(boolean result,
				ArrayList<FeedXMLContentInfo> contentInfos);
	}

	/**
	 * 提供给只需要解析基本信息或者详细的默认实现
	 * 
	 * @see OnFinishParseXMLListener
	 */
	public static class SimpleOnFinishParseXMLListener
			implements
				OnFinishParseXMLListener {

		@Override
		public void onFinishParseBaseInfo(boolean result,
				FeedXMLBaseInfo baseInfo) {

		}

		@Override
		public void onFinishParseContent(boolean result,
				ArrayList<FeedXMLContentInfo> contentInfos) {

		}

	}

	/**
	 * 设置完成解析XML的监听器
	 * 
	 * @param onFinishParseXMLListener
	 *            监听器
	 */
	public void setOnFinishedParseXMLListener(
			OnFinishParseXMLListener onFinishParseXMLListener) {
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
	 * 解析本地XML流
	 * 
	 * @param localStream
	 *            本地XML流
	 * @param encodingString
	 *            编码方式
	 * @param parseType
	 *            解析类型 ，可选：{@link FeedXMLParser#TYPE_PARSE_BASE_INFO}或者
	 *            {@link FeedXMLParser#TYPE_PARSE_CONTENT}
	 */
	public void parse(InputStream localStream, String encodingString,
			int parseType) {

		this.mEncodingString = encodingString;

		// 判断解析类型
		if (parseType == TYPE_PARSE_BASE_INFO) {

			this.mBaseInfo = new FeedXMLBaseInfo();
			getXMLBaseInfo(localStream);

		} else {

			this.mContentInfoList = new ArrayList<FeedXMLContentInfo>();
			getXMLContentInfo(localStream);

		}

	}

	/**
	 * 取消解析XML
	 */
	public void cancelParse() {

		if (mHttpUtils != null) {
			mHttpUtils.disconnet();
		}

	}

	/**
	 * 本地解析基本信息任务
	 */
	class getLocalBaseInfoTask extends AsyncTask<InputStream, Void, Void> {

		@Override
		protected Void doInBackground(InputStream... params) {// 子线程
			parseXMLBaseInfo(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {// 主线程

			if (mOnFinishParseListener != null) {
				mOnFinishParseListener.onFinishParseBaseInfo(true, mBaseInfo);
			}

		}

	}

	/**
	 * 从本地读取XML的内容并解析
	 * 
	 * @param localStream
	 */
	private void getXMLBaseInfo(InputStream localStream) {
		new getLocalBaseInfoTask().execute(localStream);
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
					mOnFinishParseListener.onFinishParseBaseInfo(true,
							mBaseInfo);
				}

			}

			@Override
			public void onFailure() {// 主线程
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseBaseInfo(false,
							mBaseInfo);
				}
			}

		});

		mHttpUtils.connect(mFeedUrl, 10000, 10000,
				Executors.newSingleThreadExecutor());
	}

	/**
	 * 解析XML输入流基本信息
	 * 
	 * @param inputStream
	 *            XML输入流
	 */
	private void parseXMLBaseInfo(InputStream inputStream) {

		XMLParseUtils xmlUtils = new XMLParseUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			@Override
			public void onGetName(XmlPullParser parser, String name,
					int eventType) throws XmlPullParserException, IOException {

				// LogUtils.w("FeedXMLParser", name);

				// 检查XML类型
				if (TextUtils.isEmpty(mBaseInfo.type)) {

					if ("rss".equals(name)) {// rss类型
						mBaseInfo.type = FeedXMLBaseInfo.TYPE_RSS;
					} else if ("feed".equals(name)) {// atom类型
						mBaseInfo.type = FeedXMLBaseInfo.TYPE_ATOM;
					}

				}

				// 检查XML标题
				if (TextUtils.isEmpty(mBaseInfo.title)) {

					if ("title".equals(name)) {// 标题
						mBaseInfo.title = parser.nextText();
					}

				}

				// 检查XML时间
				if (mBaseInfo.time == null) {

					if ("updated".equals(name)) {// ATOM

						String timeString = TimeUtils.LoopToTransTime(parser
								.nextText());

						mBaseInfo.time = TimeUtils.string2Timestamp(timeString);

					} else if ("pubDate".equals(name)
							|| "lastBuildDate".equals(name)) {// RSS

						String timeString = TimeUtils.LoopToTransTime(parser
								.nextText());

						mBaseInfo.time = TimeUtils.string2Timestamp(timeString);
					}

				}

				// 检查XML概要
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

				// 第一个元素退出解析,不必解析全部
				if ("item".equals(parser.getName())) {// RSS
					return true;
				}
				if ("entry".equals(parser.getName())) {// ATOM
					return true;
				}

				return false;
			}

			@Override
			public void doWhenFinishedParse() {

				// 处理数据为空的情况

				// 解析不到类型
				if (TextUtils.isEmpty(mBaseInfo.type)) {
					mBaseInfo.type = FeedXMLBaseInfo.TYPE_UNKNOWN;
				}

				// 无网络数据
				if (TextUtils.isEmpty(mBaseInfo.title)) {

					mBaseInfo.title = "无标题";
				}

				// 无预览内容
				if (TextUtils.isEmpty(mBaseInfo.summary)) {
					mBaseInfo.summary = "没有接收到数据";
				}

				// 无获取到时间，设置为当前时间
				if (mBaseInfo.time == null) {
					mBaseInfo.time = new Timestamp(System.currentTimeMillis());
				}

			}

		});

		xmlUtils.parseStream(inputStream, mEncodingString);

	}

	/**
	 * 本地解析内容任务
	 */
	class getLocalContentInfoTask extends AsyncTask<InputStream, Void, Void> {

		@Override
		protected Void doInBackground(InputStream... params) {// 子线程
			parseXMLContent(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {// 主线程

			if (mOnFinishParseListener != null) {
				mOnFinishParseListener.onFinishParseContent(true,
						mContentInfoList);
			}

		}

	}

	/**
	 * 从本地读取XML的内容并解析
	 * 
	 * @param localStream
	 */
	private void getXMLContentInfo(InputStream localStream) {
		new getLocalContentInfoTask().execute(localStream);
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
					mOnFinishParseListener.onFinishParseContent(true,
							mContentInfoList);
				}

			}

			@Override
			public void onFailure() {// 主线程
				if (mOnFinishParseListener != null) {
					mOnFinishParseListener.onFinishParseContent(false,
							mContentInfoList);
				}
			}
		});

		mHttpUtils.connect(mFeedUrl, 15000, 15000,
				Executors.newSingleThreadExecutor());
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

		XMLParseUtils xmlUtils = new XMLParseUtils();

		xmlUtils.setOnParseListener(new OnParseListener() {

			boolean startRssItemFlag = false;
			boolean startAtomEntryFlag = false;
			FeedXMLContentInfo contentInfo = null;

			@Override
			public void onGetName(XmlPullParser parser, String name,
					int eventType) throws XmlPullParserException, IOException {

				// ----------------RSS---------------

				if ("item".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS内容开始
					contentInfo = new FeedXMLContentInfo();
					startRssItemFlag = true;
				}

				if (startRssItemFlag == true
						&& "title".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS内容标题
					contentInfo.title = parser.nextText();
				}

				if (startRssItemFlag == true
						&& "description".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS内容描述
					contentInfo.description = parser.nextText();
				}

				if (startRssItemFlag == true
						&& "pubDate".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS内容时间
					contentInfo.pubDate = parser.nextText();
				}

				if (startRssItemFlag == true && "link".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// RSS连接
					contentInfo.link = parser.nextText();
				}

				if (startRssItemFlag == true && "item".equals(parser.getName())
						&& eventType == XmlPullParser.END_TAG) {// RSS内容结束
					mContentInfoList.add(contentInfo);
					startRssItemFlag = false;
				}

				// ----------------ATOM---------------

				if ("entry".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// ATOM内容开始
					contentInfo = new FeedXMLContentInfo();
					startAtomEntryFlag = true;
				}

				if (startAtomEntryFlag == true
						&& "title".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// ATOM内容标题
					contentInfo.title = parser.nextText();
				}

				if (startAtomEntryFlag == true
						&& "summary".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// ATOM内容描述
					contentInfo.description = parser.nextText();
				}

				if (startAtomEntryFlag == true
						&& "updated".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// ATOM内容时间
					contentInfo.pubDate = parser.nextText();
				}

				if (startAtomEntryFlag == true
						&& "link".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// ATOM连接

					if (TextUtils.isEmpty(contentInfo.link)
							|| "alternate".equals(parser.getAttributeValue(
									null, "rel"))) {
						contentInfo.link = parser.getAttributeValue(null,
								"href");
					}

				}

				if (startAtomEntryFlag == true
						&& "content".equals(parser.getName())
						&& eventType == XmlPullParser.START_TAG) {// ATOM内容
					contentInfo.contentType = parser.getAttributeValue(null,
							"type");
					contentInfo.content = parser.nextText();
				}

				if (startAtomEntryFlag == true
						&& "entry".equals(parser.getName())
						&& eventType == XmlPullParser.END_TAG) {// RSS内容结束
					mContentInfoList.add(contentInfo);
					startAtomEntryFlag = false;
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

			@Override
			public void doWhenFinishedParse() {

				// 处理数据为空的情况
				for (FeedXMLContentInfo info : mContentInfoList) {

					// 无标题
					if (TextUtils.isEmpty(info.title)) {
						info.title = "无标题";
					}

					// 无描述
					if (TextUtils.isEmpty(info.description)) {

						if (info.content != null) {// 利用content代替
							info.description = info.content;
						} else {
							info.description = "没有接收到数据";
						}

					}

					// 无获取到时间，设置为当前时间
					if (info.pubDate == null) {

						info.pubDate = TimeUtils.timestamp2String(
								new Timestamp(System.currentTimeMillis()),
								TimeUtils.STANDARD_TIME_PATTERN,
								Locale.getDefault());

					}

					// 无全文链接
					if (info.link == null) {
						info.link = "";
					}

					// 无ATOM内容类型
					if (info.contentType == null) {
						info.contentType = "";
					}

					// 无ATOM内容
					if (info.content == null) {
						info.content = "";
					}

				}

			}

		});

		xmlUtils.parseStream(inputStream, mEncodingString);

	}

}
