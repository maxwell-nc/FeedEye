package pres.nc.maxwell.feedeye.utils.bitmapNew;

import pres.nc.maxwell.feedeye.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class BitmapCacheUtils {

	public static void displayBitmap(Context context, ImageView imageView,
			String url) {

		displayBitmap(context,imageView, url,true);

	}
	
	public static void displayBitmap(Context context, ImageView imageView,
			String url,boolean isEnableNetworkCache) {

		// ���ü���ʧ�ܵ�ͼƬ
		Bitmap errBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.img_load_error);

		// ���ü����е�ͼƬ
		imageView.setImageResource(R.anim.refresh_rotate);
		
		new BitmapThreeLevelsCache(imageView, url, errBitmap,isEnableNetworkCache).displayBitmap();

	}

}
