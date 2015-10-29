package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 存储发现信息的对象
 */
@SuppressWarnings("serial")
public class DiscoverItem implements Serializable {

	/**
	 * 颜色标记：黑色
	 */
	public static final int COLOR_MARK_BLACK = 0;
	
	/**
	 * 颜色标记：主题色
	 */
	public static final int COLOR_MARK_THEME_COLOR = 1;

	/**
	 * 未定义类型
	 */
	public static final int TYPE_UNDEFINE = 0;
	
	/**
	 * 博客类型
	 */
	public static final int TYPE_BLOG = 1;
	
	/**
	 * 工作相关类型
	 */
	public static final int TYPE_WORK = 2;
	
	/**
	 * 娱乐类型
	 */
	public static final int TYPE_ENTERTAINMENT = 3;
	
	/**
	 * 咨询类型
	 */
	public static final int TYPE_INFOMATION = 4;
	
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
	 * @see #TYPE_UNDEFINE
	 * @see #TYPE_BLOG
	 * @see #TYPE_WORK
	 * @see #TYPE_ENTERTAINMENT
	 * @see #TYPE_INFOMATION
	 */
	public int type;

	/**
	 * 描述
	 */
	public String description;

	/**
	 * 编码
	 */
	public String encode;

	/**
	 * 关键词
	 */
	public String[] labels;

	/**
	 * 是否高亮关键词
	 * @see #COLOR_MARK_BLACK
	 * @see #COLOR_MARK_THEME_COLOR
	 */
	public int[] colorMarks;

}
