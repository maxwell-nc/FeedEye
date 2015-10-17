package pres.nc.maxwell.feedeye.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * ������ĳ��������ĶԻ���
 */
public class MainThemeLongClickDialog {

	/**
	 * ����������
	 */
	public interface DialogDataAdapter {

		/**
		 * ��ȡ�Ի���Ĳ���ID
		 * @return �Ի��򲼾�ID
		 */
		public int getLayoutViewId();

		/**
		 * ��ȡTextView��id����
		 * @return TextView��id����
		 */
		public int[] getTextViewResIds();

		/**
		 * ��ȡÿ����Ŀ�ĵ������������
		 * @param alertDialog ��ǰ��ʾ�ĶԻ���
		 * @return ÿ����Ŀ�ĵ������������
		 */
		public OnClickListener[] getItemOnClickListener(AlertDialog alertDialog);

	}

	/**
	 * ������Activity
	 */
	private Activity mActivity;
	
	/**
	 * ��������ȥ
	 */
	private DialogDataAdapter mAdapter;

	public MainThemeLongClickDialog(Activity activity,
			DialogDataAdapter adapter) {
		this.mActivity = activity;
		this.mAdapter = adapter;
	}

	/**
	 * ��ʼ��������ť����
	 * 
	 * @param alertView
	 *            View������
	 * @param resIds
	 *            ��Դid���������
	 * @return ����˳���TextView��������
	 */
	public TextView[] initTextButtonView(View alertView, int... resIds) {
		TextView[] textViews = new TextView[resIds.length];
		for (int i = 0; i < resIds.length; i++) {
			textViews[i] = (TextView) alertView.findViewById(resIds[i]);
		}
		return textViews;
	}

	/**
	 * ��ʾ�Ի���
	 */
	public void show() {

		// ���������Ĳ���
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
	 * ���볤����������Ŀλ�ú͵�ǰ�Ի���ļ�����
	 */
	public static abstract class AlertDialogOnClickListener implements OnClickListener {

		/**
		 * �����λ�ã�ListView�е�λ�ã�
		 */
		protected int position;
		
		/**
		 * �Ի���
		 */
		protected AlertDialog alertDialog;

		/**
		 * ���볤����������Ŀλ�ú͵�ǰ�Ի���
		 * @param position ������������Ŀλ��
		 * @param alertDialog ��ǰ�Ի���
		 */
		public AlertDialogOnClickListener(int position, AlertDialog alertDialog) {
			this.position = position;
			this.alertDialog = alertDialog;
		}
	}
}
