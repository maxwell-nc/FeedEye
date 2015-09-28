package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * PopWindow�ϵ�һ����ť
 */
public class PopupWindowItemView extends LinearLayout {

	/**
	 * ��ǰ����View
	 */
	private LinearLayout mThisView;

	/**
	 * �����ռ�
	 */
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/pres.nc.maxwell.feedeye";

	public PopupWindowItemView(Context context, AttributeSet attrs) {
		super(context, attrs);

		int resId = attrs.getAttributeResourceValue(NAMESPACE, "src", -1);
		String text = attrs.getAttributeValue(NAMESPACE, "text");

		initView(resId, text);
	}

	public PopupWindowItemView(Context context) {
		super(context);
		initView();
	}

	/**
	 * �����������Ե�
	 */
	private void initView() {
		mThisView = (LinearLayout) View.inflate(getContext(),
				R.layout.popup_window_lv_item, this);

	}

	/**
	 * ����������
	 * 
	 * @param resId
	 *            ͼƬId
	 * @param text
	 *            �ı�
	 */
	private void initView(int resId, String text) {
		initView();

		if (resId != -1) {
			((ImageView) mThisView.findViewById(R.id.iv_pic))
					.setImageResource(resId);
		}

		if (!TextUtils.isEmpty(text)) {
			((TextView) mThisView.findViewById(R.id.tv_title)).setText(text);
		}

	}

}
