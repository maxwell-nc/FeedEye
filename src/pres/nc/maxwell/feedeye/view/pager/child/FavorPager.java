package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.SummaryBodyActivity;
import pres.nc.maxwell.feedeye.db.FavorItemDAO;
import pres.nc.maxwell.feedeye.domain.FavorItem;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.utils.SystemUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.ArrayListLoadingMoreAdapter;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog.MainThemeAlertDialogAdapter;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 收藏页面的Pager
 */
public class FavorPager extends BasePager {

	/**
	 * 页面中的ViewPager对象
	 */
	private ViewPager mContentPager;

	/**
	 * 最近收藏的ListView对象
	 */
	private DragRefreshListView mRecentListView;

	/**
	 * 全部收藏的ListView对象
	 */
	private DragRefreshListView mFullListView;

	/**
	 * 最近收藏信息集合
	 */
	private ArrayList<FavorItem> mRecentItemList;

	/**
	 * 最近收藏显示的数据适配器
	 */
	private RecentListAdapter mRecentListAdapter;

	/**
	 * 未显示的全部收藏信息集合
	 */
	private ArrayList<FavorItem> mFullItemUnshowList;

	/**
	 * 显示了的全部收藏信息集合
	 */
	private ArrayList<FavorItem> mFullItemShowedList;

	/**
	 * 全部收藏显示的数据适配器
	 */
	private FullListAdapter mFullListAdapter;

	/**
	 * 最近收藏的布局
	 */
	private LinearLayout mRecentLayout;

	/**
	 * 最近收藏无数据显示的View
	 */
	private View mRecentNoFavor;

	/**
	 * 全部收藏的布局
	 */
	private LinearLayout mFullLayout;

	/**
	 * 全部收藏无数据显示的View
	 */
	private View mFullNoFavor;

	public FavorPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("我的收藏");
		mViewContent = setContainerContent(R.layout.pager_favor);

		mContentPager = (ViewPager) mViewContent.findViewById(R.id.vp_content);

		mRecentLayout = (LinearLayout) View.inflate(mActivity,
				R.layout.view_favor_recently, null);
		mRecentListView = (DragRefreshListView) mRecentLayout
				.findViewById(R.id.lv_recent_list);
		mRecentNoFavor = mRecentLayout.findViewById(R.id.tv_no_favor);

