package pres.nc.maxwell.feedeye.view;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 封装下拉刷新和加载更多的ListView TODO:考虑是否屏蔽加载中和刷新中的点击事件
 */
public class DragRefreshListView extends ListView {

	/**
	 * 下拉刷新的控件
	 */
	private View mHeaderView;

	/**
	 * 下拉刷新的控件的高度
	 */
	private int mHeaderViewHeight;

	/**
	 * 上拉加载更多的控件
	 */
	private View mFooterView;

	/**
	 * 上拉加载更多的高度
	 */
	private int mFooterViewHeight;

	/**
	 * HeaderView中的时间文本
	 */
	private TextView mHeaderTimeText;

	/**
	 * HeaderView中的提示文本
	 */
	private TextView mHeaderTipsText;

	/**
	 * HeaderView中的旋转图片
	 */
	private ProgressBar mHeaderRotatewPic;

	/**
	 * HeaderView中的箭头图片
	 */
	private ImageView mHeaderArrowPic;

	/**
	 * 是否上拉，如果为真则代表不能显示下拉刷新
	 */
	private boolean istoUp = false;

	/**
	 * 下拉状态
	 */
	private final int STATE_DRAGING = 1;

	/**
	 * 到达松开即可刷新状态
	 */
	private final int STATE_AREADY_REFRESH = 2;

	/**
	 * 刷新中
	 */
	private final int STATE_REFRESHING = 3;

	/**
	 * 当前刷新状态
	 * 
	 * @see #STATE_DRAGING
	 * @see #STATE_AREADY_REFRESH
	 * @see #STATE_REFRESHING
	 */
	private int dragState = STATE_DRAGING;

	/**
	 * 触摸时Y坐标
	 */
	private int downY;

	/**
	 * 移动距离
	 */
	private int deltaY;

	/**
	 * 是否正在加载更多
	 */
	private boolean isLoadingMore = false;

	/**
	 * 是否允许上拉加载更多
	 */
	public boolean isAllowLoadingMore = true;

	/**
	 * 是否允许下拉刷新
	 */
	public boolean isAllowRefresh = true;

	/**
	 * 最小刷新时间
	 */
	public int MinRefreshMills = 1000;

	/**
	 * 最小刷新时间
	 */
	public int MinLoadingMoreMills = 500;

	/**
	 * 下拉刷新和加载更多监听器
	 */
	private OnRefreshListener refreshListener;

	/**
	 * 刷新动画
	 */
	private RotateAnimation mArrowToRefreshAnimation;

	/**
	 * 正常动画
	 */
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

		// 设置selector为透明
		this.setSelector(R.color.transparent);
		// this.setCacheColorHint(R.color.transparent);

		// 初始化默认的监听器
		setOnScrollListener(new EmptyScrollListener());
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
	 * 根据状态修改HeaderView
	 */
	private void changeHeaderView() {

		switch (dragState) {

			case STATE_DRAGING :// 下拉状态
				mHeaderArrowPic.startAnimation(mArrowToNormalAnimation);
				mHeaderTipsText.setText("下拉刷新");
				break;

			case STATE_AREADY_REFRESH :// 松开就刷新状态
				mHeaderArrowPic.startAnimation(mArrowToRefreshAnimation);
				mHeaderTipsText.setText("松开刷新");
				break;

			case STATE_REFRESHING :// 刷新中状态
				mHeaderArrowPic.clearAnimation();// 防止箭头不隐藏
				mHeaderTipsText.setText("正在刷新...");
				mHeaderArrowPic.setVisibility(View.INVISIBLE);
				mHeaderRotatewPic.setVisibility(View.VISIBLE);

				// 延迟执行
				postDelayed(new Runnable() {

					@Override
					public void run() {
						/**
						 * 调用外部写的方法
						 */
						if (refreshListener != null) {
							refreshListener.onDragRefresh();
						}
					}

				}, MinRefreshMills);

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
			mHeaderTimeText.setText("上次刷新：" + TimeUtils.LoopToTransTime("getCurrentTime"));

		}

	}

