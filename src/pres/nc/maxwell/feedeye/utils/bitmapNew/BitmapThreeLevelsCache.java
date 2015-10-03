package pres.nc.maxwell.feedeye.utils.bitmapNew;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.MD5Utils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCompressUtils;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Bitmap������������ƣ������������硢���غ��ڴ滺��
 */
public class BitmapThreeLevelsCache {

	/**
	 * ��Ҫ��ʾ��ImageView
	 */
	public ImageView mImageView;

	/**
	 * Ҫ��ʾͼƬ��URL
	 */
	public String mURL;

	/**
	 * ����ʧ��ʱ���ص�ͼƬ
	 */
	public Bitmap mErrBitmap;

	/**
	 * �Ƿ�ʹ���Զ�ѹ����������Ҫ�Զ������������setCompressOptions����
	 */
	private boolean isAutoCompress = true;

	/**
	 * ������С
	 */
	private int mSampleSize;

	/**
	 * ��ɫ����
	 */
	private Bitmap.Config mConfig;
	

	/**
	 * �Ƿ������绺��
	 */
	private boolean mIsEnableNetworkCache = true;

	/**
	 * ÿ��ʹ���봴���µĶ���
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ��URL
	 */
	public BitmapThreeLevelsCache(ImageView imageView, String url,
			Bitmap errBitmap,boolean isEnableNetworkCache) {
		this.mImageView = imageView;
		this.mURL = url;
		this.mErrBitmap = errBitmap;
		this.mIsEnableNetworkCache = isEnableNetworkCache;
		mImageView.setTag(mURL);
	}

	public void displayBitmap() {

		if (isLocalFile()) {// ����ͼƬ

			LogUtils.i("BitmapThreeLevelsCache", "L1:����ͼƬ��ֱ�Ӵ�ȡ�ڴ滺��");
			if (!getMemoryCache()) {// 1.��ȡ�ڴ滺��

				File file = new File(mURL);
				setMemoryCache(file);// д���ڴ滺����
				getMemoryCache();// ���´��ڴ滺���ж�ȡ

			}

		} else {// ����ͼƬ

			LogUtils.i("BitmapThreeLevelsCache", "L1:��ȡ�ڴ滺��");
			if (!getMemoryCache()) {// 1.��ȡ�ڴ滺��

				LogUtils.i("BitmapThreeLevelsCache", "L2:��ȡ���ػ���");
				if (!getLocalCache()) {// 2.��ȡ���ػ���

					if (mIsEnableNetworkCache) {//�������绺��
						
						LogUtils.i("BitmapThreeLevelsCache", "L3:��ȡ��·����");
						getNetworkCache();// 3.��ȡ���绺��
						
					}else {//��ʹ�����绺��
						
						LogUtils.i("BitmapThreeLevelsCache", "L2:��ʹ�����绺��");
						showErrorBitmap();
						
					}

				}

			}
		}

	}

	/**
	 * �ж��Ƿ�Ϊ�����ļ�
	 * 
	 * @return ���Ǳ����ļ������棬���򷵻ؼ�
	 */
	private boolean isLocalFile() {

		if (mURL.startsWith("/")) {// �磺 /sdcard/xxx/xxx.jpg
			return true;
		}

		return false;
	}

	private void setMemoryCache(File bitmapFile) {

		// ����File����
		Bitmap bitmapCache = decodeFile(bitmapFile);

		if (bitmapCache != null) {
			// �����ڴ滺��
			BitmapLruCacheDispatcher.getInstance().getmMemoryCache()
					.put(mURL, bitmapCache);
		}

	}

	private void setLocalCache(InputStream bitmapNetworkStream) {
		BufferedOutputStream bufferedOutputStream = getBufferedOutputStream();

		try {

			// д���ػ���
			IOUtils.writeStream(bitmapNetworkStream, bufferedOutputStream);

		} finally {// �ر���

			IOUtils.closeQuietly(bitmapNetworkStream);
			IOUtils.closeQuietly(bufferedOutputStream);

		}
	}

	@SuppressWarnings("unused")
	private void setNetworkCache() {

		throw new RuntimeException(
				"Do not call this method:setNetworkCache() in "
						+ this.getClass().getName());

	}

