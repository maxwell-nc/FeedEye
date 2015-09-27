package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ���������б�ҳ���Activity
 */
@SuppressLint("DefaultLocale")
public class SearchItemActivity extends Activity {

	/**
	 * ���˰�ť
	 */
	private ImageView mBack;

	/**
	 * �����ؼ��������
	 */
	private EditText mSearchText;

	/**
	 * �����е�ͼƬ
	 */
	private ProgressBar mLoading;

	/**
	 * ����б�
	 */
	private ListView mResultListView;

	/**
	 * �Ѿ���ʾ���б�
	 */
	private ArrayList<FeedItemBean> mShowedList;

	/**
	 * δ��ʾ���б�
	 */
	private ArrayList<FeedItemBean> mUnShowList;

	/**
	 * ����������б�
	 */
	private ArrayList<FeedItemBean> mResultList;

	/**
	 * ���ListView��������
	 */
	private ResultListAdapter mListViewAdapter;

	/**
	 * ����Activityʱִ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_item);

		initView();
		initData();
	}

	/**
	 * ��ʼ��View����
	 */
	private void initView() {

		mBack = (ImageView) findViewById(R.id.iv_back);
		mSearchText = (EditText) findViewById(R.id.et_search);
		mLoading = (ProgressBar) findViewById(R.id.pb_loading);
		mResultListView = (ListView) findViewById(R.id.lv_search_result);
	}

	/**
	 * ��ʼ������
	 */
	@SuppressWarnings("unchecked")
	private void initData() {

		// �������
		mShowedList = (ArrayList<FeedItemBean>) getIntent()
				.getSerializableExtra("ShowedList");
		mUnShowList = (ArrayList<FeedItemBean>) getIntent()
				.getSerializableExtra("UnShowList");
		mResultList = new ArrayList<FeedItemBean>();

		/**
		 * ���˵���¼�
		 */
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// �رյ�ǰ����
				finish();

			}

		});

		// ����������
		mListViewAdapter = new ResultListAdapter();
		mResultListView.setAdapter(mListViewAdapter);

		/**
		 * ��������仯
		 */
		mSearchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (!TextUtils.isEmpty(mSearchText.getText())) {// ���������ִ�
					// ��ʼ��������ʾ�����ж���
					mLoading.setVisibility(View.VISIBLE);
					mResultListView.setVisibility(View.INVISIBLE);

					// ����
					new SearchTask().execute();
				} else {// �ؼ���Ϊ�գ�����������
					if (mResultList != null) {
						mResultList.clear();// ����������
						mListViewAdapter.notifyDataSetChanged();
					}
				}

			}
		});

	}

	/**
	 * �첽��������
	 */
	class SearchTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// ���߳�

			String keyword = mSearchText.getText().toString();

			LogUtils.e("SearchItemActivity", "����" + keyword);

			searchLists(keyword);

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {// ���߳�
			mListViewAdapter.notifyDataSetChanged();

			// ������ɣ���ʾ���ؽ��
			mLoading.setVisibility(View.INVISIBLE);
			mResultListView.setVisibility(View.VISIBLE);

			super.onPostExecute(result);
		}
	}

	/**
	 * ���ؼ���������
	 * 
	 * @param keyword
	 *            �ؼ���
	 */
	private void searchLists(String keyword) {

		keyword = keyword.toLowerCase();

		if (mResultList != null) {
			mResultList.clear();// ����ϴε��������
		}

		for (int i = 0; i < mShowedList.size(); i++) {

			// ��������ʾ���б�
			if (mShowedList.get(i).getTitle().toLowerCase().contains(keyword)) {
				mResultList.add(mShowedList.get(i));
			}
		}
		for (int i = 0; i < mUnShowList.size(); i++) {

			// ����δ��ʾ���б�,��Сд������
			if (mUnShowList.get(i).getTitle().toLowerCase().contains(keyword)) {
				mResultList.add(mUnShowList.get(i));
			}
		}

	}

	/**
	 * ListView��������
	 */
	class ResultListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mResultList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout itemView;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {

				itemView = (RelativeLayout) convertView;
				holder = (ViewHolder) itemView.getTag();

			} else {
				itemView = (RelativeLayout) View.inflate(
						getApplicationContext(),
						R.layout.view_lv_item_result_search, null);
				holder = new ViewHolder();

				holder.pic = (ImageView) itemView
						.findViewById(R.id.iv_item_pic);
				holder.title = (TextView) itemView.findViewById(R.id.tv_title);

				itemView.setTag(holder);
			}

			// ʹ�������������ͼƬ
			new BitmapCacheUtils().displayBitmap(holder.pic,
					mResultList.get(position).getPicURL(),
					R.drawable.anim_refresh_rotate);

			String title = mResultList.get(position).getTitle();

			String keyword = mSearchText.getText().toString().toLowerCase();

			int startIndex = title.toLowerCase().indexOf(keyword);

			if (startIndex == -1) {
				
				holder.title.setText(title);
				
			} else {// ͻ������еĹؼ���
				
				SpannableStringBuilder builder = new SpannableStringBuilder(
						title);

				// ForegroundColorSpan Ϊ����ǰ��ɫ
				ForegroundColorSpan redSpan = new ForegroundColorSpan(
						getResources().getColor(R.color.red));

				builder.setSpan(redSpan, startIndex,
						startIndex + keyword.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				holder.title.setText(builder);
			}

			return itemView;
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
	 * ViewHolder
	 */
	static class ViewHolder {
		ImageView pic;
		TextView title;
	}

}
