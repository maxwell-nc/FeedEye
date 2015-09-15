package pres.nc.maxwell.feedeye.view.pager.child;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 订阅页面的Pager
 */
public class FeedPager extends BasePager {

	private View mViewContent; // 填充到父布局中的FrameLayout中的View对象
	private ListView mListView;// 订阅列表


	public ListView getListView() {
		return mListView;
	}

	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("订阅列表");
		mViewContent = setContainerContent(R.layout.pager_main_feed);
		mListView = (ListView) mViewContent.findViewById(R.id.lv_feed_list);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		//TODO:暂时测试填充数据
		mListView.setAdapter(new BaseAdapter() {
			
			@Override
			public int getCount() {
				return 15;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = View.inflate(mActivity, R.layout.view_lv_item_feed, null);
				return view;
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			
		});
	}

}