	/**
	 * 手动设置正在加载中
	 * 
	 * @see #completeRefresh()
	 */
	public void setOnRefreshing() {

		// 判断是否正在加载
		if (isLoadingMore) {
			return;
		}

		// 判断是否正在刷新
		if (dragState != STATE_DRAGING) {
			return;
		}

		refresh();

	}

	/**
	 * 触摸事件拦截
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {

			case MotionEvent.ACTION_DOWN :// 触摸按下

				downY = (int) ev.getY();
				istoUp = false;// 重置

				break;

			case MotionEvent.ACTION_MOVE :// 触摸按住移动

				// 不允许刷新不处理
				if (!isAllowRefresh) {
					break;
				}

				// 刷新时、加载更多时不处理
				if (dragState == STATE_REFRESHING || isLoadingMore) {
					break;
				}

				int currentY = (int) ev.getY();

				deltaY = (currentY - downY) / 3;

				// 第一个项目下拉才显示下拉刷新
				if (deltaY < 0 || getFirstVisiblePosition() > 1) {

					dragState = STATE_DRAGING;
					changeHeaderView();
					istoUp = true;

					break;
				}

				// 上拉后不再能下拉刷新
				if (!istoUp) {

					int paddingTop = -mHeaderViewHeight + deltaY;

					// LogUtils.i("FeedPager", "mHeaderViewHeight:"+
					// mHeaderViewHeight + "\npaddingTop:" + paddingTop+
					// "\ndeltaY:" + deltaY);

					// 限制下拉高度
					if (paddingTop >= -mHeaderViewHeight
							&& getFirstVisiblePosition() < 1) {

						setSelection(0);// 确保向下滚动不影响
						mHeaderView.setPadding(0, paddingTop, 0, 0);

						if (deltaY >= mHeaderViewHeight
								&& dragState == STATE_DRAGING) {

							// 松开就刷新
							dragState = STATE_AREADY_REFRESH;
							changeHeaderView();

						} else if (deltaY < mHeaderViewHeight
								&& dragState == STATE_AREADY_REFRESH) {
							// 松开不刷新
							dragState = STATE_DRAGING;
							changeHeaderView();

						}

						// super.onTouchEvent(ev);
						// return true;

						break;
					}

				}

				break;

			case MotionEvent.ACTION_UP :// 触摸松开

				// 不允许刷新不处理
				if (!isAllowRefresh) {
					break;
				}

				if (dragState == STATE_DRAGING) {// 不刷新
					mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
				} else if (dragState == STATE_AREADY_REFRESH) {// 刷新
					refresh();
				}

				break;

		}

		// 父类中有禁止滑动时点击的逻辑
		return super.onTouchEvent(ev);

	}

	/**
	 * 刷新逻辑
	 */
	private void refresh() {
		mHeaderView.setPadding(0, 0, 0, 0);
		dragState = STATE_REFRESHING;
		changeHeaderView();
		if (!istoUp) {
			setSelection(0);
		}
	}

	/**
	 * 提供监听器给调用者填入刷新逻辑
	 * 
	 * @see SimpleOnRefreshListener
	 */
	public interface OnRefreshListener {
		/**
		 * 下拉刷新时的操作，操作后需要调用completeRefresh()方法
		 * 
		 * @see #completeRefresh()
		 */
		void onDragRefresh();

		/**
		 * 加载更多时的操作，操作后需要调用completeRefresh()方法
		 * 
		 * @see #completeRefresh()
		 */
		void onLoadingMore();
	}

	/**
	 * 空实现，给只需要上拉加载更多或者只需要下拉刷新时设置的监听器
	 * 
	 * @see OnRefreshListener
	 */
	public static class SimpleOnRefreshListener implements OnRefreshListener {

		@Override
		public void onDragRefresh() {
		}

		@Override
		public void onLoadingMore() {
		}

	}

