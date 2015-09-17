package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 封装下拉刷新和加载更多的ListView
 */
public class DragRefreshListView extends ListView {

	private View mHeaderView;
	private int mHeaderViewHeight;

	private TextView mHeaderTimeText;// HeaderView中的时间文本
	private TextView mHeaderTipsText;// HeaderView中的提示文本
	private ProgressBar mHeaderRotatewPic;// HeaderView中的旋转图片
	private ImageView mHeaderArrowPic;// HeaderView中的箭头图片

	private int downY; // 触摸时Y坐标

	private final int STATE_DRAGING = 1; // 下拉状态
	private final int STATE_AREADY_REFRESH = 2; // 到达松开即可刷新状态
	private final int STATE_REFRESHING = 3; // 刷新中

	private int dragState = STATE_DRAGING; // 当前刷新状态

	/**
	 * 动画集
	 */
	private RotateAnimation mArrowToRefreshAnimation;
	private RotateAnimation mArrowToNormalAnimation;

	public DragRefreshListView(Context context) {
		super(context);
		initHeaderView();
	}

	public DragRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
	}

	/**
	 * 初始化HeaderView
	 */
	private void initHeaderView() {
		mHeaderView = View.inflate(getContext(),
				R.layout.view_header_listview_refresh, null);

		mHeaderArrowPic = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
		mHeaderRotatewPic = (ProgressBar) mHeaderView
				.findViewById(R.id.pb_rotate);
		mHeaderTipsText = (TextView) mHeaderView.findViewById(R.id.tv_tips);
		mHeaderTimeText = (TextView) mHeaderView.findViewById(R.id.tv_time);

		addHeaderView(mHeaderView);

		// 默认隐藏HeaderView
		mHeaderView.measure(0, 0);// headerView根节点不能为RelativeLayout，否则空指针异常
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

		initHeaderAnimation();
	}

	private void initHeaderAnimation() {
		// 箭头旋转动画 - 变成松开就刷新
		mArrowToRefreshAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mArrowToRefreshAnimation.setDuration(300);
		mArrowToRefreshAnimation.setFillAfter(true);

		// 箭头旋转动画 - 变成松开不刷新
		mArrowToNormalAnimation = new RotateAnimation(-180, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mArrowToNormalAnimation.setDuration(300);
		mArrowToNormalAnimation.setFillAfter(true);

	}

	/**
	 * 触摸事件拦截
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {

		case MotionEvent.ACTION_DOWN:// 触摸按下

			downY = (int) ev.getY();

			break;

		case MotionEvent.ACTION_MOVE:// 触摸按住移动

			int deltaY = (int) (ev.getY() - downY);
			int paddingTop = -mHeaderViewHeight + deltaY;
			// LogUtils.i("FeedPager", "paddingTop:" + paddingTop);

			// 第一个项目下拉才显示下拉刷新
			if (paddingTop > -mHeaderViewHeight
					&& getFirstVisiblePosition() == 0) {

				mHeaderView.setPadding(0, paddingTop, 0, 0);

				if (paddingTop >= 0 && dragState == STATE_DRAGING) {
					// 松开就刷新
					dragState = STATE_AREADY_REFRESH;
					changeHeaderView();
				} else if (paddingTop <= 0 && dragState == STATE_AREADY_REFRESH) {
					// 松开不刷新
					dragState = STATE_DRAGING;
					changeHeaderView();
				}

			}

			break;

		case MotionEvent.ACTION_UP:// 触摸松开

			if (dragState == STATE_DRAGING) {
				mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

			} else if (dragState == STATE_AREADY_REFRESH) {
				mHeaderView.setPadding(0, 0, 0, 0);
				dragState = STATE_REFRESHING;
				changeHeaderView();
			}

			break;
		}

		return super.onTouchEvent(ev);

	}

	/**
	 * 根据状态修改HeaderView
	 */
	private void changeHeaderView() {

		switch (dragState) {

		case STATE_DRAGING:// 下拉状态
			mHeaderArrowPic.startAnimation(mArrowToNormalAnimation);

			break;

		case STATE_AREADY_REFRESH:// 松开就刷新状态
			mHeaderArrowPic.startAnimation(mArrowToRefreshAnimation);

			break;

		case STATE_REFRESHING:// 刷新中状态
			mHeaderArrowPic.clearAnimation();
			mHeaderArrowPic.setVisibility(View.INVISIBLE);
			mHeaderRotatewPic.setVisibility(View.VISIBLE);

			break;

		}
	}
}
