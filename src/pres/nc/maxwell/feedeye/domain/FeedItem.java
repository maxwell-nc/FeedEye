package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 存储订阅信息的对象
 */
@SuppressWarnings("serial")
public class FeedItem implements Serializable {

	/**
	 * 数据库生成的id主键,默认的-1代表新添加
	 */
	public int itemId = -1;

	/**
	 * 订阅URL
	 */
	public String feedURL;

	/**
	 * 图片URL
	 */
	public String picURL;
	
	/**
	 * 编码方式
	 */
	public String encoding;

	/**
	 * 基本信息，如标题、更新时间等
	 */
	public FeedXMLBaseInfo baseInfo = new FeedXMLBaseInfo();

	/**
	 * 删除标记,不要手动设置 用于同步：如果为"1"则表示本地已删除，但未同步，不要删除记录
	 */
	public String deleteFlag = "0";

}
