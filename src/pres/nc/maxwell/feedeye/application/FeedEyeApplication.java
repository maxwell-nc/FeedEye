package pres.nc.maxwell.feedeye.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.app.Application;

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
		
		//TODO：检查SD是否存在
		
		//添加.nomedia文件
		IOUtils.addNoMediaMarkFileInSdcard("/FeedEye/ImgCache");
		
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

			String timeStr = TimeUtils.LoopToTransTime(TimeUtils.GET_CURRENT_TIME_MARK,
					"yyyy-MM-dd-HH-mm-ss");

			File file = IOUtils.getFileInSdcard("/FeedEye/ErrLog", timeStr
					+ ".log");

			BufferedWriter bufferedWriter = null;
			StringWriter stringWriter = null;
			PrintWriter printWriter = null;

			try {

				FileWriter fileWriter = new FileWriter(file);
				bufferedWriter = new BufferedWriter(fileWriter);

				// 记录错误日志
				stringWriter = new StringWriter();
				printWriter = new PrintWriter(stringWriter);
				ex.printStackTrace(printWriter);
				ex.printStackTrace();// 打印一份到控制台

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
