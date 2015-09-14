package pres.nc.maxwell.feedeye.activity;


import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Window;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private TextView tv_splash_ver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//����ʾ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		initView();
		showVersionInSplash();

		new Thread() {

			// TODO: �Ժ���ܻشӷ�����������
			@Override
			public void run() {
				SystemClock.sleep(2000);
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(intent);
				finish();
			};

		}.start();

	}

	/**
	 * ��ʼ��View
	 */
	private void initView() {
		tv_splash_ver = (TextView) this.findViewById(R.id.tv_splash_ver);
	}

	/**
	 * ��Splash������ʾ�汾��
	 */
	private void showVersionInSplash() {

		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			// can not reach
			e.printStackTrace();
		}
		
		if (packageInfo != null) {
			String versionName = getResources().getString(
					R.string.splash_version)
					+ packageInfo.versionName;
			tv_splash_ver.setText(versionName);
		} else {
			tv_splash_ver.setText(R.string.unknown_version);
		}

	}

}