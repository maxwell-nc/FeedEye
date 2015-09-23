package pres.nc.maxwell.feedeye.db;

import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * ������Ϣ��DAO�࣬����feed_item��
 */
public class FeedItemDAO {

	/**
	 * ���ݿ�򿪰�����
	 */
	private DatabaseOpenHelper mDatabaseOpenHelper;

	/**
	 * �����ı���
	 */
	private static final String mTableName = "feed_item";

	/**
	 * ��ʼ��
	 * 
	 * @param context
	 *            ������
	 */
	public FeedItemDAO(Context context) {
		mDatabaseOpenHelper = new DatabaseOpenHelper(context);

		// ������ǰ�����ݿ⣬����ʹ���߲������������ݿ�
	}

	/**
	 * ���һ��������Ϣ
	 * 
	 * @param feedItemBean
	 *            ������Ϣ
	 * @return �Ƿ�ɹ����
	 */
	public boolean addItem(FeedItemBean feedItemBean) {

		ContentValues map = new ContentValues();
		
		// id����Ҫ���룬���ݿ����ɵ�
		if ( feedItemBean.getItemId() != -1) {
			throw new RuntimeException("Do not set item id if you want to add to database");
		}
		
		map.put("pic_url", feedItemBean.getPicURL());
		map.put("title", feedItemBean.getTitle());
		map.put("preview_content", feedItemBean.getPreviewContent());
		map.put("last_time", TimeUtils.timestamp2String(
				feedItemBean.getLastTime(), "yyyy-MM-dd HH:mm:ss"));

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		// ��������
		long rowId = db.insert(mTableName, null, map);

		db.close();

		return rowId == -1 ? false : true;
	}

	
	/**
	 * ɾ��һ��������Ϣ
	 * @param feedItemBean Ҫɾ���Ķ�����Ϣ
	 * @return �Ƿ�ɹ�ɾ��
	 */
	public boolean removeItem(FeedItemBean feedItemBean) {

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		String idString = String.valueOf(feedItemBean.getItemId());
		
		int rowCount = db.delete(mTableName, "id=?",
				new String[] { idString });

		db.close();

		return rowCount == 1 ? true : false;
	}
	
	
	
}
