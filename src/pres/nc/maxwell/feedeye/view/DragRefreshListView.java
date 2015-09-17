package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.SystemInfoUtils;
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

	private onRefreshingListener refreshingListener;//刷新监听器
	
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

			// 除以2为了减慢下拉速度
			int deltaY = (int) (ev.getY() - downY) / 2;

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
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:// 触摸松开

			if (dragState == STATE_DRAGING) {// 不刷新
				mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

			} else if (dragState == STATE_AREADY_REFRESH) {// 刷新
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
			mHeaderTipsText.setText("下拉刷新");
			break;

		case STATE_AREADY_REFRESH:// 松开就刷新状态
			mHeaderArrowPic.startAnimation(mArrowToRefreshAnimation);
			mHeaderTipsText.setText("松开刷新");
			break;

		case STATE_REFRESHING:// 刷新中状态
			mHeaderArrowPic.clearAnimation();
			mHeaderTipsText.setText("正在刷新...");
			mHeaderArrowPic.setVisibility(View.INVISIBLE);
			mHeaderRotatewPic.setVisibility(View.VISIBLE);

			/**
			 * 调用外部写的方法
			 */
			if(refreshingListener!=null){
				refreshingListener.onRefresh();
			}
			
			break;

		}
	}


	
	/**
	 * 刷新逻辑完成，调用此方法重置HeaderView状态,注意必须在主线程运行此方法
	 */
	public void completeRefresh(){
		
		mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
		dragState = STATE_DRAGING;
		
		mHeaderArrowPic.setVisibility(View.VISIBLE);
		mHeaderRotatewPic.setVisibility(View.INVISIBLE);
		
		mHeaderTipsText.setText("下拉刷新");
		mHeaderTimeText.setText("最后刷新时间："+SystemInfoUtils.getCurrentTime());
	}
	
	
	/**
	 * 提供监听器给调用者填入刷新逻辑
	 */
	public interface onRefreshingListener {
		/**
		 * 刷新时的操作，操作后需要调用completeRefresh()方法
		 */
		void onRefresh();
	}

	/**
	 * 监听方法，给外部监听刷新时间
	 */
	public void setOnRefreshingListener(onRefreshingListener refreshingListener) {
		this.refreshingListener = refreshingListener;
	}
}
