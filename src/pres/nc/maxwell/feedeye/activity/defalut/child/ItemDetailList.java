package pres.nc.maxwell.feedeye.activity.defalut.child;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
	 * �����е�ͼƬ
	 */
	private ProgressBar mLoadingPic;

	/**
	 * ���ز������ı�
	 */
	private TextView mNothingFoundText;

	/**
	 * ����������
	 */
	private ItemDetailListAdapter mListViewAdapter;

	/**
	 * ��ǰҳ��Ķ���bean
	 */
	private FeedItemBean mInfoBean;

	private int mCount;

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

		mLoadingPic = (ProgressBar) mCustomContainerView
				.findViewById(R.id.pb_loading);

		mNothingFoundText = (TextView) mCustomContainerView
				.findViewById(R.id.tv_nothing_found);
	}

	@Override
	protected void initData() {
		super.initData();

		mInfoBean = (FeedItemBean) getIntent().getExtras().getSerializable(
				"FeedItemBean");

		// ���ñ���
		mTitleView.setText(mInfoBean.getTitle());

		// ������ʾ������
		mNothingFoundText.setVisibility(View.INVISIBLE);
		mLoadingPic.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.INVISIBLE);

		// ��ȡ��Ϣ

		final FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(feedXMLParser.new OnFinishParseDefaultListener() {

					@Override
					public void onFinishParseContent(boolean result) {

						mCount = feedXMLParser.mContentInfo.mContentCount;

						Toast.makeText(mThisActivity, "������" + mCount + "������",
								Toast.LENGTH_SHORT).show();

						// ��������������
						mListViewAdapter = new ItemDetailListAdapter();
						mListView.setAdapter(mListViewAdapter);
						
						// TODO�����ݽ���Ƿ���ʾListView
						// ������ʾ������
						mNothingFoundText.setVisibility(View.INVISIBLE);
						mLoadingPic.setVisibility(View.INVISIBLE);
						mListView.setVisibility(View.VISIBLE);
						
					}

				});


		// ��������
		feedXMLParser.parse(mInfoBean.getFeedURL(), "UTF-8" ,FeedXMLParser.TYPE_PARSE_CONTENT);

	}

	/**
	 * ��ϸ��Ϣ������������
	 */
	class ItemDetailListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCount;
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

			// �����߼�
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
		TextView title;
		TextView preview;
		TextView time;
	}

}
