package pres.nc.maxwell.feedeye.utils.bitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 压缩bitmap工具类
 */
public class BitmapCompressUtils {

	/**
	 * Bitmap输入流
	 */
	private InputStream mInputStream;

	/**
	 * Bitmap的File对象
	 */
	private File mFile;

	/**
	 * Bitmap解析配置
	 */
	private BitmapFactory.Options mOptions;

	/**
	 * 自动计算采样大小，算法参考xUtils开源项目
	 * 
	 * @param viewHeight
	 *            ImageView的高
	 * @param viewWidth
	 *            ImageView的宽
	 * @return 采样大小
	 */
	public int AutoCalculateSampleSize(int viewHeight, int viewWidth) {
		
		mOptions.inJustDecodeBounds = true;

		if (mInputStream != null) {
			BitmapFactory.decodeStream(mInputStream, null, mOptions);
		}
		if (mFile != null) {
			BitmapFactory.decodeFile(mFile.getPath(), mOptions);
		}

		int height = mOptions.outHeight;
		int width = mOptions.outWidth;

		int defaultSampleSize = 1;

		if (height > viewHeight || width > viewWidth) {
			int heightRadio = height / viewHeight;
			int widthRadio = width / viewWidth;

			defaultSampleSize = heightRadio > widthRadio ? heightRadio
					: widthRadio;
		}

		mOptions.inJustDecodeBounds = false;

		return defaultSampleSize;
	}

	// ================================ 操作InputStream对象 ========================

	/**
	 * 初始化压缩配置
	 * 
	 * @param inputStream
	 *            传入Bitmap网络流
	 */
	public BitmapCompressUtils(InputStream inputStream) {
		this.mInputStream = inputStream;
		mOptions = new BitmapFactory.Options();
	}

	/**
	 * 自定义压缩Bitmap
	 * 
	 * @param sampleSize
	 *            采样大小
	 * @param config
	 *            颜色配置
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapInputStream(int sampleSize, Bitmap.Config config) {

		if (mInputStream == null) {
			return null;
		}

		mOptions.inSampleSize = sampleSize;
		mOptions.inPreferredConfig = config;
		mOptions.inJustDecodeBounds = false;

		LogUtils.w("BitmapCompressUtils", "手动设置压缩选项，采样率为："+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory
				.decodeStream(mInputStream, null, mOptions);

		return bitmap;
	}

	/**
	 * 根据View宽高自动压缩图片
	 * 
	 * @param viewHeight
	 *            View的高度
	 * @param viewWidth
	 *            View的宽度
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapInputStream(int viewHeight, int viewWidth) {

		if (mInputStream == null) {
			return null;
		}

		mOptions.inSampleSize = AutoCalculateSampleSize(viewHeight, viewWidth);
		mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		try {
			mInputStream.reset();// 由于已经解析了一次，需要重置inputSteam
		} catch (IOException e) {
			e.printStackTrace();
		}

		LogUtils.w("BitmapCompressUtils", "自动设置压缩选项，采样率为："+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory
				.decodeStream(mInputStream, null, mOptions);

		return bitmap;
	}

	// ======================= 操作File对象 =================================

	/**
	 * 初始化压缩配置
	 * 
	 * @param file
	 *            传入Bitmap的File对象
	 */
	public BitmapCompressUtils(File file) {
		this.mFile = file;
		mOptions = new BitmapFactory.Options();
	}

	/**
	 * 自定义压缩Bitmap
	 * 
	 * @param sampleSize
	 *            采样大小
	 * @param config
	 *            颜色配置
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapFile(int sampleSize, Bitmap.Config config) {

		if (mFile == null) {
			return null;
		}

		mOptions.inSampleSize = sampleSize;
		mOptions.inPreferredConfig = config;
		mOptions.inJustDecodeBounds = false;


		LogUtils.w("BitmapCompressUtils", "手动设置压缩选项，采样率为："+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory.decodeFile(mFile.getPath(), mOptions);

		return bitmap;
	}

	/**
	 * 根据View宽高自动压缩图片
	 * 
	 * @param viewHeight
	 *            View的高度
	 * @param viewWidth
	 *            View的宽度
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapFile(int viewHeight, int viewWidth) {

		if (mFile == null) {
			return null;
		}

		mOptions.inSampleSize = AutoCalculateSampleSize(viewHeight, viewWidth);
		mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		LogUtils.w("BitmapCompressUtils", "自动设置压缩选项，采样率为："+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory.decodeFile(mFile.getPath(), mOptions);

		return bitmap;
	}
}
