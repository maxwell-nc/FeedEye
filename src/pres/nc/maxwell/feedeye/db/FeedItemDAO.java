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
	 * ��Bean�Ž�Map
	 * 
	 * @param feedItemBean
	 *            ������Ϣ
	 * @return ����map
	 */
	private ContentValues putBeanInMap(FeedItemBean feedItemBean) {

		ContentValues map = new ContentValues();
		map.put("pic_url", feedItemBean.getPicURL());
		map.put("title", feedItemBean.getTitle());
		map.put("preview_content", feedItemBean.getPreviewContent());
		map.put("last_time", TimeUtils.timestamp2String(
				feedItemBean.getLastTime(), "yyyy-MM-dd HH:mm:ss"));
		map.put("delete_flag", feedItemBean.getDeleteFlag());

		return map;
	}

	/**
	 * ���һ��������Ϣ
	 * 
	 * @param feedItemBean
	 *            ������Ϣ
	 * @return �Ƿ�ɹ����
	 */
	public boolean addItem(FeedItemBean feedItemBean) {

		int itemId = feedItemBean.getItemId();
		if (itemId != -1) {// ��ֹ�Ƿ�����
			throw new RuntimeException(
					"Do not set item id if you want to add to database");
		}

		String isDeleted = feedItemBean.getDeleteFlag();
		if (isDeleted == "-1") {// �Ѿ����Ϊɾ���Ķ���
			throw new RuntimeException("Do not update already deleted id item");
		}

		ContentValues map = putBeanInMap(feedItemBean);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		// ��������
		long rowId = db.insert(mTableName, null, map);

		db.close();

		return rowId == -1 ? false : true;
	}

	/**
	 * ɾ��һ��������Ϣ��δʵ��ͬ����������ɾ����
	 * 
	 * @param feedItemBean
	 *            Ҫɾ���Ķ�����Ϣ
	 * @return �Ƿ�ɹ�ɾ��
	 */
	public boolean removeItem(FeedItemBean feedItemBean) {

		int itemId = feedItemBean.getItemId();
		if (itemId <= -1) {// ��ֹ�Ƿ�����
			throw new RuntimeException("Do not delete no-id item");
		}

		/*
		 * ��ʱ����Ҫɾ�� String idString = String.valueOf(feedItemBean.getItemId());
		 * 
		 * SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		 * 
		 * int rowCount = db.delete(mTableName, "id=?", new String[] { idString
		 * });
		 * 
		 * db.close();
		 */

		// ����ɾ����ǣ�����ͬ��ɾ��
		feedItemBean.setDeleteFlag("-1");

		String idString = String.valueOf(itemId);
		ContentValues map = putBeanInMap(feedItemBean);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		int rowCount = db.update(mTableName, map, "id=?",
				new String[] { idString });

		db.close();

		return rowCount == 1 ? true : false;
	}

	/**
	 * ���¶�����Ϣ
	 * 
	 * @param feedItemBean
	 *            ������Ϣ
	 * @return �Ƿ�ɹ�����
	 */
	public boolean updateItem(FeedItemBean feedItemBean) {

		int itemId = feedItemBean.getItemId();
		if (itemId <= -1) {// ��ֹ�Ƿ�����
			throw new RuntimeException("Do not update non-set id item");
		}

		String isDeleted = feedItemBean.getDeleteFlag();
		if (isDeleted == "-1") {// �Ѿ����Ϊɾ���Ķ���
			throw new RuntimeException("Do not update already deleted id item");
		}

		String idString = String.valueOf(itemId);
		ContentValues map = putBeanInMap(feedItemBean);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		int rowCount = db.update(mTableName, map, "id=?",
				new String[] { idString });
		db.close();

		return rowCount == 1 ? true : false;
	}
}
