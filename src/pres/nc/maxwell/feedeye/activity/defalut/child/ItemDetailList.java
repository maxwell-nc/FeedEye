package pres.nc.maxwell.feedeye.activity.defalut.child;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import android.app.Activity;
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
public class ItemDetailList extends DefaultNewActivity {

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
	 * 当前页面的订阅bean
	 */
	private FeedItemBean mInfoBean;

	private int mCount;

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

		mInfoBean = (FeedItemBean) getIntent().getExtras().getSerializable(
				"FeedItemBean");

		// 设置标题
		mTitleView.setText(mInfoBean.getTitle());

		// 设置显示加载中
		mNothingFoundText.setVisibility(View.INVISIBLE);
		mLoadingPic.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.INVISIBLE);

		// 读取信息

		final FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(feedXMLParser.new OnFinishParseDefaultListener() {

					@Override
					public void onFinishParseContent(boolean result) {

						mCount = feedXMLParser.mContentInfo.mContentCount;

						Toast.makeText(mThisActivity, "加载了" + mCount + "条数据",
								Toast.LENGTH_SHORT).show();

						// 设置数据适配器
						mListViewAdapter = new ItemDetailListAdapter();
						mListView.setAdapter(mListViewAdapter);
						
						// TODO：根据结果是否显示ListView
						// 设置显示加载中
						mNothingFoundText.setVisibility(View.INVISIBLE);
						mLoadingPic.setVisibility(View.INVISIBLE);
						mListView.setVisibility(View.VISIBLE);
						
					}

				});


		// 解析数据
		feedXMLParser.parse(mInfoBean.getFeedURL(), "UTF-8" ,FeedXMLParser.TYPE_PARSE_CONTENT);

	}

	/**
	 * 详细信息的数据适配器
	 */
	class ItemDetailListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCount;
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
			holder.title.setText("测试信息标题");
			holder.preview.setText("测试信息内容：如果内容很长则自动变换高度，最长为三行！");
			holder.time.setText("上午11:10");

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
