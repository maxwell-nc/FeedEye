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
 * 搜索订阅列表页面的Activity
 */
@SuppressLint("DefaultLocale")
public class SearchItemActivity extends Activity {

	/**
	 * 后退按钮
	 */
	private ImageView mBack;

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
	 * 创建Activity时执行
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
	 * 初始化View对象
	 */
	private void initView() {

		mBack = (ImageView) findViewById(R.id.iv_back);
		mSearchText = (EditText) findViewById(R.id.et_search);
		mLoading = (ProgressBar) findViewById(R.id.pb_loading);
		mResultListView = (ListView) findViewById(R.id.lv_search_result);
	}

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {

		// 获得数据
		mShowedList = (ArrayList<FeedItemBean>) getIntent()
				.getSerializableExtra("ShowedList");
		mUnShowList = (ArrayList<FeedItemBean>) getIntent()
				.getSerializableExtra("UnShowList");
		mResultList = new ArrayList<FeedItemBean>();

		/**
		 * 后退点击事件
		 */
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 关闭当前界面
				finish();

			}

		});

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

					// 搜索
					new SearchTask().execute();
				} else {// 关键字为空，清空搜索结果
					if (mResultList != null) {
						mResultList.clear();// 清空搜索结果
						mListViewAdapter.notifyDataSetChanged();
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
			mListViewAdapter.notifyDataSetChanged();

			// 搜索完成，显示加载结果
			mLoading.setVisibility(View.INVISIBLE);
			mResultListView.setVisibility(View.VISIBLE);

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
					R.drawable.anim_refresh_rotate);

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
