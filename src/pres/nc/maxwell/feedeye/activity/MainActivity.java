package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;
import java.util.List;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.view.NavigationButtonGroupView;
import pres.nc.maxwell.feedeye.view.NoScrollViewPager;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import pres.nc.maxwell.feedeye.view.pager.child.FeedPager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * ��ҳ���Activity
 */
public class MainActivity extends Activity {

	/**
	 * ������ҳ������
	 */
	private NoScrollViewPager mContentPager;

	/**
	 * ������ť��View
	 */
	private NavigationButtonGroupView mNaviBtnGroup;

	/**
	 * ����Pager���б�
	 */
	private List<BasePager> mPagerList;

	/**
	 * ����ҳ��
	 */
	private FeedPager mFeedPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initView();
		initData();
	}

	/**
	 * ��ʼ��View����
	 */
	private void initView() {
		mContentPager = (NoScrollViewPager) findViewById(R.id.vp_content);
		mNaviBtnGroup = (NavigationButtonGroupView) findViewById(R.id.btn_navi_group);
	}

	/**
	 * ��ʼ������
	 */
	private void initData() {

		mNaviBtnGroup.getNaviBtnGroupView().setOnCheckedChangeListener(
				new CheckedChange(mContentPager));

		mPagerList = new ArrayList<BasePager>();

		// ��Ӳ��ֽ�ViewPager
		mFeedPager = new FeedPager(this);

		mPagerList.add(mFeedPager);
		for (int i = 1; i < 4; i++) {
			mPagerList.add(new BasePager(this));
		}

		mContentPager.setAdapter(new PagerInflateAdapter(mPagerList));

	}

	/**
	 * ����Activityʱ����������
	 * 
	 * ������Ϊ1ʱ��
	 * 
	 * @See ���󷵻�����{@link FeedPager#addNewFeedItem()}
	 * @See �������ݣ�
	 *      {@link pres.nc.maxwell.feedeye.activity.defalut.child.AddFeedActivity#addItem()}
	 * @See ���շ�������{@link FeedPager#finishedAddItem()}
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 1 :// ��ӽ���

				if (resultCode != -1) {

					FeedItem feedItem = (FeedItem) data.getExtras()
							.getSerializable("FeedItem");

					if (feedItem != null) {
						mFeedPager.finishedAddItem(feedItem);
					}

				}
				break;
		}

	}

}

/**
 * ������ť����¼�������
 */
class CheckedChange implements OnCheckedChangeListener {

	private NoScrollViewPager mContentPager;

	public CheckedChange(NoScrollViewPager mContentPager) {
		super();
		this.mContentPager = mContentPager;
	}

	/**
	 * ����л����棬ȥ��ViewPager���л�Ч��
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.rb_feed :// ����
				mContentPager.setCurrentItem(0, false);
				break;
			case R.id.rb_discover :// ����
				mContentPager.setCurrentItem(1, false);
				break;
			case R.id.rb_favor :// �ղ�
				mContentPager.setCurrentItem(2, false);
				break;
			case R.id.rb_setting :// ����
				mContentPager.setCurrentItem(3, false);
				break;
		}
	}
}

/**
 * ViewPager���ҳ��������
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
		// pager.getTitleView().setText("�����б�" + position);

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