package pres.nc.maxwell.feedeye.activity.defalut;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 带后退按钮默认的Activity
 */
public class DefaultNewActivity extends Activity {

	/**
	 * 后退按钮
	 */
	protected ImageView mBack;
	
	/**
	 * 顶部条
	 */
	protected RelativeLayout mBar;
	
	/**
	 * 内容部分
	 */
	protected FrameLayout mContainer;

	/**
	 * 创建时执行
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_default);
		super.onCreate(savedInstanceState);
		
		initView();
		initData();
	}
	
	/**
	 * 初始化View对象
	 */
	protected void initView() {

		mBack = (ImageView) findViewById(R.id.iv_back);
		mBar = (RelativeLayout) findViewById(R.id.rl_bar);
		mContainer = (FrameLayout) findViewById(R.id.fl_container);
		
	}
	
	protected void initData() {
		
		/**
		 * 后退点击事件
		 */
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 关闭当前界面
				finish();

			}

		});
		
	}
	
}
