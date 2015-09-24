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
 * 订阅页面的Pager
 */
public class FeedPager extends BasePager {

	/**
	 * 填充到父布局中的FrameLayout中的View对象
	 */
	private View mViewContent;

	/**
	 * 订阅列表
	 */
	private DragRefreshListView mListView;

	/**
	 * 存储从数据库中读取出来的Item信息
	 */
	private ArrayList<FeedItemBean> mItemInfoList;

	/**
	 * ListView中的Item集合
	 */
	private ArrayList<FeedPagerListViewItem> mItemList;

	/**
	 * ListView中已显示的Item集合
	 */
	private ArrayList<FeedPagerListViewItem> mItemShowedList;

	/**
	 * 一次展示的Item数量
	 */
	private final int SHOW_ITEM_COUNT = 20;

	/**
	 * ListView为空显示的提示图片
	 */
	private ImageView mNothingImg;

	/**
	 * ListView数据适配器
	 */
	private FeedPagerListViewAdapter mListViewAdapter;

	public DragRefreshListView getListView() {
		return mListView;
	}

	/**
	 * 构造方法
	 * 
	 * @param mActivity
	 *            Activity
	 */
	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	/**
	 * 初始化界面显示
	 */
	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("我的订阅");
		mViewContent = setContainerContent(R.layout.pager_main_feed);
		mListView = (DragRefreshListView) mViewContent
				.findViewById(R.id.lv_feed_list);

		// ListView为空时显示的图片
		mNothingImg = (ImageView) mViewContent.findViewById(R.id.iv_nothing);

		mItemList = new ArrayList<FeedPagerListViewItem>();
		mItemShowedList = new ArrayList<FeedPagerListViewItem>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// 首先异步查询数据库
		new ReadItemInfoDBTask().execute();

		// 在上面的AsyncTask执行后会执行doWhenFinishedReadDB()
	}

	/**
	 * 读取FeedItem信息的异步任务
	 */
	class ReadItemInfoDBTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// 子线程

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			mItemInfoList = feedItemDAO.queryAllItems();

			LogUtils.i("FeedPager", "查询结果：" + mItemInfoList.toString());
			LogUtils.i("FeedPager", "查询结果：" + mItemInfoList.size());

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {// 主线程
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {// 主线程

			// 数据库读取完成
			doWhenFinishedReadDB();

		}
	}

	/**
	 * 完成数据库读取后执行的操作
	 */
	private void doWhenFinishedReadDB() {

		int infoCount = mItemInfoList.size();

		// 根据订阅信息条数初始化mItemList
		for (int i = 0; i < infoCount; i++) {
			FeedPagerListViewItem item = new FeedPagerListViewItem(mActivity);
			mItemList.add(item);
		}

		// 插入要加载的Item
		insertMoreItem();

		// 设置ListView适配器，添加数据
		if (mItemShowedList.size() == 0) {// 无数据
			// 不显示加载条
			getLoadingBarView().setVisibility(View.INVISIBLE);

			// 提示没有数据，需要添加
			mNothingImg.setVisibility(View.VISIBLE);
		} else {// 有数据
			setListViewData(500);
		}

	}

	/**
	 * 使用功能按钮，初始化按钮
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
	 * 设置ListView适配器,加载数据
	 * 
	 * @param delayTime
	 *            延迟时间
	 */
	private void setListViewData(final int delayTime) {
		new Thread() {
			public void run() {

				// 延迟加载，防止进入时卡屏
				try {
					Thread.sleep(delayTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mActivity.runOnUiThread(new Runnable() {
					public void run() {

						mListViewAdapter = new FeedPagerListViewAdapter();
						// 设置ListView适配器
						mListView.setAdapter(mListViewAdapter);

						// 不显示加载条
						getLoadingBarView().setVisibility(View.INVISIBLE);
					}
				});

			};
		}.start();
	}

	/**
	 * 如果有更多数据则插入更多数据
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
	 * 利用ViewHolder优化ListView，减少findViewById的次数
	 */
	static class ViewHolder {
		public ImageView mItemPic; // 图片
		public TextView mItemTitle; // 订阅标题
		public TextView mItemPreview; // 订阅预览
		public TextView mItemTime; // 时间
		public ImageView mItemCount; // 未读数
	}

	/**
	 * 订阅列表的适配器
	 */
	class FeedPagerListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			// 打印要显示的数目
			// LogUtils.w("FeedPager", "ListCount:" + mItemShowedList.size());

			return mItemShowedList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			RelativeLayout view;
			ViewHolder holder;

			// 复用ConvertView
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 复用View并取出holder
				view = (RelativeLayout) convertView;
				holder = (ViewHolder) view.getTag();

				setHolderBeanInfo(holder,mItemInfoList.get(position));
	
				// 检查是否复用ConvertView，平时不需要打印，费时
				// LogUtils.v("FeedPager", "复用View");

			} else {
				// 不可复用

				// TODO:暂时填充测试数据
				FeedPagerListViewItem item = mItemShowedList.get(position);
				
				item.parseBean(mItemInfoList.get(position));

				view = (RelativeLayout) item.getItemView();

				// 利用ViewHolder记录子孩子View对象
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
	 * ListView刷新监听器，用于写下拉刷新逻辑和上拉加载逻辑
	 */
	class ListViewRefreshListener implements OnRefreshListener {

		@Override
		public void onDragRefresh() {

			// TODO：暂时模拟刷新操作
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 修改UI必须在主线程执行
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mListView.completeRefresh();
							mListViewAdapter.notifyDataSetChanged();
							Toast.makeText(mActivity, "刷新成功",
									Toast.LENGTH_SHORT).show();
						}
					});

				};
			}.start();
		}

		@Override
		public void onLoadingMore() {
			// TODO：暂时模拟刷新操作
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 模拟插入数据
					insertMoreItem();

					// 修改UI必须在主线程执行
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mListView.completeRefresh();
							mListViewAdapter.notifyDataSetChanged();
							Toast.makeText(mActivity, "加载更多成功",
									Toast.LENGTH_SHORT).show();
						}
					});

				}

			}.start();
		}
	}

	
	/**
	 * 设置holder的数据
	 * @param holder ViewHolder对象
	 * @param feedItemBean 订阅信息
	 */
	private void setHolderBeanInfo(ViewHolder holder,FeedItemBean feedItemBean) {
	
		if (feedItemBean == null) {
			return;
		} 
		
		// 使用三级缓存加载图片
		new BitmapCacheUtils().displayBitmap(holder.mItemPic, feedItemBean.getPicURL(),
				R.drawable.anim_refresh_rotate);
		holder.mItemTitle.setText(feedItemBean.getTitle());
		holder.mItemPreview.setText(feedItemBean.getPreviewContent());
		holder.mItemTime.setText(TimeUtils.timestamp2String(feedItemBean.getLastTime(),
				"HH:mm"));

	}
	
}
