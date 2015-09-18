package pres.nc.maxwell.feedeye.view.pager;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BasePager {

	protected Activity mActivity;
	protected View mBaseView; // ��������
	protected TextView mTitle; // ����
	protected ImageView mFuncButtonLeft; // ���ܰ�ť��
	protected ImageView mFuncButtonRight; // ���ܰ�ť��
	protected FrameLayout mContainer; // ��������
	protected ProgressBar mLoadingBar; // ����ͼ��

	public TextView getTitleView() {
		return mTitle;
	}

	/**
	 * �����ȵ���useFunctionButton()������ʼ�����ܰ�ť
	 */
	public ImageView getFuncButtonLeftView() {
		return mFuncButtonLeft;
	}

	/**
	 * �����ȵ���useFunctionButton()������ʼ�����ܰ�ť
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
	 * ���캯��������ʹ��ViewPager��Activity
	 * 
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

		// ���View�����Ƿ�Ϊnull
		// LogUtils.v("BasePager", mBaseView == null ? "BaseView null":
		// "BaseView not null");

		mTitle = (TextView) mBaseView.findViewById(R.id.tv_title);
		mContainer = (FrameLayout) mBaseView.findViewById(R.id.fl_container);
		mLoadingBar = (ProgressBar) mBaseView.findViewById(R.id.pb_loading);
	}

	/**
	 * ����ʹ��FunctionButton���ܣ���ʼ�����ܰ�ťView������������
	 */
	protected void useFunctionButton() {
		mFuncButtonLeft = (ImageView) mBaseView
				.findViewById(R.id.iv_func_btn_left);
		mFuncButtonRight = (ImageView) mBaseView
				.findViewById(R.id.iv_func_btn_right);
	}

	/**
	 * ��ʼ�����ݣ�����ʵ��
	 */
	protected void initData() {
	}

	/**
	 * ����Pager��View����
	 * 
	 * @return ��������Pager
	 */
	public View getView() {
		return mBaseView;
	}

	/**
	 * ���FrameLayout���֣��ɼ̳е��������
	 * 
	 * @param resourceId
	 *            ��Դid
	 * @return �������Ķ�������
	 */
	protected View setContainerContent(int resourceId) {
		return View.inflate(mActivity, resourceId, mContainer);
	}

}
