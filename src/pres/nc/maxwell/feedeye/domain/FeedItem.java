package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * �洢������Ϣ�Ķ���
 */
@SuppressWarnings("serial")
public class FeedItem implements Serializable {

	/**
	 * ���ݿ����ɵ�id����,Ĭ�ϵ�-1���������
	 */
	public int itemId = -1;

	/**
	 * ����URL
	 */
	public String feedURL;

	/**
	 * ͼƬURL
	 */
	public String picURL;
	
	/**
	 * ���뷽ʽ
	 */
	public String encoding;

	/**
	 * ������Ϣ������⡢����ʱ���
	 */
	public FeedXMLBaseInfo baseInfo = new FeedXMLBaseInfo();

	/**
	 * ɾ�����,��Ҫ�ֶ����� ����ͬ�������Ϊ"1"���ʾ������ɾ������δͬ������Ҫɾ����¼
	 */
	public String deleteFlag = "0";

}
