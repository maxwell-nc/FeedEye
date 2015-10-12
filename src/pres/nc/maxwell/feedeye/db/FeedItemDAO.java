package pres.nc.maxwell.feedeye.db;

import java.util.ArrayList;
import java.util.Locale;

import pres.nc.maxwell.feedeye.domain.FeedItem;
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
	 * ��FeedItem�Ž�Map
	 * 
	 * @param feedItem
	 *            ������Ϣ
	 * @return ����map
	 */
	private ContentValues putFeedItemInMap(FeedItem feedItem) {

		ContentValues map = new ContentValues();

		map.put("pic_url", feedItem.picURL);
		map.put("feed_url", feedItem.feedURL);
		map.put("encoding", feedItem.encoding);
		
		map.put("type", feedItem.baseInfo.type);
		map.put("title", feedItem.baseInfo.title);
		map.put("summary", feedItem.baseInfo.summary);
		map.put("pub_date", TimeUtils.timestamp2String(feedItem.baseInfo.time,
				TimeUtils.STANDARD_TIME_PATTERN, Locale.getDefault()));
		
		map.put("delete_flag", feedItem.deleteFlag);

		return map;
	}

	/**
	 * ���һ��������Ϣ���Զ��޸�FeedItem��id��Ϣ
	 * 
	 * @param feedItem
	 *            ������Ϣ
	 * @return �Ƿ�ɹ����
	 */
	public boolean addItem(FeedItem feedItem) {

		int itemId = feedItem.itemId;
		if (itemId != -1) {// ��ֹ�Ƿ�����
			throw new RuntimeException(
					"Do not set item id if you want to add to database");
		}

		String isDeleted = feedItem.deleteFlag;
		if (isDeleted == "-1") {// �Ѿ����Ϊɾ���Ķ���
			throw new RuntimeException("Do not update already deleted id item");
		}

		ContentValues map = putFeedItemInMap(feedItem);

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
			feedItem.itemId = queryIdByRowId(rowId);

			return true;
		}

	}

	/**
	 * ɾ��һ��������Ϣ��δʵ��ͬ����������ɾ����
	 * 
	 * @param feedItem
	 *            Ҫɾ���Ķ�����Ϣ
	 * @return �Ƿ�ɹ�ɾ��
	 */
	public boolean removeItem(FeedItem feedItem) {

		int itemId = feedItem.itemId;
		if (itemId <= -1) {// ��ֹ�Ƿ�����
			throw new RuntimeException("Do not delete no-id item");
		}

		// ����ɾ����ǣ�����ͬ��ɾ��
		feedItem.deleteFlag = "-1";

		String idString = String.valueOf(itemId);
		ContentValues map = putFeedItemInMap(feedItem);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		int rowCount = db.update(mTableName, map, "id=?",
				new String[]{idString});

		db.close();
		db = null;

		return rowCount == 1 ? true : false;
	}

	/**
	 * ���¶�����Ϣ
	 * 
	 * @param feedItem
	 *            ������Ϣ
	 * @return �Ƿ�ɹ�����
	 */
	public boolean updateItem(FeedItem feedItem) {

		int itemId = feedItem.itemId;
		if (itemId <= -1) {// ��ֹ�Ƿ�����
			throw new RuntimeException("Do not update non-set id item");
		}

		String isDeleted = feedItem.deleteFlag;
		if (isDeleted == "-1") {// �Ѿ����Ϊɾ���Ķ���
			throw new RuntimeException("Do not update already deleted id item");
		}

		String idString = String.valueOf(itemId);
		ContentValues map = putFeedItemInMap(feedItem);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		int rowCount = db.update(mTableName, map, "id=?",
				new String[]{idString});
		db.close();
		db = null;

		return rowCount == 1 ? true : false;
	}

	/**
	 * ��������ѯ���е�item���������Ѿ�ɾ����
	 * 
	 * @return FeedItem����
	 */
	public ArrayList<FeedItem> queryAllItems() {

		return queryItems(null, null, false);

	}

	/**
	 * ��������ѯitem���µ����ݷ��ڵ�һ
	 * 
	 * @param selection
	 *            ѡ������
	 * @param selectionArgs
	 *            ѡ��������Ӧ�Ĳ�������
	 * @param isReturnDeletedData
	 *            �Ƿ���ʾ�Ѿ�ɾ����δͬ��ɾ��������
	 * @return ��ѯ�Ľ��
	 */
	public ArrayList<FeedItem> queryItems(String selection,
			String[] selectionArgs, boolean isReturnDeletedData) {

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		Cursor cursor = db.query(mTableName, null, selection, selectionArgs,
				null, null, "id DESC");// �µ����ݷ��ڵ�һ

		ArrayList<FeedItem> retList = new ArrayList<FeedItem>();

		while (cursor.moveToNext()) {// ��ѯ���н��

			if (!isReturnDeletedData) {// ����ʾ�Ѿ�ɾ��������
				if ("-1".equals(cursor.getString(6))) {// �����Ѿ���ɾ������ͬ��
					continue;
				}
			}

			// ����δɾ��

			FeedItem feedItem = new FeedItem();

			feedItem.itemId = Integer.parseInt(cursor.getString(0));
			feedItem.feedURL = cursor.getString(1);
			feedItem.picURL = cursor.getString(2);
			feedItem.encoding = cursor.getString(3);
			
			feedItem.baseInfo.type = cursor.getString(4);
			feedItem.baseInfo.title = cursor.getString(5);
			feedItem.baseInfo.summary = cursor.getString(6);
			feedItem.baseInfo.time = TimeUtils.string2Timestamp(cursor
					.getString(7));

			retList.add(feedItem);

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
				new String[]{rowIdString}, null, null, null, null);

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

	/**
	 * ���ͬ����ɾ�����Ϊɾ��������
	 */
	public void completeSynchronized() {

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		db.delete(mTableName, "delete_flag=-1", null);

		db.close();
		db = null;

	}

}
