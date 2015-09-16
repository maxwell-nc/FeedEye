package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.FeedPagerListViewItem;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ����ҳ���Pager
 */
public class FeedPager extends BasePager {

	private View mViewContent; // ��䵽�������е�FrameLayout�е�View����
	
	private ListView mListView;// �����б�
	private ArrayList<FeedPagerListViewItem> mItemList;// ListView�е�Item����
	
	private View headerView;
	private int headerViewHeight;

	public ListView getListView() {
		return mListView;
	}

	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("�����б�");
		mViewContent = setContainerContent(R.layout.pager_main_feed);
		mListView = (ListView) mViewContent.findViewById(R.id.lv_feed_list);

		mItemList = new ArrayList<FeedPagerListViewItem>();

	}

	@Override
	protected void initData() {
		super.initData();

		// TODO:��ʱ����������
		for (int i = 0; i < 150; i++) {
			FeedPagerListViewItem item = new FeedPagerListViewItem(mActivity);
			mItemList.add(item);
		}

		headerView = View.inflate(mActivity,
				R.layout.view_header_listview_refresh, null);
		mListView.addHeaderView(headerView);

		// Ĭ������HeaderView
		headerView.measure(0, 0);// headerView���ڵ㲻��ΪRelativeLayout�������ָ���쳣
		headerViewHeight = headerView.getMeasuredHeight();
		headerView.setPadding(0, -headerViewHeight, 0, 0);

		//����ListView������
		mListView.setAdapter(new FeedPagerListViewAdapter());

		//����ListView�����¼�
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//���ʱY����
				float y = event.getY();
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;

				case MotionEvent.ACTION_MOVE:
					//TODO:�������
					
					break;
				case MotionEvent.ACTION_UP:
					
					break;
				}

				return false;
			}
		});
	}

	/**
	 * ����ViewHolder�Ż�ListView������findViewById�Ĵ���
	 */
	static class ViewHolder {
		public ImageView mItemPic; // ͼƬ
		public TextView mItemTitle; // ���ı���
		public TextView mItemPreview; // ����Ԥ��
		public TextView mItemTime; // ʱ��
		public ImageView mItemCount; // δ����
	}

	/**
	 * �����б��������
	 */
	class FeedPagerListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 150;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			RelativeLayout view;
			ViewHolder holder;

			// ����ConvertView
			if (convertView != null && convertView instanceof RelativeLayout) {
				// ����View��ȡ��holder
				view = (RelativeLayout) convertView;
				holder = (ViewHolder) view.getTag();

				holder.mItemPic.setImageDrawable(mActivity.getResources()
						.getDrawable(R.drawable.btn_navi_favor_selected));

				// ����Ƿ���ConvertView��ƽʱ����Ҫ��ӡ����ʱ
				// LogUtils.v("FeedPager", "����View");

			} else {
				// ���ɸ���

				// TODO:��ʱ����������
				FeedPagerListViewItem item = mItemList.get(position);
				item.getItemTitle().setText("���Զ����Ƿ���ȷ");
				if (position == 5) {

					item.getItemPic().setImageDrawable(
							mActivity.getResources().getDrawable(
									R.drawable.ic_launcher));

				}
				view = (RelativeLayout) item.getItemView();

				// ����ViewHolder��¼�Ӻ���View����
				holder = new ViewHolder();

				holder.mItemPic = item.getItemPic();
				holder.mItemTitle = item.getItemTitle();
				holder.mItemPreview = item.getItemPreview();
				holder.mItemTime = item.getItemTime();
				holder.mItemCount = item.getItemCount();

				view.setTag(holder);
			}

			return view;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

	}
}
