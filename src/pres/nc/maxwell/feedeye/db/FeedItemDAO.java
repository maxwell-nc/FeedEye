package pres.nc.maxwell.feedeye.db;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
		map.put("feed_url", feedItemBean.getFeedURL());
		map.put("title", feedItemBean.getTitle());
		map.put("preview_content", feedItemBean.getPreviewContent());
		map.put("last_time", TimeUtils.timestamp2String(
				feedItemBean.getLastTime(), "yyyy-MM-dd HH:mm:ss"));
		map.put("delete_flag", feedItemBean.getDeleteFlag());

		return map;
	}

	/**
	 * ���һ��������Ϣ���Զ��޸�bean��id��Ϣ
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

		LogUtils.i("FeedPager", "���rowId��" + rowId);

		db.close();
		db = null;

		if (rowId == -1) {// ����ʧ��

			return false;

		} else {// ����ɹ�

			// ����item��id����Ҫ��rowid��idʹ��
			feedItemBean.setItemId(queryIdByRowId(rowId));

			return true;
		}

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
		 * 
		 * db = null;
		 */

		// ����ɾ����ǣ�����ͬ��ɾ��
		feedItemBean.setDeleteFlag("-1");

		String idString = String.valueOf(itemId);
		ContentValues map = putBeanInMap(feedItemBean);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		int rowCount = db.update(mTableName, map, "id=?",
				new String[] { idString });

		db.close();
		db = null;

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
		db = null;

		return rowCount == 1 ? true : false;
	}

	/**
	 * ��������ѯ���е�item
	 * 
	 * @return item��bean����
	 */
	public ArrayList<FeedItemBean> queryAllItems() {

		return queryItems(null, null);

	}

	/**
	 * ��������ѯitem
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public ArrayList<FeedItemBean> queryItems(String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		Cursor cursor = db.query(mTableName, null, selection, selectionArgs,
				null, null, null, null);

		ArrayList<FeedItemBean> retList = new ArrayList<FeedItemBean>();

		while (cursor.moveToNext()) {// ��ѯ���н��

			FeedItemBean feedItemBean = new FeedItemBean();

			feedItemBean.setItemId(Integer.parseInt(cursor.getString(0)));
			feedItemBean.setFeedURL(cursor.getString(1));
			feedItemBean.setPicURL(cursor.getString(2));
			feedItemBean.setTitle(cursor.getString(3));
			feedItemBean.setPreviewContent(cursor.getString(4));
			feedItemBean.setLastTime(TimeUtils.string2Timestamp(cursor
					.getString(5)));

			retList.add(feedItemBean);
		}

		db.close();
		db = null;

		return retList;

	}

	/**
	 * ����rowid��ѯid����Ҫ��rowid��idʹ��
	 * 
	 * @param rowId
	 *            �к�
	 * @return id����ֵ
	 */
	public int queryIdByRowId(long rowId) {

		String rowIdString = String.valueOf(rowId);
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		Cursor cursor = db.query(mTableName, null, "rowid=?",
				new String[] { rowIdString }, null, null, null, null);

		String idString = null;
		if (cursor.moveToNext()) {// ֻ��һ����¼
			idString = cursor.getString(0);// ���ݿ����ɵ�id����������rowId��
		}

		db.close();
		db = null;

		if (idString != null) {// ��ѯ������
			return Integer.parseInt(idString);
		} else {// ��ѯ��������
			return -1;
		}

	}

}
