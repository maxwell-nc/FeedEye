package pres.nc.maxwell.feedeye.activity.defalut.child;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

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
	 * 数据适配器
	 */
	private ItemDetailListAdapter mListViewAdapter;

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

	}

	@Override
	protected void initData() {
		super.initData();

		// 获取传递过来的数据
		FeedItemBean infoBean = (FeedItemBean) getIntent().getExtras()
				.getSerializable("FeedItemBean");

		// 设置标题
		mTitleView.setText(infoBean.getTitle());

		// 设置数据适配器
		mListViewAdapter = new ItemDetailListAdapter();
		mListView.setAdapter(mListViewAdapter);

	}

	/**
	 * 详细信息的数据适配器
	 */
	class ItemDetailListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 10;
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
			
			//处理逻辑
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
		public TextView title;
		public TextView preview;
		public TextView time;
	}

}
