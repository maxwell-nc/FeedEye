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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * 添加订阅页面的Activity
 */
public class AddFeedActivity extends DefaultNewActivity {

	/**
	 * Activity对象
	 */
	private final AddFeedActivity  mThisActivity = this;
	
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
	 * 编码单选组
	 */
	private RadioGroup mEncodingGroup;

	/**
	 * 编码格式，默认UTF-8
	 */
	private String mEncodingString = "utf-8";
	
	/**
	 * 加载中帧布局
	 */
	private FrameLayout mLoadingFrame;
	
	/**
	 * 初始化View对象
	 */
	@Override
	protected void initView() {

		super.initView();

		setAsCloseImage();

		addView(R.layout.activity_add_feed_bar,
				R.layout.activity_add_feed_container);

		mFinishButtonView = (ImageView) mCustomBarView.findViewById(R.id.iv_finish);

		mUrlText = (EditText) mCustomContainerView.findViewById(R.id.et_url);
		mTitleText = (EditText) mCustomContainerView.findViewById(R.id.et_title);

		mEncodingGroup = (RadioGroup) mCustomContainerView.findViewById(R.id.rg_encoding);
		mLoadingFrame = (FrameLayout) mCustomContainerView.findViewById(R.id.fl_loading);
		
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

				boolean finishAdd = addItem();
				LogUtils.e("AddFeedActivity", finishAdd + "");

			}

		});
		
		/**
		 * 判断编码类型
		 */
		mEncodingGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (group.getCheckedRadioButtonId()) {
				case R.id.rb_utf8:
					mEncodingString = "utf-8";
					break;
				case R.id.rb_gb2312:
					mEncodingString = "gbk";
					break;
				case R.id.rb_iso8859_1:
					mEncodingString = "iso8859-1";
					break;
				}
				
			}
		});
		
	}

	
	@Override
	protected boolean beforeClose() {

		setResult(-1, null);//-1表示没有返回数据
		return super.beforeClose();
	}
	
	/**
	 * 完成添加
	 * 
	 * @return 返回是否成功添加
	 */
	public boolean addItem() {

		//显示处理中
		mLoadingFrame.setVisibility(View.VISIBLE);
		//禁止在提交
		mFinishButtonView.setVisibility(View.INVISIBLE);
		
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
					public void onFinishedParseXMLBaseInfo(boolean result) {

						if (result) {//成功读取
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

							// 设置时间
							if (!TextUtils.isEmpty(feedXMLParser.mFeedTime)) {
								
								
								if ("RSS".equals(feedXMLParser.mFeedType)) {
									feedItemBean.setLastTime(TimeUtils
											.varString2Timestamp(feedXMLParser.mFeedTime));
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
							
							//返回数量
							if(feedXMLParser.mFeedType == "RSS"){
								returnData.putExtra("count", feedXMLParser.mItemCount);
							}else if (feedXMLParser.mFeedType == "ATOM") {
								returnData.putExtra("count", feedXMLParser.mEntryCount);
							}
							
							setResult(0, returnData);
							
							Toast.makeText(mThisActivity, "添加成功", Toast.LENGTH_LONG).show();

							//关闭Activity
							finish();
							
						}else {//读取失败

							//恢复
							mLoadingFrame.setVisibility(View.GONE);
							mFinishButtonView.setVisibility(View.VISIBLE);
							
							Toast.makeText(mThisActivity, "获取失败，请检查地址和网络", Toast.LENGTH_LONG).show();
						}
					}

				});

		//解析数据
		feedXMLParser.parseUrl(urlString,mEncodingString);

		

		return true;
	}
}
