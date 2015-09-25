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
	 * 保存未显示的Item信息
	 */
	private ArrayList<FeedItemBean> mItemInfoUnshowList;
	
	/**
	 * 保存已经显示的信息
	 */
	private ArrayList<FeedItemBean> mItemInfoShowedList;

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

		mItemInfoUnshowList = new ArrayList<FeedItemBean>();
		mItemInfoShowedList = new ArrayList<FeedItemBean>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// 首先异步查询数据库
		new ReadItemInfoDBTask().execute();

		// 在上面的AsyncTask执行后会执行doWhenFinishedReadDB()

		// 设置点击没有订阅信息的图片添加订阅
		mNothingImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO:执行添加订阅操作

			}

		});

	}

	/**
	 * 添加测试数据
	 */
	private void addTestData() {

		FeedItemBean feedItemBean = new FeedItemBean();
		feedItemBean.setFeedURL("http://blog.csdn.net/maxwell_nc/rss/list");
		feedItemBean
				.setPicURL("https://avatars3.githubusercontent.com/u/14196813?v=3&s=1");
		feedItemBean.setTitle("我的GitHub"+new Random().nextInt(Integer.MAX_VALUE));
		feedItemBean.setPreviewContent("最近又提交了很多代码，欢迎浏览我的GitHub仓库");
		feedItemBean.setLastTime(new Timestamp(System.currentTimeMillis()));

		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.addItem(feedItemBean);

		mItemInfoShowedList.add(0,feedItemBean);//插到第一个
	}

	/**
	 * 读取FeedItem信息的异步任务
	 */
	class ReadItemInfoDBTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// 子线程

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			mItemInfoUnshowList = feedItemDAO.queryAllItems();

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

		// 插入要加载的Item
		insertMoreItem();

		// 设置ListView适配器，添加数据
		if (mItemInfoShowedList.size() == 0) {// 无数据
			// 不显示加载条
			getLoadingBarView().setVisibility(View.INVISIBLE);
			mListView.setVisibility(View.INVISIBLE);//防止下拉BUG

			// 提示没有数据，需要添加
			mNothingImg.setVisibility(View.VISIBLE);
		} else {// 有数据
			setListViewData(500);
		}

		// 添加监听器
		addListViewListener();
	}

	/**
	 * 使用功能按钮，初始化按钮
	 */
	@Override
	protected void useFunctionButton() {

		super.useFunctionButton();

		mFuncButtonLeft.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_search));// 搜索按钮
		mFuncButtonLeft.setVisibility(View.VISIBLE);

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_add));// 添加按钮
		mFuncButtonRight.setVisibility(View.VISIBLE);

		mFuncButtonRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mItemInfoShowedList.size()==0) {//无数据时，初始化adapter防止空指针异常
					mListViewAdapter = new FeedPagerListViewAdapter();
					// 设置ListView适配器
					mListView.setAdapter(mListViewAdapter);
					mListView.setVisibility(View.VISIBLE);
					mNothingImg.setVisibility(View.INVISIBLE);
				}
				
				// TODO:插入测试数据
				addTestData();
				
				mListViewAdapter.notifyDataSetChanged();//刷新适配器
				mListView.setSelection(mListView.getHeaderViewsCount());//显示第一个非HeaderView

			}
		});
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
	 * 
	 * @return 添加了的条目数
	 */
	private int insertMoreItem() {
		int addCount = 0;// 要添加的数量

		if (mItemInfoUnshowList.size() == 0) {// 没数据可以加载了
			return 0;
		}

		// 有剩余数据
		if (mItemInfoUnshowList.size() > SHOW_ITEM_COUNT) {
			addCount = SHOW_ITEM_COUNT;
		} else {
			addCount = mItemInfoUnshowList.size();
		}

		// 添加到显示列表
		for (int i = 0; i < addCount; i++) {
			mItemInfoShowedList.add(mItemInfoUnshowList.get(i));
		}
		for (int i = addCount - 1; i >= 0; i--) {
			mItemInfoUnshowList.remove(i);
		}

		return addCount;
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

			int itemCount = mItemInfoShowedList.size();

			// 打印要显示的数目
			// LogUtils.w("FeedPager", "ListCount:" + mItemShowedList.size());
			mTitle.setText("我的订阅(" + itemCount + ")");

			return itemCount;
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

				parseBean(mItemInfoShowedList.get(position), holder);

				// 检查是否复用ConvertView，平时不需要打印，费时
				// LogUtils.v("FeedPager", "复用View");

			} else {
				// 不可复用

				view = (RelativeLayout) View.inflate(mActivity,
						R.layout.view_lv_item_feed, null);

				// 利用ViewHolder记录子孩子View对象
				holder = new ViewHolder();

				holder.mItemPic = (ImageView) view
						.findViewById(R.id.iv_item_feed_pic);//图片
				holder.mItemTitle = (TextView) view
						.findViewById(R.id.tv_item_feed_title);//标题
				holder.mItemPreview = (TextView) view
						.findViewById(R.id.tv_item_feed_preview);//预览
				holder.mItemTime = (TextView) view
						.findViewById(R.id.tv_item_feed_time);//时间
				holder.mItemCount = (ImageView) view
						.findViewById(R.id.iv_item_feed_count);//数量

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
	 * ListView刷新监听器，用于写下拉刷新逻辑和上拉加载逻辑
	 */
	class ListViewRefreshListener implements OnRefreshListener {

		@Override
		public void onDragRefresh() {

			// TODO：暂时模拟刷新操作
			new Thread() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 修改UI必须在主线程执行
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mListViewAdapter.notifyDataSetChanged();

							mListView.completeRefresh();

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
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 成功插入的数据条数
					final int addCount = insertMoreItem();

					// 修改UI必须在主线程执行
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mListViewAdapter.notifyDataSetChanged();

							mListView.completeRefresh();

							if (addCount == 0) {
								Toast.makeText(mActivity, "没有更多数据了",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mActivity,
										"成功加载了" + addCount + "条数据",
										Toast.LENGTH_SHORT).show();
							}

						}
					});

				}

			}.start();
		}
	}

	/**
	 * 解析feedItemBean并显示
	 * 
	 * @param feedItemBean
	 *            订阅信息
	 * @param viewHolder
	 *            view集合
	 * @return 是否成功解析
	 */
	private boolean parseBean(FeedItemBean feedItemBean, ViewHolder viewHolder) {

		if (feedItemBean == null) {
			return false;
		}

		// 使用三级缓存加载图片
		new BitmapCacheUtils().displayBitmap(viewHolder.mItemPic,
				feedItemBean.getPicURL(), R.drawable.anim_refresh_rotate);
		viewHolder.mItemTitle.setText(feedItemBean.getTitle());
		viewHolder.mItemPreview.setText(feedItemBean.getPreviewContent());
		viewHolder.mItemTime.setText(TimeUtils.timestamp2String(
				feedItemBean.getLastTime(), "HH:mm"));

		return true;
	}

	/**
	 * 添加ListView的各种监听器
	 */
	private void addListViewListener() {

		// 设置每项Item的点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO：待添加点击事件

				// 检查NaturePositionOnItemClickListener是否生效
				LogUtils.w("FeedPager", "item position:" + position);
			}
		});

		// 添加刷新监听
		mListView.setOnRefreshListener(new ListViewRefreshListener());

	}
}
