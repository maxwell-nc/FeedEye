package pres.nc.maxwell.feedeye.utils.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.xmlpull.v1.XmlSerializer;

import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.util.Xml;

/**
 * 控制网络XML数据的缓存
 */
public class XMLCacheUtils {

	/**
	 * 保存本地缓存
	 * @param feedItem 用于传递基本信息等信息
	 * @param contentInfos 内容数据集合
	 */
	public static void SaveLocalCache(FeedItem feedItem,
			ArrayList<FeedXMLContentInfo> contentInfos) {

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

			if (baseInfo.type == FeedXMLBaseInfo.TYPE_ATOM) {// TODO:ATOM类型
				// 写基本信息

				// 写内容

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
