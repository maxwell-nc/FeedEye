package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

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
	 * ����
	 */
	public String title;

	/**
	 * Ԥ������
	 */
	public String previewContent;
	
	/**
	 * ���뷽ʽ
	 */
	public String encoding;

	/**
	 * �ϴθ���ʱ��
	 */
	public Timestamp lastTime;

	/**
	 * ɾ�����,��Ҫ�ֶ����� ����ͬ�������Ϊ"1"���ʾ������ɾ������δͬ������Ҫɾ����¼
	 */
	public String deleteFlag = "0";

}
