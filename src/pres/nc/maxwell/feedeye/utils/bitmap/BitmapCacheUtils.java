package pres.nc.maxwell.feedeye.utils.bitmap;

import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapLocalCahe;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapMemoryCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.child.BitmapNetworkCache;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Bitmap�������湤����
 */
public class BitmapCacheUtils {

	
	public void displayBitmap(ImageView imageView, String url) {

		// �ڴ滺��
		BitmapMemoryCache bitmapMemoryCache = new BitmapMemoryCache(imageView,
				url);
		
		//�ֶ�����ѹ��ѡ��
		//bitmapMemoryCache.setCompressOptions(16, Bitmap.Config.RGB_565);
		
		if (!bitmapMemoryCache.displayBitmap()) {//�ڴ���û�л���

			LogUtils.i("BitmapCacheUtils", "�ڴ���û�л���");
			
			// ���ػ���
			BitmapLocalCahe bitmapLocalCahe = new BitmapLocalCahe(
					bitmapMemoryCache);

			if (!bitmapLocalCahe.displayBitmap()) {//������û�л���

				LogUtils.i("BitmapCacheUtils", "������û�л���");
				
				// ��·����
				BitmapNetworkCache bitmapNetworkCache = new BitmapNetworkCache(
						bitmapLocalCahe);
				bitmapNetworkCache.displayBitmap();//���棬�����޷���ȡ����ʾ����ͼƬ

			}

		}

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
