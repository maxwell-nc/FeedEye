package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 订阅的XML的内容信息
 */
@SuppressWarnings("serial")
public class FeedXMLContentInfo implements Serializable{

	/**
	 * 内容标题
	 */
	public String title;
	
	/**
	 * 内容描述
	 */
	public String description;
	
	/**
	 * 内容发布时间
	 */
	public String pubDate;

	/**
	 * 全文连接
	 */
	public String link;

	/**
	 * ATOM的内容类型
	 */
	public String contentType;
	
	/**
	 * ATOM的内容
	 */
	public String content;
}
