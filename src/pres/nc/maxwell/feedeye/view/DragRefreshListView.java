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
 * ��װ����ˢ�ºͼ��ظ����ListView
 */
public class DragRefreshListView extends ListView {

	/**
	 * ����ˢ�µĿؼ�
	 */
	private View mHeaderView;

	/**
	 * ����ˢ�µĿؼ��ĸ߶�
	 */
	private int mHeaderViewHeight;

	/**
	 * �������ظ���Ŀؼ�
	 */
	private View mFooterView;

	/**
	 * �������ظ���ĸ߶�
	 */
	private int mFooterViewHeight;

	/**
	 * HeaderView�е�ʱ���ı�
	 */
	private TextView mHeaderTimeText;

	/**
	 * HeaderView�е���ʾ�ı�
	 */
	private TextView mHeaderTipsText;

	/**
	 * HeaderView�е���תͼƬ
	 */
	private ProgressBar mHeaderRotatewPic;

	/**
	 * HeaderView�еļ�ͷͼƬ
	 */
	private ImageView mHeaderArrowPic;

	/**
	 * ����ʱY����
	 */
	private int downY;

	/**
	 * �Ƿ����������Ϊ�����������ʾ����ˢ��
	 */
	private boolean istoUp = false;

	/**
	 * ����״̬
	 */
	private final int STATE_DRAGING = 1;

	/**
	 * �����ɿ�����ˢ��״̬
	 */
	private final int STATE_AREADY_REFRESH = 2;

	/**
	 * ˢ����
	 */
	private final int STATE_REFRESHING = 3;

	/**
	 * ��ǰˢ��״̬
	 * 
	 * @see #STATE_DRAGING
	 * @see #STATE_AREADY_REFRESH
	 * @see #STATE_REFRESHING
	 */
	private int dragState = STATE_DRAGING;

	/**
	 * �Ƿ����ڼ��ظ���
	 */
	private boolean isLoadingMore = false;

	/**
	 * �Ƿ������������ظ���
	 */
	public boolean isAllowLoadingMore = true;

	/**
	 * �Ƿ���������ˢ��
	 */
	public boolean isAllowRefresh = true;

	/**
	 * ����ˢ�ºͼ��ظ��������
	 */
	private OnRefreshListener refreshListener;

	/**
	 * ˢ�¶���
	 */
	private RotateAnimation mArrowToRefreshAnimation;

	/**
	 * ��������
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
	 * ��ʼ��View
	 */
	private void initView() {
		initHeaderView();
		initFooterView();

		// ����selectorΪ͸��
		this.setSelector(R.color.transparent);
		// this.setCacheColorHint(R.color.transparent);

		// ��ʼ��Ĭ�ϵļ�����
		setOnScrollListener(new EmptyScrollListener());
	}

	/**
	 * ��ʼ��HeaderView
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

		// Ĭ������HeaderView
		mHeaderView.measure(0, 0);// headerView���ڵ㲻��ΪRelativeLayout�������ָ���쳣
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

		initHeaderAnimation();
	}

	private void initHeaderAnimation() {
		// ��ͷ��ת���� - ����ɿ���ˢ��
		mArrowToRefreshAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mArrowToRefreshAnimation.setDuration(300);
		mArrowToRefreshAnimation.setFillAfter(true);

		// ��ͷ��ת���� - ����ɿ���ˢ��
		mArrowToNormalAnimation = new RotateAnimation(-180, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mArrowToNormalAnimation.setDuration(300);
		mArrowToNormalAnimation.setFillAfter(true);

	}

	/**
	 * ��ʼ��FooterView
	 */
	private void initFooterView() {
		mFooterView = View.inflate(getContext(),
				R.layout.view_footer_listview_load, null);

		addFooterView(mFooterView);

		// Ĭ������footerView
		mFooterView.measure(0, 0);// footerView���ڵ㲻��ΪRelativeLayout�������ָ���쳣
		mFooterViewHeight = mHeaderView.getMeasuredHeight();
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

	}

	/**
	 * ����״̬�޸�HeaderView
	 */
	private void changeHeaderView() {

		switch (dragState) {

			case STATE_DRAGING :// ����״̬
				mHeaderArrowPic.startAnimation(mArrowToNormalAnimation);
				mHeaderTipsText.setText("����ˢ��");
				break;

			case STATE_AREADY_REFRESH :// �ɿ���ˢ��״̬
				mHeaderArrowPic.startAnimation(mArrowToRefreshAnimation);
				mHeaderTipsText.setText("�ɿ�ˢ��");
				break;

			case STATE_REFRESHING :// ˢ����״̬
				mHeaderArrowPic.clearAnimation();// ��ֹ��ͷ������
				mHeaderTipsText.setText("����ˢ��...");
				mHeaderArrowPic.setVisibility(View.INVISIBLE);
				mHeaderRotatewPic.setVisibility(View.VISIBLE);

				/**
				 * �����ⲿд�ķ���
				 */
				if (refreshListener != null) {
					refreshListener.onDragRefresh();
				}

				break;
		}

	}

	/**
	 * ˢ���߼���ɣ����ô˷�������HeaderView״̬,ע����������߳����д˷���
	 */
	public void completeRefresh() {

		if (isLoadingMore) {// ���ظ������
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
			isLoadingMore = false;

		} else {// ����ˢ�����
			mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
			dragState = STATE_DRAGING;

			mHeaderArrowPic.setVisibility(View.VISIBLE);
			mHeaderRotatewPic.setVisibility(View.INVISIBLE);

			mHeaderTipsText.setText("����ˢ��");
			mHeaderTimeText.setText("�ϴ�ˢ�£�" + SystemInfoUtils.getCurrentTime());

		}

	}

