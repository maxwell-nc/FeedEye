package pres.nc.maxwell.feedeye.utils.cache;

import android.widget.ImageView;

/**
 * Bitmap�������湤����
 */
public class BitmapCacheUtils {

	public void displayBitmap(ImageView imageView, String url) {

		// �ڴ滺��
		// ���ػ���
		// ��·����
		new BitmapNetworkCacheUtils().displayBitmapAsyncFromNetwork(imageView,
				url);

	}
}
