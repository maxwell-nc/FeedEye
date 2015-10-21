package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.utils.SystemUtils;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 操作PopupWindow的工具类
 * 
 * @see PopupWindowItemView
 */
public class PopupWindowUtils {

	/**
	 * 显示的Activity
	 */
	private Activity activity;

	/**
	 * 要显示的布局
	 */
	public View popupView;

	/**
	 * 显示的popupWindow对象
	 */
	public PopupWindow popupWindow;

	/**
	 * popupWindow宽度
	 */
	public int popupViewWidth;

	/**
	 * popupWindow高度
	 */
	public int popupViewHeight;

	public PopupWindowUtils(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 创建一个新的popupWindow对象
	 * 
	 * @param viewId
	 *            popupWindow的布局id
	 * @return popupWindow对象
	 */
	public PopupWindow newPopupWindowInstance(int viewId) {

		popupView = View.inflate(activity, viewId, null);

		// 测量宽高
		popupView.measure(0, 0);
		popupViewWidth = popupView.getMeasuredWidth();
		popupViewHeight = popupView.getMeasuredHeight();

		popupWindow = new PopupWindow(popupView, popupViewWidth,
				popupViewHeight);

		// 透明背景
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		// 设置动画
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

		// 设置焦点
		popupWindow.setFocusable(true);

		return popupWindow;
	}

	/**
	 * 在某个View附近显示popupWindow,基于gravityView使用 {@link Gravity#NO_GRAVITY}
	 * 
	 * @param gravityView
	 *            对齐的view
	 * @param view
	 *            某个View
	 */
	public void showNearView(View gravityView, View view) {

		if (popupWindow == null) {
			return;
		}

		popupWindow.showAtLocation(gravityView, Gravity.NO_GRAVITY,
				view.getRight() - popupViewWidth, view.getBottom()
						+ SystemUtils.getStatusBarHeight(activity));

	}

	/**
	 * 尝试关闭popupWindow,注意这个方法是静态方法，非操作实例对象中的成员
	 * 
	 * @param 要关闭的popupWindow
	 * @return 是否成功关闭
	 */
	public static boolean tryToClosePopupWindow(PopupWindow popupWindow) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			return true;
		}
		return false;
	}
}
