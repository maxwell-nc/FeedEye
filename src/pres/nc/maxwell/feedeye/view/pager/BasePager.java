package pres.nc.maxwell.feedeye.view.pager;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BasePager {

	/**
	 * 依附的Activity
	 */
	protected Activity mActivity;
	
	/**
	 * 整个布局
	 */
	protected View mBaseView;
	
	/**
	 * 标题
	 */
	protected TextView mTitle; 
	
	/**
	 * 功能按钮左
	 */
	protected ImageView mFuncButtonLeft; 
	
	/**
	 * 功能按钮右
	 */
	protected ImageView mFuncButtonRight; 
	
	/**
	 * 布局容器
	 */
	protected FrameLayout mContainer; 
	
	/**
	 * 加载图标
	 */
	protected ProgressBar mLoadingBar;

	/**
	 * 填充到父布局中的FrameLayout中的View对象
	 */
	protected View mViewContent;

	
	public TextView getTitleView() {
		return mTitle;
	}

	/**
	 * 必须先调用useFunctionButton()方法初始化功能按钮
	 */
	public ImageView getFuncButtonLeftView() {
		return mFuncButtonLeft;
	}

	/**
	 * 必须先调用useFunctionButton()方法初始化功能按钮
	 */
	public ImageView getFuncButtonRightView() {
		return mFuncButtonRight;
	}

	public FrameLayout getContainerView() {
		return mContainer;
	}

	public ProgressBar getLoadingBarView() {
		return mLoadingBar;
	}

	/**
	 * 构造函数，传入使用ViewPager的Activity
	 * 
	 * @param mActivity
	 */
	public BasePager(Activity mActivity) {
		this.mActivity = mActivity;
		initView();
		initData();
	}

	/**
	 * 初始化布局中相同的部分
	 */
	protected void initView() {
		mBaseView = View.inflate(mActivity, R.layout.pager_base, null);
		
		// 检查View对象是否为null
		// LogUtils.v("BasePager", mBaseView == null ? "BaseView null":
		// "BaseView not null");

		mTitle = (TextView) mBaseView.findViewById(R.id.tv_title);
		mContainer = (FrameLayout) mBaseView.findViewById(R.id.fl_container);
		mLoadingBar = (ProgressBar) mBaseView.findViewById(R.id.pb_loading);
	}

	/**
	 * 子类使用FunctionButton功能，初始化功能按钮View，不主动调用
	 */
	protected void useFunctionButton() {
		mFuncButtonLeft = (ImageView) mBaseView
				.findViewById(R.id.iv_func_btn_left);
		mFuncButtonRight = (ImageView) mBaseView
				.findViewById(R.id.iv_func_btn_right);
	}

	/**
	 * 初始化数据，子类实现
	 */
	protected void initData() {
	}

	/**
	 * 返回Pager的View对象
	 * 
	 * @return 用于填充的Pager
	 */
	public View getView() {
		return mBaseView;
	}

	/**
	 * 填充FrameLayout部分，由继承的子类调用
	 * 
	 * @param resourceId
	 *            资源id
	 * @return 返回填充的对象引用
	 */
	protected View setContainerContent(int resourceId) {
		return View.inflate(mActivity, resourceId, mContainer);
	}

}
