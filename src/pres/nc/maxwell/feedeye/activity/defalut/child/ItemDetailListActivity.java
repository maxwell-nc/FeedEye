package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.SummaryBodyActivity;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils.OnFinishGetLocalCacheListener;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.ArrayListLoadingMoreAdapter;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog;
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog.AlertDialogOnClickListener;
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog.DialogDataAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.ClipboardManager;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 详细信息列表的页面的Activity
 */
@SuppressWarnings("deprecation")
public class ItemDetailListActivity extends DefaultNewActivity {

	/**
	 * 此Activity，方便匿名内部类调用
	 */
	private Activity mThisActivity;

	/**
	 * 标题文本
	 */
	private TextView mTitleView;

	/**
	 * 数据列表
	 */
	private DragRefreshListView mListView;

	/**
	 * 加载中的布局
	 */
	private RelativeLayout mLoadingLayout;

	/**
	 * 加载不到的布局
	 */
	private RelativeLayout mNothingFoundLayout;

	/**
	 * 数据适配器
	 */
	private ItemDetailListAdapter mListViewAdapter;

	/**
	 * 当前页面的订阅条目信息
	 */
	private FeedItem mFeedItem;

	/**
	 * 已显示内容信息集合
	 */
	public ArrayList<FeedXMLContentInfo> mContentInfoShowedList;

	/**
	 * 未显示内容信息集合
	 */
	public ArrayList<FeedXMLContentInfo> mContentInfoUnshowList;

	/**
	 * 获取本地缓存监听器
	 */
	private OnGetLocalCacheListener mLocalCacheListener;

	/**
	 * 本地缓存的基本信息
	 */
	private FeedXMLBaseInfo localCacheBaseInfo;

	/**
	 * 没有数据加载到的状态
	 */
	private static final int STATE_NOTHING = 1;

	/**
	 * 加载数据中的状态
	 */
	private static final int STATE_LOADING = 2;

	/**
	 * 显示数据中的状态
	 */
	private static final int STATE_SHOWING = 3;

	/**
	 * 是否自动加载最新信息
	 */
	private boolean isAutoRefresh = false;

	@Override
	protected void onDestroy() {
		mListViewAdapter.shutdownThreadPool();
		super.onDestroy();
	}

	@Override
	protected void initView() {
		super.initView();

		mThisActivity = this;

		addView(R.layout.activity_item_detail_list_bar,
				R.layout.activity_item_detail_list_container);

		// 标题
		mTitleView = (TextView) mCustomBarView.findViewById(R.id.tv_title);

		mListView = (DragRefreshListView) mCustomContainerView
				.findViewById(R.id.lv_detail);

		mLoadingLayout = (RelativeLayout) mCustomContainerView
				.findViewById(R.id.rl_loading);

		mNothingFoundLayout = (RelativeLayout) mCustomContainerView
				.findViewById(R.id.rl_nothing);

	}

