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
	protected View mBaseView;			// ��������
	protected TextView mTitle;			// ����
	protected ImageView mShareButton;	// ����ť
	protected FrameLayout mContainer;	// ��������


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
	 * ���캯��������ʹ��ViewPager��Activity
	 * @param mActivity
	 */
	public BasePager(Activity mActivity) {
		this.mActivity = mActivity;
		initView();
	}

	/**
	 * ��ʼ����������ͬ�Ĳ���
	 */
	protected void initView() {
		mBaseView = View.inflate(mActivity, R.layout.pager_base, null);
		
		//���View�����Ƿ�Ϊnull
		LogUtils.v("BasePager", mBaseView==null?"null":"not null");
		
		mTitle = (TextView) mBaseView.findViewById(R.id.tv_title);
		mShareButton = (ImageView) mBaseView.findViewById(R.id.iv_share);
		mContainer = (FrameLayout) mBaseView.findViewById(R.id.fl_container);
	}

	/**
	 * ����Pager��View����
	 * @return ��������Pager
	 */
	public View getView(){
		return mBaseView;
	}

	/**
	 * ���FrameLayout���֣��ɼ̳е��������
	 * @param resourceId ��Դid
	 * @return �������Ķ�������
	 */
	protected View setContainerContent(int resourceId){
		return View.inflate(mActivity, resourceId, mContainer);
	}
	
}
