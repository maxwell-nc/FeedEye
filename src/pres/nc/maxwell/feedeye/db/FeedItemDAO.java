package pres.nc.maxwell.feedeye.db;

import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.content.ContentValues;
import android.content.Context;
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
		map.put("title", feedItemBean.getTitle());
		map.put("preview_content", feedItemBean.getPreviewContent());
		map.put("last_time", TimeUtils.timestamp2String(
				feedItemBean.getLastTime(), "yyyy-MM-dd HH:mm:ss"));
		map.put("delete_flag", feedItemBean.getDeleteFlag());

		return map;
	}

	/**
	 * 添加一条订阅信息
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

		db.close();

		return rowId == -1 ? false : true;
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
		 */

		// 设置删除标记，用于同步删除
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

		return rowCount == 1 ? true : false;
	}
}
