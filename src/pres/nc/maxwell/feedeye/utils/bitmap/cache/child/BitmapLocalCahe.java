package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import android.graphics.Bitmap;
import android.widget.ImageView;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;

/**
 * Bitmap±¾µØ»º´æ
 */
public class BitmapLocalCahe extends BitmapCache{

	public BitmapLocalCahe(ImageView imageView, String url) {
		super(imageView, url);
	}

	@Override
	public void displayBitmap() {
		
	}

	@Override
	protected Bitmap getCache() {
		return null;
	}

	@Override
	protected void setCache() {
		
	}

	
	
	
	
}
