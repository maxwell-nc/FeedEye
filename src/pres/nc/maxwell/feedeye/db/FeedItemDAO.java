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
	private static final String tableName = "feed_item";
	
	/**
	 * 初始化
	 * @param context 上下文
	 */
	public FeedItemDAO(Context context) {
		mDatabaseOpenHelper = new DatabaseOpenHelper(context);
		
		//不必提前打开数据库，可能使用者不立即操作数据库
	}
	
	/**
	 * 添加一条订阅信息
	 * @param feedItemBean 订阅消息
	 * @return 是否成功添加
	 */
	public boolean addItem(FeedItemBean feedItemBean){
		
		ContentValues map = new ContentValues();
		//id不需要插入，数据库生成的
		map.put("pic_url", feedItemBean.getPicURL());
		map.put("title", feedItemBean.getTitle());
		map.put("preview_content", feedItemBean.getPreviewContent());
		map.put("last_time", TimeUtils.timestamp2String(feedItemBean.getLastTime(), "yyyy-MM-dd HH:mm:ss"));
		
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		
		//插入数据
		long rowId = db.insert(tableName, null, map);
		
		db.close();
		
		return rowId==-1?false:true;
	}
	
	
	
}
