package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.SummaryBodyActivity;
import pres.nc.maxwell.feedeye.db.FavorItemDAO;
import pres.nc.maxwell.feedeye.domain.FavorItem;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.ArrayListLoadingMoreAdapter;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	 * 未显示的收藏信息集合
	 */
	private ArrayList<FavorItem> mItemUnshowList;

	/**
	 * 最近收藏显示的数据列表
	 */
	private RecentListAdapter mRecentListAdapter;

	public FavorPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("收藏页面");
		mViewContent = setContainerContent(R.layout.pager_favor);

		mContentPager = (ViewPager) mViewContent.findViewById(R.id.vp_content);

		mRecentListView = (DragRefreshListView) View.inflate(mActivity,
				R.layout.view_favor_recently, null);
		mFullListView = (DragRefreshListView) View.inflate(mActivity,
				R.layout.view_favor_full_list, null);

	}

	@Override
	protected void initData() {
		super.initData();

		// 初始化集合
		mItemUnshowList = new ArrayList<FavorItem>();

		mRecentListAdapter = new RecentListAdapter();
		mRecentListView.setAdapter(mRecentListAdapter);
		// TODO：
		// mFullListView.setAdapter(new FullListAdapter(mFullListView, null,
		// null, 0));
		mFullListView.setAdapter(new RecentListAdapter());

		// 禁止最近收藏列表刷新和加载更多
		mRecentListView.isAllowRefresh = false;
		mRecentListView.isAllowLoadingMore = false;

		// 设置ViewPager适配器
		mContentPager.setAdapter(new FavorPagerAdapter());

		// 从数据库拉去数据
		refreshDataFromDataBase();

	}

	private void refreshDataFromDataBase() {

		// 显示加载条
		getLoadingBarView().setVisibility(View.VISIBLE);

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
			favorItemDAO.queryAllItems(mItemUnshowList);

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

		// TODO:首部插入position和缓存问题冲突
		mItemUnshowList.addAll(0, favorItems);

		// 刷新
		mRecentListAdapter.notifyDataSetChanged();

	}

	/**
	 * 最近收藏列表数据适配器
	 */
	public class RecentListAdapter extends BaseAdapter {

		// TODO
		@Override
		public int getCount() {
			return mItemUnshowList.size();
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
			parseFavorItem(position, holder);

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

		private boolean parseFavorItem(int position, ViewHolder viewHolder) {

			final FavorItem favorItem = mItemUnshowList.get(position);

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

			// TODO：各点击监听器
			// 点击内容
			viewHolder.contentContainer
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent intent = new Intent(mActivity,
									SummaryBodyActivity.class);
							// 传递数据
							intent.putExtra("FeedXMLContentInfo",
									favorItem.contentInfo);

							// 组成FeedItem
							FeedItem feedItem = new FeedItem();
							// TODO：部分信息为空
							feedItem.feedURL = favorItem.feedURL;
							feedItem.feedURL = favorItem.feedURL;
							feedItem.baseInfo.title = favorItem.feedSourceName;
							feedItem.baseInfo.summary = favorItem.summary;
							feedItem.baseInfo.time = TimeUtils.string2Timestamp(TimeUtils
									.LoopToTransTime(favorItem.contentInfo.pubDate));

							intent.putExtra("FeedItem", feedItem);

							mActivity.startActivity(intent);

						}
					});

			// 取消收藏
			viewHolder.cancelFavor.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			// 复制内容
			viewHolder.copy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			// 转发
			viewHolder.send.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			return true;

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
	class FullListAdapter extends ArrayListLoadingMoreAdapter<String> {

		// TODO：暂时设计为String泛型
		public FullListAdapter(DragRefreshListView dragRefreshListView,
				ArrayList<String> unshowList, ArrayList<String> showedList,
				int onceShowedCount) {
			dragRefreshListView.super(unshowList, showedList, onceShowedCount);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
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
					view = mRecentListView;
					break;
				case 1 :
					view = mFullListView;
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

}
