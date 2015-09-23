package pres.nc.maxwell.feedeye.db;

import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.content.ContentValues;
import android.content.Context;
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
	private static final String tableName = "feed_item";
	
	/**
	 * ��ʼ��
	 * @param context ������
	 */
	public FeedItemDAO(Context context) {
		mDatabaseOpenHelper = new DatabaseOpenHelper(context);
		
		//������ǰ�����ݿ⣬����ʹ���߲������������ݿ�
	}
	
	/**
	 * ���һ��������Ϣ
	 * @param feedItemBean ������Ϣ
	 * @return �Ƿ�ɹ����
	 */
	public boolean addItem(FeedItemBean feedItemBean){
		
		ContentValues map = new ContentValues();
		//id����Ҫ���룬���ݿ����ɵ�
		map.put("pic_url", feedItemBean.getPicURL());
		map.put("title", feedItemBean.getTitle());
		map.put("preview_content", feedItemBean.getPreviewContent());
		map.put("last_time", TimeUtils.timestamp2String(feedItemBean.getLastTime(), "yyyy-MM-dd HH:mm:ss"));
		
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		
		//��������
		long rowId = db.insert(tableName, null, map);
		
		db.close();
		
		return rowId==-1?false:true;
	}
	
	
	
}
