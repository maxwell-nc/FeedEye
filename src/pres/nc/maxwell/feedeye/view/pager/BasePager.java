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
	protected View mView;				// ��������
	protected TextView mTitle;			// ����
	protected ImageView mShareButton;	// ����ť
	protected FrameLayout mContainer;	// ��������

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
		mView = View.inflate(mActivity, R.layout.pager_base, null);
		
		//���View�����Ƿ�Ϊnull
		LogUtils.v("BasePager", mView==null?"null":"not null");
		
		mTitle = (TextView) mView.findViewById(R.id.tv_title);
		mShareButton = (ImageView) mView.findViewById(R.id.iv_share);
		mContainer = (FrameLayout) mView.findViewById(R.id.fl_container);
	}

	/**
	 * ����ʵ�֣���䲼���е�FrameLayout����ͬ�Ĳ��֣�
	 * 
	 * @return ��������View
	 */
	public abstract View getView();

	
	
}
