package pres.nc.maxwell.feedeye.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

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

	/**
	 * ���SD���еĴ洢File����
	 * 
	 * @param dir
	 *            SD���µ�Ŀ¼����"/xxx"
	 * @param filename
	 *            �ļ���
	 * @return File����
	 */
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
	
	
	/**
	 * ת��URIΪ����·��
	 * @param activity Activity����
	 * @param uri Uri
	 * @return ����·���ı�
	 */
	public static String getAbsolutePathFromURI(Activity activity,
			Uri uri) {
		String absPath = null;
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = activity.getContentResolver().query(uri, projection,
				null, null, null);
		if (cursor.moveToFirst()) {

			int index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			absPath = cursor.getString(index);
		}
		cursor.close();
		return absPath;
	}
	
}
