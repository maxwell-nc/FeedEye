package pres.nc.maxwell.feedeye.view.pager.child;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.SearchItemActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.SystemInfoUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
	 * ��������
	 */
	private PopupWindow mPopupWindow;

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

		// ͬ�����ִ��
		// TODO��ͬ��δʵ��
		// new FeedItemDAO(mActivity).completeSynchronized();

	}

	/**
	 * ��Ӳ�������
	 */
	private void addTestData() {

		FeedItemBean feedItemBean = new FeedItemBean();
		feedItemBean.setFeedURL("http://blog.csdn.net/maxwell_nc/rss/list");
		feedItemBean
				.setPicURL("https://avatars3.githubusercontent.com/u/14196813?v=3&s=1");
		feedItemBean.setTitle("�ҵ�GitHub"
				+ new Random().nextInt(Integer.MAX_VALUE));
		feedItemBean.setPreviewContent("������ύ�˺ܶ���룬��ӭ����ҵ�GitHub�ֿ�");
		feedItemBean.setLastTime(new Timestamp(System.currentTimeMillis()));

		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.addItem(feedItemBean);

		mItemInfoShowedList.add(0, feedItemBean);// �嵽��һ��
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
			mListView.setVisibility(View.INVISIBLE);// ��ֹ����BUG

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

		// ������ť�¼�
		mFuncButtonLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, SearchItemActivity.class);
				// ��������
				intent.putExtra("ShowedList", mItemInfoShowedList);
				intent.putExtra("UnShowList", mItemInfoUnshowList);
				mActivity.startActivity(intent);
			}

		});

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_add));// ��Ӱ�ť
		mFuncButtonRight.setVisibility(View.VISIBLE);

		// ��Ӱ�ť�¼�
		mFuncButtonRight.setOnClickListener(new AddFeedOnClickListener());

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

			if (mItemInfoUnshowList.size() == 0) {// û�и�����
				mTitle.setText("�ҵĶ���(" + itemCount + ")");
			} else {
				mTitle.setText("�ҵĶ���(" + itemCount + "+)");
			}

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

				// ����Ƿ���ConvertView��ƽʱ����Ҫ��ӡ����ʱ
				// LogUtils.v("FeedPager", "����View");

			} else {
				// ���ɸ���

				view = (RelativeLayout) View.inflate(mActivity,
						R.layout.view_lv_item_feed, null);

				// ����ViewHolder��¼�Ӻ���View����
				holder = new ViewHolder();

				holder.mItemPic = (ImageView) view
						.findViewById(R.id.iv_item_feed_pic);// ͼƬ
				holder.mItemTitle = (TextView) view
						.findViewById(R.id.tv_item_feed_title);// ����
				holder.mItemPreview = (TextView) view
						.findViewById(R.id.tv_item_feed_preview);// Ԥ��
				holder.mItemTime = (TextView) view
						.findViewById(R.id.tv_item_feed_time);// ʱ��
				holder.mItemCount = (ImageView) view
						.findViewById(R.id.iv_item_feed_count);// ����

				view.setTag(holder);

			}

			parseBean(mItemInfoShowedList.get(position), holder);
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

			new Thread() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					mItemInfoShowedList.clear();
					mItemInfoUnshowList.clear();

					FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
					mItemInfoUnshowList = feedItemDAO.queryAllItems();

					// ����Ҫ���ص�Item
					insertMoreItem();

					// �޸�UI���������߳�ִ��
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mListViewAdapter.notifyDataSetChanged();

							if (mItemInfoUnshowList.size() > 0) {
								// �����ټ��ظ���
								mListView.setAllowLoadingMore(true);
							}

							Toast.makeText(mActivity, "ˢ�³ɹ�",
									Toast.LENGTH_SHORT).show();

							mListView.completeRefresh();
						}
					});

				};
			}.start();
		}

		@Override
		public void onLoadingMore() {

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

							if (addCount == 0) {
								Toast.makeText(mActivity, "û�и���������",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mActivity,
										"�ɹ�������" + addCount + "������",
										Toast.LENGTH_SHORT).show();
							}
							if (mItemInfoUnshowList.size() <= 0) {
								// ��ֹ�ټ��ظ���
								mListView.setAllowLoadingMore(false);
							}
							mListView.completeRefresh();

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
				feedItemBean.getPicURL(), R.anim.refresh_rotate);
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

		/**
		 * ��������¼�
		 */
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				View alertView = View.inflate(mActivity,
						R.layout.view_long_click_lv_feed, null);

				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setView(alertView);
				final AlertDialog alertDialog = builder.show();

				TextView deleteButton = (TextView) alertView
						.findViewById(R.id.tv_delete);

				// ���ɾ����Ŀ
				deleteButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// ���ʱɾ����Ŀ
						deleteFeedItem(position);

						alertDialog.dismiss();// �Ի���ر�
					}

				});

				return false;
			}

		});

		// ���ˢ�¼���
		mListView.setOnRefreshListener(new ListViewRefreshListener());

	}

	/**
	 * ɾ���б��е�Item������ˢ�����ݿ�
	 * 
	 * @param position
	 *            Ҫɾ����Item��λ��
	 */
	private void deleteFeedItem(int position) {

		position = position - mListView.getHeaderViewsCount();// ת����ȷ���±�

		// �����ݿ���ɾ��
		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.removeItem(mItemInfoShowedList.get(position));

		// ��������������ɾ����֪ͨ���ݸ���
		mItemInfoShowedList.remove(position);
		mListViewAdapter.notifyDataSetChanged();
	}

	/**
	 * ��Ӷ��İ�ť�ĵ��������
	 */
	class AddFeedOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (closePopupWindow()) {
				mPopupWindow.dismiss();
				return;
			}

			View popupView = View.inflate(mActivity,
					R.layout.popup_window_add_feed, null);

			// �������
			popupView.measure(0, 0);
			int popupViewWidth = popupView.getMeasuredWidth();
			int popupViewHeight = popupView.getMeasuredHeight();

			mPopupWindow = new PopupWindow(popupView, popupViewWidth,
					popupViewHeight);

			// ͸������
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));

			// ���ö���
			mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

			// ���ý���
			mPopupWindow.setFocusable(true);

			// ��ʾ
			mPopupWindow.showAtLocation(mContainer, Gravity.TOP + Gravity.LEFT,
					(int) mFuncButtonRight.getRight() - popupViewWidth,
					(int) (mFuncButtonRight.getBottom() + SystemInfoUtils
							.getStatusBarHeight(mActivity)));

			// ��Ӷ���
			popupView.findViewById(R.id.pwiv_add).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (mItemInfoShowedList.size() == 0) {// ������ʱ����ʼ��adapter��ֹ��ָ���쳣
								mListViewAdapter = new FeedPagerListViewAdapter(); // ����ListView������
								mListView.setAdapter(mListViewAdapter);
								mListView.setVisibility(View.VISIBLE);
								mNothingImg.setVisibility(View.INVISIBLE);
							}

							// TODO:�����������
							addTestData();

							mListViewAdapter.notifyDataSetChanged();// ˢ��������
							mListView.setSelection(mListView
									.getHeaderViewsCount());// ��ʾ��һ����HeaderView

							closePopupWindow();
						}
					});

			// ����Ӧ��
			popupView.findViewById(R.id.pwiv_share).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// ����
							Intent intent = new Intent();
							intent.setAction("android.intent.action.SEND");
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.setType("text/plain");
							intent.putExtra(Intent.EXTRA_TEXT,
									"�ҷ�����һ�������Ӧ�ã��������ֽ���FeedEye���Ͻ������ذɣ���ַ�ǣ�https://github.com/maxwell-nc/FeedEye");
							mActivity.startActivity(intent);

							closePopupWindow();
						}
					});

			// �����ͷ���
			popupView.findViewById(R.id.pwiv_help).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							//����Ŀҳ��
							Intent intent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("https://github.com/maxwell-nc/FeedEye"));
							mActivity.startActivity(intent);
							closePopupWindow();
						}
					});

		}
	}

	/**
	 * �ر�popupWindow
	 * 
	 * @return �Ƿ�ɹ��ر�
	 */
	private boolean closePopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			return true;
		}
		return false;
	}

}
