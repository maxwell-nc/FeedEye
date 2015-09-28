package pres.nc.maxwell.feedeye.activity.defalut.child;

import android.view.View;
import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;

/**
 * 添加订阅页面的Activity
 */
public class AddFeedActivity extends DefaultNewActivity {

	/**
	 * 初始化View对象
	 */
	@Override
	protected void initView() {
		super.initView();

		View barView = View.inflate(this, R.layout.activity_add_feed_bar, null);
		mBar.addView(barView);

	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initData() {
		super.initData();
	}

}
