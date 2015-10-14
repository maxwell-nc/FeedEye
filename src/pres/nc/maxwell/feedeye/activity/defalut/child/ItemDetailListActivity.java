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
import pres.nc.maxwell.feedeye.engine.FeedXMLParser.OnFinishParseXMLListener;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils.OnFinishGetLocalCacheListener;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
	 * 加载中的图片
	 */
	private ProgressBar mLoadingPic;

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

		mLoadingPic = (ProgressBar) mCustomContainerView
				.findViewById(R.id.pb_loading);

		mNothingFoundText = (TextView) mCustomContainerView
				.findViewById(R.id.tv_nothing_found);
	}

	@Override
	protected void initData() {
		super.initData();

		mFeedItem = (FeedItem) getIntent().getExtras().getSerializable(
				"FeedItem");

		// 设置标题
		mTitleView.setText(mFeedItem.baseInfo.title);

		// 设置显示加载中
		mNothingFoundText.setVisibility(View.INVISIBLE);
		mLoadingPic.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.INVISIBLE);

		LoadData();
		
		mListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onLoadingMore() {
				
				mListView.completeRefresh();
			}
			
			@Override
			public void onDragRefresh() {
				getLatestDataFromNetwork();
			}
		});
		
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
			GetDetailFromNetwork();
		}

	}

	/**
	 * 从网络上加载数据
	 */
	private void GetDetailFromNetwork() {
		// TODO：判断是否已经有本地缓存
		mListView.setOnRefreshing();
		
		// 设置显示加载中
		/*mNothingFoundText.setVisibility(View.INVISIBLE);
		mLoadingPic.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.INVISIBLE);*/	
	}

	/**
	 * 从网络中加载最新数据
	 */
	private void getLatestDataFromNetwork() {
		// 读取网络信息
		FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new OnFinishParseXMLListener() {

					@Override
					public void onFinishParseContent(boolean result,
							ArrayList<FeedXMLContentInfo> contentInfos) {

						mContentInfoList = contentInfos;

						XMLCacheUtils
								.setLocalCache(mFeedItem, mContentInfoList);

						Toast.makeText(mThisActivity,
								"加载了" + mContentInfoList.size() + "条数据",
								Toast.LENGTH_SHORT).show();

						// 设置数据适配器
						mListViewAdapter = new ItemDetailListAdapter();
						mListView.setAdapter(mListViewAdapter);

						// TODO：根据结果是否显示ListView
						// 设置显示ListView
						/*mNothingFoundText.setVisibility(View.INVISIBLE);
						mLoadingPic.setVisibility(View.INVISIBLE);
						mListView.setVisibility(View.VISIBLE);*/
						mListView.completeRefresh();
					}

					@Override
					public void onFinishParseBaseInfo(boolean result,
							FeedXMLBaseInfo baseInfo) {
						// 不需要
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

			mContentInfoList = contentInfos;
			// TODO:判断是否需要重新拉取数据
			if (mContentInfoList != null) {// 读取本地信息

				Toast.makeText(mThisActivity,
						"本地加载了" + mContentInfoList.size() + "条数据",
						Toast.LENGTH_SHORT).show();

				// 设置数据适配器
				mListViewAdapter = new ItemDetailListAdapter();
				mListView.setAdapter(mListViewAdapter);

				// 设置显示ListView
				mNothingFoundText.setVisibility(View.INVISIBLE);
				mLoadingPic.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.VISIBLE);

				try {// 获取本地缓存时间
					XMLCacheUtils.getLocalCacheBaseInfo(mFeedItem,
							mLocalCacheListener);
				} catch (FileNotFoundException e) {// 缓存被删除
					e.printStackTrace();
				}

			} else {// 存在缓存却读取不了

				GetDetailFromNetwork();

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
					.setOnFinishedParseXMLListener(new OnFinishParseXMLListener() {

						@Override
						public void onFinishParseContent(boolean result,
								ArrayList<FeedXMLContentInfo> contentInfos) {
							// 不需要
						}

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

									GetDetailFromNetwork();

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
			return mContentInfoList.size();
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

			holder.preview.setText(getPreview(position));
			holder.time
					.setText("发表于："
							+ TimeUtils.LoopToTransTime(mContentInfoList
									.get(position).pubDate));

			return itemView;
		}

		/**
		 * 获取预览内容
		 * 
		 * @param position
		 *            条目位置
		 * @return 预览内容文本
		 */
		private CharSequence getPreview(int position) {

			String tempString = mContentInfoList.get(position).description;
			if (tempString.length() > 250) {
				tempString = tempString.substring(0, 250);
			}

			return Html.fromHtml(tempString).toString();
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
