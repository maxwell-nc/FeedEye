package pres.nc.maxwell.feedeye.utils.bitmap;

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
	 * Bitmap��������
	 */
	private BitmapFactory.Options mOptions;

	
	/**
	 * ��ʼ��ѹ������
	 * @param inputStream ����Bitmap������
	 */
	public BitmapCompressUtils(InputStream inputStream) {
		this.mInputStream = inputStream;
		mOptions = new BitmapFactory.Options();
	}

	/**
	 * �Զ���ѹ��Bitmap������
	 * 
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapInputStream(int sampleSize, Bitmap.Config config) {
		
		mOptions.inSampleSize = sampleSize;
		mOptions.inPreferredConfig = config;
		mOptions.inJustDecodeBounds = false;
		
		Bitmap bitmap = BitmapFactory
				.decodeStream(mInputStream, null, mOptions);
		
		return bitmap;
	}

	/**
	 * ����View�����Զ�ѹ��ͼƬ
	 * 
	 * @param viewHeight
	 *            View�ĸ߶�
	 * @param viewWidth
	 *            View�Ŀ���
	 * @return Bitmap
	 */
	public Bitmap CompressBitmapInputStream(int viewHeight, int viewWidth) {

		mOptions.inSampleSize = AutoCalculateSampleSize(viewHeight, viewWidth);
		mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		
		try {
			mInputStream.reset();//�����Ѿ�������һ�Σ���Ҫ����inputSteam
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Bitmap bitmap = BitmapFactory
				.decodeStream(mInputStream, null, mOptions);

		LogUtils.w("BitmapCompressUtils", bitmap == null ? "bitmap is null"
				: "bitmap is not null");

		return bitmap;
	}

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
		
		BitmapFactory.decodeStream(mInputStream, null, mOptions);
		
	
		final int height = mOptions.outHeight;
		final int width = mOptions.outWidth;

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

}