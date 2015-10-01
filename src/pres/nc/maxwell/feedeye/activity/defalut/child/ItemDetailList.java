package pres.nc.maxwell.feedeye.activity.defalut.child;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * ��ϸ��Ϣ�б��ҳ���Activity
 */
public class ItemDetailList extends DefaultNewActivity {

	/**
	 * ��Activity�����������ڲ������
	 */
	private Activity mThisActivity;

	/**
	 * �����ı�
	 */
	private TextView mTitleView;

	/**
	 * �����б�
	 */
	private DragRefreshListView mListView;

	/**
	 * ����������
	 */
	private ItemDetailListAdapter mListViewAdapter;

	@Override
	protected void initView() {
		super.initView();

		mThisActivity = this;

		addView(R.layout.activity_item_detail_list_bar,
				R.layout.activity_item_detail_list_container);

		// ����
		mTitleView = (TextView) mCustomBarView.findViewById(R.id.tv_title);

		mListView = (DragRefreshListView) mCustomContainerView
				.findViewById(R.id.lv_detail);

	}

	@Override
	protected void initData() {
		super.initData();

		// ��ȡ���ݹ���������
		FeedItemBean infoBean = (FeedItemBean) getIntent().getExtras()
				.getSerializable("FeedItemBean");

		// ���ñ���
		mTitleView.setText(infoBean.getTitle());

		// ��������������
		mListViewAdapter = new ItemDetailListAdapter();
		mListView.setAdapter(mListViewAdapter);

	}

	/**
	 * ��ϸ��Ϣ������������
	 */
	class ItemDetailListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			FrameLayout itemView;

			if (convertView != null && convertView instanceof FrameLayout) {// ����

				itemView = (FrameLayout) convertView;
				holder = (ViewHolder) itemView.getTag();
				
			} else {// ��Ҫ�´���
				itemView = (FrameLayout) View.inflate(mThisActivity,
						R.layout.view_lv_item_detail, null);

				holder = new ViewHolder();

				holder.title = (TextView) itemView.findViewById(R.id.tv_title);
				holder.preview = (TextView) itemView
						.findViewById(R.id.tv_preview);
				holder.time = (TextView) itemView.findViewById(R.id.tv_time);
				
				itemView.setTag(holder);
			}
			
			//�����߼�
			holder.title.setText("������Ϣ����");
			holder.preview.setText("������Ϣ���ݣ�������ݺܳ����Զ��任�߶ȣ��Ϊ���У�");
			holder.time.setText("����11:10");

			return itemView;
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
	 * ViewHolder
	 * 
	 * @see ItemDetailListAdapter
	 */
	static class ViewHolder {
		public TextView title;
		public TextView preview;
		public TextView time;
	}

}
