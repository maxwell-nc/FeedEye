package pres.nc.maxwell.feedeye.utils.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * XML�����Ĺ�����
 */
public class XMLParseUtils {

	/**
	 * ����������
	 */
	public interface OnParseListener {
		/**
		 * �Ƿ�ֻ������ʼ��ǩ
		 * 
		 * @return �������ʾֻ������ʼ��ǩ
		 */
		public boolean isOnlyParseStartTag();

		/**
		 * ������ǩ��
		 * 
		 * @param parser
		 *            ������
		 * @param name
		 *            ��ǩ��
		 * @param eventType
		 *            ������������
		 */
		public void onGetName(XmlPullParser parser, String name, int eventType)
				throws XmlPullParserException, IOException;

		/**
		 * �Ƿ��жϽ���
		 * 
		 * @param parser
		 *            ������
		 * @return ����������ʾ����Ҫ�ٽ���������ֹͣ
		 */
		public boolean isInterruptParse(XmlPullParser parser);
	}

	/**
	 * ������
	 */
	private OnParseListener onParseListener;

	/**
	 * ���ý���������
	 * 
	 * @param listener
	 *            ������
	 */
	public void setOnParseListener(OnParseListener listener) {
		this.onParseListener = listener;
	}

	/**
	 * ����������XML
	 * 
	 * @param inputStream
	 *            ������
	 * @param encodingString
	 *            ���뷽ʽ
	 */
	public void parseStream(InputStream inputStream, String encodingString) {

		XmlPullParser parser = Xml.newPullParser();

		try {
			parser.setInput(inputStream, encodingString);
			int eventType = parser.getEventType();

			// ���Ͻ���
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (onParseListener != null) {

					if (onParseListener.isOnlyParseStartTag()) {// ֱ�ӽ�����ʼ��ǩ

						if (eventType != XmlPullParser.START_TAG
								|| parser.getName() == null) {
							eventType = parser.next();
							continue;
						}

					}

					onParseListener.onGetName(parser, parser.getName(),
							eventType);// ������ǩ��

					if (onParseListener.isInterruptParse(parser)) {// ����
						break;
					}

				} else {// �����ü�����������
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
