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
 * ��������XML���ݵĻ���
 */
public class XMLCacheUtils {

	/**
	 * ��ɶ�ȡ�������ݼ�����
	 * 
	 * @author Forest
	 * 
	 */
	public interface OnFinishGetLocalCacheListener {

		/**
		 * ��ɻ�ȡ��������
		 * @param contentInfos �������ݻ���
		 */
		public void onFinishGetContentInfo(
				ArrayList<FeedXMLContentInfo> contentInfos);
		/**
		 * ��ɻ�ȡ���ػ�����Ϣ
		 * @param baseInfo ���ػ�����Ϣ����
		 */
		public void onFinishGetBaseInfo(FeedXMLBaseInfo baseInfo);

	}

	/**
	 * ��ȡ���ػ���-������Ϣ
	 * 
	 * @param feedItem
	 *            ������Ҫ��ȡ����Ϣ
	 * @param listener
	 *            ��ɻ�ȡ�ļ�����
	 * @throws FileNotFoundException
	 *             ���治����
	 */
	public static void getLocalCacheBaseInfo(FeedItem feedItem,
			final OnFinishGetLocalCacheListener listener)
			throws FileNotFoundException {

		File file = IOUtils.getFileInSdcard("/FeedEye/DetailCache",
				MD5Utils.getMD5String(feedItem.feedURL));

		final FileInputStream fileInputStream = new FileInputStream(file);

		// ��ȡ��Ϣ
		FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new FeedXMLParser.SimpleOnFinishParseXMLListener() {

					@Override
					public void onFinishParseBaseInfo(boolean result,
							FeedXMLBaseInfo baseInfo) {

						if (listener != null) {
							listener.onFinishGetBaseInfo(baseInfo);
						}

						// �ر���
						IOUtils.closeQuietly(fileInputStream);
					}
				});

		// ��������
		feedXMLParser.parse(fileInputStream, feedItem.encoding,
				FeedXMLParser.TYPE_PARSE_BASE_INFO);

	}

	/**
	 * ��ȡ���ػ���-������Ϣ
	 * 
	 * @param feedItem
	 *            ������Ҫ��ȡ����Ϣ
	 * @param listener
	 *            ��ɻ�ȡ�ļ�����
	 * @throws FileNotFoundException
	 *             ���治����
	 */
	public static void getLocalCacheContentInfo(FeedItem feedItem,
			final OnFinishGetLocalCacheListener listener)
			throws FileNotFoundException {

		File file = IOUtils.getFileInSdcard("/FeedEye/DetailCache",
				MD5Utils.getMD5String(feedItem.feedURL));

		final FileInputStream fileInputStream = new FileInputStream(file);

		// ��ȡ��Ϣ
		FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new FeedXMLParser.SimpleOnFinishParseXMLListener() {

					@Override
					public void onFinishParseContent(boolean result,
							ArrayList<FeedXMLContentInfo> contentInfos) {

						if (listener != null) {
							listener.onFinishGetContentInfo(contentInfos);
						}

						// �ر���
						IOUtils.closeQuietly(fileInputStream);

					}

				});

		// ��������
		feedXMLParser.parse(fileInputStream, feedItem.encoding,
				FeedXMLParser.TYPE_PARSE_CONTENT);

	}

	/**
	 * ���汾�ػ���
	 * 
	 * @param feedItem
	 *            ���ڴ��ݻ�����Ϣ����Ϣ
	 * @param contentInfos
	 *            �������ݼ���
	 */
	public static void setLocalCache(FeedItem feedItem,
			ArrayList<FeedXMLContentInfo> contentInfos) {

		if (contentInfos.size()<=0) {//��ֹд�������
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

			// �ж�����
			FeedXMLBaseInfo baseInfo = feedItem.baseInfo;

			if (baseInfo.type.equals(FeedXMLBaseInfo.TYPE_RSS)) {// RSS����

				// <rss version="2.0">
				xmlSerializer.startTag(null, "rss");
				xmlSerializer.attribute(null, "version", "2.0");

				xmlSerializer.startTag(null, "channel");// <channel>

				// д������Ϣ
				xmlSerializer.startTag(null, "title");
				xmlSerializer.text(baseInfo.title);
				xmlSerializer.endTag(null, "title");

				xmlSerializer.startTag(null, "description");
				xmlSerializer.cdsect(baseInfo.summary);// ����CDATA
				// xmlSerializer.text(contentInfo.description);
				xmlSerializer.endTag(null, "description");

				xmlSerializer.startTag(null, "pubDate");
				xmlSerializer.text(TimeUtils.timestamp2String(baseInfo.time,
						TimeUtils.STANDARD_TIME_PATTERN, Locale.getDefault()));
				xmlSerializer.endTag(null, "pubDate");

				// д����
				for (FeedXMLContentInfo contentInfo : contentInfos) {
					xmlSerializer.startTag(null, "item");// <item>

					xmlSerializer.startTag(null, "title");
					xmlSerializer.text(contentInfo.title);
					xmlSerializer.endTag(null, "title");

					xmlSerializer.startTag(null, "description");
					xmlSerializer.cdsect(contentInfo.description);// ����CDATA
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

			if (baseInfo.type.equals(FeedXMLBaseInfo.TYPE_ATOM)) {// TODO:ATOM����
				
				// <feed xmlns="http://www.w3.org/2005/Atom">
				xmlSerializer.startTag(null, "feed");
				xmlSerializer.attribute(null, "xmlns", "http://www.w3.org/2005/Atom");

				// д������Ϣ
				xmlSerializer.startTag(null, "title");
				xmlSerializer.text(baseInfo.title);
				xmlSerializer.endTag(null, "title");

				xmlSerializer.startTag(null, "subtitle");
				xmlSerializer.cdsect(baseInfo.summary);// ����CDATA
				// xmlSerializer.text(contentInfo.description);
				xmlSerializer.endTag(null, "subtitle");

				xmlSerializer.startTag(null, "updated");
				xmlSerializer.text(TimeUtils.timestamp2String(baseInfo.time,
						TimeUtils.STANDARD_TIME_PATTERN, Locale.getDefault()));
				xmlSerializer.endTag(null, "updated");

				// д����
				for (FeedXMLContentInfo contentInfo : contentInfos) {
					xmlSerializer.startTag(null, "entry");// <entry>

					xmlSerializer.startTag(null, "title");
					xmlSerializer.text(contentInfo.title);
					xmlSerializer.endTag(null, "title");

					xmlSerializer.startTag(null, "summary");
					xmlSerializer.cdsect(contentInfo.description);// ����CDATA
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

		// �ر���
		IOUtils.closeQuietly(fileOutputStream);

	}

}
