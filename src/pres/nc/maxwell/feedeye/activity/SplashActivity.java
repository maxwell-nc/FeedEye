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
		//不显示标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		initView();
		showVersionInSplash();

		new Thread() {

			// TODO: 以后可能回从服务器检查更新
			@Override
			public void run() {
				SystemClock.sleep(1500);
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(intent);
				finish();
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

	/**
	 * 在Splash点击返回按钮
	 */
	@Override
	public void onBackPressed() {
		//不作处理
		//super.onBackPressed();
	}
}
