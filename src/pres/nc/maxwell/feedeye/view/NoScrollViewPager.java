package pres.nc.maxwell.feedeye.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * �޷����һ�����ViewPager 
 */
public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context) {
		super(context);
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	/**
	 * ��ֹ���ش����¼�
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//return super.onInterceptTouchEvent(ev);
		return false;
	}
	
	
	/**
	 * ��ֹ��Ӧ�����¼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//return super.onTouchEvent(ev);
		return false;
	}
}