	private boolean getMemoryCache() {

		Bitmap bitmapCache = BitmapLruCacheDispatcher.getInstance()
				.getmMemoryCache().get(mURL);

		if (bitmapCache != null) {// �л���
			String tagURL = (String) mImageView.getTag();

			// LogUtils.i("BitmapThreeLevelsCache", "tagURL" + tagURL);
			// LogUtils.i("BitmapThreeLevelsCache", "mURL" + mURL);

			if (mURL.equals(tagURL)) {// ����Ƿ�Ϊ��Ҫ��ʾ��ImageView
				mImageView.setImageBitmap(bitmapCache);
				return true;
			} else {// wrong tag
					// ���ô���
				return false;
			}

		} else {// û�л���

			return false;
		}

	}

	private boolean getLocalCache() {

		File cacheFile = getCacheFile();

		if (cacheFile.exists()) {// ���ػ������

			// �����ڴ滺��
			setMemoryCache(cacheFile);

			// ���¶�ȡ����
			getMemoryCache();

			return true;

		} else {

			return false;

		}

	}

	private void getNetworkCache() {
		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {

			@Override
			public void onConnect(InputStream inputStream) {// ���߳�
				// ���ñ��ػ���
				setLocalCache(inputStream);
				LogUtils.i("BitmapThreeLevelsCache", mURL);

			}

			@Override
			public void onSuccess() {// ���߳�
				// ���¶�ȡ����
				getLocalCache();
			}

			@Override
			public void onFailure() {// ���߳�

				// TODO����ȡfaviconʧ��
				String tagURL = (String) mImageView.getTag();
				if (mURL.equals(tagURL)) {// ����Ƿ�Ϊ��Ҫ��ʾ��ImageView

					showErrorBitmap();

				} else {// wrong tag

				}

			}

		});

		httpUtils.Connect(mURL, 10000, 10000);

	}

	/**
	 * ���أ��������ڱ���ͼƬ�������
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
	 * ���أ�����Cache�����File����
	 * 
	 * @return Ҫ������File����
	 */
	private File getCacheFile() {

		// �ж�SD������,δʵ��
		// File sdFile = Environment.getExternalStorageDirectory();
		// long freeSpace = sdFile.getFreeSpace();

		// ��URL��MD5ֵΪ�ļ���
		String fileName = MD5Utils.getMD5String(mURL);

		File file = IOUtils.getFileInSdcard("/FeedEyeCache", fileName);

		return file;
	}

	/**
	 * ѹ��������ѹ��ѡ�ʹ�ô˷�������Ĭ�ϲ�ʹ���Զ�ѹ��
	 * 
	 * @param sampleSize
	 *            ������С
	 * @param config
	 *            ��ɫ����
	 * @return ����this��������ʽ����
	 */
	public BitmapThreeLevelsCache setCompressOptions(int sampleSize,
			Bitmap.Config config) {

		// ȡ���Զ�ѹ��
		isAutoCompress = false;

		this.mSampleSize = sampleSize;
		this.mConfig = config;

		return this;
	}

	/**
	 * ѹ��������file����ѹ��Bitmap
	 * 
	 * @param bitmapFile
	 *            ͼƬ�ļ�����
	 * @return ѹ���������Bitmap
	 */
	private Bitmap decodeFile(File bitmapFile) {
		Bitmap bitmapCache = null;

		// ����ImageView���ֿ��
		mImageView.measure(0, 0);// ʹ��onLayoutListener������������
		int viewHeight = mImageView.getMeasuredHeight();
		int viewWidth = mImageView.getMeasuredWidth();

		if (isAutoCompress) {// �Զ�ѹ��ͼƬ
			bitmapCache = new BitmapCompressUtils((File) bitmapFile)
					.CompressBitmapFile(viewHeight, viewWidth);
		} else {// �ֶ�ѹ��ͼƬ
			bitmapCache = new BitmapCompressUtils((File) bitmapFile)
					.CompressBitmapFile(mSampleSize, mConfig);
		}

		return bitmapCache;
	}

	/**
	 * ʧ�ܣ���ʾ����ʧ�ܵĵ�ͼƬ
	 */
	private void showErrorBitmap() {
		// ʧ�ܺ������ڴ滺��Ϊ����ʧ�ܵ�ͼƬ����ֹ��η�������
		if (mErrBitmap != null) {

			mImageView.setImageBitmap(mErrBitmap);
			// �����ڴ滺��
			BitmapLruCacheDispatcher.getInstance()
					.getmMemoryCache().put(mURL, mErrBitmap);
		} else {
			LogUtils.i("BitmapThreeLevelsCache",
					"mErrBitmap is null");
		}
	}

}
