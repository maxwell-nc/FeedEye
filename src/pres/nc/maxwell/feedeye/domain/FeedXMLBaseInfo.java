package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * ���ĵ�XML�Ļ�����Ϣ
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfo implements Serializable {

	/**
	 * ��������
	 */
	public String type;

	/**
	 * ���ı���
	 */
	public String title;

	/**
	 * ����ʱ��
	 */
	public Timestamp time;

	/**
	 * ���ĸ�Ҫ
	 */
	public String summary;

}
