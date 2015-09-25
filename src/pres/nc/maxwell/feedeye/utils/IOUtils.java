package pres.nc.maxwell.feedeye.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import android.os.Environment;

/**
 * ����IO������
 */
public class IOUtils {

	/**
	 * �ر�������������IO�쳣
	 * 
	 * @param inputStream
	 *            ������
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
	 * �ر������������IO�쳣
	 * 
	 * @param outputStream
	 *            �����
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
	 * �ر��ַ���������IO�쳣
	 * 
	 * @param reader
	 *            �ַ���
	 */
	public static void closeQuietly(Reader reader) {

		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				reader = null;
			}
		}
	}

	/**
	 * �ر��ַ���������IO�쳣
	 * 
	 * @param writer
	 *            �ַ���
	 */
	public static void closeQuietly(Writer writer) {

		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				writer = null;
			}
		}
	}

	public static File getFileInSdcard(String dir, String filename) {

		// sdcardλ��
		String savePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + dir;

		// ����ļ��в�����, �����ļ���
		File errLogFile = new File(savePath);

		if (!errLogFile.exists()) {
			errLogFile.mkdirs();
		}

		File file = new File(savePath, filename);
		
		return file;
	}

	/**
	 * ������д�뵽�����
	 * 
	 * @param inputStream
	 *            ������
	 * @param outputStream
	 *            �����
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
