package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * �洢������Ϣ�Ķ���
 */
@SuppressWarnings("serial")
public class FeedItemBean implements Serializable {

	/**
	 * ���ݿ����ɵ�id����,Ĭ�ϵ�-1���������
	 */
	private int itemId = -1;

	/**
	 * ����URL
	 */
	private String feedURL;

	/**
	 * ͼƬURL
	 */
	private String picURL;

	/**
	 * ����
	 */
	private String title;

	/**
	 * Ԥ������
	 */
	private String previewContent;

	/**
	 * �ϴθ���ʱ��
	 */
	private Timestamp lastTime;

	/**
	 * ɾ�����,��Ҫ�ֶ����� ����ͬ�������Ϊ"1"���ʾ������ɾ������δͬ������Ҫɾ����¼
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
