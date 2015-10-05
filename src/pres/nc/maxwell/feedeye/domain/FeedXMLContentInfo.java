package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 订阅的XML的内容信息
 */
@SuppressWarnings("serial")
public class FeedXMLContentInfo implements Serializable{


	/**
	 * 信息数量
	 */
	private int contentCount;

	public int getContentCount() {
		return contentCount;
	}

	public void setContentCount(int contentCount) {
		this.contentCount = contentCount;
	}
	
}
