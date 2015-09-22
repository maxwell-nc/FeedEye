package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.FeedPagerListViewItem;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
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

	private View mViewContent; // ��䵽�������е�FrameLayout�е�View����

	private DragRefreshListView mListView;// �����б�
	private ArrayList<FeedPagerListViewItem> mItemList;// ListView�е�Item����
	private ArrayList<FeedPagerListViewItem> mItemShowedList;// ListView������ʾ��Item����

	private final int SHOW_ITEM_COUNT = 20;// һ��չʾ��Item����

	private FeedPagerListViewAdapter mListViewAdapter;

	private ImageView mNothingImg;// ListViewΪ����ʾ����ʾͼƬ

	public DragRefreshListView getListView() {
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
		mListView = (DragRefreshListView) mViewContent
				.findViewById(R.id.lv_feed_list);

		mNothingImg = (ImageView) mViewContent.findViewById(R.id.iv_nothing);

		mItemList = new ArrayList<FeedPagerListViewItem>();
		mItemShowedList = new ArrayList<FeedPagerListViewItem>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// TODO:��ʱ����������
		for (int i = 0; i < 2000; i++) {
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

		mListView.setOnRefreshListener(new ListViewRefreshListener());

		// ���õ��ͼƬ���
		mNothingImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO:ִ����Ӷ��Ĳ���
			}

		});

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
	 * ����ListView������
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

				holder.mItemTitle.setText("���ö���" + position);
				//holder.mItemPic.setImageDrawable(mActivity.getResources()
				//		.getDrawable(R.drawable.btn_navi_favor_selected));

				//���Դ���
				new BitmapCacheUtils()
						.displayBitmapWithLoadingImage(holder.mItemPic,
								"https://avatars3.githubusercontent.com/u/14196813?v=3&s="+position,R.drawable.anim_refresh_rotate);

				// ����Ƿ���ConvertView��ƽʱ����Ҫ��ӡ����ʱ
				// LogUtils.v("FeedPager", "����View");

			} else {
				// ���ɸ���

				// TODO:��ʱ����������
				FeedPagerListViewItem item = mItemShowedList.get(position);
				item.initListViewItem();
				item.getItemTitle().setText("�´�������" + position);
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
}
