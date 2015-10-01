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
 * ������ľ����
 */
public class ThemeAlertDialog {

	/**
	 * ������ʾ��Activity
	 */
	private Activity mActivity;

	public ThemeAlertDialog(Activity activity) {
		this.mActivity = activity;
	}

	/**
	 * ������ľ��������������
	 */
	public interface ThemeAlertDialogAdapter {

		/**
		 * ���ñ���
		 * @return ����
		 */
		public String getTitle();

		/**
		 * ����Container
		 * @return �Զ����View
		 */
		public View getContentView();

		/**
		 * �޸Ļ����ľ����View
		 * @param title ����
		 * @param container ����
		 * @param confirmButtom ȷ�ϰ�ť
		 * @param cancelButtom ȡ����ť
		 */
		public void changeViewAtLast(TextView title, FrameLayout container,
				TextView confirmButtom, TextView cancelButtom);
		
		/**
		 * ���ȷ�ϰ�ť�ĵ��������
		 * @param alertDialog ��ʾ�еľ����
		 * @return ������
		 */
		public OnClickListener getOnConfirmClickLister(final AlertDialog alertDialog);
		
		/**
		 * ���ȡ����ť�ĵ��������
		 * @param alertDialog ��ʾ�еľ����
		 * @return ������
		 */
		public OnClickListener getOnCancelClickLister(final AlertDialog alertDialog);
	}

	/**
	 * ��������������ʾ
	 * @param adapter ����������
	 */
	public void setAdapter(ThemeAlertDialogAdapter adapter) {

		// ��û�����View
		View baseView = View.inflate(mActivity,
				R.layout.alert_dialog_theme_base, null);

		TextView title = (TextView) baseView.findViewById(R.id.tv_title);
		FrameLayout container = (FrameLayout) baseView
				.findViewById(R.id.fl_container);
		TextView confirmButtom = (TextView) baseView.findViewById(R.id.tv_yes);
		TextView cancelButtom = (TextView) baseView.findViewById(R.id.tv_no);

		// ����û����õ�View
		View userView = adapter.getContentView();
		if (userView!=null) {
			container.addView(userView);
		}
		
		// ���ñ���
		String customTitle = adapter.getTitle();
		if(!TextUtils.isEmpty(customTitle)){
			title.setText(customTitle);
		}
		

		// �û��Զ�������
		adapter.changeViewAtLast(title, container, confirmButtom, cancelButtom);

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		builder.setView(baseView);

		AlertDialog alertDialog = builder.show();
		

		//����ȷ�ϵ���¼�
		OnClickListener customConfirmClickLister = adapter.getOnConfirmClickLister(alertDialog);
		if (customConfirmClickLister!=null) {
			confirmButtom.setOnClickListener(customConfirmClickLister);
		}
		
		//����ȡ������¼�
		OnClickListener customCancelClickLister = adapter.getOnCancelClickLister(alertDialog);
		if (customCancelClickLister!=null) {
			cancelButtom.setOnClickListener(customCancelClickLister);
		}
		
	}

}
