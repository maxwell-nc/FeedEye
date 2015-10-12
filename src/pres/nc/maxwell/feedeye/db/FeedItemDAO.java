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
 * 订阅信息的DAO类，访问feed_item表
 */
public class FeedItemDAO {

	/**
	 * 数据库打开帮助类
	 */
	private DatabaseOpenHelper mDatabaseOpenHelper;

	/**
	 * 操作的表名
	 */
	private static final String mTableName = "feed_item";

	/**
	 * 初始化
	 * 
	 * @param context
	 *            上下文
	 */
	public FeedItemDAO(Context context) {
		mDatabaseOpenHelper = new DatabaseOpenHelper(context);

		// 不必提前打开数据库，可能使用者不立即操作数据库
	}

	/**
	 * 把FeedItem放进Map
	 * 
	 * @param feedItem
	 *            订阅信息
	 * @return 返回map
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
	 * 添加一条订阅信息，自动修改FeedItem的id信息
	 * 
	 * @param feedItem
	 *            订阅消息
	 * @return 是否成功添加
	 */
	public boolean addItem(FeedItem feedItem) {

		int itemId = feedItem.itemId;
		if (itemId != -1) {// 防止非法插入
			throw new RuntimeException(
					"Do not set item id if you want to add to database");
		}

		String isDeleted = feedItem.deleteFlag;
		if (isDeleted == "-1") {// 已经标记为删除的对象
			throw new RuntimeException("Do not update already deleted id item");
		}

		ContentValues map = putFeedItemInMap(feedItem);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		// 插入数据
		long rowId = db.insert(mTableName, null, map);

		LogUtils.i("FeedPager", "添加rowId：" + rowId);

		db.close();
		db = null;

		if (rowId == -1) {// 插入失败

			return false;

		} else {// 插入成功

			// 更新item的id，不要把rowid当id使用
			feedItem.itemId = queryIdByRowId(rowId);

			return true;
		}

	}

	/**
	 * 删除一条订阅信息（未实现同步，非真正删除）
	 * 
	 * @param feedItem
	 *            要删除的订阅信息
	 * @return 是否成功删除
	 */
	public boolean removeItem(FeedItem feedItem) {

		int itemId = feedItem.itemId;
		if (itemId <= -1) {// 防止非法更新
			throw new RuntimeException("Do not delete no-id item");
		}

		// 设置删除标记，用于同步删除
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
	 * 更新订阅信息
	 * 
	 * @param feedItem
	 *            订阅信息
	 * @return 是否成功更新
	 */
	public boolean updateItem(FeedItem feedItem) {

		int itemId = feedItem.itemId;
		if (itemId <= -1) {// 防止非法更新
			throw new RuntimeException("Do not update non-set id item");
		}

		String isDeleted = feedItem.deleteFlag;
		if (isDeleted == "-1") {// 已经标记为删除的对象
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
	 * 无条件查询所有的item，不包含已经删除的
	 * 
	 * @return FeedItem集合
	 */
	public ArrayList<FeedItem> queryAllItems() {

		return queryItems(null, null, false);

	}

	/**
	 * 按条件查询item，新的数据放在第一
	 * 
	 * @param selection
	 *            选择条件
	 * @param selectionArgs
	 *            选择条件对应的参数数组
	 * @param isReturnDeletedData
	 *            是否显示已经删除但未同步删除的数据
	 * @return 查询的结果
	 */
	public ArrayList<FeedItem> queryItems(String selection,
			String[] selectionArgs, boolean isReturnDeletedData) {

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		Cursor cursor = db.query(mTableName, null, selection, selectionArgs,
				null, null, "id DESC");// 新的数据放在第一

		ArrayList<FeedItem> retList = new ArrayList<FeedItem>();

		while (cursor.moveToNext()) {// 查询所有结果

			if (!isReturnDeletedData) {// 不显示已经删除的数据
				if ("-1".equals(cursor.getString(6))) {// 数据已经被删除，待同步
					continue;
				}
			}

			// 数据未删除

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
	 * 根据rowid查询id，不要把rowid当id使用
	 * 
	 * @param rowId
	 *            行号
	 * @return id主键值
	 */
	public int queryIdByRowId(long rowId) {

		String rowIdString = String.valueOf(rowId);
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		Cursor cursor = db.query(mTableName, null, "rowid=?",
				new String[]{rowIdString}, null, null, null, null);

		String idString = null;
		if (cursor.moveToNext()) {// 只有一条记录
			idString = cursor.getString(0);// 数据库生成的id（主键，非rowId）
		}

		db.close();
		db = null;

		if (idString != null) {// 查询到数据
			return Integer.parseInt(idString);
		} else {// 查询不到数据
			return -1;
		}

	}

	/**
	 * 完成同步，删除标记为删除的数据
	 */
	public void completeSynchronized() {

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		db.delete(mTableName, "delete_flag=-1", null);

		db.close();
		db = null;

	}

}
