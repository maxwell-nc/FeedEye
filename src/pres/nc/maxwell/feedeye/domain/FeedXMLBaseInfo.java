package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 订阅的XML的基本信息
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfo implements Serializable {

	/**
	 * 订阅类型
	 */
	public String feedType;

	/**
	 * 订阅标题
	 */
	public String feedTitle;

	/**
	 * 订阅时间
	 */
	public String feedTime;

	/**
	 * 订阅概要
	 */
	public String feedSummary;

}
