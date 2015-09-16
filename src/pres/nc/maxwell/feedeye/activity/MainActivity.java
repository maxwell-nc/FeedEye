package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;
import java.util.List;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.NavigationButtonGroupView;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import pres.nc.maxwell.feedeye.view.pager.child.FeedPager;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity {

	private ViewPager mContentPager;// ������ҳ������
	private NavigationButtonGroupView mNaviBtnGroup;

	private List<BasePager> mPagerList;

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
		mContentPager = (ViewPager) findViewById(R.id.vp_content);
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
		mPagerList.add(new FeedPager(this));
		for (int i = 1; i < 4; i++) {
			mPagerList.add(new BasePager(this));
		}

		mContentPager.setAdapter(new PagerInflateAdapter(mPagerList));
		
	}

}

/**
 * ������ť����¼�������
 */
class CheckedChange implements OnCheckedChangeListener {

	private ViewPager mContentPager;

	public CheckedChange(ViewPager mContentPager) {
		super();
		this.mContentPager = mContentPager;
	}

	/**
	 * ����л����棬ȥ��ViewPager���л�Ч��
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_feed:// ����
			mContentPager.setCurrentItem(0, false);
			break;
		case R.id.rb_discover:// ����
			mContentPager.setCurrentItem(1, false);
			break;
		case R.id.rb_favor:// �ղ�
			mContentPager.setCurrentItem(2, false);
			break;
		case R.id.rb_setting:// ����
			mContentPager.setCurrentItem(3, false);
			break;
		}
	}
}


/**
 * ViewPager���ҳ��������
 */
class PagerInflateAdapter extends PagerAdapter{

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
		//pager.getTitleView().setText("�����б�" + position);

		View view = pager.getView();
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position,
			Object object) {
		container.removeView((View) object);
		// super.destroyItem(container, position, object);
	}

}