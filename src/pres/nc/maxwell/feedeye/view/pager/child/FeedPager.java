package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.FeedPagerListViewItem;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����ҳ���Pager
 */
public class FeedPager extends BasePager {

	/**
	 * ��䵽�������е�FrameLayout�е�View����
	 */
	private View mViewContent;

	/**
	 * �����б�
	 */
	private DragRefreshListView mListView;

	/**
	 * �洢�����ݿ��ж�ȡ������Item��Ϣ
	 */
	private ArrayList<FeedItemBean> mItemInfoList;

	/**
	 * ListView�е�Item����
	 */
	private ArrayList<FeedPagerListViewItem> mItemList;

	/**
	 * ListView������ʾ��Item����
	 */
	private ArrayList<FeedPagerListViewItem> mItemShowedList;

	/**
	 * һ��չʾ��Item����
	 */
	private final int SHOW_ITEM_COUNT = 20;

	/**
	 * ListViewΪ����ʾ����ʾͼƬ
	 */
	private ImageView mNothingImg;

	/**
	 * ListView����������
	 */
	private FeedPagerListViewAdapter mListViewAdapter;

	public DragRefreshListView getListView() {
		return mListView;
	}

	/**
	 * ���췽��
	 * 
	 * @param mActivity
	 *            Activity
	 */
	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	/**
	 * ��ʼ��������ʾ
	 */
	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("�ҵĶ���");
		mViewContent = setContainerContent(R.layout.pager_main_feed);
		mListView = (DragRefreshListView) mViewContent
				.findViewById(R.id.lv_feed_list);

		// ListViewΪ��ʱ��ʾ��ͼƬ
		mNothingImg = (ImageView) mViewContent.findViewById(R.id.iv_nothing);

		mItemList = new ArrayList<FeedPagerListViewItem>();
		mItemShowedList = new ArrayList<FeedPagerListViewItem>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// �����첽��ѯ���ݿ�
		new ReadItemInfoDBTask().execute();

		// �������AsyncTaskִ�к��ִ��doWhenFinishedReadDB()
	}

	/**
	 * ��ȡFeedItem��Ϣ���첽����
	 */
	class ReadItemInfoDBTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// ���߳�

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			mItemInfoList = feedItemDAO.queryAllItems();

			LogUtils.i("FeedPager", "��ѯ�����" + mItemInfoList.toString());
			LogUtils.i("FeedPager", "��ѯ�����" + mItemInfoList.size());

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {// ���߳�

			// ���ݿ��ȡ���
			doWhenFinishedReadDB();

		}
	}

	/**
	 * ������ݿ��ȡ��ִ�еĲ���
	 */
	private void doWhenFinishedReadDB() {

		int infoCount = mItemInfoList.size();

		// ���ݶ�����Ϣ������ʼ��mItemList
		for (int i = 0; i < infoCount; i++) {
			FeedPagerListViewItem item = new FeedPagerListViewItem(mActivity);
			mItemList.add(item);
		}

		// ����Ҫ���ص�Item
		insertMoreItem();

		// ����ListView���������������
		if (mItemShowedList.size() == 0) {// ������
			// ����ʾ������
			getLoadingBarView().setVisibility(View.INVISIBLE);

			// ��ʾû�����ݣ���Ҫ���
			mNothingImg.setVisibility(View.VISIBLE);
		} else {// ������
			setListViewData(500);
		}

	}

	/**
	 * ʹ�ù��ܰ�ť����ʼ����ť
	 */
	@Override
	protected void useFunctionButton() {

		super.useFunctionButton();

		mFuncButtonLeft.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_search));
		mFuncButtonLeft.setVisibility(View.VISIBLE);

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_add));
		mFuncButtonRight.setVisibility(View.VISIBLE);
	};

	/**
	 * ����ListView������,��������
	 * 
	 * @param delayTime
	 *            �ӳ�ʱ��
	 */
	private void setListViewData(final int delayTime) {
		new Thread() {
			public void run() {

				// �ӳټ��أ���ֹ����ʱ����
				try {
					Thread.sleep(delayTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mActivity.runOnUiThread(new Runnable() {
					public void run() {

						mListViewAdapter = new FeedPagerListViewAdapter();
						// ����ListView������
						mListView.setAdapter(mListViewAdapter);

						// ����ʾ������
						getLoadingBarView().setVisibility(View.INVISIBLE);
					}
				});

			};
		}.start();
	}

	/**
	 * ����и�������������������
	 */
	private void insertMoreItem() {
		int addCount = 0;

		if (mItemList.size() > SHOW_ITEM_COUNT) {
			addCount = SHOW_ITEM_COUNT;
		} else {
			addCount = mItemList.size();
		}

		for (int i = 0; i < addCount; i++) {
			mItemShowedList.add(mItemList.get(i));
		}
		for (int i = addCount - 1; i >= 0; i--) {
			mItemList.remove(i);
		}
	};

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

			// ��ӡҪ��ʾ����Ŀ
			// LogUtils.w("FeedPager", "ListCount:" + mItemShowedList.size());

			return mItemShowedList.size();
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

				setHolderBeanInfo(holder,mItemInfoList.get(position));
	
				// ����Ƿ���ConvertView��ƽʱ����Ҫ��ӡ����ʱ
				// LogUtils.v("FeedPager", "����View");

			} else {
				// ���ɸ���

				// TODO:��ʱ����������
				FeedPagerListViewItem item = mItemShowedList.get(position);
				
				item.parseBean(mItemInfoList.get(position));

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

	/**
	 * ListViewˢ�¼�����������д����ˢ���߼������������߼�
	 */
	class ListViewRefreshListener implements OnRefreshListener {

		@Override
		public void onDragRefresh() {

			// TODO����ʱģ��ˢ�²���
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// �޸�UI���������߳�ִ��
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mListView.completeRefresh();
							mListViewAdapter.notifyDataSetChanged();
							Toast.makeText(mActivity, "ˢ�³ɹ�",
									Toast.LENGTH_SHORT).show();
						}
					});

				};
			}.start();
		}

		@Override
		public void onLoadingMore() {
			// TODO����ʱģ��ˢ�²���
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// ģ���������
					insertMoreItem();

					// �޸�UI���������߳�ִ��
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mListView.completeRefresh();
							mListViewAdapter.notifyDataSetChanged();
							Toast.makeText(mActivity, "���ظ���ɹ�",
									Toast.LENGTH_SHORT).show();
						}
					});

				}

			}.start();
		}
	}

	
	/**
	 * ����holder������
	 * @param holder ViewHolder����
	 * @param feedItemBean ������Ϣ
	 */
	private void setHolderBeanInfo(ViewHolder holder,FeedItemBean feedItemBean) {
	
		if (feedItemBean == null) {
			return;
		} 
		
		// ʹ�������������ͼƬ
		new BitmapCacheUtils().displayBitmap(holder.mItemPic, feedItemBean.getPicURL(),
				R.drawable.anim_refresh_rotate);
		holder.mItemTitle.setText(feedItemBean.getTitle());
		holder.mItemPreview.setText(feedItemBean.getPreviewContent());
		holder.mItemTime.setText(TimeUtils.timestamp2String(feedItemBean.getLastTime(),
				"HH:mm"));

	}
	
}
