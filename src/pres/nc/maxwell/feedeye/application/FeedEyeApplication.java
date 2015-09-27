package pres.nc.maxwell.feedeye.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.app.Application;

/**
 * �Զ����Application
 */
public class FeedEyeApplication extends Application {

	@Override
	public void onCreate() {

		/**
		 * ����δ������쳣
		 */
		Thread.currentThread().setUncaughtExceptionHandler(
				new ExpectionHandler());

		super.onCreate();
	}

	/**
	 * ȫ���쳣��������
	 */
	private class ExpectionHandler implements UncaughtExceptionHandler {

		/**
		 * ��δ�����쳣���������������־�ļ���Ȼ����ɱ
		 */
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			String timeStr = TimeUtils.date2String(new Date(), "yyyy-MM-dd-HH-mm-ss");
	
			File file = IOUtils.getFileInSdcard("/FeedEyeErrLog", "errorLog" + timeStr + ".log");
			
			BufferedWriter bufferedWriter = null;
			StringWriter stringWriter = null;
			PrintWriter printWriter = null;
			
			try {
				
				FileWriter fileWriter = new FileWriter(file);
				bufferedWriter = new BufferedWriter(fileWriter);
				
				//��¼������־
				stringWriter = new StringWriter();
				printWriter = new PrintWriter(stringWriter);
				ex.printStackTrace(printWriter);
				ex.printStackTrace();//��ӡһ�ݵ�����̨
				
				bufferedWriter.write(stringWriter.toString());
				bufferedWriter.flush();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				IOUtils.closeQuietly(stringWriter);
				IOUtils.closeQuietly(printWriter);
				IOUtils.closeQuietly(bufferedWriter);
				// ��ɱ
				android.os.Process.killProcess(android.os.Process.myPid());
				
			}
		}

	}
}
