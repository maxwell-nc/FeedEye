package pres.nc.maxwell.feedeye.utils.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * XML解析的工具类
 */
public class XMLParseUtils {

	/**
	 * 解析监听器
	 */
	public interface OnParseListener {
		/**
		 * 是否只解析开始标签
		 * 
		 * @return 返回真表示只解析开始标签
		 */
		public boolean isOnlyParseStartTag();

		/**
		 * 解析标签名
		 * 
		 * @param parser
		 *            解析器
		 * @param name
		 *            标签名
		 * @param eventType
		 *            解析到的类型
		 */
		public void onGetName(XmlPullParser parser, String name, int eventType)
				throws XmlPullParserException, IOException;

		/**
		 * 是否中断解析
		 * 
		 * @param parser
		 *            解析器
		 * @return 如果返回真表示不需要再解析，解析停止
		 */
		public boolean isInterruptParse(XmlPullParser parser);
	}

	/**
	 * 监听器
	 */
	private OnParseListener onParseListener;

	/**
	 * 设置解析监听器
	 * 
	 * @param listener
	 *            监听器
	 */
	public void setOnParseListener(OnParseListener listener) {
		this.onParseListener = listener;
	}

	/**
	 * 解析网络流XML
	 * 
	 * @param inputStream
	 *            网络流
	 * @param encodingString
	 *            编码方式
	 */
	public void parseStream(InputStream inputStream, String encodingString) {

		XmlPullParser parser = Xml.newPullParser();

		try {
			parser.setInput(inputStream, encodingString);
			int eventType = parser.getEventType();

			// 不断解析
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (onParseListener != null) {

					if (onParseListener.isOnlyParseStartTag()) {// 直接解析开始标签

						if (eventType != XmlPullParser.START_TAG
								|| parser.getName() == null) {
							eventType = parser.next();
							continue;
						}

					}

					onParseListener.onGetName(parser, parser.getName(),
							eventType);// 解析标签名

					if (onParseListener.isInterruptParse(parser)) {// 跳出
						break;
					}

				} else {// 不设置监听器无意义
					break;
				}

				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
