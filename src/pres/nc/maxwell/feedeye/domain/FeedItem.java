package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

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
	 * 标题
	 */
	public String title;

	/**
	 * 预览内容
	 */
	public String previewContent;
	
	/**
	 * 编码方式
	 */
	public String encoding;

	/**
	 * 上次更新时间
	 */
	public Timestamp lastTime;

	/**
	 * 删除标记,不要手动设置 用于同步：如果为"1"则表示本地已删除，但未同步，不要删除记录
	 */
	public String deleteFlag = "0";

}
