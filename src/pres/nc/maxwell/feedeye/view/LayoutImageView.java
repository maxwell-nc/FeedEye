package pres.nc.maxwell.feedeye.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 封装OnLayoutChangeListener的ImageView，为兼容低版本
 */
public class LayoutImageView extends ImageView {

	public LayoutImageView(Context context) {
		super(context);
	}
	
	public LayoutImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LayoutImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 兼容低版本的监听器集合
	 */
	private ArrayList<SupportOnLayoutChangeListener> onLayoutChangeListeners;

	/**
	 * 添加布局改变监听器
	 * @param listener 监听器
	 */
	public void addOnLayoutChangeListener(SupportOnLayoutChangeListener listener) {

		if (onLayoutChangeListeners == null) {
			onLayoutChangeListeners = new ArrayList<SupportOnLayoutChangeListener>();
		}
		if (!onLayoutChangeListeners.contains(listener)) {// 不重复添加
			onLayoutChangeListeners.add(listener);
		}

	}

	/**
	 * 移除监听器
	 * @param listener 监听器
	 */
	public void removeOnLayoutChangeListener(
			SupportOnLayoutChangeListener listener) {

		if (onLayoutChangeListeners == null || listener == null) {
			return;
		}
		onLayoutChangeListeners.remove(listener);;// 先不删除

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (onLayoutChangeListeners != null) {

			@SuppressWarnings("unchecked")
			ArrayList<SupportOnLayoutChangeListener> listenersCopy = (ArrayList<SupportOnLayoutChangeListener>) onLayoutChangeListeners
					.clone();//使用克隆集合，防止多线程操作同一集合问题
			int numListeners = listenersCopy.size();
			for (int i = 0; i < numListeners; ++i) {
				listenersCopy.get(i).onLayoutChange(this);
			}
			
		}

	}

	/**
	 * 兼容低版本的布局改变监听器
	 */
	public interface SupportOnLayoutChangeListener {
		
		/**
		 * 布局改变
		 * @param thisView 改变的View
		 */
		public void onLayoutChange(LayoutImageView thisView);
		
	}
}
