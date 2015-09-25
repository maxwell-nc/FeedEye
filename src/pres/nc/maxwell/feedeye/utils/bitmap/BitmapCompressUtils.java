package pres.nc.maxwell.feedeye.utils.bitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * ѹ��bitmap������
 */
public class BitmapCompressUtils {

	/**
	 * Bitmap������
	 */
	private InputStream mInputStream;

	/**
	 * Bitmap��File����
	 */
	private File mFile;

	/**
	 * Bitmap��������
	 */
	private BitmapFactory.Options mOptions;

	/**
	 * �Զ����������С���㷨�ο�xUtils��Դ��Ŀ
	 * 
	 * @param viewHeight
	 *            ImageView�ĸ�
	 * @param viewWidth
	 *            ImageView�Ŀ�
	 * @return ������С
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

	// ================================ ����InputStream���� ========================

	/**
	 * ��ʼ��ѹ������
	 * 
	 * @param inputStream
	 *            ����Bitmap������
	 */
	public BitmapCompressUtils(InputStream inputStream) {
		this.mInputStream = inputStream;
		mOptions = new BitmapFactory.Options();
	}

	/**
	 * �Զ���ѹ��Bitmap
	 * 
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapInputStream(int sampleSize, Bitmap.Config config) {

		if (mInputStream == null) {
			return null;
		}

		mOptions.inSampleSize = sampleSize;
		mOptions.inPreferredConfig = config;
		mOptions.inJustDecodeBounds = false;

		LogUtils.w("BitmapCompressUtils", "�ֶ�����ѹ��ѡ�������Ϊ��"+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory
				.decodeStream(mInputStream, null, mOptions);

		return bitmap;
	}

	/**
	 * ����View����Զ�ѹ��ͼƬ
	 * 
	 * @param viewHeight
	 *            View�ĸ߶�
	 * @param viewWidth
	 *            View�Ŀ��
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapInputStream(int viewHeight, int viewWidth) {

		if (mInputStream == null) {
			return null;
		}

		mOptions.inSampleSize = AutoCalculateSampleSize(viewHeight, viewWidth);
		mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		try {
			mInputStream.reset();// �����Ѿ�������һ�Σ���Ҫ����inputSteam
		} catch (IOException e) {
			e.printStackTrace();
		}

		LogUtils.w("BitmapCompressUtils", "�Զ�����ѹ��ѡ�������Ϊ��"+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory
				.decodeStream(mInputStream, null, mOptions);

		return bitmap;
	}

	// ======================= ����File���� =================================

	/**
	 * ��ʼ��ѹ������
	 * 
	 * @param file
	 *            ����Bitmap��File����
	 */
	public BitmapCompressUtils(File file) {
		this.mFile = file;
		mOptions = new BitmapFactory.Options();
	}

	/**
	 * �Զ���ѹ��Bitmap
	 * 
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapFile(int sampleSize, Bitmap.Config config) {

		if (mFile == null) {
			return null;
		}

		mOptions.inSampleSize = sampleSize;
		mOptions.inPreferredConfig = config;
		mOptions.inJustDecodeBounds = false;


		LogUtils.w("BitmapCompressUtils", "�ֶ�����ѹ��ѡ�������Ϊ��"+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory.decodeFile(mFile.getPath(), mOptions);

		return bitmap;
	}

	/**
	 * ����View����Զ�ѹ��ͼƬ
	 * 
	 * @param viewHeight
	 *            View�ĸ߶�
	 * @param viewWidth
	 *            View�Ŀ��
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapFile(int viewHeight, int viewWidth) {

		if (mFile == null) {
			return null;
		}

		mOptions.inSampleSize = AutoCalculateSampleSize(viewHeight, viewWidth);
		mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		LogUtils.w("BitmapCompressUtils", "�Զ�����ѹ��ѡ�������Ϊ��"+mOptions.inSampleSize);
		Bitmap bitmap = BitmapFactory.decodeFile(mFile.getPath(), mOptions);

		return bitmap;
	}
}
