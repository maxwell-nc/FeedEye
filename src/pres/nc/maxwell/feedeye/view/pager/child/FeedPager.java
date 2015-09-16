package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.FeedPagerListViewItem;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * ����ҳ���Pager
 */
public class FeedPager extends BasePager {

	private View mViewContent; // ��䵽�������е�FrameLayout�е�View����
	private ListView mListView;// �����б�
	private ArrayList<FeedPagerListViewItem> mItemList;// ListView�е�Item����

	public ListView getListView() {
		return mListView;
	}

	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("�����б�");
		mViewContent = setContainerContent(R.layout.pager_main_feed);
		mListView = (ListView) mViewContent.findViewById(R.id.lv_feed_list);

		mItemList = new ArrayList<FeedPagerListViewItem>();

	}

	@Override
	protected void initData() {
		super.initData();

		// TODO:��ʱ����������
		for (int i = 0; i < 15; i++) {
			FeedPagerListViewItem item = new FeedPagerListViewItem(mActivity);
			mItemList.add(item);
		}

		mListView.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return 15;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				// TODO:��ʱ����������
				FeedPagerListViewItem item = mItemList.get(position);
				item.getmItemTitle().setText("���Զ����Ƿ���ȷ");
				if (position == 5) {

					item.getmItemPic().setImageDrawable(
							mActivity.getResources().getDrawable(
									R.drawable.ic_launcher));

				}
				return item.getItemView();
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

		});
	}

}
