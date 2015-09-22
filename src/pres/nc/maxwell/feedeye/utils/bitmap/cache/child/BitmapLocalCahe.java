package pres.nc.maxwell.feedeye.utils.bitmap.cache.child;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.bitmap.cache.BitmapCacheDefaultImpl;
import android.os.Environment;
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
	 * �����ʵ������
	 */
	private static final BitmapLocalCahe mThis = new BitmapLocalCahe();

	
	/**
	 * ���ش����ʵ������
	 * @return �����ʵ������
	 */
	public static BitmapLocalCahe getInstance() {

		return mThis;

	}

	
	/**
	 * �������󣬲�Ҫ�����µ�ʵ������
	 */
	private BitmapLocalCahe() {

	}

	/**
	 * ����Ҫ�����Ĳ�������ʼ��BitmapLocalCahe
	 */
	@Override
	public void setParams(ImageView imageView, String url) {
		super.setParams(imageView, url);

		//��ȡBitmapMemoryCache
		mBitmapMemoryCache = BitmapMemoryCache.getInstance();
	}

	/**
	 * �ӱ����л�ȡBitmap��д���ڴ滺�棬�ٶ�����ʾ
	 */
	@Override
	public boolean displayBitmap(ImageView imageView, String url) {
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
			if (!mBitmapMemoryCache.displayBitmap(mImageView, mURL)) {

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
