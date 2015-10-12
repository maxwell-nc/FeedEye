package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 订阅的XML的基本信息
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfo implements Serializable {

	/**
	 * 订阅类型
	 */
	public String type;

	/**
	 * 订阅标题
	 */
	public String title;

	/**
	 * 订阅时间
	 */
	public Timestamp time;

	/**
	 * 订阅概要
	 */
	public String summary;

}
