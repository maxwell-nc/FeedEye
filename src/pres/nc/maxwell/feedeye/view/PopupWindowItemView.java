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
 * PopWindow上的一个按钮
 * 
 * @see PopupWindowUtils
 */
public class PopupWindowItemView extends LinearLayout {

	/**
	 * 当前布局View
	 */
	private LinearLayout mThisView;

	/**
	 * 显示文本的TextView
	 */
	private TextView mTextView;

	/**
	 * 显示图标的ImageView
	 */
	private ImageView mImageView;

	/**
	 * 命名空间
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
	 * 不带设置属性的
	 */
	private void initView() {
		mThisView = (LinearLayout) View.inflate(getContext(),
				R.layout.popup_window_lv_item, this);

	}

	/**
	 * 带设置属性
	 * 
	 * @param resId
	 *            图片Id
	 * @param text
	 *            文本
	 */
	private void initView(int resId, String text) {
		initView();

		if (resId != -1) {
			mImageView = (ImageView) mThisView.findViewById(R.id.iv_pic);
			mImageView.setImageResource(resId);
		}

		if (!TextUtils.isEmpty(text)) {
			mTextView = (TextView) mThisView.findViewById(R.id.tv_title);
			mTextView.setText(text);
		}

	}

	/**
	 * 设置文本
	 * @param text 显示的文本
	 */
	public void setText(CharSequence text){
		mTextView.setText(text);
	}
	
	/**
	 * 返回文本
	 * @return 显示的文本
	 */
	public CharSequence getText(){
		return mTextView.getText();
	}
	
	/**
	 * 设置图标
	 * @param resId 图标资源id
	 */
	public void setIcon(int resId){
		mImageView.setImageResource(resId);
	}
	
}
