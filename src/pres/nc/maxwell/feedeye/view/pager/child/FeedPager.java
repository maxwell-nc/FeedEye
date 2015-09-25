package pres.nc.maxwell.feedeye.view.pager.child;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	 * ����δ��ʾ��Item��Ϣ
	 */
	private ArrayList<FeedItemBean> mItemInfoUnshowList;
	
	/**
	 * �����Ѿ���ʾ����Ϣ
	 */
	private ArrayList<FeedItemBean> mItemInfoShowedList;

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

		mItemInfoUnshowList = new ArrayList<FeedItemBean>();
		mItemInfoShowedList = new ArrayList<FeedItemBean>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// �����첽��ѯ���ݿ�
		new ReadItemInfoDBTask().execute();

		// �������AsyncTaskִ�к��ִ��doWhenFinishedReadDB()

		// ���õ��û�ж�����Ϣ��ͼƬ��Ӷ���
		mNothingImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO:ִ����Ӷ��Ĳ���

			}

		});

	}

	/**
	 * ��Ӳ�������
	 */
	private void addTestData() {

		FeedItemBean feedItemBean = new FeedItemBean();
		feedItemBean.setFeedURL("http://blog.csdn.net/maxwell_nc/rss/list");
		feedItemBean
				.setPicURL("https://avatars3.githubusercontent.com/u/14196813?v=3&s=1");
		feedItemBean.setTitle("�ҵ�GitHub"+new Random().nextInt(Integer.MAX_VALUE));
		feedItemBean.setPreviewContent("������ύ�˺ܶ���룬��ӭ����ҵ�GitHub�ֿ�");
		feedItemBean.setLastTime(new Timestamp(System.currentTimeMillis()));

		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.addItem(feedItemBean);

		mItemInfoShowedList.add(0,feedItemBean);//�嵽��һ��
	}

	/**
	 * ��ȡFeedItem��Ϣ���첽����
	 */
	class ReadItemInfoDBTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// ���߳�

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			mItemInfoUnshowList = feedItemDAO.queryAllItems();

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

		// ����Ҫ���ص�Item
		insertMoreItem();

		// ����ListView���������������
		if (mItemInfoShowedList.size() == 0) {// ������
			// ����ʾ������
			getLoadingBarView().setVisibility(View.INVISIBLE);
			mListView.setVisibility(View.INVISIBLE);//��ֹ����BUG

			// ��ʾû�����ݣ���Ҫ���
			mNothingImg.setVisibility(View.VISIBLE);
		} else {// ������
			setListViewData(500);
		}

		// ��Ӽ�����
		addListViewListener();
	}

	/**
	 * ʹ�ù��ܰ�ť����ʼ����ť
	 */
	@Override
	protected void useFunctionButton() {

		super.useFunctionButton();

		mFuncButtonLeft.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_search));// ������ť
		mFuncButtonLeft.setVisibility(View.VISIBLE);

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_add));// ��Ӱ�ť
		mFuncButtonRight.setVisibility(View.VISIBLE);

		mFuncButtonRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mItemInfoShowedList.size()==0) {//������ʱ����ʼ��adapter��ֹ��ָ���쳣
					mListViewAdapter = new FeedPagerListViewAdapter();
					// ����ListView������
					mListView.setAdapter(mListViewAdapter);
					mListView.setVisibility(View.VISIBLE);
					mNothingImg.setVisibility(View.INVISIBLE);
				}
				
				// TODO:�����������
				addTestData();
				
				mListViewAdapter.notifyDataSetChanged();//ˢ��������
				mListView.setSelection(mListView.getHeaderViewsCount());//��ʾ��һ����HeaderView

			}
		});
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
	 * 
	 * @return ����˵���Ŀ��
	 */
	private int insertMoreItem() {
		int addCount = 0;// Ҫ��ӵ�����

		if (mItemInfoUnshowList.size() == 0) {// û���ݿ��Լ�����
			return 0;
		}

		// ��ʣ������
		if (mItemInfoUnshowList.size() > SHOW_ITEM_COUNT) {
			addCount = SHOW_ITEM_COUNT;
		} else {
			addCount = mItemInfoUnshowList.size();
		}

		// ��ӵ���ʾ�б�
		for (int i = 0; i < addCount; i++) {
			mItemInfoShowedList.add(mItemInfoUnshowList.get(i));
		}
		for (int i = addCount - 1; i >= 0; i--) {
			mItemInfoUnshowList.remove(i);
		}

		return addCount;
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

			int itemCount = mItemInfoShowedList.size();

			// ��ӡҪ��ʾ����Ŀ
			// LogUtils.w("FeedPager", "ListCount:" + mItemShowedList.size());
			mTitle.setText("�ҵĶ���(" + itemCount + ")");

			return itemCount;
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

				parseBean(mItemInfoShowedList.get(position), holder);

				// ����Ƿ���ConvertView��ƽʱ����Ҫ��ӡ����ʱ
				// LogUtils.v("FeedPager", "����View");

			} else {
				// ���ɸ���

				view = (RelativeLayout) View.inflate(mActivity,
						R.layout.view_lv_item_feed, null);

				// ����ViewHolder��¼�Ӻ���View����
				holder = new ViewHolder();

				holder.mItemPic = (ImageView) view
						.findViewById(R.id.iv_item_feed_pic);//ͼƬ
				holder.mItemTitle = (TextView) view
						.findViewById(R.id.tv_item_feed_title);//����
				holder.mItemPreview = (TextView) view
						.findViewById(R.id.tv_item_feed_preview);//Ԥ��
				holder.mItemTime = (TextView) view
						.findViewById(R.id.tv_item_feed_time);//ʱ��
				holder.mItemCount = (ImageView) view
						.findViewById(R.id.iv_item_feed_count);//����

				view.setTag(holder);

				parseBean(mItemInfoShowedList.get(position), holder);

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
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// �޸�UI���������߳�ִ��
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mListViewAdapter.notifyDataSetChanged();

							mListView.completeRefresh();

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
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// �ɹ��������������
					final int addCount = insertMoreItem();

					// �޸�UI���������߳�ִ��
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mListViewAdapter.notifyDataSetChanged();

							mListView.completeRefresh();

							if (addCount == 0) {
								Toast.makeText(mActivity, "û�и���������",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mActivity,
										"�ɹ�������" + addCount + "������",
										Toast.LENGTH_SHORT).show();
							}

						}
					});

				}

			}.start();
		}
	}

	/**
	 * ����feedItemBean����ʾ
	 * 
	 * @param feedItemBean
	 *            ������Ϣ
	 * @param viewHolder
	 *            view����
	 * @return �Ƿ�ɹ�����
	 */
	private boolean parseBean(FeedItemBean feedItemBean, ViewHolder viewHolder) {

		if (feedItemBean == null) {
			return false;
		}

		// ʹ�������������ͼƬ
		new BitmapCacheUtils().displayBitmap(viewHolder.mItemPic,
				feedItemBean.getPicURL(), R.drawable.anim_refresh_rotate);
		viewHolder.mItemTitle.setText(feedItemBean.getTitle());
		viewHolder.mItemPreview.setText(feedItemBean.getPreviewContent());
		viewHolder.mItemTime.setText(TimeUtils.timestamp2String(
				feedItemBean.getLastTime(), "HH:mm"));

		return true;
	}

	/**
	 * ���ListView�ĸ��ּ�����
	 */
	private void addListViewListener() {

		// ����ÿ��Item�ĵ���¼�
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO������ӵ���¼�

				// ���NaturePositionOnItemClickListener�Ƿ���Ч
				LogUtils.w("FeedPager", "item position:" + position);
			}
		});

		// ���ˢ�¼���
		mListView.setOnRefreshListener(new ListViewRefreshListener());

	}
}
