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
import android.os.Environment;

/**
 * Bitmap���ػ���
 */
public class BitmapLocalCahe extends BitmapCache<InputStream> {

	/**
	 * ���������ڴ滺��
	 */
	private BitmapMemoryCache mBitmapMemoryCache;

	/**
	 * ���ػ�����ļ���
	 */
	private String mFileName;

	/**
	 * ��ʼ������
	 * 
	 * @param bitmapMemoryCache
	 *            ���������ڴ滺��
	 */
	public BitmapLocalCahe(BitmapMemoryCache bitmapMemoryCache) {

		super(bitmapMemoryCache.getImageView(), bitmapMemoryCache.getURL());
		this.mBitmapMemoryCache = bitmapMemoryCache;

	}

	/**
	 * �ӱ����л�ȡBitmap��д���ڴ滺�棬�ٶ�����ʾ
	 */
	@Override
	public boolean displayBitmap() {

		return getCache();
	}

	/**
	 * �ӱ�����Ѱ��Cache������д���ڴ沢��ʾ��û�򷵻�false
	 * 
	 * @return �����Ƿ�ɹ�
	 */
	@Override
	protected boolean getCache() {

		LogUtils.i("BitmapLocalCahe", "�ӱ����ж�ȡCache");

		File cacheFile = getCacheFile();
		if (cacheFile.exists()) {// ���ػ������

			//�����ڴ滺��
			mBitmapMemoryCache.setCache(cacheFile);
			
			//���ڴ�����ʾ
			if (!mBitmapMemoryCache.displayBitmap()) {
				
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
	public void setCache(InputStream bitmapNetworkStream) {

		LogUtils.i("BitmapLocalCahe", "���ñ��ػ���");

		BufferedOutputStream bufferedOutputStream = getBufferedOutputStream();

		try {

			// д���ػ���
			IOUtils.writeStream(bitmapNetworkStream, bufferedOutputStream);

		} finally {// �ر���

			IOUtils.closeQuietly(bitmapNetworkStream);
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
		File sdFile = Environment.getExternalStorageDirectory();

		// �ж�SD������,δʵ��
		// long freeSpace = sdFile.getFreeSpace();

		String savePath = sdFile.getAbsolutePath() + "/FeedEyeCache";

		// ����ļ��в�����, �����ļ���
		File saveDir = new File(savePath);

		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}

		LogUtils.w("BitmapLocalCahe", savePath);

		// ��URL��MD5ֵΪ�ļ���
		mFileName = MD5Utils.getMD5String(getURL());

		File file = new File(savePath, mFileName);

		return file;
	}

}
