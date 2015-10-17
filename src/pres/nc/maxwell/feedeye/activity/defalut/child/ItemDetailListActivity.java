package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils.OnFinishGetLocalCacheListener;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 详细信息列表的页面的Activity
 */
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
	 * 加载不到的文本
	 */
	private TextView mNothingFoundText;

	/**
	 * 数据适配器
	 */
	private ItemDetailListAdapter mListViewAdapter;

	/**
	 * 当前页面的订阅条目信息
	 */
	private FeedItem mFeedItem;

	/**
	 * 内容信息集合
	 */
	public ArrayList<FeedXMLContentInfo> mContentInfoList;

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

		mNothingFoundText = (TextView) mCustomContainerView
				.findViewById(R.id.tv_nothing_found);
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
		mContentInfoList = new ArrayList<FeedXMLContentInfo>();

		// 设置数据适配器
		mListViewAdapter = new ItemDetailListAdapter();
		mListView.setAdapter(mListViewAdapter);
		
		mListView.isAllowRefresh =false;
		
		// 不使用加载更多
		mListView.isAllowLoadingMore = false;

		// 设置刷新监听
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onLoadingMore() {
				// 不使用此功能
			}

			@Override
			public void onDragRefresh() {
				getLatestDataFromNetwork();
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
				mNothingFoundText.setVisibility(View.VISIBLE);
				mLoadingLayout.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.INVISIBLE);
				break;
			case STATE_LOADING :
				mNothingFoundText.setVisibility(View.INVISIBLE);
				mLoadingLayout.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.INVISIBLE);
				break;
			case STATE_SHOWING :
				mNothingFoundText.setVisibility(View.INVISIBLE);
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

		if (mContentInfoList.size() != 0) {// 已有显示数据

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

							// TODO:考虑是否判断时间
							// 插入到首部
							mContentInfoList.addAll(0, contentInfos);

							// 更新
							mListViewAdapter.notifyDataSetChanged();

							// 设置本地缓存,最新的部分（旧的舍弃）
							XMLCacheUtils
									.setLocalCache(mFeedItem, contentInfos);

						}

						// 改变显示状态
						if (mContentInfoList.size() != 0) {

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

				mContentInfoList = contentInfos;

				LogUtils.w("ItemDetailListActivity",
						"本地加载了" + mContentInfoList.size() + "条数据");

				if (mContentInfoList.size() != 0) {// 本地缓存数据不为空

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
	 * 详细信息的数据适配器
	 */
	class ItemDetailListAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			int count = mContentInfoList.size();

			return count;
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
				holder.time = (TextView) itemView.findViewById(R.id.tv_time);

				itemView.setTag(holder);
			}

			// 处理逻辑
			holder.title.setText(mContentInfoList.get(position).title);

			holder.preview.setText("加载中...");
			// 异步加载文本信息
			new showHtmlText().execute(position, holder.preview);

			holder.time
					.setText("发表于："
							+ TimeUtils.LoopToTransTime(mContentInfoList
									.get(position).pubDate));

			return itemView;
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
	 * 获取预览内容
	 * 
	 * @param position
	 *            条目位置
	 * @return 预览内容文本
	 */
	private CharSequence getPreviewText(int position) {

		String tempString = mContentInfoList.get(position).description;
		if (tempString.length() > 250) {
			tempString = tempString.substring(0, 250);
		}
		
		tempString = Html.fromHtml(tempString).toString().trim();
		
		tempString = tempString.replace("￼", "[图片]").replace("\n", "");
		
		return tempString;
	}

	/**
	 * 异步转换Html为文本，第一个参数为条目位置，第二参数为要显示的TextView
	 */
	private class showHtmlText extends AsyncTask<Object, Void, String> {

		int position;
		TextView textView;

		@Override
		protected String doInBackground(Object... params) {// 子线程

			position = (Integer) params[0];
			textView = (TextView) params[1];

			String textString = (String) getPreviewText(position);
			return textString;
		}

		@Override
		protected void onPostExecute(String result) {// 主线程

			position = position + mListView.getHeaderViewsCount();

			if (position >= mListView.getFirstVisiblePosition()
					&& position <= mListView.getLastVisiblePosition()) {// 判断是否还在显示中
				textView.setText(result);
			}

		}
	}

	/**
	 * ViewHolder
	 * 
	 * @see ItemDetailListAdapter
	 */
	static class ViewHolder {
		TextView title;
		TextView preview;
		TextView time;
	}

}