	/**
	 * �ֶ��������ڼ�����
	 * 
	 * @see #completeRefresh()
	 */
	public void setOnRefreshing() {

		// �ж��Ƿ����ڼ���
		if (isLoadingMore) {
			return;
		}

		// �ж��Ƿ�����ˢ��
		if (dragState != STATE_DRAGING) {
			return;
		}

		refresh();

	}

	/**
	 * �����¼�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {

			case MotionEvent.ACTION_DOWN :// ��������

				downY = (int) ev.getY();
				istoUp = false;// ����

				break;

			case MotionEvent.ACTION_MOVE :// ������ס�ƶ�

				// ������ˢ�²�����
				if (!isAllowRefresh) {
					break;
				}

				// ˢ��ʱ�����ظ���ʱ������
				if (dragState == STATE_REFRESHING || isLoadingMore) {
					break;
				}

				int currentY = (int) ev.getY();

				// ����3Ϊ�˼��������ٶ�
				int deltaY = (currentY - downY) / 3;

				// ��һ����Ŀ��������ʾ����ˢ��
				if (deltaY < 0 || getFirstVisiblePosition() > 1) {

					dragState = STATE_DRAGING;
					changeHeaderView();
					istoUp = true;

					break;
				}

				// ��������������ˢ��
				if (!istoUp) {

					int paddingTop = -mHeaderViewHeight + deltaY;

					// LogUtils.i("FeedPager", "mHeaderViewHeight:"+
					// mHeaderViewHeight + "\npaddingTop:" + paddingTop+
					// "\ndeltaY:" + deltaY);

					// ���������߶�
					if (paddingTop >= -mHeaderViewHeight
							&& getFirstVisiblePosition() < 1) {

						setSelection(0);// ȷ�����¹�����Ӱ��
						mHeaderView.setPadding(0, paddingTop, 0, 0);

						if (deltaY >= mHeaderViewHeight
								&& dragState == STATE_DRAGING) {

							// �ɿ���ˢ��
							dragState = STATE_AREADY_REFRESH;
							changeHeaderView();

						} else if (deltaY < mHeaderViewHeight
								&& dragState == STATE_AREADY_REFRESH) {
							// �ɿ���ˢ��
							dragState = STATE_DRAGING;
							changeHeaderView();

						}

						// super.onTouchEvent(ev);
						// return true;

						break;
					}

				}

				break;

			case MotionEvent.ACTION_UP :// �����ɿ�

				// ������ˢ�²�����
				if (!isAllowRefresh) {
					break;
				}

				downY = -1;// ����

				if (dragState == STATE_DRAGING) {// ��ˢ��
					mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
					if (!istoUp) {
						setSelection(getFirstVisiblePosition());
					}

				} else if (dragState == STATE_AREADY_REFRESH) {// ˢ��
					refresh();
				}

				break;

		}

		// �������н�ֹ����ʱ������߼�
		return super.onTouchEvent(ev);

	}

	/**
	 * ˢ���߼�
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
	 * �ṩ������������������ˢ���߼�
	 */
	public interface OnRefreshListener {
		/**
		 * ����ˢ��ʱ�Ĳ�������������Ҫ����completeRefresh()����
		 * 
		 * @see #completeRefresh()
		 */
		void onDragRefresh();

		/**
		 * ���ظ���ʱ�Ĳ�������������Ҫ����completeRefresh()����
		 * 
		 * @see #completeRefresh()
		 */
		void onLoadingMore();
	}

	/**
	 * �������������ⲿ����ˢ��ʱ��
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	/**
	 * װ���û��Ĺ���������
	 */
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		// ���ù�������
		super.setOnScrollListener(new LoadingMoreScrollListener(l));

	}

	/**
	 * ʲô���������EmptyScrollListener �������ظ�����Ҫ������������ �����û������ü�����ʱ����ʹ�ô���Ϊ�û��ļ�����
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
	 * ���ڼ��������¼����������ظ���
	 */
	class LoadingMoreScrollListener implements OnScrollListener {

		/**
		 * �û����õĹ���������
		 */
		OnScrollListener userListener;

		/**
		 * ��ȡ �û����õĹ���������
		 * 
		 * @param orginListener
		 *            ����������
		 * @see EmptyScrollListener
		 */
		public LoadingMoreScrollListener(OnScrollListener userListener) {
			this.userListener = userListener;
		}

		/**
		 * ����״̬�ı䣬�����������ظ���
		 */
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// �����п���ʱ���Ҳ��ڼ����У����һ����Ŀ�ڵײ�ʱ
			if (scrollState == OnScrollListener.SCROLL_STATE_FLING
					|| scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

				if (getLastVisiblePosition() == (getCount() - 1)
						&& !isLoadingMore && dragState != STATE_REFRESHING) {

					if (isAllowLoadingMore) {
						isLoadingMore = true;

						// ��ʾFooterView
						mFooterView.setPadding(0, 0, 0, 0);
						setSelection(getCount());

						/**
						 * �����ⲿд�ķ���
						 */
						if (refreshListener != null) {
							refreshListener.onLoadingMore();
						}
					}
				}
			}

			// �û��Ĳ���
			userListener.onScrollStateChanged(view, scrollState);
		}

		/**
		 * �����¼�
		 */
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			// �û��Ĳ���
			userListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

}
