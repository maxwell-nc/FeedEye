package pres.nc.maxwell.feedeye.db;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.domain.FavorItem;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 收藏信息的DAO类，访问favor_item表
 */
public class FavorItemDAO extends BaseSyncDAO{

	public FavorItemDAO(Context context) {
		super(context, "favor_item");
	}

	/**
	 * 把FavorItem放进Map
	 * 
	 * @param favorItem
	 *            收藏信息
	 * @return 返回map
	 */
	private ContentValues putFavorItemInMap(FavorItem favorItem) {

		ContentValues map = new ContentValues();
		map.put("feed_source_name", favorItem.feedSourceName);
		map.put("feed_url", favorItem.feedURL);

		map.put("title", favorItem.contentInfo.title);
		map.put("description", favorItem.contentInfo.description);
		map.put("pubdate", favorItem.contentInfo.pubDate);
		map.put("content_link", favorItem.contentInfo.link);
		map.put("atom_type", favorItem.contentInfo.contentType);
		map.put("atom_content", favorItem.contentInfo.content);
		map.put("pic_link_1", favorItem.picLink1);
		map.put("pic_link_2", favorItem.picLink2);
		map.put("pic_link_3", favorItem.picLink3);
		map.put("summary", favorItem.summary);

		map.put("delete_flag", favorItem.deleteFlag);

		return map;
	}

	/**
	 * 添加一条收藏信息，自动修改FavorItem的id信息
	 * 
	 * @param favorItem
	 *            收藏消息
	 * @return 是否成功添加
	 */
	public boolean addItem(FavorItem favorItem) {

		int itemId = favorItem.itemId;
		if (itemId != -1) {// 防止非法插入
			throw new RuntimeException(
					"Do not set item id if you want to add to database");
		}

		String isDeleted = favorItem.deleteFlag;
		if (isDeleted == "-1") {// 已经标记为删除的对象
			throw new RuntimeException("Do not update already deleted id item");
		}

		ContentValues map = putFavorItemInMap(favorItem);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		// 插入数据
		long rowId = db.insert(mTableName, null, map);

		LogUtils.i("FavorItemDAO", "添加rowId：" + rowId);

		db.close();
		db = null;

		if (rowId == -1) {// 插入失败

			return false;

		} else {// 插入成功

			// 更新item的id，不要把rowid当id使用
			favorItem.itemId = queryIdByRowId(rowId);

			return true;
		}

	}

	/**
	 * 删除一条收藏信息（非真正删除）
	 * 
	 * @param favorItem
	 *            要删除的收藏信息
	 * @return 是否成功删除
	 */
	public boolean removeItem(FavorItem favorItem) {

		int itemId = favorItem.itemId;
		if (itemId <= -1) {// 防止非法更新
			throw new RuntimeException("Do not delete no-id item");
		}

		// 设置删除标记，用于同步删除
		favorItem.deleteFlag = "-1";

		String idString = String.valueOf(itemId);
		ContentValues map = putFavorItemInMap(favorItem);

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		int rowCount = db.update(mTableName, map, "id=?",
				new String[]{idString});

		db.close();
		db = null;

		return rowCount == 1 ? true : false;
	}

	/**
	 * 更新收藏信息
	 * 
	 * @param favorItem
	 *            收藏信息
	 * @return 是否成功更新
	 */
	public boolean updateItem(FavorItem favorItem) {

		int itemId = favorItem.itemId;
		if (itemId <= -1) {// 防止非法更新
			throw new RuntimeException("Do not update non-set id item");
		}

		String isDeleted = favorItem.deleteFlag;
		if (isDeleted == "-1") {// 已经标记为删除的对象
			throw new RuntimeException("Do not update already deleted id item");
		}

		String idString = String.valueOf(itemId);
		ContentValues map = putFavorItemInMap(favorItem);

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
	 * @param result
	 *            用于存放查询的结果
	 */
	public void queryAllItems(ArrayList<FavorItem> result) {

		queryItems(null, null, false, result);

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
	 * @param result
	 *            用于存放查询的结果,如果为null则抛异常
	 */
	public void queryItems(String selection, String[] selectionArgs,
			boolean isReturnDeletedData, ArrayList<FavorItem> result) {

		// 检查是否创建了
		if (result == null) {
			throw new RuntimeException("result can't not be null");
		}

		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

		Cursor cursor = db.query(mTableName, null, selection, selectionArgs,
				null, null, "id DESC");// 新的数据放在第一

		result.clear();// 清空原来的数据

		while (cursor.moveToNext()) {// 查询所有结果

			if (!isReturnDeletedData) {// 不显示已经删除的数据
				if ("-1".equals(cursor.getString(13))) {// 数据已经被删除，待同步
					continue;
				}
			}

			// 数据未删除
			FavorItem favorItem = new FavorItem();
			
			favorItem.itemId = Integer.parseInt(cursor.getString(0));
			favorItem.feedSourceName = cursor.getString(1);
			favorItem.feedURL = cursor.getString(2);
			
			favorItem.contentInfo.title = cursor.getString(3);
			favorItem.contentInfo.description = cursor.getString(4);
			favorItem.contentInfo.pubDate = cursor.getString(5);
			favorItem.contentInfo.link = cursor.getString(6);
			favorItem.contentInfo.contentType = cursor.getString(7);
			favorItem.contentInfo.content = cursor.getString(8);
			favorItem.picLink1 = cursor.getString(9);
			favorItem.picLink2 = cursor.getString(10);
			favorItem.picLink3 = cursor.getString(11);
			favorItem.summary = cursor.getString(12);
			
			result.add(favorItem);

		}

		db.close();
		db = null;

	}


}
