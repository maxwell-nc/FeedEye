package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;
import java.util.HashSet;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.child.AddFeedActivity;
import pres.nc.maxwell.feedeye.domain.DiscoverItem;
import pres.nc.maxwell.feedeye.utils.JSONParseUtils;
import pres.nc.maxwell.feedeye.utils.JSONParseUtils.OnParseListener;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 发现页面的Pager
 */
public class DiscovePager extends BasePager {

	/**
	 * 标签列表
	 */
	private ListView mLabelView;

	/**
	 * 列表数据适配器
	 */
	private ListViewAdapter mListViewAdapter;

	/**
	 * 详细列表
	 */
	private ListView mListView;

	/**
	 * 所有的Item数据
	 */
	private ArrayList<DiscoverItem> mItemsList;

	/**
	 * 显示的列表数据
	 */
	private ArrayList<DiscoverItem> mItemsShowList;

	/**
	 * 没有数据时显示的
	 */
	private RelativeLayout mNothingLayout;

	public DiscovePager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("发现");
		mViewContent = setContainerContent(R.layout.pager_discover);
		mLabelView = (ListView) mViewContent.findViewById(R.id.lv_label);
		mListView = (ListView) mViewContent.findViewById(R.id.lv_content);
		mNothingLayout = (RelativeLayout) mViewContent
				.findViewById(R.id.rl_nothing);

	}

	@Override
	protected void initData() {
		super.initData();

		LoadData();

	}

	/**
	 * 加载数据
	 */
	private void LoadData() {

		// 显示加载条
		getLoadingBarView().setVisibility(View.VISIBLE);
		mNothingLayout.setVisibility(View.INVISIBLE);

		new JSONParseUtils(new OnParseListener() {

			@Override
			public void OnFinishParse(ArrayList<DiscoverItem> items) {

				mItemsList = items;
				mItemsShowList = items;

				mListViewAdapter = new ListViewAdapter();
				mListView.setAdapter(mListViewAdapter);
				mLabelView.setAdapter(new LabelViewAdapter());

				// 不显示加载条
				getLoadingBarView().setVisibility(View.INVISIBLE);
				useFunctionButton();
			}

			@Override
			public void onFailed() {

				// 显示重新加载
				mListView.setVisibility(View.INVISIBLE);
				mLabelView.setVisibility(View.INVISIBLE);
				getLoadingBarView().setVisibility(View.INVISIBLE);
				mNothingLayout.setVisibility(View.VISIBLE);

				mNothingLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						LoadData();
					}

				});

			}

		}).parseUrl("http://192.168.253.1:8080/feedeye/test.json");
	}

	@Override
	protected void useFunctionButton() {
		super.useFunctionButton();

		mFuncButtonLeft.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_refresh));

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_label_view_style));

		// 设置刷新or显示全部
		mFuncButtonLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mLabelView.getVisibility() == View.VISIBLE) {// 刷新

					LoadData();
					Toast.makeText(mActivity, "刷新成功", Toast.LENGTH_SHORT)
							.show();

				} else {// 显示全部

					mItemsShowList = mItemsList;
					mListViewAdapter.notifyDataSetChanged();
					Toast.makeText(mActivity, "显示了所有发现", Toast.LENGTH_SHORT)
							.show();

				}
			}

		});

		// 设置点击切换界面风格
		mFuncButtonRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switchTab();
			}

		});

		// 显示按钮
		mFuncButtonLeft.setVisibility(View.VISIBLE);
		mFuncButtonRight.setVisibility(View.VISIBLE);
	}

	/**
	 * 用于记录子控件对象
	 */
	static class ViewHolder {

		TextView addFeedItem;

		TextView label1;
		TextView label2;
		TextView label3;
		TextView label4;

		TextView title;
		TextView description;

		LayoutImageView icon;
	}

	class LabelViewAdapter extends BaseAdapter {

		ViewHolder holder;

		/**
		 * 记录已经显示的标签
		 */
		HashSet<String> labelsSet = new HashSet<String>();

		@Override
		public int getCount() {
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

				view = (LinearLayout) View.inflate(mActivity,
						R.layout.view_label_item_discover, null);

				// 利用ViewHolder记录子孩子View对象
				holder = new ViewHolder();

				holder.label1 = (TextView) view.findViewById(R.id.tv_label1);
				holder.label2 = (TextView) view.findViewById(R.id.tv_label2);
				holder.label3 = (TextView) view.findViewById(R.id.tv_label3);
				holder.label4 = (TextView) view.findViewById(R.id.tv_label4);

				view.setTag(holder);

			}

			// 设置数据
			DiscoverItem discoverItem = mItemsList.get(position);

			// 显示标签
			showLabel(holder.label1, discoverItem.labels[0]);
			showLabel(holder.label2, discoverItem.labels[1]);
			showLabel(holder.label3, discoverItem.labels[2]);
			showLabel(holder.label4, discoverItem.labels[3]);

			// 改变标签颜色
			changeLabelColor(holder.label1, discoverItem.colorMarks[0]);
			changeLabelColor(holder.label2, discoverItem.colorMarks[1]);
			changeLabelColor(holder.label3, discoverItem.colorMarks[2]);
			changeLabelColor(holder.label4, discoverItem.colorMarks[3]);

			OnClickListener lableOnClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectLabelItem((TextView) v);
					switchTab();
				}

			};

			holder.label1.setOnClickListener(lableOnClickListener);
			holder.label2.setOnClickListener(lableOnClickListener);
			holder.label3.setOnClickListener(lableOnClickListener);
			holder.label4.setOnClickListener(lableOnClickListener);

			return view;
		}
		/**
		 * 修改标签
		 * 
		 * @param labelView
		 *            标签View对象
		 * @param label
		 *            标签文本
		 */
		private void showLabel(TextView labelView, String label) {

			if (TextUtils.isEmpty(label) || labelsSet.contains(label)) {
				labelView.setVisibility(View.GONE);
			} else {
				labelView.setVisibility(View.VISIBLE);
				labelView.setText(label);
				labelsSet.add(label);
			}
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

	class ListViewAdapter extends BaseAdapter {

		ViewHolder holder;

		@Override
		public int getCount() {
			return mItemsShowList.size();
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

				view = (LinearLayout) View.inflate(mActivity,
						R.layout.view_lv_item_discover, null);

				// 利用ViewHolder记录子孩子View对象
				holder = new ViewHolder();

				holder.addFeedItem = (TextView) view
						.findViewById(R.id.tv_add_feed);
				holder.title = (TextView) view.findViewById(R.id.tv_title);
				holder.description = (TextView) view.findViewById(R.id.tv_desc);

				holder.icon = (LayoutImageView) view.findViewById(R.id.iv_icon);

				holder.label1 = (TextView) view.findViewById(R.id.tv_label1);
				holder.label2 = (TextView) view.findViewById(R.id.tv_label2);
				holder.label3 = (TextView) view.findViewById(R.id.tv_label3);
				holder.label4 = (TextView) view.findViewById(R.id.tv_label4);

				view.setTag(holder);

			}

			// 设置数据
			final DiscoverItem discoverItem = mItemsShowList.get(position);

			holder.title.setText(discoverItem.name);
			holder.description.setText(discoverItem.description);

			// 设置标签
			holder.label1.setText(discoverItem.labels[0]);
			holder.label2.setText(discoverItem.labels[1]);
			holder.label3.setText(discoverItem.labels[2]);
			holder.label4.setText(discoverItem.labels[3]);

			// 改变颜色
			changeLabelColor(holder.label1, discoverItem.colorMarks[0]);
			changeLabelColor(holder.label2, discoverItem.colorMarks[1]);
			changeLabelColor(holder.label3, discoverItem.colorMarks[2]);
			changeLabelColor(holder.label4, discoverItem.colorMarks[3]);

			// 改变类型图标
			setTypeIcon(holder.icon,discoverItem.type);

			OnClickListener lableOnClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectLabelItem((TextView) v);
				}

			};

			holder.label1.setOnClickListener(lableOnClickListener);
			holder.label2.setOnClickListener(lableOnClickListener);
			holder.label3.setOnClickListener(lableOnClickListener);
			holder.label4.setOnClickListener(lableOnClickListener);

			holder.addFeedItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, AddFeedActivity.class);
					intent.putExtra("DiscoverItem", discoverItem);

					// 打开并获得添加结果
					mActivity.startActivityForResult(intent, 1);
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

	}

	/**
	 * 改变标签颜色
	 * 
	 * @param label
	 *            标签
	 * @param colorMark
	 *            颜色标记，0为黑色，1为主题色
	 */
	private void changeLabelColor(final TextView label, int colorMark) {

		if (colorMark == DiscoverItem.COLOR_MARK_BLACK) {
			label.setTextColor(mActivity.getResources().getColor(R.color.black));
		} else if (colorMark == DiscoverItem.COLOR_MARK_THEME_COLOR) {
			label.setTextColor(mActivity.getResources().getColor(
					R.color.theme_color));
		}

	}

	/**
	 * 改变类型图标
	 * @param icon 图标显示的View
	 * @param type 显示的类型
	 */
	public void setTypeIcon(LayoutImageView icon, int type) {
		
		if (type==DiscoverItem.TYPE_UNDEFINE) {
			icon.setImageResource(R.drawable.icon_type_unknown);
		}else if (type==DiscoverItem.TYPE_BLOG) {
			icon.setImageResource(R.drawable.icon_type_blog);
		}else if (type==DiscoverItem.TYPE_WORK) {
			icon.setImageResource(R.drawable.icon_type_work);
		}else if (type==DiscoverItem.TYPE_ENTERTAINMENT) {
			icon.setImageResource(R.drawable.icon_type_entertainment);
		}else if (type==DiscoverItem.TYPE_INFOMATION) {
			icon.setImageResource(R.drawable.icon_type_infomation);
		}
		
	}

	/**
	 * 切换标签页
	 */
	private void switchTab() {

		if (mLabelView.getVisibility() == View.VISIBLE) {// 显示详细模式

			mFuncButtonLeft.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.btn_return));
			mFuncButtonRight.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.btn_title_list_view_style));

			mLabelView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		} else {// 显示标签模式

			mFuncButtonLeft.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.btn_refresh));
			mFuncButtonRight.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.btn_title_label_view_style));

			mLabelView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示选定标签的结果
	 * 
	 * @param tv
	 *            文本标签
	 */
	private void selectLabelItem(TextView tv) {
		// 筛选条件
		mItemsShowList = new ArrayList<DiscoverItem>();

		String keyword = (String) tv.getText();
		for (DiscoverItem item : mItemsList) {

			if (item.labels[0].equals(keyword)
					|| item.labels[1].equals(keyword)
					|| item.labels[2].equals(keyword)
					|| item.labels[3].equals(keyword)) {
				mItemsShowList.add(item);
			}

		}

		Toast.makeText(mActivity, "已显示:" + keyword + "相关的内容",
				Toast.LENGTH_SHORT).show();

		mListViewAdapter.notifyDataSetChanged();
	}
}
