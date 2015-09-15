package pres.nc.maxwell.feedeye.view.pager.child;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.view.View;
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


}
