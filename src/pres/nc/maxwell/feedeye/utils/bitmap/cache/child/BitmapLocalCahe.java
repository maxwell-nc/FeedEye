package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCache;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.widget.ImageView;

/**
 * Bitmap���ػ���
 */
public class BitmapLocalCahe extends BitmapCacheDefaultImpl {

	/**
	 * ���������ڴ滺��
	 */
	private BitmapMemoryCache mBitmapMemoryCache;

	/**
	 * ���ػ�����ļ���
	 */
	private String mFileName;

	/**
	 * ����Ҫ�����Ĳ���
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);
	}

	/**
	 * �ӱ����л�ȡBitmap��д���ڴ滺�棬�ٶ�����ʾ
	 * @param cache ����BitmapMemoryCache����
	 * @return �����Ƿ�ɹ���ȡ���ػ���
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url,BitmapCache cache) {

		// ��ȡBitmapMemoryCache
		mBitmapMemoryCache = (BitmapMemoryCache) cache;
		
		setParams(imageView, url);
		return getCache();
	}

	/**
	 * �ӱ�����Ѱ��Cache������д���ڴ沢��ʾ��û�򷵻�false
	 * 
	 * @return �����Ƿ�ɹ�
	 */
	@Override
	public boolean getCache() {

		LogUtils.i("BitmapLocalCahe", "�ӱ����ж�ȡCache");

		File cacheFile = getCacheFile();
		if (cacheFile.exists()) {// ���ػ������

			// �����ڴ滺��
			mBitmapMemoryCache.setCache(cacheFile);

			// ���ڴ�����ʾ
			if (!mBitmapMemoryCache.displayBitmap(mImageView, mURL,null)) {

				return false;

			} else {

				return true;

			}

		} else {// ���ػ��治����
			return false;
		}

	}

	/**
	 * ���ñ��ػ��棬���������غ����
	 * 
	 * @param bitmapNetworkStream
	 *            ������
	 */
	@Override
	public <T> void setCache(T bitmapNetworkStream) {

		if (!(bitmapNetworkStream instanceof InputStream)) {
			return;
		}

		LogUtils.i("BitmapLocalCahe", "���ñ��ػ���");

		BufferedOutputStream bufferedOutputStream = getBufferedOutputStream();

		try {

			// д���ػ���
			IOUtils.writeStream((InputStream) bitmapNetworkStream,
					bufferedOutputStream);

		} finally {// �ر���

			IOUtils.closeQuietly((InputStream) bitmapNetworkStream);
			IOUtils.closeQuietly(bufferedOutputStream);

		}

	}

	/**
	 * �������ڱ���ͼƬ�������
	 * 
	 * @return ���ڱ���ͼƬ�������
	 */
	private BufferedOutputStream getBufferedOutputStream() {

		File file = getCacheFile();

		// �������ڱ���ͼƬ�������
		BufferedOutputStream bufferedOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

		return bufferedOutputStream;
	}

	/**
	 * ����Cache�����File����
	 * 
	 * @return Ҫ������File����
	 */
	private File getCacheFile() {

		// �ж�SD������,δʵ��
		// File sdFile = Environment.getExternalStorageDirectory();
		// long freeSpace = sdFile.getFreeSpace();

		// ��URL��MD5ֵΪ�ļ���
		mFileName = MD5Utils.getMD5String(getURL());

		File file = IOUtils.getFileInSdcard("/FeedEyeCache", mFileName);

		return file;
	}

}
