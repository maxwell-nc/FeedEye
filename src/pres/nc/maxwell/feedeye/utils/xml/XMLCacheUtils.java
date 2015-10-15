package pres.nc.maxwell.feedeye.utils.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.xmlpull.v1.XmlSerializer;

import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.util.Xml;

/**
 * 控制网络XML数据的缓存
 */
public class XMLCacheUtils {

	/**
	 * 完成读取本地数据监听器
	 * 
	 * @author Forest
	 * 
	 */
	public interface OnFinishGetLocalCacheListener {

		/**
		 * 完成获取本地内容
		 * @param contentInfos 本地内容缓存
		 */
		public void onFinishGetContentInfo(
				ArrayList<FeedXMLContentInfo> contentInfos);
		/**
		 * 完成获取本地基本信息
		 * @param baseInfo 本地基本信息缓存
		 */
		public void onFinishGetBaseInfo(FeedXMLBaseInfo baseInfo);

	}

	/**
	 * 读取本地缓存-基本信息
	 * 
	 * @param feedItem
	 *            传递需要读取的信息
	 * @param listener
	 *            完成获取的监听器
	 * @throws FileNotFoundException
	 *             缓存不存在
	 */
	public static void getLocalCacheBaseInfo(FeedItem feedItem,
			final OnFinishGetLocalCacheListener listener)
			throws FileNotFoundException {

		File file = IOUtils.getFileInSdcard("/FeedEye/DetailCache",
				MD5Utils.getMD5String(feedItem.feedURL));

		final FileInputStream fileInputStream = new FileInputStream(file);

		// 读取信息
		FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new FeedXMLParser.SimpleOnFinishParseXMLListener() {

					@Override
					public void onFinishParseBaseInfo(boolean result,
							FeedXMLBaseInfo baseInfo) {

						if (listener != null) {
							listener.onFinishGetBaseInfo(baseInfo);
						}

						// 关闭流
						IOUtils.closeQuietly(fileInputStream);
					}
				});

