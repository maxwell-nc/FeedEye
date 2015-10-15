package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * ���ĵ�XML��������Ϣ
 */
@SuppressWarnings("serial")
public class FeedXMLContentInfo implements Serializable{

	/**
	 * ���ݱ���
	 */
	public String title;
	
	/**
	 * ��������
	 */
	public String description;
	
	/**
	 * ���ݷ���ʱ��
	 */
	public String pubDate;

	/**
	 * ȫ������
	 */
	public String link;

	/**
	 * ATOM����������
	 */
	public String contentType;
	
	/**
	 * ATOM������
	 */
	public String content;
}
