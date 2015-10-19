package pres.nc.maxwell.feedeye.activity.defalut;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 带后退按钮默认的Activity
 */
public class DefaultNewActivity extends Activity {

	/**
	 * 后退按钮
	 */
	protected ImageView mBack;

	/**
	 * 顶部容器
	 */
	protected FrameLayout mBar;

	/**
	 * 内容容器
	 */
	protected FrameLayout mContainer;

	/**
	 * 顶部View
	 */
	protected View mCustomBarView;

	/**
	 * 内容View
	 */
	protected View mCustomContainerView;

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
	 * 添加子View
	 */
	protected void addView(int barViewId, int containerViewId) {
		
		if (barViewId != -1) {
			mCustomBarView = View.inflate(this, barViewId, null);
			mBar.addView(mCustomBarView);
		}

		if (containerViewId != -1) {
			mCustomContainerView = View.inflate(this, containerViewId, null);
			mContainer.addView(mCustomContainerView);
		}

	}

	/**
	 * 把后退图片换成关闭
	 */
	protected void setAsCloseImage() {
		mBack.setImageResource(R.drawable.btn_close);
	}

	/**
	 * 初始化View对象
	 */
	protected void initView() {

		mBack = (ImageView) findViewById(R.id.iv_back);
		mBar = (FrameLayout) findViewById(R.id.fl_bar);
		mContainer = (FrameLayout) findViewById(R.id.fl_container);

	}

	/**
	 * 在关闭界面前处理事情
	 * 
	 * @return 是否允许关闭
	 */
	protected boolean beforeClose() {
		return true;
	}

	/**
	 * 初始化数据
	 */
	protected void initData() {

		/**
		 * 后退点击事件
		 */
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 在关闭界面前处理事情
				if (beforeClose()) {
					// 关闭当前界面
					finish();
				}

			}

		});

	}

}