		// 解析数据
		feedXMLParser.parse(fileInputStream, feedItem.encoding,
				FeedXMLParser.TYPE_PARSE_BASE_INFO);

	}

	/**
	 * 读取本地缓存-内容信息
	 * 
	 * @param feedItem
	 *            传递需要读取的信息
	 * @param listener
	 *            完成获取的监听器
	 * @throws FileNotFoundException
	 *             缓存不存在
	 */
	public static void getLocalCacheContentInfo(FeedItem feedItem,
			final OnFinishGetLocalCacheListener listener)
			throws FileNotFoundException {

		File file = IOUtils.getFileInSdcard("/FeedEye/DetailCache",
				MD5Utils.getMD5String(feedItem.feedURL));

		final FileInputStream fileInputStream = new FileInputStream(file);

		// 读取信息
		FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new FeedXMLParser.SimpleOnFinishParseXMLListener() {

					@Override
					public void onFinishParseContent(boolean result,
							ArrayList<FeedXMLContentInfo> contentInfos) {

						if (listener != null) {
							listener.onFinishGetContentInfo(contentInfos);
						}

						// 关闭流
						IOUtils.closeQuietly(fileInputStream);

					}

				});

		// 解析数据
		feedXMLParser.parse(fileInputStream, feedItem.encoding,
				FeedXMLParser.TYPE_PARSE_CONTENT);

	}

	/**
	 * 保存本地缓存
	 * 
	 * @param feedItem
	 *            用于传递基本信息等信息
	 * @param contentInfos
	 *            内容数据集合
	 */
	public static void setLocalCache(FeedItem feedItem,
			ArrayList<FeedXMLContentInfo> contentInfos) {

		if (contentInfos.size()<=0) {//防止写入空数据
			return;
		}
		
		XmlSerializer xmlSerializer = Xml.newSerializer();

		File file = IOUtils.getFileInSdcard("/FeedEye/DetailCache",
				MD5Utils.getMD5String(feedItem.feedURL));

		FileOutputStream fileOutputStream = null;

		try {

			fileOutputStream = new FileOutputStream(file);

			xmlSerializer.setOutput(fileOutputStream, "utf-8");
			xmlSerializer.startDocument("utf-8", true);

			// 判断类型
			FeedXMLBaseInfo baseInfo = feedItem.baseInfo;

			if (baseInfo.type.equals(FeedXMLBaseInfo.TYPE_RSS)) {// RSS类型

				// <rss version="2.0">
				xmlSerializer.startTag(null, "rss");
				xmlSerializer.attribute(null, "version", "2.0");

				xmlSerializer.startTag(null, "channel");// <channel>

				// 写基本信息
				xmlSerializer.startTag(null, "title");
				xmlSerializer.text(baseInfo.title);
				xmlSerializer.endTag(null, "title");

				xmlSerializer.startTag(null, "description");
				xmlSerializer.cdsect(baseInfo.summary);// 生成CDATA
				// xmlSerializer.text(contentInfo.description);
				xmlSerializer.endTag(null, "description");

				xmlSerializer.startTag(null, "pubDate");
				xmlSerializer.text(TimeUtils.timestamp2String(baseInfo.time,
						TimeUtils.STANDARD_TIME_PATTERN, Locale.getDefault()));
				xmlSerializer.endTag(null, "pubDate");

				// 写内容
				for (FeedXMLContentInfo contentInfo : contentInfos) {
					xmlSerializer.startTag(null, "item");// <item>

					xmlSerializer.startTag(null, "title");
					xmlSerializer.text(contentInfo.title);
					xmlSerializer.endTag(null, "title");

					xmlSerializer.startTag(null, "description");
					xmlSerializer.cdsect(contentInfo.description);// 生成CDATA
					// xmlSerializer.text(contentInfo.description);
					xmlSerializer.endTag(null, "description");

					xmlSerializer.startTag(null, "pubDate");
					xmlSerializer.text(contentInfo.pubDate);
					xmlSerializer.endTag(null, "pubDate");

					xmlSerializer.endTag(null, "item");// </item>
				}

				xmlSerializer.endTag(null, "channel");// </channel>
				xmlSerializer.endTag(null, "rss");// </rss>
			}

			if (baseInfo.type.equals(FeedXMLBaseInfo.TYPE_ATOM)) {// TODO:ATOM类型
				
				// <feed xmlns="http://www.w3.org/2005/Atom">
				xmlSerializer.startTag(null, "feed");
				xmlSerializer.attribute(null, "xmlns", "http://www.w3.org/2005/Atom");

				// 写基本信息
				xmlSerializer.startTag(null, "title");
				xmlSerializer.text(baseInfo.title);
				xmlSerializer.endTag(null, "title");

				xmlSerializer.startTag(null, "subtitle");
				xmlSerializer.cdsect(baseInfo.summary);// 生成CDATA
				// xmlSerializer.text(contentInfo.description);
				xmlSerializer.endTag(null, "subtitle");

				xmlSerializer.startTag(null, "updated");
				xmlSerializer.text(TimeUtils.timestamp2String(baseInfo.time,
						TimeUtils.STANDARD_TIME_PATTERN, Locale.getDefault()));
				xmlSerializer.endTag(null, "updated");

				// 写内容
				for (FeedXMLContentInfo contentInfo : contentInfos) {
					xmlSerializer.startTag(null, "entry");// <entry>

					xmlSerializer.startTag(null, "title");
					xmlSerializer.text(contentInfo.title);
					xmlSerializer.endTag(null, "title");

					xmlSerializer.startTag(null, "summary");
					xmlSerializer.cdsect(contentInfo.description);// 生成CDATA
					// xmlSerializer.text(contentInfo.description);
					xmlSerializer.endTag(null, "summary");

					xmlSerializer.startTag(null, "updated");
					xmlSerializer.text(contentInfo.pubDate);
					xmlSerializer.endTag(null, "updated");

					xmlSerializer.endTag(null, "entry");// </entry>
				}

				xmlSerializer.endTag(null, "feed");// </feed>

			}

			xmlSerializer.endDocument();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 关闭流
		IOUtils.closeQuietly(fileOutputStream);

	}

}
