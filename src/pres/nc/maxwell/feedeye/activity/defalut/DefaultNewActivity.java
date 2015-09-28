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
 * �����˰�ťĬ�ϵ�Activity
 */
public class DefaultNewActivity extends Activity {

	/**
	 * ���˰�ť
	 */
	protected ImageView mBack;
	
	/**
	 * ������
	 */
	protected RelativeLayout mBar;
	
	/**
	 * ���ݲ���
	 */
	protected FrameLayout mContainer;

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
	 * ��ʼ��View����
	 */
	protected void initView() {

		mBack = (ImageView) findViewById(R.id.iv_back);
		mBar = (RelativeLayout) findViewById(R.id.rl_bar);
		mContainer = (FrameLayout) findViewById(R.id.fl_container);
		
	}
	
	protected void initData() {
		
		/**
		 * ���˵���¼�
		 */
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// �رյ�ǰ����
				finish();

			}

		});
		
	}
	
}
