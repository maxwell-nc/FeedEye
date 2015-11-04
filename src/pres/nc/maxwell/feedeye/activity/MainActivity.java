package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;
import java.util.List;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.child.ItemDetailListActivity;
import pres.nc.maxwell.feedeye.domain.FavorItem;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.utils.AppSettingUtils;
import pres.nc.maxwell.feedeye.utils.VersionUtils;
import pres.nc.maxwell.feedeye.view.NavigationButtonGroupView;
import pres.nc.maxwell.feedeye.view.NoScrollViewPager;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import pres.nc.maxwell.feedeye.view.pager.child.DiscovePager;
import pres.nc.maxwell.feedeye.view.pager.child.FavorPager;
import pres.nc.maxwell.feedeye.view.pager.child.FeedPager;
import pres.nc.maxwell.feedeye.view.pager.child.SettingPager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 主页面的Activity
 */
public class MainActivity extends NightActivity {

	/**
	 * 主界面页面内容
	 */
	private NoScrollViewPager mContentPager;

	/**
	 * 导航按钮组View
	 */
	private NavigationButtonGroupView mNaviBtnGroup;

	/**
	 * 所有Pager的列表
	 */
	private List<BasePager> mPagerList;

	/**
	 * 订阅页面
	 */
	private FeedPager mFeedPager;

	/**
	 * 发现页面
	 */
	private DiscovePager mDiscovePager;

	/**
	 * 收藏页面
	 */
	private FavorPager mFavorPager;

	/**
	 * 设置页面
	 */
	private SettingPager mSettingPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		initData();
	}

	/**
	 * 初始化View对象
	 */
	private void initView() {

		// 检查更新
		String autoUpdate = AppSettingUtils.get(this,
				AppSettingUtils.KEY_UPDATE_SETTING, "on");

		if ("on".equals(autoUpdate)) {// 开启了自动检查更新
			VersionUtils.checkUpdate(this);
		}

		mContentPager = (NoScrollViewPager) findViewById(R.id.vp_content);
		mNaviBtnGroup = (NavigationButtonGroupView) findViewById(R.id.btn_navi_group);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		mNaviBtnGroup.getNaviBtnGroupView().setOnCheckedChangeListener(
				new CheckedChange(mContentPager));

		mPagerList = new ArrayList<BasePager>();

		// 添加布局进ViewPager
		mFeedPager = new FeedPager(this);
		mDiscovePager = new DiscovePager(this);
		mFavorPager = new FavorPager(this);
		mSettingPager = new SettingPager(this);

		mPagerList.add(mFeedPager);// 订阅
		mPagerList.add(mDiscovePager);// 发现
		mPagerList.add(mFavorPager);// 收藏
		mPagerList.add(mSettingPager);// 设置

		mContentPager.setOffscreenPageLimit(3);// 设置3个缓存页面+1个显示的
		mContentPager.setAdapter(new PagerInflateAdapter(mPagerList));
		   
	}
	
	/**
	 * 返回Activity时处理返回数据
	 * 
	 * @See 请求码为1时：<br>
	 *      返回数据：
	 *      {@link pres.nc.maxwell.feedeye.activity.defalut.child.AddFeedActivity#addItem()}
	 * 
	 * @see 请求码为2时：<br>
	 *      返回数据：{@link ItemDetailListActivity#FavorOnClickListener}
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 1 :// 添加界面

				if (resultCode == 1) {// 添加成功

					FeedItem feedItem = (FeedItem) data.getExtras()
							.getSerializable("FeedItem");

					if (feedItem != null) {
						mFeedPager.finishedAddItem(feedItem);
						// 切换到订阅信息页面
						mNaviBtnGroup.getNaviBtnGroupView().check(R.id.rb_feed);
					}

				}
				break;
			case 2 :// 收藏增加通知
				if (resultCode == 1) {// 收藏增加

					@SuppressWarnings("unchecked")
					ArrayList<FavorItem> favorItems = (ArrayList<FavorItem>) data
							.getExtras().getSerializable("FavorItems");

					if (favorItems != null) {
						mFavorPager.addFavorItemAtFirst(favorItems);
					}

				}
				break;
		}

	}

	@Override
	public void onBackPressed() {

		// 返回标签选择
		if (mNaviBtnGroup.getNaviBtnGroupView().getCheckedRadioButtonId() == R.id.rb_discover
				&& mDiscovePager.mButtonState == DiscovePager.STATE_SHOW_ALL) {
			mDiscovePager.switchTab();
			return;
		}

		super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		// 订阅
		if (mContentPager.getCurrentItem() == 0) {
			// 复写点击菜单键
			if (keyCode == KeyEvent.KEYCODE_MENU) {

				// 模拟点击更多选项
				mFeedPager.onClickAddItem();

				return true;
			}

		}

		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 导航按钮点击事件监听器
	 */
	class CheckedChange implements OnCheckedChangeListener {

		private NoScrollViewPager mContentPager;

		public CheckedChange(NoScrollViewPager mContentPager) {
			super();
			this.mContentPager = mContentPager;
		}

		/**
		 * 点击切换界面，去掉ViewPager的切换效果
		 */
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.rb_feed :// 订阅
					mContentPager.setCurrentItem(0, false);
					break;
				case R.id.rb_discover :// 发现
					mContentPager.setCurrentItem(1, false);
					break;
				case R.id.rb_favor :// 收藏
					mContentPager.setCurrentItem(2, false);
					break;
				case R.id.rb_setting :// 设置
					mContentPager.setCurrentItem(3, false);
					break;
			}
		}
	}

	/**
	 * ViewPager填充页面适配器
	 */
	class PagerInflateAdapter extends PagerAdapter {

		private List<BasePager> mPagerList;

		public PagerInflateAdapter(List<BasePager> mPagerList) {
			super();
			this.mPagerList = mPagerList;
		}

		@Override
		public int getCount() {
			return mPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BasePager pager = mPagerList.get(position);
			// pager.getTitleView().setText("测试列表" + position);

			View view = pager.getView();
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