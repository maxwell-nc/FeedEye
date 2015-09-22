package pres.nc.maxwell.feedeye.utils.bitmap.cache;

import android.widget.ImageView;

/**
 * Bitmap����������
 */
public interface BitmapCache {

	/**
	 * ����Ҫ�����Ĳ���
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾͼƬ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ����ַ
	 */
	public abstract void setParams(ImageView imageView, String url);

	/**
	 * ��ʾBitmap�����ⲿ����
	 * 
	 * @param imageView
	 *            ��Ҫ��ʾͼƬ��ImageView
	 * @param url
	 *            Ҫ��ʾͼƬ��URL
	 * @return �����Ƿ�ɹ�
	 */
	public abstract boolean displayBitmap(ImageView imageView, String url);

	/**
	 * ��ȡ���棬��������һ�����棬�ڲ�����
	 * 
	 * @return �����Ƿ�ɹ���ȡ
	 */
	public abstract boolean getCache();

	/**
	 * ���滺��,����һ������������
	 * 
	 * @param <T>
	 *            ��һ����������
	 * 
	 * @param t
	 *            ����
	 */
	public abstract <T> void setCache(T t);

}
