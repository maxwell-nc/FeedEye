package pres.nc.maxwell.feedeye.view.pager.child;

import android.app.Activity;
import android.view.View;
import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.pager.BasePager;

/**
 * 设置页面的Pager
 */
public class SettingPager extends BasePager {

	public SettingPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();
		mTitle.setText("设置");

		mViewContent = setContainerContent(R.layout.pager_setting);
		
		getLoadingBarView().setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void initData() {
		super.initData();
	}
	
}
