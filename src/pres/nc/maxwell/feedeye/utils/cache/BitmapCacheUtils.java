package pres.nc.maxwell.feedeye.utils.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

	
	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			Bitmap bitmap) {

		imageView.setImageBitmap(bitmap);
		displayBitmap(imageView, url);

	}
	
	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			Drawable drawable) {

		imageView.setImageDrawable(drawable);
		displayBitmap(imageView, url);

	}
	
	public void displayBitmapWithLoadingImage(ImageView imageView, String url,
			int resId) {

		imageView.setImageResource(resId);
		displayBitmap(imageView, url);

	}
}
