package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 底部导航栏
 */
public class NavigationButtonGroupView extends FrameLayout {


	private View mView;
	private RadioGroup mNaviBtnGroup;

	private RadioButton mNaviBtnFeed;
	private RadioButton mNaviBtnDiscover;
	private RadioButton mNaviBtnFavor;
	private RadioButton mNaviBtnSetting;

	public RadioGroup getmNaviBtnGroup() {
		return mNaviBtnGroup;
	}

	public RadioButton getmNaviBtnFeed() {
		return mNaviBtnFeed;
	}

	public RadioButton getmNaviBtnDiscover() {
		return mNaviBtnDiscover;
	}

	public RadioButton getmNaviBtnFavor() {
		return mNaviBtnFavor;
	}

	public RadioButton getmNaviBtnSetting() {
		return mNaviBtnSetting;
	}

	
	
	public NavigationButtonGroupView(Context context) {
		super(context);
		initView(context);
	}

	public NavigationButtonGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	
	/**
	 * 初始化布局和子View对象
	 * @param context 上下文
	 */
	private void initView(Context context) {
		//填充到ViewGroup中
		mView = View.inflate(context, R.layout.view_mavi_button_group, this);
		
		mNaviBtnGroup = (RadioGroup)mView.findViewById(R.id.rg_navi);
		mNaviBtnFeed = (RadioButton)mView.findViewById(R.id.rb_feed);
		mNaviBtnDiscover = (RadioButton)mView.findViewById(R.id.rb_discover);
		mNaviBtnFavor = (RadioButton)mView.findViewById(R.id.rb_favor);
		mNaviBtnSetting = (RadioButton)mView.findViewById(R.id.rb_setting);	
	}

	
	

}
