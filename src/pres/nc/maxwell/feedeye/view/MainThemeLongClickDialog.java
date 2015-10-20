package pres.nc.maxwell.feedeye.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 主题风格的长按弹出的对话框
 */
public class MainThemeLongClickDialog {

	/**
	 * 数据适配器
	 */
	public interface DialogDataAdapter {

		/**
		 * 获取对话框的布局ID
		 * @return 对话框布局ID
		 */
		public int getLayoutViewId();

		/**
		 * 获取TextView的id数组
		 * @return TextView的id数组
		 */
		public int[] getTextViewResIds();

		/**
		 * 获取每个条目的点击监听器数组
		 * @param alertDialog 当前显示的对话框
		 * @return 每个条目的点击监听器数组
		 */
		public OnClickListener[] getItemOnClickListener(final AlertDialog alertDialog);

	}

	/**
	 * 依附的Activity
	 */
	private Activity mActivity;
	
	/**
	 * 数据适配去
	 */
	private DialogDataAdapter mAdapter;

	public MainThemeLongClickDialog(Activity activity,
			DialogDataAdapter adapter) {
		this.mActivity = activity;
		this.mAdapter = adapter;
	}

	/**
	 * 初始化各个按钮对象
	 * 
	 * @param alertView
	 *            View父对象
	 * @param resIds
	 *            资源id（任意个）
	 * @return 返回顺序的TextView对象数据
	 */
	public TextView[] initTextButtonView(View alertView, int... resIds) {
		TextView[] textViews = new TextView[resIds.length];
		for (int i = 0; i < resIds.length; i++) {
			textViews[i] = (TextView) alertView.findViewById(resIds[i]);
		}
		return textViews;
	}

	/**
	 * 显示对话框
	 */
	public void show() {

		// 整个警告框的布局
		View alertView = View.inflate(mActivity, mAdapter.getLayoutViewId(),
				null);

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		builder.setView(alertView);

		TextView[] textViews = initTextButtonView(alertView,
				mAdapter.getTextViewResIds());

		final AlertDialog alertDialog = builder.show();
		for (int i = 0; i < textViews.length; i++) {
			textViews[i].setOnClickListener(mAdapter
					.getItemOnClickListener(alertDialog)[i]);
		}

	}

	/**
	 * 传入长按弹出的条目位置和当前对话框的监听器
	 */
	public static abstract class AlertDialogOnClickListener implements OnClickListener {

		/**
		 * 点击的位置（ListView中的位置）
		 */
		protected int position;
		
		/**
		 * 对话框
		 */
		protected AlertDialog alertDialog;

		/**
		 * 传入长按弹出的条目位置和当前对话框
		 * @param position 长按弹出的条目位置
		 * @param alertDialog 当前对话框
		 */
		public AlertDialogOnClickListener(int position, AlertDialog alertDialog) {
			this.position = position;
			this.alertDialog = alertDialog;
		}
	}
}
