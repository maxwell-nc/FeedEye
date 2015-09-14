package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;
import java.util.List;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class MainActivity extends Activity {

	private ViewPager mContentPager;//������ҳ������
	
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
		
		
		mPagerList = new ArrayList<BasePager>();
		
		//��Ӳ��ֽ�ViewPager
		for (int i = 0; i < 4; i++) {
			mPagerList.add(new BasePager(this) {
				
				@Override
				public View getView() {
					return mView;
				}
			});
		}
		
		
		mContentPager.setAdapter(new PagerAdapter() {
			
			@Override
			public int getCount() {
				return mPagerList.size();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view==object;
			}
			
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				BasePager pager = mPagerList.get(position);
				View view = pager.getView();
				container.addView(view);
				return view;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView((View) object);
				//super.destroyItem(container, position, object);
			}

		});
	}
	

	/**
	 * ��ʼ������
	 */
	private void initData() {
		
	}
	
	
}
