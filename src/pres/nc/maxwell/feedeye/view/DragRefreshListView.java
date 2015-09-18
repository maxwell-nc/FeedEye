package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.SystemInfoUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
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

	private View mFooterView;
	private int mFooterViewHeight;

	private TextView mHeaderTimeText;// HeaderView中的时间文本
	private TextView mHeaderTipsText;// HeaderView中的提示文本
	private ProgressBar mHeaderRotatewPic;// HeaderView中的旋转图片
	private ImageView mHeaderArrowPic;// HeaderView中的箭头图片

	private int downY; // 触摸时Y坐标

	private final int STATE_DRAGING = 1; // 下拉状态
	private final int STATE_AREADY_REFRESH = 2; // 到达松开即可刷新状态
	private final int STATE_REFRESHING = 3; // 刷新中

	private int dragState = STATE_DRAGING; // 当前刷新状态
	private boolean isLoadingMore = false; // 是否正在加载更多

	private OnRefreshListener refreshListener;// 下拉刷新和加载更多监听器

	/**
	 * 动画集
	 */
	private RotateAnimation mArrowToRefreshAnimation;
	private RotateAnimation mArrowToNormalAnimation;

	public DragRefreshListView(Context context) {
		super(context);
		initView();
	}

	public DragRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		initHeaderView();
		initFooterView();

		// 设置滚动监听监听
		setOnScrollListener(new DefaultOnScrollListener());
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
	 * 初始化FooterView
	 */
	private void initFooterView() {
		mFooterView = View.inflate(getContext(),
				R.layout.view_footer_listview_load, null);

		addFooterView(mFooterView);

		// 默认隐藏footerView
		mFooterView.measure(0, 0);// footerView根节点不能为RelativeLayout，否则空指针异常
		mFooterViewHeight = mHeaderView.getMeasuredHeight();
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

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

			//刷新时不允许再刷新
			if(dragState == STATE_REFRESHING){
				break;
			}
			
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
			if (refreshListener != null) {
				refreshListener.onDragRefresh();
			}

			break;

		}
	}

	/**
	 * 刷新逻辑完成，调用此方法重置HeaderView状态,注意必须在主线程运行此方法
	 */
	public void completeRefresh() {

		if (isLoadingMore) {// 加载更多完成
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
			isLoadingMore = false;

		} else {// 下拉刷新完成
			mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
			dragState = STATE_DRAGING;

			mHeaderArrowPic.setVisibility(View.VISIBLE);
			mHeaderRotatewPic.setVisibility(View.INVISIBLE);

			mHeaderTipsText.setText("下拉刷新");
			mHeaderTimeText.setText("最后刷新时间："
					+ SystemInfoUtils.getCurrentTime());
		}

	}

	/**
	 * 提供监听器给调用者填入刷新逻辑
	 */
	public interface OnRefreshListener {
		/**
		 * 下拉刷新时的操作，操作后需要调用completeRefresh()方法
		 */
		void onDragRefresh();

		/**
		 * 加载更多时的操作，操作后需要调用completeRefresh()方法
		 */
		void onLoadingMore();
	}

	/**
	 * 监听方法，给外部监听刷新时间
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	/**
	 * 用于监听滚动事件，上拉加载更多
	 */
	class DefaultOnScrollListener implements OnScrollListener {
		/**
		 * 滚动状态改变，用于上拉加载更多
		 */
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// 当滑行空闲时并且不在加载中，最后一条项目在底部时
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& getLastVisiblePosition() == (getCount() - 1)
					&& !isLoadingMore && dragState != STATE_REFRESHING) {

				isLoadingMore = true;

				// 显示FooterView
				mFooterView.setPadding(0, 0, 0, 0);
				setSelection(getCount());

				/**
				 * 调用外部写的方法
				 */
				if (refreshListener != null) {
					refreshListener.onLoadingMore();
				}
			}

		}

		/**
		 * 滚动事件
		 */
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// Not Use
		}
	}

}