	@Override
	protected void initData() {
		super.initData();

		// 设置显示加载中
		changeDisplayState(STATE_LOADING);

		// 获取传递进来的数据
		mFeedItem = (FeedItem) getIntent().getExtras().getSerializable(
				"FeedItem");

		// 设置标题
		mTitleView.setText(mFeedItem.baseInfo.title);

		// 初始化内容信息列表
		mContentInfoShowedList = new ArrayList<FeedXMLContentInfo>();
		mContentInfoUnshowList = new ArrayList<FeedXMLContentInfo>();

		// 设置数据适配器
		mListViewAdapter = new ItemDetailListAdapter(mContentInfoUnshowList,
				mContentInfoShowedList, 8);
		// 设置适配器
		mListView.setAdapter(mListViewAdapter);
		
		// 设置刷新监听
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onDragRefresh() {
				getLatestDataFromNetwork();
			}

			@Override
			public void onLoadingMore() {

				// 成功插入的数据条数
				final int addCount = mListViewAdapter.insertMoreItem();

				// 修改UI必须在主线程执行
				mListViewAdapter.notifyDataSetChanged();

				if (addCount == 0) {
					Toast.makeText(mThisActivity, "没有更多数据了", Toast.LENGTH_SHORT)
							.show();
				} 
				
				if (mContentInfoUnshowList.size() <= 0) {
					// 禁止再加载更多
					mListView.isAllowLoadingMore = false;
				}
				mListView.completeRefresh();

			}

		});

		// 失败时重新加载
		mNothingFoundLayout.setOnClickListener(new ReloadClickListener());

		// 设置长按条目事件
		mListView.setOnItemLongClickListener(new ItemLongClickListener());

		// 设置点击条目事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(mThisActivity,
						SummaryBodyActivity.class);
				// 传递数据
				intent.putExtra(
						"FeedXMLContentInfo",
						mContentInfoShowedList.get(position
								- mListView.getHeaderViewsCount()));
				intent.putExtra("FeedItem", mFeedItem);

				mThisActivity.startActivity(intent);

			}

		});

		// 加载数据
		LoadData();

	}

	/**
	 * 改变显示状态
	 * 
	 * @see #STATE_NOTHING
	 * @see #STATE_LOADING
	 * @see #STATE_SHOWING
	 */
	private void changeDisplayState(int state) {

		switch (state) {
			case STATE_NOTHING :
				mNothingFoundLayout.setVisibility(View.VISIBLE);
				mLoadingLayout.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.INVISIBLE);
				break;
			case STATE_LOADING :
				mNothingFoundLayout.setVisibility(View.INVISIBLE);
				mLoadingLayout.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.INVISIBLE);
				break;
			case STATE_SHOWING :
				mNothingFoundLayout.setVisibility(View.INVISIBLE);
				mLoadingLayout.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.VISIBLE);
				break;
		}

	}

	/**
	 * 先尝试加载本地缓存数据，判断本地缓存是否有效
	 */
	private void LoadData() {

		mLocalCacheListener = new OnGetLocalCacheListener();

		try {// 尝试获取本地缓存
			XMLCacheUtils.getLocalCacheContentInfo(mFeedItem,
					mLocalCacheListener);
		} catch (FileNotFoundException e) {// 不存在则从网络获取
			GetInfosFromNetwork();
		}

	}

	/**
	 * 从网络上加载数据
	 */
	private void GetInfosFromNetwork() {

		if (mContentInfoShowedList.size() != 0) {// 已有显示数据

			mListView.setOnRefreshing();// 设置强制刷新

		} else {// 无显示数据

			changeDisplayState(STATE_LOADING);
			getLatestDataFromNetwork();

		}

	}

	/**
	 * 从网络中加载最新数据
	 */
	private void getLatestDataFromNetwork() {
		// 读取网络信息
		FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new FeedXMLParser.SimpleOnFinishParseXMLListener() {

					@Override
					public void onFinishParseContent(boolean result,
							ArrayList<FeedXMLContentInfo> contentInfos) {

						LogUtils.w("ItemDetailListActivity", "从网络获取了"
								+ contentInfos.size() + "条数据");

						if (!contentInfos.isEmpty()) {

							Toast.makeText(mThisActivity,
									"更新了" + contentInfos.size() + "条数据",
									Toast.LENGTH_SHORT).show();
							// TODO：判断是否需要刷新adapter？

							mContentInfoShowedList.clear();
							mContentInfoUnshowList.clear();
							mContentInfoUnshowList.addAll(contentInfos);

							// 插入
							mListViewAdapter.insertMoreItem();
							
							// 更新
							mListViewAdapter.notifyDataSetChanged();

							// 设置本地缓存,最新的部分（旧的舍弃）
							XMLCacheUtils
									.setLocalCache(mFeedItem, contentInfos);

						} else {// 获取失败

							Toast.makeText(mThisActivity, "加载不到任何数据",
									Toast.LENGTH_SHORT).show();

						}

						// 改变显示状态
						if (mContentInfoShowedList.size() != 0) {

							changeDisplayState(STATE_SHOWING);

						} else {

							changeDisplayState(STATE_NOTHING);

						}

						mListView.completeRefresh();
					}

				});

		// 解析数据
		feedXMLParser.parse(mFeedItem.feedURL, mFeedItem.encoding,
				FeedXMLParser.TYPE_PARSE_CONTENT);
	}

	/**
	 * 获取本地缓存监听器
	 */
	class OnGetLocalCacheListener implements OnFinishGetLocalCacheListener {

		@Override
		public void onFinishGetContentInfo(
				ArrayList<FeedXMLContentInfo> contentInfos) {

			if (contentInfos != null) {// 读取本地信息

				mContentInfoShowedList.clear();
				mContentInfoUnshowList.clear();
				mContentInfoUnshowList.addAll(contentInfos);

				// 插入
				mListViewAdapter.insertMoreItem();

				LogUtils.w("ItemDetailListActivity",
						"本地加载了" + contentInfos.size() + "条数据");

				if (mContentInfoShowedList.size() != 0) {// 本地缓存数据不为空

					// 更新
					mListViewAdapter.notifyDataSetChanged();

					// 设置显示ListView
					changeDisplayState(STATE_SHOWING);

				} else {
					// 设置显示没有数据
					changeDisplayState(STATE_NOTHING);
				}

				if (isAutoRefresh) {
					try {// 获取本地缓存时间
						XMLCacheUtils.getLocalCacheBaseInfo(mFeedItem,
								mLocalCacheListener);
					} catch (FileNotFoundException e) {// 缓存被删除
						e.printStackTrace();
					}
				}

			} else {// 缓存读取出错或缓存有问题

				GetInfosFromNetwork();

			}

		}

		@Override
		public void onFinishGetBaseInfo(FeedXMLBaseInfo baseInfo) {

			LogUtils.w("ItemDetailListActivity", "解析了本地缓存基本信息");

			localCacheBaseInfo = baseInfo;

			// 检查是否需要更新数据
			checkIfUpdate();

		}

		/**
		 * 检查网络最后更新时间是否大于本地缓存时间，是则更新缓存
		 */
		private void checkIfUpdate() {
			// 读取信息
			FeedXMLParser feedXMLParser = new FeedXMLParser();

			feedXMLParser
					.setOnFinishedParseXMLListener(new FeedXMLParser.SimpleOnFinishParseXMLListener() {

						@Override
						public void onFinishParseBaseInfo(boolean result,
								FeedXMLBaseInfo baseInfo) {

							if (result && baseInfo != null) {// 有网络并获取成功

								LogUtils.w("ItemDetailListActivity", "网络更新时间："
										+ baseInfo.time.toString());
								LogUtils.w("ItemDetailListActivity", "本地缓存时间："
										+ localCacheBaseInfo.time.toString());

								if (baseInfo.time.getTime() > localCacheBaseInfo.time
										.getTime()) {// 需要更新

									LogUtils.w("ItemDetailListActivity",
											"本地缓存需要更新");

									// 更新feedItem的时间
									mFeedItem.baseInfo = baseInfo;
									new FeedItemDAO(mThisActivity)
											.updateItem(mFeedItem);

									GetInfosFromNetwork();

								} else {// 无需更新

									LogUtils.w("ItemDetailListActivity",
											"本地缓存无需更新");

								}

							} else {// 无网络，显示本地数据

								LogUtils.w("ItemDetailListActivity",
										"无网络，显示本地数据");

							}

						}
					});

			// 解析数据
			feedXMLParser.parse(mFeedItem.feedURL, mFeedItem.encoding,
					FeedXMLParser.TYPE_PARSE_BASE_INFO);
		}

	}

	/**
	 * ViewHolder
	 * 
	 * @see ItemDetailListAdapter
	 */
	static class ViewHolder {
		TextView title;
		ImageView previewPic1;
		ImageView previewPic2;
		ImageView previewPic3;
		TextView preview;
		TextView time;
	}

	/**
	 * 详细信息的数据适配器
	 */
	class ItemDetailListAdapter
			extends
				ArrayListLoadingMoreAdapter<FeedXMLContentInfo> {

		/**
		 * 清空缓存
		 */
		@Override
		public void notifyDataSetChanged() {
			listItemCaches.clear();// 清空缓存
			super.notifyDataSetChanged();
		}

		/**
		 * 用于加载文本
		 * 
		 * @see showHtmlTextTask
		 */
		private ExecutorService showTextThreadPool;

		/**
		 * 数据缓存，key是位置
		 */
		private SparseArray<ListItemCache> listItemCaches;

		public ItemDetailListAdapter(ArrayList<FeedXMLContentInfo> unshowList,
				ArrayList<FeedXMLContentInfo> showedList, int onceShowedCount) {
			mListView.super(unshowList, showedList, onceShowedCount);

			// 初始化线程池
			showTextThreadPool = Executors.newCachedThreadPool();

			listItemCaches = new SparseArray<ListItemCache>();
		}

		/**
		 * 关闭线程池
		 */
		public void shutdownThreadPool() {
			showTextThreadPool.shutdownNow();
			BitmapCacheUtils.shutdownDefalutThreadPool();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			FrameLayout itemView;

			if (convertView != null && convertView instanceof FrameLayout) {// 复用

				itemView = (FrameLayout) convertView;
				holder = (ViewHolder) itemView.getTag();

			} else {// 需要新创建
				itemView = (FrameLayout) View.inflate(mThisActivity,
						R.layout.view_lv_item_detail, null);

				holder = new ViewHolder();

				holder.title = (TextView) itemView.findViewById(R.id.tv_title);
				holder.preview = (TextView) itemView
						.findViewById(R.id.tv_preview);
				holder.previewPic1 = (ImageView) itemView
						.findViewById(R.id.iv_preview1);
				holder.previewPic2 = (ImageView) itemView
						.findViewById(R.id.iv_preview2);
				holder.previewPic3 = (ImageView) itemView
						.findViewById(R.id.iv_preview3);
				holder.time = (TextView) itemView.findViewById(R.id.tv_time);

				itemView.setTag(holder);
			}

			// 处理逻辑
			holder.title.setText(mContentInfoShowedList.get(position).title);

			ListItemCache itemCache = listItemCaches.get(position);

			if (itemCache != null) {// 使用缓存

				holder.preview.setText(itemCache.previewString);
				holder.previewPic1.setVisibility(itemCache.pic1Visibility);
				holder.previewPic2.setVisibility(itemCache.pic2Visibility);
				holder.previewPic3.setVisibility(itemCache.pic3Visibility);

				if (itemCache.link1 != null) {
					BitmapCacheUtils.displayBitmap(mThisActivity,
							holder.previewPic1, itemCache.link1, null);
				}
				if (itemCache.link2 != null) {
					BitmapCacheUtils.displayBitmap(mThisActivity,
							holder.previewPic2, itemCache.link2, null);
				}
				if (itemCache.link3 != null) {
					BitmapCacheUtils.displayBitmap(mThisActivity,
							holder.previewPic3, itemCache.link3, null);
				}

			} else {

				holder.preview.setText("加载中...");

				holder.previewPic1.setVisibility(View.GONE);
				holder.previewPic2.setVisibility(View.GONE);
				holder.previewPic3.setVisibility(View.GONE);

				// 异步加载文本信息
				new showHtmlTextTask().executeOnExecutor(showTextThreadPool,
						position, holder, listItemCaches);

			}

			holder.time.setText("发表于："
					+ TimeUtils.LoopToTransTime(mContentInfoShowedList
							.get(position).pubDate));

			return itemView;
		}

	}

	class ListItemCache {

		int pic1Visibility;
		String link1;
		int pic2Visibility;
		String link2;
		int pic3Visibility;
		String link3;
		String previewString;

	}

	/**
	 * 异步转换Html为文本，第一个参数为条目位置，第二参数为要显示的TextView,第三个参数是用于记录加载了的数据
	 */
	private class showHtmlTextTask extends AsyncTask<Object, Void, String> {

		int position;
		ViewHolder holder;
		ArrayList<String> imgLinks;
		SparseArray<ListItemCache> listItemCaches;

		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(Object... params) {// 子线程

			position = ((Integer) params[0]).intValue();
			holder = (ViewHolder) params[1];
			listItemCaches = (SparseArray<ListItemCache>) params[2];

			String textString = (String) getPreviewText(position);
			return textString;
		}

		@Override
		protected void onPostExecute(String result) {// 主线程

			position = position + mListView.getHeaderViewsCount();

			if (position >= mListView.getFirstVisiblePosition()
					&& position <= mListView.getLastVisiblePosition()) {// 判断是否还在显示中

				holder.preview.setText(result);

				int size = imgLinks.size();

				// 统计有效图片地址
				int availableImgCount = size;

				for (int i = size; i < 0; i--) {

					if ("无法识别的图片地址".equals(imgLinks.get(i))) {
						imgLinks.remove(i);
						availableImgCount--;
					}
				}

				if (availableImgCount >= 1) {
					holder.previewPic1.setVisibility(View.VISIBLE);
					holder.previewPic2.setVisibility(View.INVISIBLE);// 占位
					holder.previewPic3.setVisibility(View.INVISIBLE);// 占位
					BitmapCacheUtils.displayBitmap(mThisActivity,
							holder.previewPic1, imgLinks.get(0), null);

					if (availableImgCount >= 2) {
						holder.previewPic2.setVisibility(View.VISIBLE);
						holder.previewPic3.setVisibility(View.INVISIBLE);// 占位
						BitmapCacheUtils.displayBitmap(mThisActivity,
								holder.previewPic2, imgLinks.get(1), null);

						if (availableImgCount >= 3) {
							holder.previewPic3.setVisibility(View.VISIBLE);
							BitmapCacheUtils.displayBitmap(mThisActivity,
									holder.previewPic3, imgLinks.get(2), null);
						}
					}

				}

				// 设置缓存
				ListItemCache itemCache = new ListItemCache();

				itemCache.pic1Visibility = holder.previewPic1.getVisibility();
				itemCache.pic2Visibility = holder.previewPic2.getVisibility();
				itemCache.pic3Visibility = holder.previewPic3.getVisibility();

				if (availableImgCount >= 1) {
					itemCache.link1 = imgLinks.get(0);
					if (availableImgCount >= 2) {
						itemCache.link2 = imgLinks.get(1);
						if (availableImgCount >= 3) {
							itemCache.link3 = imgLinks.get(2);
						}
					}
				}

				itemCache.previewString = result;

				listItemCaches.put(position - mListView.getHeaderViewsCount(),
						itemCache);

			}

		}
		/**
		 * 获取预览内容
		 * 
		 * @param position
		 *            条目位置
		 * @return 预览内容文本
		 */
		private CharSequence getPreviewText(int position) {

			String orgString = mContentInfoShowedList.get(position).description;

			imgLinks = new ArrayList<String>();
			String tempString = HTTPUtils.html2Text(orgString, true, imgLinks)
					.replace("\n", "");

			LogUtils.w(this, imgLinks.toString());
			return tempString;
		}
	}

	/**
	 * "点击重新加载"按钮的监听器
	 */
	private class ReloadClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			// 设置显示加载中
			changeDisplayState(STATE_LOADING);

			// 延迟加载
			new Thread() {

				@Override
				public void run() {

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 重新加载
					LoadData();
				};

			}.start();

		}
	}

	/**
	 * 设置长按条目事件
	 */
	private class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {

			new MainThemeLongClickDialog(mThisActivity,
					new DialogDataAdapter() {

						@Override
						public int getLayoutViewId() {
							return R.layout.view_long_click_lv_detail;
						}

						@Override
						public int[] getTextViewResIds() {
							int[] ids = {R.id.tv_favor, R.id.tv_share,
									R.id.tv_copy};
							return ids;
						}

						@Override
						public OnClickListener[] getItemOnClickListener(
								final AlertDialog alertDialog) {

							OnClickListener[] listeners = {
									new FavorOnClickListener(position,
											alertDialog),// 收藏
									new ShareOnClickListener(position,
											alertDialog),// 分享
									new CopyLinkOnClickListener(position,
											alertDialog)// 复制
							};

							return listeners;
						}
					}).show();

			return true;
		}

		/**
		 * 收藏的点击事件
		 */
		class FavorOnClickListener extends AlertDialogOnClickListener {

			public FavorOnClickListener(int position, AlertDialog alertDialog) {
				super(position, alertDialog);
			}

			@Override
			public void onClick(View v) {

				// TODO：添加收藏的逻辑

				alertDialog.dismiss();// 对话框关闭
			}

		}

		/**
		 * 复制链接的点击事件
		 */
		class CopyLinkOnClickListener extends AlertDialogOnClickListener {

			public CopyLinkOnClickListener(int position, AlertDialog alertDialog) {
				super(position, alertDialog);
			}

			@Override
			public void onClick(View v) {

				int realPosition = position - mListView.getHeaderViewsCount();

				// 获取链接
				String link = mContentInfoShowedList.get(realPosition).link;

				ClipboardManager clipManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipManager.setText(link);

				Toast.makeText(mThisActivity, "复制成功", Toast.LENGTH_SHORT)
						.show();

				alertDialog.dismiss();// 对话框关闭
			}
		}

		/**
		 * 分享的点击事件
		 */
		class ShareOnClickListener extends AlertDialogOnClickListener {

			public ShareOnClickListener(int position, AlertDialog alertDialog) {
				super(position, alertDialog);
			}

			@Override
			public void onClick(View v) {

				int realPosition = position - mListView.getHeaderViewsCount();

				// 获取标题
				String msgTitle = mContentInfoShowedList.get(realPosition).title;
				// 获取摘要
				String msgSummary = mContentInfoShowedList.get(realPosition).description
						.substring(0, 120);

				msgSummary = HTTPUtils.html2Text(msgSummary, true, null);
				// 获取链接
				String link = mContentInfoShowedList.get(realPosition).link;

				Intent intent = new Intent();
				intent.setAction("android.intent.action.SEND");
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, msgTitle + ":\n"
						+ msgSummary + "...\n" + link);
				mThisActivity.startActivity(intent);

				alertDialog.dismiss();// 对话框关闭
			}

		}
	}

}
