package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * ���ĵ�XML��������Ϣ
 */
@SuppressWarnings("serial")
public class FeedXMLContentInfo implements Serializable{


	/**
	 * ��Ϣ����
	 */
	private int contentCount;

	public int getContentCount() {
		return contentCount;
	}

	public void setContentCount(int contentCount) {
		this.contentCount = contentCount;
	}
	
}
