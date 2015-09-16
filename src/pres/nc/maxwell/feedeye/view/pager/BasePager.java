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
	protected ImageView mFuncButton;	// ���ܰ�ť
	protected FrameLayout mContainer;	// ��������


	public TextView getTitleView() {
		return mTitle;
	}

	public ImageView getFuncButtonView() {
		return mFuncButton;
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
		initData();
	}

	/**
	 * ��ʼ����������ͬ�Ĳ���
	 */
	protected void initView() {
		mBaseView = View.inflate(mActivity, R.layout.pager_base, null);
		
		//���View�����Ƿ�Ϊnull
		LogUtils.v("BasePager", mBaseView==null?"null":"not null");
		
		mTitle = (TextView) mBaseView.findViewById(R.id.tv_title);
		mFuncButton = (ImageView) mBaseView.findViewById(R.id.iv_func_btn);
		mContainer = (FrameLayout) mBaseView.findViewById(R.id.fl_container);
	}

	
	/**
	 * ��ʼ�����ݣ�����ʵ��
	 */
	protected void initData() {
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
