package pres.nc.maxwell.feedeye.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ���ݿ�򿪰����࣬���������б�����ݿ⣬����ͬ��(δ��ʵ��)
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	/**
	 * �������ݿ�
	 * 
	 * @param context
	 *            ������
	 */
	public DatabaseOpenHelper(Context context) {

		super(context, "feedlist.db", null, 1);

	}

	/**
	 * ��ʼ����ṹ
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlString = "CREATE TABLE feed_item ("
				+ "id integer primary key autoincrement,"// id������
				+ "feed_url text not null,"// ���ĵĵ�ַ
				+ "pic_url text not null,"// ͼƬURL
				+ "encoding varchar(10) not null,"// ���뷽ʽ
				+ "type varchar(10) not null,"// ����
				+ "title varchar(100) not null,"// ����
				+ "summary varchar(100),"// ��Ҫ����
				+ "pub_date timeStamp not null DEFAULT (datetime('now','localtime')),"// ʱ��
				+ "delete_flag char(1) DEFAULT '0'"// ɾ����ǣ�����ͬ��
				+ ");";

		db.execSQL(sqlString);

	}

	/**
	 * ����ʱʹ��
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// �������
	}

}
