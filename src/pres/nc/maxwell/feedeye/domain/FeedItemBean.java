package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 存储订阅信息的对象
 */
@SuppressWarnings("serial")
public class FeedItemBean implements Serializable {

	/**
	 * 数据库生成的id主键,默认的-1代表新添加
	 */
	private int itemId = -1;

	/**
	 * 订阅URL
	 */
	private String feedURL;

	/**
	 * 图片URL
	 */
	private String picURL;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 预览内容
	 */
	private String previewContent;

	/**
	 * 上次更新时间
	 */
	private Timestamp lastTime;

	/**
	 * 删除标记,不要手动设置 用于同步：如果为"1"则表示本地已删除，但未同步，不要删除记录
	 */
	private String deleteFlag = "0";

	public int getItemId() {
		return itemId;
	}

	public String getFeedURL() {
		return feedURL;
	}

	public String getPicURL() {
		return picURL;
	}

	public String getTitle() {
		return title;
	}

	public String getPreviewContent() {
		return previewContent;
	}

	public Timestamp getLastTime() {
		return lastTime;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setFeedURL(String feedURL) {
		this.feedURL = feedURL;
	}

	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPreviewContent(String previewContent) {
		this.previewContent = previewContent;
	}

	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

}
