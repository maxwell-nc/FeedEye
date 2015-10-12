package pres.nc.maxwell.feedeye.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库打开帮助类，创建订阅列表的数据库，用于同步(未来实现)
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	/**
	 * 创建数据库
	 * 
	 * @param context
	 *            上下文
	 */
	public DatabaseOpenHelper(Context context) {

		super(context, "feedlist.db", null, 1);

	}

	/**
	 * 初始化表结构
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlString = "CREATE TABLE feed_item ("
				+ "id integer primary key autoincrement,"// id自增长
				+ "feed_url text not null,"// 订阅的地址
				+ "pic_url text not null,"// 图片URL
				+ "encoding varchar(10) not null,"// 编码方式
				+ "type varchar(10) not null,"// 类型
				+ "title varchar(100) not null,"// 标题
				+ "summary varchar(100),"// 概要内容
				+ "pub_date timeStamp not null DEFAULT (datetime('now','localtime')),"// 时间
				+ "delete_flag char(1) DEFAULT '0'"// 删除标记，用于同步
				+ ");";

		db.execSQL(sqlString);

	}

	/**
	 * 更新时使用
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 无需更新
	}

}
