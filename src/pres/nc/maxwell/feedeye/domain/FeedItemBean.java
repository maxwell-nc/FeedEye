package pres.nc.maxwell.feedeye.domain;

import java.sql.Timestamp;

/**
 * 存储订阅信息的对象
 */
public class FeedItemBean {
	
	/**
	 * 数据库生成的id主键,默认的-1代表新添加
	 */
	private int itemId = -1;
	
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

	
	
	public int getItemId() {
		return itemId;
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
	
	
}
