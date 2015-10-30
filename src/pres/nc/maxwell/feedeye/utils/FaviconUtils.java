package pres.nc.maxwell.feedeye.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取Favicon的工具类
 */
public class FaviconUtils {

	/**
	 * 获取网址对应的Favicon
	 * 
	 * @param orgUrl
	 *            网址
	 * @return Favicon的地址
	 */
	public static String getFaviconUrl(String orgUrl) {

		String faviconUrl = null;

		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");// 匹配顶级域名

		Matcher m = p.matcher(orgUrl);

		if (m.find()) {

			// 补全http头
			String host = m.group();

			if (host.startsWith("http://") || host.startsWith("https://")) {

				faviconUrl = host + "/favicon.ico";

			} else {

				if (orgUrl.startsWith("https://")) {
					faviconUrl = "https://" + host + "/favicon.ico";
				} else {
					faviconUrl = "http://" + host + "/favicon.ico";
				}

			}

		}

		return faviconUrl;

		// 使用第三方favicon API接口
		// return "http://api.byi.pw/favicon/?url="+orgUrl;
	}

}
