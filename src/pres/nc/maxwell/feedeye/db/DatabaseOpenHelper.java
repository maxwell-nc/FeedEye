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
				+ "pic_url text not null,"// ͼƬURL
				+ "title varchar(100) not null,"// ����
				+ "preview_content varchar(100),"// Ԥ������
				+ "last_time timeStamp not null DEFAULT (datetime('now','localtime')),"// ʱ��
				+ "delete_flag char(1) DEFAULT '0'"// ɾ����ǣ�����ͬ��
				+ ");";

		db.execSQL(sqlString);

	}

	/**
	 * ����ʱʹ��
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//�������
	}

}
