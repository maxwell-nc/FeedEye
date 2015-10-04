package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * ���ĵ�XML�Ļ�����Ϣ
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfoBean implements Serializable {

	/**
	 * ��������
	 */
	private String feedType;

	/**
	 * ���ı���
	 */
	private String feedTitle;

	/**
	 * ����ʱ��
	 */
	private String feedTime;

	/**
	 * ���ĸ�Ҫ
	 */
	private String feedSummary;

	
	public String getFeedType() {
		return feedType;
	}

	public String getFeedTitle() {
		return feedTitle;
	}

	public String getFeedTime() {
		return feedTime;
	}

	public String getFeedSummary() {
		return feedSummary;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}

	public void setFeedTime(String feedTime) {
		this.feedTime = feedTime;
	}

	public void setFeedSummary(String feedSummary) {
		this.feedSummary = feedSummary;
	}
}
