package pres.nc.maxwell.feedeye.activity;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.VersionUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

/**
 * 闪屏页面的Activity
 */
public class SplashActivity extends Activity {

	/**
	 * 版本信息
	 */
	private TextView tv_splash_ver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		initView();
		showVersionInSplash();

		new Thread() {

			@Override
			public void run() {

				SystemClock.sleep(1500);

				finish();
				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));

			};

		}.start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		tv_splash_ver = (TextView) this.findViewById(R.id.tv_splash_ver);
	}

	/**
	 * 在Splash界面显示版本号
	 */
	private void showVersionInSplash() {

		String versionName = VersionUtils.getVersionName(this);
		if (versionName != null) {
			versionName = getResources().getString(R.string.splash_version)
					+ versionName;
			tv_splash_ver.setText(versionName);
		} else {
			tv_splash_ver.setText(R.string.unknown_version);
		}

	}

	/**
	 * 在Splash点击返回按钮
	 */
	@Override
	public void onBackPressed() {
		// 不作处理
		// super.onBackPressed();
	}
}
