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
	 * 把Bean放进Map
	 * 
	 * @param feedItemBean
	 *            订阅信息
	 * @return 返回map
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
	 * 添加一条订阅信息，自动修改bean的id信息
	 * 
	 * @param feedItemBean
	 *            订阅消息
	 * @return 是否成功添加
	 */
	public boolean addItem(FeedItemBean feedItemBean) {

		int itemId = feedItemBean.getItemId();
		if (itemId != -1) {// 防止非法插入
			throw new RuntimeException(
					"Do not set item id if you want to add to database");
		}

		String isDeleted = feedItemBean.getDeleteFlag();
		if (isDeleted == "-1") {// 已经标记为删除的对象
			throw new RuntimeException("Do not update already deleted id item");
		}

		ContentValues map = putBeanInMap(feedItemBean);

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
			feedItemBean.setItemId(queryIdByRowId(rowId));

			return true;
		}

	}

	/**
	 * 删除一条订阅信息（未实现同步，非真正删除）
	 * 
	 * @param feedItemBean
	 *            要删除的订阅信息
	 * @return 是否成功删除
	 */
	public boolean removeItem(FeedItemBean feedItemBean) {

		int itemId = feedItemBean.getItemId();
		if (itemId <= -1) {// 防止非法更新
			throw new RuntimeException("Do not delete no-id item");
		}

		/*
		 * 暂时不需要删除 String idString = String.valueOf(feedItemBean.getItemId());
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

		// 设置删除标记，用于同步删除
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
	 * 更新订阅信息
	 * 
	 * @param feedItemBean
	 *            订阅信息
	 * @return 是否成功更新
	 */
	public boolean updateItem(FeedItemBean feedItemBean) {

		int itemId = feedItemBean.getItemId();
		if (itemId <= -1) {// 防止非法更新
			throw new RuntimeException("Do not update non-set id item");
		}

		String isDeleted = feedItemBean.getDeleteFlag();
		if (isDeleted == "-1") {// 已经标记为删除的对象
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
	 * 无条件查询所有的item
	 * 
	 * @return item的bean集合
	 */
	public ArrayList<FeedItemBean> queryAllItems() {

		return queryItems(null, null);

	}

	/**
	 * 按条件查询item
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

		while (cursor.moveToNext()) {// 查询所有结果

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
				new String[] { rowIdString }, null, null, null, null);

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

}
