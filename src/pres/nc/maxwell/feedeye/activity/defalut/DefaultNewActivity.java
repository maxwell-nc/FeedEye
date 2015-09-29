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
 * �����˰�ťĬ�ϵ�Activity
 */
public class DefaultNewActivity extends Activity {

	/**
	 * ���˰�ť
	 */
	protected ImageView mBack;

	/**
	 * ��������
	 */
	protected FrameLayout mBar;

	/**
	 * ��������
	 */
	protected FrameLayout mContainer;

	/**
	 * ����View
	 */
	protected View mBarView;

	/**
	 * ����View
	 */
	protected View mContainerView;

	/**
	 * ����ʱִ��
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
	 * �����View
	 */
	protected void addView(int barViewId, int containerViewId) {
		mBarView = View.inflate(this, barViewId, null);
		mBar.addView(mBarView);
		
		mContainerView = View.inflate(this, containerViewId, null);
		mContainer.addView(mContainerView);
	}

	/**
	 * �Ѻ���ͼƬ���ɹر�
	 */
	protected void setAsCloseImage() {
		mBack.setImageResource(R.drawable.btn_close);
	}
	
	/**
	 * ��ʼ��View����
	 */
	protected void initView() {

		mBack = (ImageView) findViewById(R.id.iv_back);
		mBar = (FrameLayout) findViewById(R.id.fl_bar);
		mContainer = (FrameLayout) findViewById(R.id.fl_container);

	}

	/**
	 * �ڹرս���ǰ��������
	 * @return �Ƿ�����ر�
	 */
	protected boolean beforeClose() {
		return true;
	}
	
	/**
	 * ��ʼ������
	 */
	protected void initData() {

		/**
		 * ���˵���¼�
		 */
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//�ڹرս���ǰ��������
				if(beforeClose()){
					// �رյ�ǰ����
					finish();
				}
				
			}

			

		});

	}

}
