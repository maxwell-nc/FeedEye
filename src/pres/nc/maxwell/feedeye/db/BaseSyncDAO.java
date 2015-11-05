package pres.nc.maxwell.feedeye.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 带同步的DAO
 */
public class BaseSyncDAO {

	/**
	 * 数据库打开帮助类
	 */
	protected DatabaseOpenHelper mDatabaseOpenHelper;

	/**
	 * 操作的表名
	 */
	protected String mTableName;

	/**
	 * 初始化数据库和表名
	 * 
	 * @param context
	 *            上下文
	 * @param tableName
	 *            表格名
	 */
	public BaseSyncDAO(Context context, String tableName) {
		this.mTableName = tableName;
		mDatabaseOpenHelper = new DatabaseOpenHelper(context);
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
