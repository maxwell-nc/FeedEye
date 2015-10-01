package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 搜索订阅列表页面的Activity
 */
@SuppressLint("DefaultLocale")
public class SearchItemActivity extends DefaultNewActivity {

	/**
	 * 代表当前Activity，用于跳转Activity
	 */
	private Activity mThisActivity;
	
	/**
	 * 搜索关键字输入框
	 */
	private EditText mSearchText;

	/**
	 * 加载中的图片
	 */
	private ProgressBar mLoading;

	/**
	 * 结果列表
	 */
	private ListView mResultListView;

	/**
	 * 什么都没找到的文本
	 */
	private TextView mNothingFound;

	/**
	 * 已经显示的列表
	 */
	private ArrayList<FeedItemBean> mShowedList;

	/**
	 * 未显示的列表
	 */
	private ArrayList<FeedItemBean> mUnShowList;

	/**
	 * 搜索结果的列表
	 */
	private ArrayList<FeedItemBean> mResultList;

	/**
	 * 结果ListView的适配器
	 */
	private ResultListAdapter mListViewAdapter;

	/**
	 * 初始化View对象
	 */
	@Override
	protected void initView() {
		
		mThisActivity = this;
		
		super.initView();
		
		addView(R.layout.activity_search_item_bar, R.layout.activity_search_item_container);

		mSearchText = (EditText) mCustomBarView.findViewById(R.id.et_search);
		mLoading = (ProgressBar) mCustomContainerView.findViewById(R.id.pb_loading);
		mResultListView = (ListView) mCustomContainerView
				.findViewById(R.id.lv_search_result);
		mNothingFound = (TextView) mCustomContainerView
				.findViewById(R.id.tv_nothing_found);
	}

	/**
	 * 初始化数据
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void initData() {

		super.initData();

		// 获得数据
		mShowedList = (ArrayList<FeedItemBean>) getIntent()
				.getSerializableExtra("ShowedList");
		mUnShowList = (ArrayList<FeedItemBean>) getIntent()
				.getSerializableExtra("UnShowList");
		mResultList = new ArrayList<FeedItemBean>();

		// 设置适配器
		mListViewAdapter = new ResultListAdapter();
		mResultListView.setAdapter(mListViewAdapter);

		/**
		 * 监听输入变化
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

				if (!TextUtils.isEmpty(mSearchText.getText())) {// 不搜索空字串
					// 开始搜索，显示加载中动画
					mLoading.setVisibility(View.VISIBLE);
					mResultListView.setVisibility(View.INVISIBLE);
					mNothingFound.setVisibility(View.INVISIBLE);

					// 搜索
					new SearchTask().execute();
				} else {// 关键字为空，清空搜索结果
					if (mResultList != null) {
						mResultList.clear();// 清空搜索结果
						// mListViewAdapter.notifyDataSetChanged();
					}
					mLoading.setVisibility(View.INVISIBLE);
					mResultListView.setVisibility(View.INVISIBLE);
					mNothingFound.setVisibility(View.VISIBLE);
				}

			}
		});
		
		//搜索后跳转到结果页面
		mResultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int clickItem = mResultList.get(position).getItemId();
				
				for (int i = 0; i < mShowedList.size(); i++) {

					// 搜索已显示的列表
					if (mShowedList.get(i).getItemId() == clickItem) {
						Intent intent = new Intent(mThisActivity,ItemDetailList.class);
						intent.putExtra("FeedItemBean", mShowedList.get(i));
						mThisActivity.startActivity(intent);
						
						finish();//不需要搜索界面？
					}
					
				}
				
				
				for (int i = 0; i < mUnShowList.size(); i++) {

					// 搜索未显示的列表
					if (mUnShowList.get(i).getItemId() == clickItem) {
						Intent intent = new Intent(mThisActivity,ItemDetailList.class);
						intent.putExtra("FeedItemBean", mUnShowList.get(i));
						mThisActivity.startActivity(intent);
						
						finish();//不需要搜索界面？
					}
				}
				
				
			}
		});

	}

	/**
	 * 异步搜索任务
	 */
	class SearchTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// 子线程

			String keyword = mSearchText.getText().toString();

			LogUtils.e("SearchItemActivity", "搜索" + keyword);

			searchLists(keyword);

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {// 主线程

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {// 主线程

			// 搜索完成，显示加载结果

			if (mResultList.size() == 0) {// 找不到

				mLoading.setVisibility(View.INVISIBLE);
				mResultListView.setVisibility(View.INVISIBLE);
				mNothingFound.setVisibility(View.VISIBLE);

			} else {

				mLoading.setVisibility(View.INVISIBLE);
				mResultListView.setVisibility(View.VISIBLE);
				mNothingFound.setVisibility(View.INVISIBLE);

				mListViewAdapter.notifyDataSetChanged();
			}

			super.onPostExecute(result);
		}
	}

	/**
	 * 按关键字来搜索
	 * 
	 * @param keyword
	 *            关键字
	 */
	private void searchLists(String keyword) {

		keyword = keyword.toLowerCase();

		if (mResultList != null) {
			mResultList.clear();// 清空上次的搜索结果
		}

		for (int i = 0; i < mShowedList.size(); i++) {

			// 搜索已显示的列表
			if (mShowedList.get(i).getTitle().toLowerCase().contains(keyword)) {
				mResultList.add(mShowedList.get(i));
			}
		}
		for (int i = 0; i < mUnShowList.size(); i++) {

			// 搜索未显示的列表,大小写不明感
			if (mUnShowList.get(i).getTitle().toLowerCase().contains(keyword)) {
				mResultList.add(mUnShowList.get(i));
			}
		}

	}

	/**
	 * ListView的适配器
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

			// 使用三级缓存加载图片
			new BitmapCacheUtils().displayBitmap(holder.pic,
					mResultList.get(position).getPicURL(),
					R.anim.refresh_rotate);

			String title = mResultList.get(position).getTitle();

			String keyword = mSearchText.getText().toString().toLowerCase();

			int startIndex = title.toLowerCase().indexOf(keyword);

			if (startIndex == -1) {

				holder.title.setText(title);

			} else {// 突出结果中的关键字

				SpannableStringBuilder builder = new SpannableStringBuilder(
						title);

				// ForegroundColorSpan 为文字前景色
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
