package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.ArrayListLoadingMoreAdapter;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FavorPager extends BasePager {

	/**
	 * 页面中的ViewPager对象
	 */
	private ViewPager mContentPager;
	
	/**
	 * ViewPager的指示器
	 */
	private PagerTabStrip mPagerIndicator;
	
	/**
	 * 最近收藏的ListView对象
	 */
	private DragRefreshListView mRecentListView;
	
	/**
	 * 全部收藏的ListView对象
	 */
	private DragRefreshListView mFullListView;

	public FavorPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("收藏页面");
		mViewContent = setContainerContent(R.layout.pager_favor);

		// 不显示加载条
		getLoadingBarView().setVisibility(View.INVISIBLE);

		mContentPager = (ViewPager) mViewContent.findViewById(R.id.vp_content);
		mPagerIndicator = (PagerTabStrip) mViewContent
				.findViewById(R.id.pts_indicator);

		mRecentListView = (DragRefreshListView) View.inflate(mActivity,
				R.layout.view_favor_recently, null);
		mFullListView = (DragRefreshListView) View.inflate(mActivity,
				R.layout.view_favor_full_list, null);
	}

	@Override
	protected void initData() {
		super.initData();

		mRecentListView.setAdapter(new RecentListAdapter());
		//TODO：
		//mFullListView.setAdapter(new FullListAdapter(mFullListView, null, null, 0));
		mFullListView.setAdapter(new RecentListAdapter());
		
		//设置ViewPager适配器
		mContentPager.setAdapter(new FavorPagerAdapter());

	}

	/**
	 * 最近收藏列表数据适配器
	 */
	class RecentListAdapter extends BaseAdapter{

		//TODO
		@Override
		public int getCount() {
			return 0;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
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
	 * 所有收藏列表数据适配器
	 */
	class FullListAdapter extends ArrayListLoadingMoreAdapter<String>{

		
		//TODO：暂时设计为String泛型
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
