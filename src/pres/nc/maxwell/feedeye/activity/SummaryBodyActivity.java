package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.utils.DensityUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SummaryBodyActivity extends Activity {

	/**
	 * 正文头部的标题
	 */
	private TextView mHeaderTitle;
	
	/**
	 * 正文头部的提供源和时间
	 */
	private TextView mHeaderSouceTime;
	
	/**
	 * 正文头部的链接
	 */
	private TextView mHeaderLink;
	
	/**
	 * 正文内容容器
	 */
	private LinearLayout mBodyContainer;

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

		mBodyContainer = (LinearLayout) findViewById(R.id.ll_container);

		mHeaderTitle = (TextView) mBodyContainer.findViewById(R.id.tv_title);
		mHeaderSouceTime = (TextView) mBodyContainer
				.findViewById(R.id.tv_source_time);
		mHeaderLink = (TextView) mBodyContainer.findViewById(R.id.tv_link);

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

		// 设置正文头部标题
		mHeaderTitle.setText(feedXMLContentInfo.title);

		// 提供源
		String source = "";
		if (feedItem.baseInfo.title.length() > 20) {
			source = feedItem.baseInfo.title.substring(0, 17) + "...";
		} else {
			source = feedItem.baseInfo.title;
		}

		// 获取时间
		String sourceTime = source
				+ " / "
				+ TimeUtils.LoopToTransTime(feedXMLContentInfo.pubDate,
						TimeUtils.STANDARD_TIME_PATTERN);

		mHeaderSouceTime.setText(sourceTime);

		// 设置连接
		mHeaderLink.setText(feedXMLContentInfo.link);

		String[] texts;// 存放各段文本
		final ArrayList<String> imgList = new ArrayList<String>();// 存放图片链接的集合

		// 获得正文内容
		if (TextUtils.isEmpty(feedXMLContentInfo.content)) {
			texts = HTTPUtils.html2Texts(feedXMLContentInfo.description,
					imgList);
		} else {
			texts = HTTPUtils.html2Texts(feedXMLContentInfo.content, imgList);
		}

		// 添加第一条文本
		TextView textFragment = getContentStyleTextView(texts[0]);
		mBodyContainer.addView(textFragment);

		int size = imgList.size();
		for (int i = 0; i < size; i++) {

			//如果有图片则显示
			if (!TextUtils.isEmpty(imgList.get(i))) {

				LayoutImageView imageView = getContentStyleImageView();
				mBodyContainer.addView(imageView);

				BitmapCacheUtils.removeCache(imgList.get(i));// 清除内存缓存，重新采样
				BitmapCacheUtils.displayBitmapOnLayoutChange(
						SummaryBodyActivity.this, imageView, imgList.get(i),
						null);
			}

			//显示下一段文本
			textFragment = getContentStyleTextView(texts[i + 1]);// 已经有一条文本
			mBodyContainer.addView(textFragment);

		}

	}

	/**
	 * 获得正文样式的LayoutImageView
	 * 
	 * @return LayoutImageView
	 */
	private LayoutImageView getContentStyleImageView() {

		final LayoutParams imageViewParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, DensityUtils.dp2px(this, 200));

		LayoutImageView layoutImageView = new LayoutImageView(this);
		layoutImageView.setLayoutParams(imageViewParams);
		layoutImageView.setScaleType(ScaleType.FIT_CENTER);

		return layoutImageView;
	}

	/**
	 * 获得正文样式的TextView
	 * 
	 * @param text
	 *            显示的文本
	 * @return TextView对象
	 */
	private TextView getContentStyleTextView(String text) {

		final LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		TextView tv = new TextView(this);
		tv.setLayoutParams(params);
		tv.setGravity(Gravity.LEFT);
		tv.setTextColor(getResources().getColor(R.color.drak_grey));
		tv.setSelected(true);
		tv.setTextSize(14);// dp值

		tv.setText(text);

		return tv;
	}
}
