package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 存储收藏信息的对象
 */
@SuppressWarnings("serial")
public class FavorItem implements Serializable {

	/**
	 * 数据库生成的id主键,默认的-1代表新添加
	 */
	public int itemId = -1;

	/**
	 * 源订阅的名字
	 */
	public String feedSourceName;

	/**
	 * 源订阅URL
	 */
	public String feedURL;

	/**
	 * 详细信息内容
	 */
	public FeedXMLContentInfo contentInfo = new FeedXMLContentInfo();

	/**
	 * 预览图片地址1
	 */
	public String picLink1;

	/**
	 * 预览图片地址2
	 */
	public String picLink2;

	/**
	 * 预览图片地址3
	 */
	public String picLink3;

	/**
	 * 摘要正文
	 */
	public String summary;

	/**
	 * 删除标记,不要手动设置 用于同步：如果为"1"则表示本地已删除，但未同步，不要删除记录
	 */
	public String deleteFlag = "0";
}
