package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.FeedPagerListViewItem;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
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

	private View mViewContent; // 填充到父布局中的FrameLayout中的View对象

	private DragRefreshListView mListView;// 订阅列表
	private ArrayList<FeedPagerListViewItem> mItemList;// ListView中的Item集合
	private ArrayList<FeedPagerListViewItem> mItemShowedList;// ListView中已显示的Item集合

	private final int SHOW_ITEM_COUNT = 20;// 一次展示的Item数量

	private FeedPagerListViewAdapter mListViewAdapter;

	private ImageView mNothingImg;// ListView为空显示的提示图片

	public DragRefreshListView getListView() {
		return mListView;
	}

	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("订阅列表");
		mViewContent = setContainerContent(R.layout.pager_main_feed);
		mListView = (DragRefreshListView) mViewContent
				.findViewById(R.id.lv_feed_list);

		mNothingImg = (ImageView) mViewContent.findViewById(R.id.iv_nothing);

		mItemList = new ArrayList<FeedPagerListViewItem>();
		mItemShowedList = new ArrayList<FeedPagerListViewItem>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// TODO:暂时填充测试数据
		for (int i = 0; i < 2000; i++) {
			FeedPagerListViewItem item = new FeedPagerListViewItem(mActivity);
			mItemList.add(item);
		}

		// 插入要加载的Item
		insertMoreItem();

		// 设置ListView适配器，添加数据
		if (mItemShowedList.size() == 0) {// 无数据
			// 不显示加载条
			getLoadingBarView().setVisibility(View.INVISIBLE);

			// 提示没有数据，需要添加
			mNothingImg.setVisibility(View.VISIBLE);
		} else {// 有数据
			setListViewData(500);
		}

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

		mListView.setOnRefreshListener(new ListViewRefreshListener());

		// 设置点击图片添加
		mNothingImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO:执行添加订阅操作
			}

		});

	}

	/**
	 * 使用功能按钮，初始化按钮
	 */
	@Override
	protected void useFunctionButton() {

		super.useFunctionButton();

		mFuncButtonLeft.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_search));
		mFuncButtonLeft.setVisibility(View.VISIBLE);

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_add));
		mFuncButtonRight.setVisibility(View.VISIBLE);
	};

	/**
	 * 设置ListView适配器
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
	 */
	private void insertMoreItem() {
		int addCount = 0;

		if (mItemList.size() > SHOW_ITEM_COUNT) {
			addCount = SHOW_ITEM_COUNT;
		} else {
			addCount = mItemList.size();
		}

		for (int i = 0; i < addCount; i++) {
			mItemShowedList.add(mItemList.get(i));
		}
		for (int i = addCount - 1; i >= 0; i--) {
			mItemList.remove(i);
		}
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

			// 打印要显示的数目
			// LogUtils.w("FeedPager", "ListCount:" + mItemShowedList.size());

			return mItemShowedList.size();
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

				holder.mItemTitle.setText("复用对象" + position);
				//holder.mItemPic.setImageDrawable(mActivity.getResources()
				//		.getDrawable(R.drawable.btn_navi_favor_selected));

				//测试代码
				new BitmapCacheUtils()
						.displayBitmapWithLoadingImage(holder.mItemPic,
								"https://avatars3.githubusercontent.com/u/14196813?v=3&s="+position,R.drawable.anim_refresh_rotate);

				// 检查是否复用ConvertView，平时不需要打印，费时
				// LogUtils.v("FeedPager", "复用View");

			} else {
				// 不可复用

				// TODO:暂时填充测试数据
				FeedPagerListViewItem item = mItemShowedList.get(position);
				item.initListViewItem();
				item.getItemTitle().setText("新创建对象" + position);
				if (position == 5) {

					item.getItemPic().setImageDrawable(
							mActivity.getResources().getDrawable(
									R.drawable.ic_launcher));

				}
				view = (RelativeLayout) item.getItemView();

				// 利用ViewHolder记录子孩子View对象
				holder = new ViewHolder();

				holder.mItemPic = item.getItemPic();
				holder.mItemTitle = item.getItemTitle();
				holder.mItemPreview = item.getItemPreview();
				holder.mItemTime = item.getItemTime();
				holder.mItemCount = item.getItemCount();

				view.setTag(holder);
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
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 修改UI必须在主线程执行
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mListView.completeRefresh();
							mListViewAdapter.notifyDataSetChanged();
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
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 模拟插入数据
					insertMoreItem();

					// 修改UI必须在主线程执行
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mListView.completeRefresh();
							mListViewAdapter.notifyDataSetChanged();
							Toast.makeText(mActivity, "加载更多成功",
									Toast.LENGTH_SHORT).show();
						}
					});

				}

			}.start();
		}
	}
}
