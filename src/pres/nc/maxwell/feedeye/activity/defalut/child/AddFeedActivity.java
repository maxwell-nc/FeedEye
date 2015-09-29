package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.sql.Timestamp;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser.OnFinishedParseXMLListener;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 添加订阅页面的Activity
 */
public class AddFeedActivity extends DefaultNewActivity {

	/**
	 * 完成添加按钮
	 */
	private ImageView mFinishButtonView;

	/**
	 * 网址输入框
	 */
	private EditText mUrlText;

	/**
	 * 标题输入框
	 */
	private EditText mTitleText;

	/**
	 * 初始化View对象
	 */
	@Override
	protected void initView() {

		super.initView();

		setAsCloseImage();

		addView(R.layout.activity_add_feed_bar,
				R.layout.activity_add_feed_container);

		mFinishButtonView = (ImageView) mBarView.findViewById(R.id.iv_finish);

		mUrlText = (EditText) mContainerView.findViewById(R.id.et_url);
		mTitleText = (EditText) mContainerView.findViewById(R.id.et_title);

	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initData() {
		super.initData();

		/**
		 * 点击完成按钮
		 */
		mFinishButtonView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean finishAdd = finishAdd();
				LogUtils.e("AddFeedActivity", finishAdd + "");

			}

		});
	}

	/**
	 * 完成添加
	 * 
	 * @return 返回是否成功添加
	 */
	private boolean finishAdd() {

		String urlString = mUrlText.getText().toString();
		if (TextUtils.isEmpty(urlString)) {
			return false;
		}

		final String titleString = mTitleText.getText().toString();
		

		if (!(urlString.startsWith("http://") || urlString
				.startsWith("https://"))) {
			return false;// 无效
		}

		// 添加信息
		final FeedItemBean feedItemBean = new FeedItemBean();
		final FeedItemDAO feedItemDAO = new FeedItemDAO(this);

		// 设置URL
		feedItemBean.setFeedURL(urlString);

		final FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new OnFinishedParseXMLListener() {

					@Override
					public void onFinishedParseXML() {

						// 设置标题
						if (TextUtils.isEmpty(titleString)) {// 设置为空

							if (TextUtils.isEmpty(feedXMLParser.mFeedTitle)) {// 网络结果为空
								feedItemBean.setTitle("无标题");
							} else {// 用户不写，有网络数据
								feedItemBean.setTitle(feedXMLParser.mFeedTitle);
							}

						} else {// 用户自定义
							feedItemBean.setTitle(titleString);
						}

						// 设置预览内容
						if (!TextUtils.isEmpty(feedXMLParser.mFeedSummary)) {
							feedItemBean
									.setPreviewContent(feedXMLParser.mFeedSummary);
						} else {
							feedItemBean.setPreviewContent("没有接收到数据");
						}

						// 设置预览内容
						if (!TextUtils.isEmpty(feedXMLParser.mFeedTime)) {
							if ("RSS".equals(feedXMLParser.mFeedType)) {
								feedItemBean.setLastTime(TimeUtils
										.gmt2Timestamp(feedXMLParser.mFeedTime));
							}
							
							
						} else {
							feedItemBean.setLastTime(new Timestamp(System
									.currentTimeMillis()));
						}

						// TODO：图片待获取
						feedItemBean.setPicURL("null");

						feedItemDAO.addItem(feedItemBean);

						//返回数据给MainActivity
						Intent returnData = new Intent();
						returnData.putExtra("feedItemBean", feedItemBean);
						setResult(0, returnData);
						
						//关闭Activity
						finish();
					}

				});

		//解析数据
		feedXMLParser.parseUrl(urlString);

		

		return true;
	}
}
