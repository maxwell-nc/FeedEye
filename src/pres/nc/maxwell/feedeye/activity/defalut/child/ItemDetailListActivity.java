package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser.OnFinishParseXMLListener;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils;
import pres.nc.maxwell.feedeye.utils.xml.XMLCacheUtils.OnFinishGetLocalCacheListener;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ��ϸ��Ϣ�б��ҳ���Activity
 */
public class ItemDetailListActivity extends DefaultNewActivity {

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
	 * �����еĲ���
	 */
	private RelativeLayout mLoadingLayout;

	/**
	 * ���ز������ı�
	 */
	private TextView mNothingFoundText;

	/**
	 * ����������
	 */
	private ItemDetailListAdapter mListViewAdapter;

	/**
	 * ��ǰҳ��Ķ�����Ŀ��Ϣ
	 */
	private FeedItem mFeedItem;

	/**
	 * ������Ϣ����
	 */
	public ArrayList<FeedXMLContentInfo> mContentInfoList;

	/**
	 * ��ȡ���ػ��������
	 */
	private OnGetLocalCacheListener mLocalCacheListener;

	/**
	 * ���ػ���Ļ�����Ϣ
	 */
	private FeedXMLBaseInfo localCacheBaseInfo;

	/**
	 * û�����ݼ��ص���״̬
	 */
	private static final int STATE_NOTHING = 1;

	/**
	 * ���������е�״̬
	 */
	private static final int STATE_LOADING = 2;

	/**
	 * ��ʾ�����е�״̬
	 */
	private static final int STATE_SHOWING = 3;

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

		mLoadingLayout = (RelativeLayout) mCustomContainerView
				.findViewById(R.id.rl_loading);

