package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 存储发现信息的对象
 */
@SuppressWarnings("serial")
public class DiscoverItem implements Serializable {

	/**
	 * 链接
	 */
	public String link;
	
	/**
	 * 订阅名称
	 */
	public String name;
	
	/**
	 * 订阅类型
	 */
	public String type;
	
	/**
	 * 描述
	 */
	public String description;
	
	/**
	 * 编码
	 */
	public String encode;

	/**
	 * 关键词1
	 */
	public String[] labels;
	
	/**
	 * 是否高亮关键词
	 */
	public int[] colorMarks;
	
}
