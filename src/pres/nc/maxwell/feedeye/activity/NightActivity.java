package pres.nc.maxwell.feedeye.activity;

import pres.nc.maxwell.feedeye.utils.AppSettingUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 带夜间模式的Activitiy
 */
public class NightActivity extends Activity {

	private WindowManager mWindowManager;
	private TextView nightCover;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		// 设置夜间模式
		String dayNight = AppSettingUtils.get(this,
				AppSettingUtils.KEY_DAY_NIGHT, "day");
		if ("day".equals(dayNight)) {
			setDay();
		} else {
			setNight();
		}

	}

	/**
	 * 设置夜间模式
	 */
	public void setNight() {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		lp.gravity = Gravity.BOTTOM;// 可以自定义显示的位置
		lp.y = 0;// 距离底部的距离
		nightCover = new TextView(this);

		nightCover.setBackgroundColor(0x99000000);
		mWindowManager.addView(nightCover, lp);
	}

	/**
	 * 设置日间模式
	 */
	public void setDay() {
		if (nightCover != null) {
			mWindowManager.removeView(nightCover);
		}
	}

}
