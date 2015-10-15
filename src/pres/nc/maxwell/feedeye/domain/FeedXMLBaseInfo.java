package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * ���ĵ�XML�Ļ�����Ϣ
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfo implements Serializable {

	/**
	 * @see #type ATOM��XML��ʽ
	 */
	public static final String TYPE_ATOM = "ATOM";

	/**
	 * @see #type RSS��XML��ʽ
	 */
	public static final String TYPE_RSS = "RSS";

	/**
	 * @see #type λ������
	 */
	public  static final String TYPE_UNKNOWN = "UNKNOWN";
	
	/**
	 * ��������
	 */
	public String type;

	/**
	 * ���ı���
	 */
	public String title;

	/**
	 * ���ĸ�Ҫ
	 */
	public String summary;

	/**
	 * ����ʱ��
	 */
	public Timestamp time;

}
