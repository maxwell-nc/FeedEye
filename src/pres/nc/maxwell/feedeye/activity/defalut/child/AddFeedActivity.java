package pres.nc.maxwell.feedeye.activity.defalut.child;

import android.view.View;
import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;

/**
 * ��Ӷ���ҳ���Activity
 */
public class AddFeedActivity extends DefaultNewActivity {

	/**
	 * ��ʼ��View����
	 */
	@Override
	protected void initView() {
		super.initView();

		View barView = View.inflate(this, R.layout.activity_add_feed_bar, null);
		mBar.addView(barView);

	}

	/**
	 * ��ʼ������
	 */
	@Override
	protected void initData() {
		super.initData();
	}

}
