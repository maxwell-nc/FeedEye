package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 主题风格的警告框
 */
public class ThemeAlertDialog {

	/**
	 * 依赖显示的Activity
	 */
	private Activity mActivity;

	public ThemeAlertDialog(Activity activity) {
		this.mActivity = activity;
	}

	/**
	 * 主题风格的警告框数据适配器
	 */
	public interface ThemeAlertDialogAdapter {

		/**
		 * 设置标题
		 * @return 标题
		 */
		public String getTitle();

		/**
		 * 设置Container
		 * @return 自定义的View
		 */
		public View getContentView();

		/**
		 * 修改基础的警告框View
		 * @param title 标题
		 * @param container 容器
		 * @param confirmButtom 确认按钮
		 * @param cancelButtom 取消按钮
		 */
		public void changeViewAtLast(TextView title, FrameLayout container,
				TextView confirmButtom, TextView cancelButtom);
		
		/**
		 * 获得确认按钮的点击监听器
		 * @param alertDialog 显示中的警告框
		 * @return 监听器
		 */
		public OnClickListener getOnConfirmClickLister(final AlertDialog alertDialog);
		
		/**
		 * 获得取消按钮的点击监听器
		 * @param alertDialog 显示中的警告框
		 * @return 监听器
		 */
		public OnClickListener getOnCancelClickLister(final AlertDialog alertDialog);
	}

	/**
	 * 设置适配器并显示
	 * @param adapter 数据适配器
	 */
	public void setAdapter(ThemeAlertDialogAdapter adapter) {

		// 获得基础的View
		View baseView = View.inflate(mActivity,
				R.layout.alert_dialog_theme_base, null);

		TextView title = (TextView) baseView.findViewById(R.id.tv_title);
		FrameLayout container = (FrameLayout) baseView
				.findViewById(R.id.fl_container);
		TextView confirmButtom = (TextView) baseView.findViewById(R.id.tv_yes);
		TextView cancelButtom = (TextView) baseView.findViewById(R.id.tv_no);

		// 添加用户设置的View
		View userView = adapter.getContentView();
		if (userView!=null) {
			container.addView(userView);
		}
		
		// 设置标题
		String customTitle = adapter.getTitle();
		if(!TextUtils.isEmpty(customTitle)){
			title.setText(customTitle);
		}
		

		// 用户自定义设置
		adapter.changeViewAtLast(title, container, confirmButtom, cancelButtom);

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		builder.setView(baseView);

		AlertDialog alertDialog = builder.show();
		

		//设置确认点击事件
		OnClickListener customConfirmClickLister = adapter.getOnConfirmClickLister(alertDialog);
		if (customConfirmClickLister!=null) {
			confirmButtom.setOnClickListener(customConfirmClickLister);
		}
		
		//设置取消点击事件
		OnClickListener customCancelClickLister = adapter.getOnCancelClickLister(alertDialog);
		if (customCancelClickLister!=null) {
			cancelButtom.setOnClickListener(customCancelClickLister);
		}
		
	}

}
