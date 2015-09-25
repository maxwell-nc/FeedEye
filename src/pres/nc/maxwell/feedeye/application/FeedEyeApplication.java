package pres.nc.maxwell.feedeye.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pres.nc.maxwell.feedeye.utils.IOUtils;

import android.app.Application;
import android.os.Environment;

/**
 * 自定义的Application
 */
public class FeedEyeApplication extends Application {

	@Override
	public void onCreate() {

		/**
		 * 捕获未捕获的异常
		 */
		Thread.currentThread().setUncaughtExceptionHandler(
				new ExpectionHandler());

		super.onCreate();
	}

	/**
	 * 全局异常捕获处理器
	 */
	private class ExpectionHandler implements UncaughtExceptionHandler {

		/**
		 * 对未捕获异常捕获并且输出错误日志文件，然后自杀
		 */
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			// sdcard位置
			String savePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/FeedEyeErrLog";

			// 如果文件夹不存在, 创建文件夹
			File errLogFile = new File(savePath);

			if (!errLogFile.exists()) {
				errLogFile.mkdirs();
			}

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
			String timeStr = simpleDateFormat.format(new Date());

			File file = new File(savePath, "errorLog" + timeStr);
			BufferedWriter bufferedWriter = null;
			StringWriter stringWriter = null;
			PrintWriter printWriter = null;
			try {
				FileWriter fileWriter = new FileWriter(file);
				bufferedWriter = new BufferedWriter(fileWriter);

				stringWriter = new StringWriter();
				printWriter = new PrintWriter(stringWriter);
				ex.printStackTrace(printWriter);

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
				// 自杀
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}

	}
}
