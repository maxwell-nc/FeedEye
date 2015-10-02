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
 * 订阅解析器，支持XML格式
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
	public String encodingString;

	/**
	 * RSS类型：item标签数
	 */
	public int mItemCount;

	/**
	 * ATOM类型：entry标签数
	 */
	public int mEntryCount;

	/**
	 * 完成解析XML监听器
	 */
	private OnFinishedParseXMLListener mOnFinishedParseXMLListener;

	/**
	 * 解析的订阅地址
	 * 
	 * @param feedUrl
	 *            订阅地址
	 * @param encodingString
	 *            编码方式
	 */
	public void parseUrl(String feedUrl, String encodingString) {
		this.mFeedUrl = feedUrl;
		this.encodingString = encodingString;

		getXML();
	}

	/**
	 * 完成解析XML监听器
	 */
	public interface OnFinishedParseXMLListener {
		public void onFinishedParseXMLBaseInfo(boolean result);
	}

	/**
	 * 设置完成解析XML的监听器
	 * 
	 * @param onFinishedParseXMLListener
	 *            监听器
	 */
	public void setOnFinishedParseXMLListener(
			OnFinishedParseXMLListener onFinishedParseXMLListener) {
		this.mOnFinishedParseXMLListener = onFinishedParseXMLListener;
	}

	/**
	 * 从网络读取XML
	 */
	private void getXML() {

		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {//子线程
				parseXMLBaseInfo(inputStream);
			}

			@Override
			public void onSuccess() {//主线程
				if (mOnFinishedParseXMLListener != null) {
					mOnFinishedParseXMLListener
							.onFinishedParseXMLBaseInfo(true);
				}

			}

			@Override
			public void onFailure() {//主线程
				if (mOnFinishedParseXMLListener != null) {
					mOnFinishedParseXMLListener
							.onFinishedParseXMLBaseInfo(false);
				}
			}

		});

		httpUtils.Connect(mFeedUrl, 10000, 10000);
	}

	/**
	 * 解析XML输入流基本信息
	 * 
	 * @param inputStream
	 *            XML输入流
	 */
	private void parseXMLBaseInfo(InputStream inputStream) {

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inputStream, encodingString);
			int eventType = parser.getEventType();

			// 统计归零
			mItemCount = 0;
			mEntryCount = 0;

			// 不断解析
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType != XmlPullParser.START_TAG
						|| parser.getName() == null) {
					eventType = parser.next();
					continue;
				}

				// LogUtils.w("FeedXMLParser", parser.getName() );

				// 检查XML类型
				if (TextUtils.isEmpty(mFeedType)) {

					if ("rss".equals(parser.getName())) {// rss类型
						mFeedType = "RSS";
					} else if (parser.getName() == "feed") {// atom类型
						mFeedType = "ATOM";
					}

				}

				// 检查XML标题
				if (TextUtils.isEmpty(mFeedTitle)) {

					if ("title".equals(parser.getName())) {// 标题
						mFeedTitle = parser.nextText();
					}

				}

				// 检查XML时间
				if (TextUtils.isEmpty(mFeedTime)) {

					if ("updated".equals(parser.getName())) {// ATOM
						mFeedTime = parser.nextText();
					}
					if ("pubDate".equals(parser.getName())) {// RSS
						mFeedTime = parser.nextText();
					}

				}

				// 检查XML概要
				if (TextUtils.isEmpty(mFeedSummary)) {

					if ("subtitle".equals(parser.getName())) {// ATOM
						mFeedSummary = parser.nextText();
					}
					if ("description".equals(parser.getName())) {// RSS
						mFeedSummary = parser.nextText();
					}

				}

				// 检查数量
				if ("item".equals(parser.getName())) {// ATOM
					mItemCount++;
				}
				if ("entry".equals(parser.getName())) {// RSS
					mEntryCount++;
				}

				// TODO：图标

				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