	/**
	 * 监听方法，给外部监听刷新时间
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	/**
	 * 装饰用户的滚动监听器
	 */
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		// 设置滚动监听
		super.setOnScrollListener(new LoadingMoreScrollListener(l));

	}

	/**
	 * 什么都不处理的EmptyScrollListener 上拉加载更多需要滚动监听器， 用于用户不设置监听器时，会使用此作为用户的监听器
	 * 
	 * @see LoadingMoreScrollListener
	 */
	public class EmptyScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	}

	/**
	 * 用于监听滚动事件，上拉加载更多
	 */
	class LoadingMoreScrollListener implements OnScrollListener {

		/**
		 * 用户设置的滚动监听器
		 */
		OnScrollListener userListener;

		/**
		 * 获取 用户设置的滚动监听器
		 * 
		 * @param orginListener
		 *            滚动监听器
		 * @see EmptyScrollListener
		 */
		public LoadingMoreScrollListener(OnScrollListener userListener) {
			this.userListener = userListener;
		}

		/**
		 * 滚动状态改变，用于上拉加载更多
		 */
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// 当滑行空闲时并且不在加载中，最后一条项目在底部时
			if (scrollState == OnScrollListener.SCROLL_STATE_FLING
					|| scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

				if (getLastVisiblePosition() == (getCount() - 1)
						&& !isLoadingMore && dragState != STATE_REFRESHING) {

					if (isAllowLoadingMore) {
						isLoadingMore = true;

						// 显示FooterView
						mFooterView.setPadding(0, 0, 0, 0);
						setSelection(getCount());

						// 延迟执行
						postDelayed(new Runnable() {

							@Override
							public void run() {

								/**
								 * 调用外部写的方法
								 */
								if (refreshListener != null) {
									refreshListener.onLoadingMore();
								}

							}

						}, MinLoadingMoreMills);

					}
				}
			}

			// 用户的操作
			userListener.onScrollStateChanged(view, scrollState);
		}

		/**
		 * 滚动事件
		 */
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			// 用户的操作
			userListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	/**
	 * 带双ArrayList的加载更多的数据适配器
	 * 
	 * @param <T>
	 *            ArrayList的数据泛型
	 */
	public abstract class ArrayListLoadingMoreAdapter<T> extends BaseAdapter {

		/**
		 * 一次展示的Item数量
		 */
		protected final int onceShowedCount;

		/**
		 * 已显示数据列表
		 */
		ArrayList<T> showedList;

		/**
		 * 未显示数据列表
		 */
		ArrayList<T> unshowList;

		/**
		 * 初始化数据
		 * 
		 * @param unshowList
		 *            存放已显示数据列表
		 * @param showedList
		 *            存放未显示数据列表
		 * @param onceShowedCount
		 *            一次展示的Item数量
		 */
		public ArrayListLoadingMoreAdapter(ArrayList<T> unshowList,
				ArrayList<T> showedList, int onceShowedCount) {

			this.unshowList = unshowList;
			this.showedList = showedList;
			this.onceShowedCount = onceShowedCount;

		}

		/**
		 * 返回已经显示的数目
		 */
		@Override
		public int getCount() {
			return showedList.size();
		}

		/**
		 * 如果有更多数据则插入更多数据
		 * 
		 * @return 添加了的条目数
		 */
		public int insertMoreItem() {
			int addCount = 0;// 要添加的数量

			if (unshowList.size() == 0) {// 没数据可以加载了
				return 0;
			}

			// 有剩余数据
			if (unshowList.size() > onceShowedCount) {
				addCount = onceShowedCount;
			} else {// 剩下数据全部加载
				addCount = unshowList.size();

				// 没有更多数据，禁止上拉加载更多
				isAllowLoadingMore = false;
			}

			// 添加到显示列表
			for (int i = 0; i < addCount; i++) {
				showedList.add(unshowList.get(i));
			}
			for (int i = addCount - 1; i >= 0; i--) {
				unshowList.remove(i);
			}

			return addCount;
		};

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}
}
