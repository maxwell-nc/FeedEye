package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * ͼƬ���湤���ࣨʹ���������棩
 * 
 * @see BitmapThreeLevelsCache
 */
public class BitmapCacheUtils {

	/**
	 * Ĭ�ϵļ����е���ԴͼƬid
	 */
	private static final int LOAD_RESOURCE_ID = R.drawable.listview_refresh_rotate;

	/**
	 * Ĭ�ϵļ��ش������ԴͼƬid
	 */
	private static final int ERROR_RESOURCE_ID = R.drawable.img_load_error;

	/**
	 * �򵥵���ʾͼƬ��ʹ�����绺�桢Ĭ�ϼ����м���ʧ��ͼƬ���Զ�ѹ��ͼƬ
	 * 
	 * @param context
	 *            ������
	 * @param imageView
	 *            Ҫ��ʾͼƬ�Ŀؼ�
	 * @param url
	 *            Ҫ��ʾͼƬ�ĵ�ַ��֧�ֱ���ͼƬ������ͼƬ��
	 * @see BitmapCacheUtils#displayBitmap(Context, ImageView, String, boolean,
	 *      int, int, int, android.graphics.Bitmap.Config)
	 */
	public static void displayBitmap(Context context, ImageView imageView,
			String url) {

		displayBitmap(context, imageView, url, true, -1, -1, -1, null);
	}

	/**
	 * ��ʾͼƬ
	 * 
	 * @param context
	 *            ������
	 * @param imageView
	 *            Ҫ��ʾͼƬ�Ŀؼ�
	 * @param url
	 *            Ҫ��ʾͼƬ�ĵ�ַ��֧�ֱ���ͼƬ������ͼƬ��
	 * @param sampleSize
	 *            ������С��-1Ϊ��ʹ�ã�
	 * @param config
	 *            ��ɫ���ã�nullΪ��ʹ�ã�
	 * @param isEnableNetworkCache
	 *            �Ƿ�ʹ�����绺��
	 * @param loadResId
	 *            �����е���ԴͼƬid��-1Ϊ��ʹ�ã�
	 * @param errorResId
	 *            ����ʧ�ܵ���ԴͼƬid��-1Ϊ��ʹ�ã�
	 * @see BitmapCacheUtils#displayBitmap(Context, ImageView, String)
	 */
	public static void displayBitmap(Context context, ImageView imageView,
			String url, boolean isEnableNetworkCache, int loadResId,
			int errorResId, int sampleSize, Bitmap.Config config) {

		// ���ü����е�ͼƬ
		if (loadResId != -1) {
			imageView.setImageResource(loadResId);
		} else {
			imageView.setImageResource(LOAD_RESOURCE_ID);
		}

		// ���ü���ʧ�ܵ�ͼƬ
		Bitmap errBitmap = null;

		if (errorResId != -1) {
			errBitmap = BitmapFactory.decodeResource(context.getResources(),
					errorResId);
		} else {
			errBitmap = BitmapFactory.decodeResource(context.getResources(),
					ERROR_RESOURCE_ID);
		}

		// ����ѹ������
		if (sampleSize == -1 || config == null) {// �Զ�ѹ��

			new BitmapThreeLevelsCache(imageView, url, errBitmap,
					isEnableNetworkCache).displayBitmap();

		} else {// �Զ���ѹ������
			new BitmapThreeLevelsCache(imageView, url, errBitmap,
					isEnableNetworkCache)
					.setCompressOptions(sampleSize, config).displayBitmap();
		}

	}
}
