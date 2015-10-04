package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 订阅的XML的基本信息
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfoBean implements Serializable {

	/**
	 * 订阅类型
	 */
	private String feedType;

	/**
	 * 订阅标题
	 */
	private String feedTitle;

	/**
	 * 订阅时间
	 */
	private String feedTime;

	/**
	 * 订阅概要
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
