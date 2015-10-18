package pres.nc.maxwell.feedeye.utils.bitmap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	private static final int LOAD_RESOURCE_ID = R.drawable.listview_refresh_rotate_arrow;

	/**
	 * Ĭ�ϵļ��ش������ԴͼƬid
	 */
	private static final int ERROR_RESOURCE_ID = R.drawable.img_load_error;

	/**
	 * Ĭ�ϵ�ExecutorService
	 */
	private static ExecutorService DEFAULT_EXECUTOR_SERVICE = null;

	/**
	 * �򵥵���ʾͼƬ��ʹ�����绺�桢Ĭ�ϼ����м���ʧ��ͼƬ���Զ�ѹ��ͼƬ
	 * 
	 * @param context
	 *            ������
	 * @param imageView
	 *            Ҫ��ʾͼƬ�Ŀؼ�
	 * @param url
	 *            Ҫ��ʾͼƬ�ĵ�ַ��֧�ֱ���ͼƬ������ͼƬ��
	 * @param threadPool
	 *            �Զ����̳߳أ�Ϊ�������5�̵߳�Ĭ���̳߳�
	 * @see BitmapCacheUtils#displayBitmap(Context, ImageView, String, boolean,
	 *      int, int, int, android.graphics.Bitmap.Config, ExecutorService)
	 */
	public static void displayBitmap(Context context, ImageView imageView,
			String url, ExecutorService threadPool) {

		if (threadPool == null) {

			if (DEFAULT_EXECUTOR_SERVICE == null) {
				DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);
			}
			displayBitmap(context, imageView, url, true, -1, -1, -1, null,
					DEFAULT_EXECUTOR_SERVICE);

		} else {
			displayBitmap(context, imageView, url, true, -1, -1, -1, null,
					threadPool);
		}

	}

	/**
	 * �ر�Ĭ�ϵ��̳߳�
	 */
	public static void shutdownDefalutThreadPool() {

		if (DEFAULT_EXECUTOR_SERVICE != null) {
			DEFAULT_EXECUTOR_SERVICE.shutdownNow();
			DEFAULT_EXECUTOR_SERVICE = null;
		}

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
	 * @param threadPool
	 *            �Զ����̳߳�
	 * @see BitmapCacheUtils#displayBitmap(Context, ImageView, String)
	 */
	public static void displayBitmap(Context context, ImageView imageView,
			String url, boolean isEnableNetworkCache, int loadResId,
			int errorResId, int sampleSize, Bitmap.Config config,
			ExecutorService threadPool) {

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
					isEnableNetworkCache, threadPool).displayBitmap();

		} else {// �Զ���ѹ������
			new BitmapThreeLevelsCache(imageView, url, errBitmap,
					isEnableNetworkCache, threadPool).setCompressOptions(
					sampleSize, config).displayBitmap();
		}

	}
}
