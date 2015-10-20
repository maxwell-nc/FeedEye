package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;

public class SummaryBodyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_summary_body);
		super.onCreate(savedInstanceState);

		initView();
		initData();
	}

	/**
	 * 初始化View
	 */
	private void initView() {

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 获取传递进来的数据
		FeedXMLContentInfo feedXMLContentInfo = (FeedXMLContentInfo) getIntent()
				.getExtras().getSerializable("FeedXMLContentInfo");
		FeedItem feedItem = (FeedItem) getIntent().getExtras().getSerializable(
				"FeedItem");

		TextView mHeaderTitle = (TextView) findViewById(R.id.tv_title);
		TextView mHeaderSouceTime = (TextView) findViewById(R.id.tv_source_time);
		TextView mHeaderLink = (TextView) findViewById(R.id.tv_link);
		final LayoutImageView mContentImg = (LayoutImageView) findViewById(R.id.iv_pic1);
		TextView mContentText = (TextView) findViewById(R.id.tv_content);

		mHeaderTitle.setText(feedXMLContentInfo.title);

		String titleString = "";
		if (feedItem.baseInfo.title.length() > 20) {
			titleString = feedItem.baseInfo.title.substring(0, 17) + "...";
		} else {
			titleString = feedItem.baseInfo.title;
		}

		String sourceTime = titleString
				+ " / "
				+ TimeUtils.LoopToTransTime(feedXMLContentInfo.pubDate,
						TimeUtils.STANDARD_TIME_PATTERN);
		mHeaderSouceTime.setText(sourceTime);
		mHeaderLink.setText(feedXMLContentInfo.link);
		final ArrayList<String> list = new ArrayList<String>();
		if (TextUtils.isEmpty(feedXMLContentInfo.content)) {

			mContentText.setText(HTTPUtils.html2Text(
					feedXMLContentInfo.description, false, list));

		} else {

			mContentText.setText(HTTPUtils.html2Text(
					feedXMLContentInfo.content, false, list));
		}

		if (list.size() > 0 && list.get(0) != null) {
			BitmapCacheUtils.removeCache(list.get(0));

			BitmapCacheUtils.displayBitmapOnLayoutChange(
					SummaryBodyActivity.this, mContentImg, list.get(0), null);

		}
	}

}
