package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.DiscoverItem;
import pres.nc.maxwell.feedeye.utils.JSONParseUtils;
import pres.nc.maxwell.feedeye.utils.JSONParseUtils.OnParseListener;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 发现页面的Pager
 */
public class DiscovePager extends BasePager {

	private GridView mGridView;
	private ListView mListView;

	private ArrayList<DiscoverItem> mItemsList;
	
	public DiscovePager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("发现");
		mViewContent = setContainerContent(R.layout.pager_discover);
		mGridView = (GridView) mViewContent.findViewById(R.id.gv_content);
		mListView = (ListView) mViewContent.findViewById(R.id.lv_content);

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		new JSONParseUtils(new OnParseListener() {
			
			@Override
			public void OnFinishParse(ArrayList<DiscoverItem> items) {
				
				mItemsList = items;
				
				mListView.setAdapter(new ListViewAdapter());
			}
			
		}).parseUrl("http://10.0.3.2:8080/feedeye/test.json");
		
		// 不显示加载条
		getLoadingBarView().setVisibility(View.INVISIBLE);
	}

	@Override
	protected void useFunctionButton() {
		super.useFunctionButton();

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_gird_view_style));// 添加按钮

		// 设置点击切换界面风格
		mFuncButtonRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mGridView.getVisibility() == View.VISIBLE) {// 显示单列模式
					mFuncButtonRight.setImageDrawable(mActivity.getResources()
							.getDrawable(R.drawable.btn_title_list_view_style));
					mGridView.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
				} else {// 显示双列模式
					mFuncButtonRight.setImageDrawable(mActivity.getResources()
							.getDrawable(R.drawable.btn_title_gird_view_style));
					mGridView.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
				}

			}

		});

		mFuncButtonRight.setVisibility(View.VISIBLE);
	}

	/**
	 * 用于记录子控件对象
	 */
	class ViewHolder {
		
		TextView addFeedItem;
		
		TextView label1;
		TextView label2;
		TextView label3;
		TextView label4;
		
		TextView title;
		TextView description;
		
	}
	
	
	class ListViewAdapter extends BaseAdapter {

		ViewHolder holder;
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mItemsList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LinearLayout view;
			// 复用ConvertView
			if (convertView != null && convertView instanceof LinearLayout) {
				// 复用View并取出holder
				view = (LinearLayout) convertView;
				holder = (ViewHolder) view.getTag();

			} else {
				// 不可复用

				view = (LinearLayout) View.inflate(mActivity, R.layout.view_lv_item_discover,
						null);

				// 利用ViewHolder记录子孩子View对象
				holder = new ViewHolder();

				holder.addFeedItem = (TextView) view
						.findViewById(R.id.tv_add_feed);
				holder.title = (TextView) view
						.findViewById(R.id.tv_title);
				holder.description = (TextView) view
						.findViewById(R.id.tv_desc);
				
				holder.label1 =  (TextView) view.findViewById(R.id.tv_label1);
				holder.label2 =  (TextView) view.findViewById(R.id.tv_label2);
				holder.label3 =  (TextView) view.findViewById(R.id.tv_label3);
				holder.label4 =  (TextView) view.findViewById(R.id.tv_label4);

				view.setTag(holder);

			}
			
			
			//设置数据
			DiscoverItem discoverItem = mItemsList.get(position);
			
			holder.title.setText(discoverItem.name);
			holder.description.setText(discoverItem.description);
			holder.label1.setText(discoverItem.key1);
			holder.label2.setText(discoverItem.key2);
			holder.label3.setText(discoverItem.key3);
			holder.label4.setText(discoverItem.key4);
			
			OnClickListener lableOnClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					changeLabelColor((TextView) v);
				}

			};
			
			holder.label1.setOnClickListener(lableOnClickListener);
			holder.label2.setOnClickListener(lableOnClickListener);
			holder.label3.setOnClickListener(lableOnClickListener);
			holder.label4.setOnClickListener(lableOnClickListener);
			

			holder.addFeedItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}

			});

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

		/**
		 * 改变标签颜色
		 * @param label 标签
		 */
		private void changeLabelColor(final TextView label) {
			if (label.getTextColors().getDefaultColor() == mActivity
					.getResources().getColor(R.color.theme_color)) {
				label.setTextColor(mActivity.getResources().getColor(R.color.black));
			}else {
				label.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
			}
		}

	}

}
