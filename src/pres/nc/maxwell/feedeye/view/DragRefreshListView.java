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

	private View mHeaderView;
	private int mHeaderViewHeight;

	private View mFooterView;
	private int mFooterViewHeight;

	private TextView mHeaderTimeText;// HeaderView�е�ʱ���ı�
	private TextView mHeaderTipsText;// HeaderView�е���ʾ�ı�
	private ProgressBar mHeaderRotatewPic;// HeaderView�е���תͼƬ
	private ImageView mHeaderArrowPic;// HeaderView�еļ�ͷͼƬ

	private int downY; // ����ʱY����

	private final int STATE_DRAGING = 1; // ����״̬
	private final int STATE_AREADY_REFRESH = 2; // �����ɿ�����ˢ��״̬
	private final int STATE_REFRESHING = 3; // ˢ����

	private int dragState = STATE_DRAGING; // ��ǰˢ��״̬
	private boolean isLoadingMore = false; // �Ƿ����ڼ��ظ���

	private OnRefreshListener refreshListener;// ����ˢ�ºͼ��ظ��������

	/**
	 * ������
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
	 * ��ʼ��View
	 */
	private void initView() {
		initHeaderView();
		initFooterView();

		// ���ù�����������
		setOnScrollListener(new DefaultOnScrollListener());
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
	 * �����¼�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {

		case MotionEvent.ACTION_DOWN:// ��������

			downY = (int) ev.getY();

			break;

		case MotionEvent.ACTION_MOVE:// ������ס�ƶ�

			//ˢ��ʱ��������ˢ��
			if(dragState == STATE_REFRESHING){
				break;
			}
			
			// ����2Ϊ�˼��������ٶ�
			int deltaY = (int) (ev.getY() - downY) / 2;

			int paddingTop = -mHeaderViewHeight + deltaY;
			// LogUtils.i("FeedPager", "paddingTop:" + paddingTop);

			// ��һ����Ŀ��������ʾ����ˢ��
			if (paddingTop > -mHeaderViewHeight
					&& getFirstVisiblePosition() == 0) {

				mHeaderView.setPadding(0, paddingTop, 0, 0);

				if (paddingTop >= 0 && dragState == STATE_DRAGING) {
					// �ɿ���ˢ��
					dragState = STATE_AREADY_REFRESH;
					changeHeaderView();
				} else if (paddingTop <= 0 && dragState == STATE_AREADY_REFRESH) {
					// �ɿ���ˢ��
					dragState = STATE_DRAGING;
					changeHeaderView();
				}
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:// �����ɿ�

			if (dragState == STATE_DRAGING) {// ��ˢ��
				mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

			} else if (dragState == STATE_AREADY_REFRESH) {// ˢ��
				mHeaderView.setPadding(0, 0, 0, 0);
				dragState = STATE_REFRESHING;
				changeHeaderView();
			}

			break;
		}

		return super.onTouchEvent(ev);

	}

	/**
	 * ����״̬�޸�HeaderView
	 */
	private void changeHeaderView() {

		switch (dragState) {

		case STATE_DRAGING:// ����״̬
			mHeaderArrowPic.startAnimation(mArrowToNormalAnimation);
			mHeaderTipsText.setText("����ˢ��");
			break;

		case STATE_AREADY_REFRESH:// �ɿ���ˢ��״̬
			mHeaderArrowPic.startAnimation(mArrowToRefreshAnimation);
			mHeaderTipsText.setText("�ɿ�ˢ��");
			break;

		case STATE_REFRESHING:// ˢ����״̬
			mHeaderArrowPic.clearAnimation();
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
			mHeaderTimeText.setText("���ˢ��ʱ�䣺"
					+ SystemInfoUtils.getCurrentTime());
		}

	}

	/**
	 * �ṩ������������������ˢ���߼�
	 */
	public interface OnRefreshListener {
		/**
		 * ����ˢ��ʱ�Ĳ�������������Ҫ����completeRefresh()����
		 */
		void onDragRefresh();

		/**
		 * ���ظ���ʱ�Ĳ�������������Ҫ����completeRefresh()����
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
	 * ���ڼ��������¼����������ظ���
	 */
	class DefaultOnScrollListener implements OnScrollListener {
		/**
		 * ����״̬�ı䣬�����������ظ���
		 */
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// �����п���ʱ���Ҳ��ڼ����У����һ����Ŀ�ڵײ�ʱ
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& getLastVisiblePosition() == (getCount() - 1)
					&& !isLoadingMore && dragState != STATE_REFRESHING) {

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

		/**
		 * �����¼�
		 */
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// Not Use
		}
	}

}
