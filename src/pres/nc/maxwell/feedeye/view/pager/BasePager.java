package pres.nc.maxwell.feedeye.view.pager;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BasePager {

	private Activity mActivity;
	protected View mView;				// 整个布局
	protected TextView mTitle;			// 标题
	protected ImageView mShareButton;	// 分享按钮
	protected FrameLayout mContainer;	// 布局容器

	/**
	 * 构造函数，传入使用ViewPager的Activity
	 * @param mActivity
	 */
	public BasePager(Activity mActivity) {

		this.mActivity = mActivity;
		
		initView();
	}

	/**
	 * 初始化布局中相同的部分
	 */
	protected void initView() {
		mView = View.inflate(mActivity, R.layout.pager_base, null);
		
		//检查View对象是否为null
		LogUtils.v("BasePager", mView==null?"null":"not null");
		
		mTitle = (TextView) mView.findViewById(R.id.tv_title);
		mShareButton = (ImageView) mView.findViewById(R.id.iv_share);
		mContainer = (FrameLayout) mView.findViewById(R.id.fl_container);
	}

	/**
	 * 子类实现，填充布局中的FrameLayout（不同的部分）
	 * 
	 * @return 用于填充的View
	 */
	public abstract View getView();

	
	
}
