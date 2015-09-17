package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.FeedPagerListViewItem;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
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
		mListView = (DragRefreshListView) mViewContent.findViewById(R.id.lv_feed_list);

		mItemList = new ArrayList<FeedPagerListViewItem>();

	}

	@Override
	protected void initData() {
		super.initData();

		// TODO:暂时填充测试数据
		for (int i = 0; i < 15; i++) {
			FeedPagerListViewItem item = new FeedPagerListViewItem(mActivity);
			mItemList.add(item);
		}

		//设置ListView适配器
		mListView.setAdapter(new FeedPagerListViewAdapter());
		
		mListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onDragRefresh() {
				
				//TODO：暂时模拟刷新操作
				new Thread(){
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						//修改UI必须在主线程执行
						mActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								mListView.completeRefresh();
								Toast.makeText(mActivity, "刷新成功", Toast.LENGTH_SHORT).show();
							}
						});
						
					};
				}.start();
			}

			@Override
			public void onLoadingMore() {
				//TODO：暂时模拟刷新操作
				new Thread(){
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						//修改UI必须在主线程执行
						mActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								mListView.completeRefresh();
								Toast.makeText(mActivity, "加载更多成功", Toast.LENGTH_SHORT).show();
							}
						});
						
					};
				}.start();
			}
		});

	}

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
			return mItemList.size();
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

				holder.mItemPic.setImageDrawable(mActivity.getResources()
						.getDrawable(R.drawable.btn_navi_favor_selected));

				// 检查是否复用ConvertView，平时不需要打印，费时
				// LogUtils.v("FeedPager", "复用View");

			} else {
				// 不可复用

				// TODO:暂时填充测试数据
				FeedPagerListViewItem item = mItemList.get(position);
				item.getItemTitle().setText("测试对象是否正确");
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
}
