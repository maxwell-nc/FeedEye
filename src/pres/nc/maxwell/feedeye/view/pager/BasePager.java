package pres.nc.maxwell.feedeye.view.pager;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class BasePager {

	protected Activity mActivity;
	protected View mBaseView;			// 整个布局
	protected TextView mTitle;			// 标题
	protected ImageView mShareButton;	// 分享按钮
	protected FrameLayout mContainer;	// 布局容器


	public TextView getTitleView() {
		return mTitle;
	}

	public ImageView getShareButtonView() {
		return mShareButton;
	}

	public FrameLayout getContainerView() {
		return mContainer;
	}

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
		mBaseView = View.inflate(mActivity, R.layout.pager_base, null);
		
		//检查View对象是否为null
		LogUtils.v("BasePager", mBaseView==null?"null":"not null");
		
		mTitle = (TextView) mBaseView.findViewById(R.id.tv_title);
		mShareButton = (ImageView) mBaseView.findViewById(R.id.iv_share);
		mContainer = (FrameLayout) mBaseView.findViewById(R.id.fl_container);
	}

	/**
	 * 返回Pager的View对象
	 * @return 用于填充的Pager
	 */
	public View getView(){
		return mBaseView;
	}

	/**
	 * 填充FrameLayout部分，由继承的子类调用
	 * @param resourceId 资源id
	 * @return 返回填充的对象引用
	 */
	protected View setContainerContent(int resourceId){
		return View.inflate(mActivity, resourceId, mContainer);
	}
	
}
