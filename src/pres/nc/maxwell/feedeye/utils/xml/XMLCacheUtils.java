package pres.nc.maxwell.feedeye.utils.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import android.util.Xml;

/**
 * 控制网络XML数据的缓存
 */
public class XMLCacheUtils {

	
	
	public static void SaveLocalCache(String filename,
			ArrayList<FeedXMLContentInfo> contentInfos) {

		XmlSerializer xmlSerializer = Xml.newSerializer();

		File file = IOUtils.getFileInSdcard("/FeedEye/DetailCache", filename);

		FileOutputStream fileOutputStream = null;
		
		try {
			
			fileOutputStream = new FileOutputStream(file);
			
			xmlSerializer.setOutput(fileOutputStream, "utf-8");
			xmlSerializer.startDocument("utf-8", true);
			
			//写内容
			for (FeedXMLContentInfo contentInfo : contentInfos) {
				xmlSerializer.startTag(null, "item");//<item>
				
				xmlSerializer.startTag(null, "title");
				xmlSerializer.text(contentInfo.title);
				xmlSerializer.endTag(null,  "title");

				xmlSerializer.startTag(null, "description");
				xmlSerializer.cdsect(contentInfo.description);//生成CDATA
				//xmlSerializer.text(contentInfo.description);
				xmlSerializer.endTag(null,  "description");

				xmlSerializer.startTag(null, "pubDate");
				xmlSerializer.text(contentInfo.pubDate);
				xmlSerializer.endTag(null,  "pubDate");
				
				xmlSerializer.endTag(null,  "item");//</item>
			}
			
			xmlSerializer.endDocument();
			
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//关闭流
		IOUtils.closeQuietly(fileOutputStream);
		
	}
}
