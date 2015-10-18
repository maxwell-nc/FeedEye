package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
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
	 * ��ʼ��View
	 */
	private void initView() {

	}

	/**
	 * ��ʼ������
	 */
	private void initData() {
		// ��ȡ���ݽ���������
		FeedXMLContentInfo feedXMLContentInfo = (FeedXMLContentInfo) getIntent()
				.getExtras().getSerializable("FeedXMLContentInfo");
		FeedItem feedItem = (FeedItem) getIntent().getExtras().getSerializable(
				"FeedItem");

		TextView mHeaderTitle = (TextView) findViewById(R.id.tv_title);
		TextView mHeaderSouceTime = (TextView) findViewById(R.id.tv_source_time);
		TextView mHeaderLink = (TextView) findViewById(R.id.tv_link);
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

		if (TextUtils.isEmpty(feedXMLContentInfo.content)) {

			mContentText.setText(HTTPUtils.html2Text(
					feedXMLContentInfo.description, false, new ArrayList<String>()));

		} else {

			mContentText.setText(HTTPUtils.html2Text(
					feedXMLContentInfo.content, false, new ArrayList<String>()));
		}

	}

}
