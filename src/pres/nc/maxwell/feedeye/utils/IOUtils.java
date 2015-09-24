package pres.nc.maxwell.feedeye.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 处理IO工具类
 */
public class IOUtils {

	/**
	 * 关闭输入流并捕获IO异常
	 * 
	 * @param inputStream
	 *            输入流
	 */
	public static void closeQuietly(InputStream inputStream) {

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				inputStream = null;
			}
		}

	}

	/**
	 * 关闭输出流并捕获IO异常
	 * 
	 * @param outputStream
	 *            输出流
	 */
	public static void closeQuietly(OutputStream outputStream) {

		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				outputStream = null;
			}
		}
	}

	
	/**
	 * 输入流写入到输出流
	 * @param inputStream 输入流
	 * @param outputStream 输出流
	 */
	public static void writeStream(InputStream inputStream,
			OutputStream outputStream) {
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
