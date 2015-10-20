package pres.nc.maxwell.feedeye.view;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 主题风格的长按弹出的对话框
 */
public class MainThemeOnClickDialog {

	/**
	 * 数据适配器
	 */
	public interface DialogDataAdapter {

		/**
		 * 获得条目的名字数组
		 * 
		 * @return 条目的名字数组
		 */
		public int[] getItemNames();

		/**
		 * 获取每个条目的点击监听器数组
		 * 
		 * @param alertDialog
		 *            当前显示的对话框
		 * @return 每个条目的点击监听器数组
		 */
		public OnClickListener[] getItemOnClickListeners(
				final AlertDialog alertDialog);

	}

	/**
	 * 依附的Activity
	 */
	private Activity mActivity;

	/**
	 * 数据适配器
	 */
	private DialogDataAdapter mAdapter;

	/**
	 * 创建新的对话框，必须手动调用show()方法才显示
	 * 
	 * @param activity
	 *            依附的Activity
	 * @param adapter
	 *            数据适配器
	 * @see #show()
	 */
	public MainThemeOnClickDialog(Activity activity, DialogDataAdapter adapter) {
		this.mActivity = activity;
		this.mAdapter = adapter;
	}

	/**
	 * 显示对话框
	 */
	public void show() {

		// 整个警告框的布局
		LinearLayout wrapper = new LinearLayout(mActivity);
		wrapper.setOrientation(LinearLayout.VERTICAL);
		wrapper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		// 获取布局填充器
		LayoutInflater inflater = mActivity.getLayoutInflater();

		ArrayList<TextView> items = new ArrayList<TextView>();

		// 生成TextView和分割线View
		int itemCount = mAdapter.getItemNames().length;
		for (int i = 0; i < itemCount; i++) {

			TextView item = (TextView) inflater.inflate(
					R.layout.view_main_theme_onclick_dialog_text, wrapper,
					false);
			item.setText(mActivity.getString(mAdapter.getItemNames()[i]));

			items.add(item);// 添加到TextView集合
			wrapper.addView(item);

			if (i != itemCount - 1) {// 最后一个不添加下划线
				View hrLine = inflater.inflate(
						R.layout.view_main_theme_onclick_dialog_hr_line,
						wrapper, false);

				wrapper.addView(hrLine);
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		builder.setView(wrapper);
		final AlertDialog alertDialog = builder.show();

		// 设置点击事件
		OnClickListener[] listeners = mAdapter
				.getItemOnClickListeners(alertDialog);
		for (int i = 0; i < listeners.length; i++) {
			items.get(i).setOnClickListener(listeners[i]);
		}

	}

	/**
	 * 传入长按弹出的条目位置和当前对话框的监听器
	 */
	public static abstract class AlertDialogOnClickListener
			implements
				OnClickListener {

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
		 * 
		 * @param position
		 *            长按弹出的条目位置
		 * @param alertDialog
		 *            当前对话框
		 */
		public AlertDialogOnClickListener(int position, AlertDialog alertDialog) {
			this.position = position;
			this.alertDialog = alertDialog;
		}

		/**
		 * 默认取消对话框
		 */
		@Override
		public void onClick(View v) {
			alertDialog.dismiss();
		}

	}

	/**
	 * 默认的图片点击监听器
	 */
	public static abstract class ImageClickListener implements OnClickListener {

		/**
		 * 消息对话框
		 */
		protected final AlertDialog alertDialog;

		/**
		 * 图片链接
		 */
		protected final String imgLink;

		/**
		 * 初始化
		 * 
		 * @param alertDialog
		 *            消息对话框
		 * @param imgLink
		 *            图片链接
		 */
		protected ImageClickListener(AlertDialog alertDialog, String imgLink) {
			this.alertDialog = alertDialog;
			this.imgLink = imgLink;
		}

		/**
		 * 默认取消对话框
		 */
		@Override
		public void onClick(View v) {
			alertDialog.dismiss();
		}

	}

}
