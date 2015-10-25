package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加订阅页面的Activity
 */
public class AddFeedActivity extends DefaultNewActivity {

	/**
	 * Activity对象
	 */
	private final AddFeedActivity mThisActivity = this;

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
	 * 要添加的订阅的地址
	 */
	private String mUrlString;

	/**
	 * 自定义的图标
	 */
	private LayoutImageView mCustomImage;

	/**
	 * 自定义的图片路径
	 */
	private TextView mCustomImagePath;

	/**
	 * XML解析器
	 */
	private FeedXMLParser mFeedXMLParser;

	/**
	 * 初始化View对象
	 */
	@Override
	protected void initView() {

		super.initView();

		setAsCloseImage();

		addView(R.layout.activity_add_feed_bar,
				R.layout.activity_add_feed_container);

		mFinishButtonView = (ImageView) mCustomBarView
				.findViewById(R.id.iv_finish);

		mUrlText = (EditText) mCustomContainerView.findViewById(R.id.et_url);
		mTitleText = (EditText) mCustomContainerView
				.findViewById(R.id.et_title);

		mCustomImage = (LayoutImageView) mCustomContainerView
				.findViewById(R.id.iv_pic);
		mCustomImagePath = (TextView) mCustomContainerView
				.findViewById(R.id.tv_pic_tips);

		mEncodingGroup = (RadioGroup) mCustomContainerView
				.findViewById(R.id.rg_encoding);
		mLoadingFrame = (FrameLayout) mCustomContainerView
				.findViewById(R.id.fl_loading);

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

				addItem();

			}

		});

		/**
		 * 判断编码类型
		 */
		mEncodingGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						switch (group.getCheckedRadioButtonId()) {
							case R.id.rb_utf8 :
								mEncodingString = "utf-8";
								break;
							case R.id.rb_gb2312 :
								mEncodingString = "gbk";
								break;
							case R.id.rb_iso8859_1 :
								mEncodingString = "iso8859-1";
								break;
						}

					}
				});

		// 自定义图标
		mCustomImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				// 请求码为1
				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {// 获取自定义的图标

			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();// 获取返回的URI
				String customImagePath = IOUtils.getAbsolutePathFromURI(
						mThisActivity, uri);
				mCustomImagePath.setText(customImagePath);
				// 显示出来
				BitmapCacheUtils.displayBitmapOnLayoutChange(mThisActivity, mCustomImage,
						customImagePath, null);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected boolean beforeClose() {

		if (mFeedXMLParser != null) {
			mFeedXMLParser.cancelParse();
		}
		setResult(-1, null);// -1表示没有返回数据
		return super.beforeClose();
	}

	/**
	 * 添加订阅信息
	 */
	public void addItem() {

		mUrlString = mUrlText.getText().toString();

		if (TextUtils.isEmpty(mUrlString)) {// 提示不能为空

			mUrlText.startAnimation(AnimationUtils.loadAnimation(mThisActivity,
					R.anim.edit_text_translate));

			return;
		}

		// 显示处理中
		mLoadingFrame.setVisibility(View.VISIBLE);
		// 禁止在提交
		mFinishButtonView.setVisibility(View.INVISIBLE);

		final String titleString = mTitleText.getText().toString();

		// 自动补全http头
		if (!(mUrlString.startsWith("http://") || mUrlString
				.startsWith("https://"))) {

			mUrlString = "http://" + mUrlString;

		}

		// 添加信息
		final FeedItem feedItem = new FeedItem();
		final FeedItemDAO feedItemDAO = new FeedItemDAO(this);

		// 设置URL
		feedItem.feedURL = mUrlString;

		mFeedXMLParser = new FeedXMLParser();

		mFeedXMLParser
				.setOnFinishedParseXMLListener(new FeedXMLParser.SimpleOnFinishParseXMLListener() {

					@Override
					public void onFinishParseBaseInfo(boolean result,
							FeedXMLBaseInfo baseInfo) {

						if (result) {// 成功读取

							// 判断是否类型合法
							if (baseInfo.type == FeedXMLBaseInfo.TYPE_UNKNOWN) {

								failedToGet("不支持的订阅类型");
								return;

							}

							// 设置基本信息
							feedItem.baseInfo = baseInfo;

							// 自定义设置标题
							if (!TextUtils.isEmpty(titleString)) {// 用户自定义

								feedItem.baseInfo.title = titleString;

							}

							// 设置图片
							String customImagePath = mCustomImagePath.getText()
									.toString();

							if (customImagePath.startsWith("/")) {// 使用本地图片

								feedItem.picURL = customImagePath;

							} else {// 没有设置

								// favicon获取

								Pattern p = Pattern
										.compile("(?<=//|)((\\w)+\\.)+\\w+");// 匹配顶级域名

								Matcher m = p.matcher(mUrlString);

								if (m.find()) {

									// 补全http头
									String host = m.group();
									if (host.startsWith("http://")
											|| host.startsWith("https://")) {
										feedItem.picURL = host + "/favicon.ico";
									} else {

										if (mUrlString.startsWith("https://")) {

											feedItem.picURL = "https://" + host
													+ "/favicon.ico";

										} else {
											feedItem.picURL = "http://" + host
													+ "/favicon.ico";
										}

									}

								} else {
									feedItem.picURL = "null";
								}

							}

							// 设置订阅URL
							feedItem.feedURL = mUrlString;

							// 设置编码方式
							feedItem.encoding = mEncodingString;

							feedItemDAO.addItem(feedItem);

							// 返回数据给MainActivity
							Intent returnData = new Intent();
							returnData.putExtra("FeedItem", feedItem);

							setResult(1, returnData);

							Toast.makeText(mThisActivity, "添加成功",
									Toast.LENGTH_LONG).show();

							// 关闭Activity
							finish();

						} else {// 读取失败

							failedToGet("获取失败，请检查地址和网络");
						}

					}

					/**
					 * 获取失败
					 * 
					 * @param errMsg
					 *            提示错误信息
					 */
					private void failedToGet(String errMsg) {
						// 恢复
						mLoadingFrame.setVisibility(View.GONE);
						mFinishButtonView.setVisibility(View.VISIBLE);

						Toast.makeText(mThisActivity, errMsg,
								Toast.LENGTH_SHORT).show();
					}

				});

		// 解析数据
		mFeedXMLParser.parse(mUrlString, mEncodingString,
				FeedXMLParser.TYPE_PARSE_BASE_INFO);

	}
}