		mNothingFoundText = (TextView) mCustomContainerView
				.findViewById(R.id.tv_nothing_found);
	}

	@Override
	protected void initData() {
		super.initData();

		// ������ʾ������
		changeDisplayState(STATE_LOADING);
		
		// ��ȡ���ݽ���������
		mFeedItem = (FeedItem) getIntent().getExtras().getSerializable(
				"FeedItem");

		// ���ñ���
		mTitleView.setText(mFeedItem.baseInfo.title);

		// ��ʼ��������Ϣ�б�
		mContentInfoList = new ArrayList<FeedXMLContentInfo>();

		// ��������������
		mListViewAdapter = new ItemDetailListAdapter();
		mListView.setAdapter(mListViewAdapter);

		// ��ʹ�ü��ظ���
		mListView.setAllowLoadingMore(false);

		// ����ˢ�¼���
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onLoadingMore() {
				// ��ʹ�ô˹���
			}

			@Override
			public void onDragRefresh() {
				getLatestDataFromNetwork();
			}

		});

		// ��������
		LoadData();

	}

	/**
	 * �ı���ʾ״̬
	 * 
	 * @see #STATE_NOTHING
	 * @see #STATE_LOADING
	 * @see #STATE_SHOWING
	 */
	private void changeDisplayState(int state) {

		switch (state) {
			case STATE_NOTHING :
				mNothingFoundText.setVisibility(View.VISIBLE);
				mLoadingLayout.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.INVISIBLE);
				break;
			case STATE_LOADING :
				mNothingFoundText.setVisibility(View.INVISIBLE);
				mLoadingLayout.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.INVISIBLE);
				break;
			case STATE_SHOWING :
				mNothingFoundText.setVisibility(View.INVISIBLE);
				mLoadingLayout.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.VISIBLE);
				break;
		}

	}

	/**
	 * �ȳ��Լ��ر��ػ������ݣ��жϱ��ػ����Ƿ���Ч
	 */
	private void LoadData() {

		// ������ʾ������
		changeDisplayState(STATE_LOADING);
		
		mLocalCacheListener = new OnGetLocalCacheListener();

		try {// ���Ի�ȡ���ػ���
			XMLCacheUtils.getLocalCacheContentInfo(mFeedItem,
					mLocalCacheListener);
		} catch (FileNotFoundException e) {// ��������������ȡ
			GetInfosFromNetwork();
		}

	}

	/**
	 * �������ϼ�������
	 */
	private void GetInfosFromNetwork() {

		if (mContentInfoList.size() != 0) {// ������ʾ����

			mListView.setOnRefreshing();// ����ǿ��ˢ��

		} else {// ����ʾ����

			changeDisplayState(STATE_LOADING);
			getLatestDataFromNetwork();

		}

	}

	/**
	 * �������м�����������
	 */
	private void getLatestDataFromNetwork() {
		// ��ȡ������Ϣ
		FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new OnFinishParseXMLListener() {

					@Override
					public void onFinishParseContent(boolean result,
							ArrayList<FeedXMLContentInfo> contentInfos) {

						LogUtils.w("ItemDetailListActivity", "�������ȡ��"
								+ contentInfos.size() + "������");

						if (!contentInfos.isEmpty()) {

							// ���뵽�ײ�
							mContentInfoList.addAll(0, contentInfos);

							// ����
							mListViewAdapter.notifyDataSetChanged();

							// ���ñ��ػ���,���µĲ��֣��ɵ�������
							XMLCacheUtils
									.setLocalCache(mFeedItem, contentInfos);

						}

						// �ı���ʾ״̬
						if (mContentInfoList.size() != 0) {

							changeDisplayState(STATE_SHOWING);

						} else {

							changeDisplayState(STATE_NOTHING);

						}

						mListView.completeRefresh();
					}

					@Override
					public void onFinishParseBaseInfo(boolean result,
							FeedXMLBaseInfo baseInfo) {
						// ����Ҫ
					}

				});

		// ��������
		feedXMLParser.parse(mFeedItem.feedURL, mFeedItem.encoding,
				FeedXMLParser.TYPE_PARSE_CONTENT);
	}

	/**
	 * ��ȡ���ػ��������
	 */
	class OnGetLocalCacheListener implements OnFinishGetLocalCacheListener {

		@Override
		public void onFinishGetContentInfo(
				ArrayList<FeedXMLContentInfo> contentInfos) {

			if (contentInfos != null) {// ��ȡ������Ϣ

				mContentInfoList = contentInfos;

				LogUtils.w("ItemDetailListActivity",
						"���ؼ�����" + mContentInfoList.size() + "������");

				if (mContentInfoList.size() != 0) {// ���ػ������ݲ�Ϊ��

					// ����
					mListViewAdapter.notifyDataSetChanged();

					// ������ʾListView
					changeDisplayState(STATE_SHOWING);

				}

				try {// ��ȡ���ػ���ʱ��
					XMLCacheUtils.getLocalCacheBaseInfo(mFeedItem,
							mLocalCacheListener);
				} catch (FileNotFoundException e) {// ���汻ɾ��
					e.printStackTrace();
				}

			} else {// �����ȡ����򻺴�������

				GetInfosFromNetwork();

			}

		}

		@Override
		public void onFinishGetBaseInfo(FeedXMLBaseInfo baseInfo) {

			LogUtils.w("ItemDetailListActivity", "�����˱��ػ��������Ϣ");

			localCacheBaseInfo = baseInfo;

			// ����Ƿ���Ҫ��������
			checkIfUpdate();

		}

		/**
		 * �������������ʱ���Ƿ���ڱ��ػ���ʱ�䣬������»���
		 */
		private void checkIfUpdate() {
			// ��ȡ��Ϣ
			FeedXMLParser feedXMLParser = new FeedXMLParser();

			feedXMLParser
					.setOnFinishedParseXMLListener(new OnFinishParseXMLListener() {

						@Override
						public void onFinishParseContent(boolean result,
								ArrayList<FeedXMLContentInfo> contentInfos) {
							// ����Ҫ
						}

						@Override
						public void onFinishParseBaseInfo(boolean result,
								FeedXMLBaseInfo baseInfo) {

							if (result && baseInfo != null) {// �����粢��ȡ�ɹ�

								LogUtils.w("ItemDetailListActivity", "�������ʱ�䣺"
										+ baseInfo.time.toString());
								LogUtils.w("ItemDetailListActivity", "���ػ���ʱ�䣺"
										+ localCacheBaseInfo.time.toString());

								if (baseInfo.time.getTime() > localCacheBaseInfo.time
										.getTime()) {// ��Ҫ����

									LogUtils.w("ItemDetailListActivity",
											"���ػ�����Ҫ����");

									// ����feedItem��ʱ��
									mFeedItem.baseInfo = baseInfo;
									new FeedItemDAO(mThisActivity)
											.updateItem(mFeedItem);

									GetInfosFromNetwork();

								} else {// �������

									LogUtils.w("ItemDetailListActivity",
											"���ػ����������");

								}

							} else {// �����磬��ʾ��������

								LogUtils.w("ItemDetailListActivity",
										"�����磬��ʾ��������");

							}

						}
					});

			// ��������
			feedXMLParser.parse(mFeedItem.feedURL, mFeedItem.encoding,
					FeedXMLParser.TYPE_PARSE_BASE_INFO);
		}

	}

	/**
	 * ��ϸ��Ϣ������������
	 */
	class ItemDetailListAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			int count = mContentInfoList.size();

			return count;
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
			holder.title.setText(mContentInfoList.get(position).title);

			holder.preview.setText(getPreview(position));
			holder.time
					.setText("�����ڣ�"
							+ TimeUtils.LoopToTransTime(mContentInfoList
									.get(position).pubDate));

			return itemView;
		}

		/**
		 * ��ȡԤ������
		 * 
		 * @param position
		 *            ��Ŀλ��
		 * @return Ԥ�������ı�
		 */
		private CharSequence getPreview(int position) {

			String tempString = mContentInfoList.get(position).description;
			if (tempString.length() > 250) {
				tempString = tempString.substring(0, 250);
			}
			
			return Html.fromHtml(tempString).toString();
			
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
