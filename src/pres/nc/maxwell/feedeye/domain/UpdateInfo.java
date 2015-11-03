package pres.nc.maxwell.feedeye.domain;

import java.io.Serializable;

/**
 * 存储更新信息的对象
 */
@SuppressWarnings("serial")
public class UpdateInfo implements Serializable {

	/**
	 * 版本号
	 */
	public int versionCode;

	/**
	 * 更新信息
	 */
	public String updateDesc;

	/**
	 * 更新地址
	 */
	public String updateUrl;
}
