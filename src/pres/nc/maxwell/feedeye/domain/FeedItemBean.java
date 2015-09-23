package pres.nc.maxwell.feedeye.domain;

import java.sql.Timestamp;

/**
 * �洢������Ϣ�Ķ���
 */
public class FeedItemBean {
	
	/**
	 * ���ݿ����ɵ�id����,Ĭ�ϵ�-1���������
	 */
	private int itemId = -1;
	
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
