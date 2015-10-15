package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 订阅的XML的基本信息
 */
@SuppressWarnings("serial")
public class FeedXMLBaseInfo implements Serializable {

	/**
	 * @see #type ATOM的XML格式
	 */
	public static final String TYPE_ATOM = "ATOM";

	/**
	 * @see #type RSS的XML格式
	 */
	public static final String TYPE_RSS = "RSS";

	/**
	 * @see #type 位置类型
	 */
	public  static final String TYPE_UNKNOWN = "UNKNOWN";
	
	/**
	 * 订阅类型
	 */
	public String type;

	/**
	 * 订阅标题
	 */
	public String title;

	/**
	 * 订阅概要
	 */
	public String summary;

	/**
	 * 订阅时间
	 */
	public Timestamp time;

}