		mFullLayout = (LinearLayout) View.inflate(mActivity,
				R.layout.view_favor_full_list, null);
		mFullListView = (DragRefreshListView) mFullLayout
				.findViewById(R.id.lv_full_list);
		mFullNoFavor = mFullLayout.findViewById(R.id.tv_no_favor);

	}

	@Override
	protected void initData() {
		super.initData();

		// 初始化集合
		mRecentItemList = new ArrayList<FavorItem>();
		mFullItemShowedList = new ArrayList<FavorItem>();
		mFullItemUnshowList = new ArrayList<FavorItem>();

		mRecentListAdapter = new RecentListAdapter();
		mRecentListView.setAdapter(mRecentListAdapter);

		mFullListAdapter = new FullListAdapter(mFullListView,
				mFullItemUnshowList, mFullItemShowedList, 20);
		mFullListView.setAdapter(mFullListAdapter);

		// 禁止最近收藏列表加载更多
		mRecentListView.isAllowLoadingMore = false;

		// 最近收藏的刷新监听
		mRecentListView
				.setOnRefreshListener(new DragRefreshListView.SimpleOnRefreshListener() {

					@Override
					public void onDragRefresh() {

						refreshDataFromDataBase();

						Toast.makeText(mActivity, "刷新成功", Toast.LENGTH_SHORT)
								.show();

						mRecentListView.completeRefresh();
					}

				});

		// 所有收藏的刷新监听
		mFullListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onLoadingMore() {

				// 成功插入的数据条数
				final int addCount = mFullListAdapter.insertMoreItem();

				// 修改UI必须在主线程执行
				mFullListAdapter.notifyDataSetChanged();

				if (addCount == 0) {
					Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(mActivity, "成功加载了" + addCount + "条数据",
							Toast.LENGTH_SHORT).show();
				}
				if (mFullItemUnshowList.size() <= 0) {
					// 禁止再加载更多
					mFullListView.isAllowLoadingMore = false;
				}
				mFullListView.completeRefresh();

			}

			@Override
			public void onDragRefresh() {

				refreshDataFromDataBase();

				Toast.makeText(mActivity, "刷新成功", Toast.LENGTH_SHORT).show();

				mFullListView.completeRefresh();
			}
		});

		// 设置ViewPager适配器
		mContentPager.setAdapter(new FavorPagerAdapter());

		// 从数据库拉去数据
		refreshDataFromDataBase();

	}

	/**
	 * 从数据库拉去数据
	 */
	private void refreshDataFromDataBase() {

		// 显示加载条
		getLoadingBarView().setVisibility(View.VISIBLE);

		// 清空集合
		mRecentItemList.clear();
		mFullItemShowedList.clear();
		mFullItemUnshowList.clear();

		// 首先异步查询数据库
		new ReadItemInfoDBTask().execute();

		// 在上面的AsyncTask执行后会执行doWhenFinishedReadDB()

		// 同步完成执行
		// TODO：同步未实现
		// new FavorItemDAO(mActivity).completeSynchronized();
	}

	/**
	 * 读取FeedItem信息的异步任务
	 */
	class ReadItemInfoDBTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// 子线程

			FavorItemDAO favorItemDAO = new FavorItemDAO(mActivity);
			// 只显示最近一个月的
			String latestMonth = TimeUtils.LoopToTransTime(
					TimeUtils.GET_CURRENT_TIME_MARK, "yyyy-MM");
			favorItemDAO.queryItems("pubdate like ?", new String[]{latestMonth
					+ "%"}, false, mRecentItemList);

			favorItemDAO.queryAllItems(mFullItemUnshowList);

			return null;

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

		// 刷新数据
		mRecentListAdapter.notifyDataSetChanged();
		mFullListAdapter.insertMoreItem();
		mFullListAdapter.notifyDataSetChanged();

		if (mFullItemUnshowList.size() > 0) {
			// 允许再加载更多
			mFullListView.isAllowLoadingMore = true;
		}

		changeVisibility();

		// 不显示加载条
		getLoadingBarView().setVisibility(View.INVISIBLE);

	}

	/**
	 * 收藏内容改变，刷新数据
	 * 
	 * @param favorItems
	 *            收藏信息
	 */
	public void addFavorItemAtFirst(ArrayList<FavorItem> favorItems) {

		// 添加新的数据
		mRecentItemList.addAll(0, favorItems);
		mFullItemShowedList.addAll(0, favorItems);

		// 刷新
		mRecentListAdapter.notifyDataSetChanged();
		mFullListAdapter.notifyDataSetChanged();

		changeVisibility();
	}

	/**
	 * 最近收藏列表数据适配器
	 */
	public class RecentListAdapter extends BaseAdapter {

		ViewHolder holder;

		@Override
		public int getCount() {

			changeVisibility();

			return mRecentItemList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			RelativeLayout view = getFaovrView(position, convertView, holder,
					mRecentItemList);

			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	/**
	 * 用于记录子控件对象
	 */
	class ViewHolder {
		RelativeLayout contentContainer;
		TextView title;
		TextView time;
		TextView from;
		LayoutImageView pic1;
		LayoutImageView pic2;
		LayoutImageView pic3;
		TextView preview;
		LinearLayout cancelFavor;
		LinearLayout copy;
		LinearLayout send;
	}

	/**
	 * 所有收藏列表数据适配器
	 */
	class FullListAdapter extends ArrayListLoadingMoreAdapter<FavorItem> {

		ViewHolder holder;

		public FullListAdapter(DragRefreshListView dragRefreshListView,
				ArrayList<FavorItem> unshowList,
				ArrayList<FavorItem> showedList, int onceShowedCount) {
			dragRefreshListView.super(unshowList, showedList, onceShowedCount);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			RelativeLayout view = getFaovrView(position, convertView, holder,
					mFullItemShowedList);
			return view;
		}

		@Override
		public int getCount() {

			changeVisibility();

			return super.getCount();
		}

	}

	/**
	 * ViewPager的适配器，目前固定页面
	 */
	class FavorPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			String[] pageTitles = {"最近添加", "所有收藏"};

			return pageTitles[position];
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View view = null;

			switch (position) {
				case 0 :
					view = mRecentLayout;
					break;
				case 1 :
					view = mFullLayout;
					break;
			}

			container.addView(view);
			return view;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			// super.destroyItem(container, position, object);
		}

	}

	private boolean parseFavorItem(final int position, ViewHolder viewHolder,
			ArrayList<FavorItem> list) {

		final FavorItem favorItem = list.get(position);

		if (favorItem == null) {
			return false;
		}

		// 显示头部部分
		viewHolder.title.setText(favorItem.contentInfo.title);
		viewHolder.time.setText(favorItem.contentInfo.pubDate);
		viewHolder.from.setText("来自[" + favorItem.feedSourceName + "]");

		viewHolder.pic1.setVisibility(View.GONE);
		viewHolder.pic2.setVisibility(View.GONE);
		viewHolder.pic3.setVisibility(View.GONE);

		// 显示图片
		if (favorItem.picLink1 != null) {
			BitmapCacheUtils.displayBitmapOnLayoutChange(mActivity,
					viewHolder.pic1, favorItem.picLink1, null);
			viewHolder.pic1.setVisibility(View.VISIBLE);

			if (favorItem.picLink2 != null) {
				BitmapCacheUtils.displayBitmapOnLayoutChange(mActivity,
						viewHolder.pic2, favorItem.picLink2, null);
				viewHolder.pic2.setVisibility(View.VISIBLE);

				if (favorItem.picLink3 != null) {
					BitmapCacheUtils.displayBitmapOnLayoutChange(mActivity,
							viewHolder.pic3, favorItem.picLink3, null);
					viewHolder.pic3.setVisibility(View.VISIBLE);
				}

			}

		}

		// 显示摘要
		viewHolder.preview.setText(favorItem.summary);

		// 点击内容
		viewHolder.contentContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mActivity, SummaryBodyActivity.class);
				// 传递数据
				intent.putExtra("FeedXMLContentInfo", favorItem.contentInfo);

				// 组成FeedItem
				FeedItem feedItem = new FeedItem();
				// TODO：部分信息为空
				feedItem.feedURL = favorItem.feedURL;
				feedItem.baseInfo.title = favorItem.feedSourceName;
				feedItem.baseInfo.summary = favorItem.summary;
				feedItem.baseInfo.time = TimeUtils.string2Timestamp(TimeUtils
						.LoopToTransTime(favorItem.contentInfo.pubDate));

				intent.putExtra("FeedItem", feedItem);

				mActivity.startActivityForResult(intent, 2);

			}
		});

		// 取消收藏
		viewHolder.cancelFavor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 弹出确认框
				new MainThemeAlertDialog(mActivity)
						.setAdapter(new MainThemeAlertDialogAdapter() {

							@Override
							public String getTitle() {
								return "是否确认删除？";
							}

							@Override
							public OnClickListener getOnConfirmClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {

										alertDialog.dismiss();

										new FavorItemDAO(mActivity)
												.removeItem(favorItem);

										// 刷新
										refreshDataFromDataBase();

										Toast.makeText(mActivity, "删除成功",
												Toast.LENGTH_SHORT).show();
									}

								};
							}

							@Override
							public OnClickListener getOnCancelClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {

										alertDialog.dismiss();

									}

								};

							}

							@Override
							public View getContentView() {
								return null;
							}

							@Override
							public void changeViewAtLast(TextView title,
									FrameLayout container,
									TextView confirmButtom,
									TextView cancelButtom) {

							}

						});

			}

		});

		// 复制连接
		viewHolder.copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SystemUtils.copyTextToClipBoard(mActivity,
						favorItem.contentInfo.link);
			}

		});

		// 转发
		viewHolder.send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String text = favorItem.contentInfo.title + ":\n"
						+ favorItem.contentInfo.link;

				SystemUtils.startShareIntentActivity(mActivity, text);

			}

		});

		return true;

	}

	/**
	 * 获得收藏的ItemView
	 * 
	 * @param position
	 *            条目位置
	 * @param convertView
	 *            复用的View
	 * @param holder
	 *            ViewHolder
	 * @param list
	 *            数据集合
	 * @return ItemView
	 */
	private RelativeLayout getFaovrView(int position, View convertView,
			ViewHolder holder, ArrayList<FavorItem> list) {
		RelativeLayout view;
		// 复用ConvertView
		if (convertView != null && convertView instanceof RelativeLayout) {
			// 复用View并取出holder
			view = (RelativeLayout) convertView;
			holder = (ViewHolder) view.getTag();

		} else {
			// 不可复用

			view = (RelativeLayout) View.inflate(mActivity,
					R.layout.view_lv_item_favor, null);

			// 利用ViewHolder记录子孩子View对象
			holder = new ViewHolder();

			holder.contentContainer = (RelativeLayout) view
					.findViewById(R.id.rl_content);
			holder.title = (TextView) holder.contentContainer
					.findViewById(R.id.tv_title);
			holder.time = (TextView) holder.contentContainer
					.findViewById(R.id.tv_time);
			holder.from = (TextView) holder.contentContainer
					.findViewById(R.id.tv_from);
			holder.pic1 = (LayoutImageView) holder.contentContainer
					.findViewById(R.id.iv_preview1);
			holder.pic2 = (LayoutImageView) holder.contentContainer
					.findViewById(R.id.iv_preview2);
			holder.pic3 = (LayoutImageView) holder.contentContainer
					.findViewById(R.id.iv_preview3);
			holder.preview = (TextView) holder.contentContainer
					.findViewById(R.id.tv_preview);
			holder.cancelFavor = (LinearLayout) view
					.findViewById(R.id.ll_cancel_favor);
			holder.copy = (LinearLayout) view.findViewById(R.id.ll_copy);
			holder.send = (LinearLayout) view.findViewById(R.id.ll_send);

			view.setTag(holder);

		}

		// 解析数据
		parseFavorItem(position, holder, list);
		return view;
	}

	/**
	 * 改变是否显示List
	 */
	private void changeVisibility() {

		if (mRecentItemList.isEmpty()) {
			mRecentNoFavor.setVisibility(View.VISIBLE);
			mRecentListView.setVisibility(View.GONE);
		} else {
			mRecentNoFavor.setVisibility(View.GONE);
			mRecentListView.setVisibility(View.VISIBLE);
		}

		if (mFullItemShowedList.isEmpty()) {
			mFullNoFavor.setVisibility(View.VISIBLE);
			mFullListView.setVisibility(View.GONE);
		} else {
			mFullNoFavor.setVisibility(View.GONE);
			mFullListView.setVisibility(View.VISIBLE);
		}
	}
}
