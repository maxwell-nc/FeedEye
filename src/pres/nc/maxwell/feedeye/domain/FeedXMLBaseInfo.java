package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * ���ĵ�XML�Ļ�����Ϣ
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfo implements Serializable {

	/**
	 * ��������
	 */
	public String feedType;

	/**
	 * ���ı���
	 */
	public String feedTitle;

	/**
	 * ����ʱ��
	 */
	public String feedTime;

	/**
	 * ���ĸ�Ҫ
	 */
	public String feedSummary;

}
