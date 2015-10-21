package pres.nc.maxwell.feedeye.activity;

import java.util.ArrayList;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.utils.DensityUtils;
import pres.nc.maxwell.feedeye.utils.HTTPUtils;
import pres.nc.maxwell.feedeye.utils.SystemUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import pres.nc.maxwell.feedeye.view.MainThemeOnClickDialog;
import pres.nc.maxwell.feedeye.view.MainThemeOnClickDialog.DialogDataAdapter;
import pres.nc.maxwell.feedeye.view.MainThemeOnClickDialog.ExtraCustomViewAdapter;
import pres.nc.maxwell.feedeye.view.MainThemeOnClickDialog.ImageClickListener;
import pres.nc.maxwell.feedeye.view.PopupWindowUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 正文内容的Activity
 */
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

	/**
	 * 滑动包装布局
	 */
	private ScrollView mBodyWrapper;

	/**
	 * 此Activity，方便匿名内部类调用
	 */
	private Activity mThisActivity;

	/**
	 * 标题容器
	 */
	private RelativeLayout mBarContainer;

	/**
	 * 调整字体大小按钮
	 */
	private ImageView mBarTextSize;

	/**
	 * 全屏按钮
	 */
	private ImageView mBarFullScreen;

	/**
	 * 更多选项按钮
	 */
	private ImageView mBarMoreOption;

	/**
	 * 正文TextView集合
	 */
	private ArrayList<TextView> mContentTextViews;

	/**
	 * Web内容容器
	 */
	private LinearLayout mWebContainer;

	/**
	 * Web内容进度条
	 */
	private ProgressBar mWebProgress;

	/**
	 * Web内容显示WebView
	 */
	private WebView mWebView;

	/**
	 * 是否正在使用WebView
	 */
	private boolean isUseWebView = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_summary_body);
		super.onCreate(savedInstanceState);

		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		// 中断操作
		BitmapCacheUtils.shutdownDefalutThreadPool();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

		// 检查是否全屏，是则退出全屏不退出Activity
		if (mBarContainer != null) {
			if (mBarContainer.getVisibility() == View.GONE) {
				mBarContainer.setVisibility(View.VISIBLE);
				return;
			}
		}

		// 网页后退或者不显示网页
		if (isUseWebView) {

			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				// 显示正文部分
				mBodyWrapper.setVisibility(View.VISIBLE);
				mWebContainer.setVisibility(View.GONE);
			}
			return;
		}

		super.onBackPressed();
	}

	/**
	 * 初始化View
	 */
	private void initView() {

		mThisActivity = this;

		// 标题栏部分
		mBarContainer = (RelativeLayout) findViewById(R.id.rl_bar);
		mBarTextSize = (ImageView) mBarContainer
				.findViewById(R.id.iv_text_size);
		mBarFullScreen = (ImageView) mBarContainer
				.findViewById(R.id.iv_fullscreen);
		mBarMoreOption = (ImageView) mBarContainer.findViewById(R.id.iv_more);

		// 正文部分
		mBodyWrapper = (ScrollView) findViewById(R.id.sv_wrapper);
		mBodyContainer = (LinearLayout) mBodyWrapper
				.findViewById(R.id.ll_container);

		mHeaderTitle = (TextView) mBodyContainer.findViewById(R.id.tv_title);
		mHeaderSouceTime = (TextView) mBodyContainer
				.findViewById(R.id.tv_source_time);
		mHeaderLink = (TextView) mBodyContainer.findViewById(R.id.tv_link);

		// web内容部分
		mWebContainer = (LinearLayout) findViewById(R.id.ll_web_container);
		mWebProgress = (ProgressBar) mWebContainer
				.findViewById(R.id.pb_progress);
		mWebView = (WebView) mWebContainer.findViewById(R.id.wv_origin);

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
		if (feedItem.baseInfo.title.length() > 15) {
			source = feedItem.baseInfo.title.substring(0, 12) + "...";
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

		// 设置点击监听器
		HeaderOnClickListener listener = new HeaderOnClickListener();
		mHeaderTitle.setOnClickListener(listener);
		mHeaderSouceTime.setOnClickListener(listener);
		mHeaderLink.setOnClickListener(listener);
		mBodyContainer.setOnClickListener(listener);

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

			final String imgLink = imgList.get(i);

			// 如果有图片则显示
			if (!TextUtils.isEmpty(imgLink)) {

				LayoutImageView imageView = getContentStyleImageView(imgLink);
				mBodyContainer.addView(imageView);

				BitmapCacheUtils.removeCacheFromMem(imgLink);// 清除内存缓存，重新采样
				BitmapCacheUtils.displayBitmapOnLayoutChange(
						SummaryBodyActivity.this, imageView, imgLink, null);
			}

			// 显示下一段文本
			textFragment = getContentStyleTextView(texts[i + 1]);// 已经有一条文本
			mBodyContainer.addView(textFragment);

		}

		// 设置全屏点击监听
		mBarFullScreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBarContainer.setVisibility(View.GONE);
			}

		});

		// 设置字体改变点击监听
		mBarTextSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO：

				if (isUseWebView) {

				} else {
					setContentTextSize();
				}

			}

			/**
			 * 设置正文内容字体
			 */
			private void setContentTextSize() {
				MainThemeOnClickDialog dialog = new MainThemeOnClickDialog(
						mThisActivity, new DialogDataAdapter() {

							@Override
							public int[] getItemNames() {
								int[] strings = {R.string.little_text_size,
										R.string.middle_text_size,
										R.string.large_text_size};
								return strings;
							}

							@Override
							public OnClickListener[] getItemOnClickListeners(
									final AlertDialog alertDialog) {
								OnClickListener[] listeners = {
										new OnClickListener() {

											@Override
											public void onClick(View v) {// 小号字体14sp
												setTextSize(14);
												alertDialog.dismiss();
											}
										}, new OnClickListener() {// 中号字体24sp

											@Override
											public void onClick(View v) {
												setTextSize(24);
												alertDialog.dismiss();
											}
										}, new OnClickListener() {// 大号字体32sp

											@Override
											public void onClick(View v) {
												setTextSize(32);
												alertDialog.dismiss();
											}
										}};
								return listeners;
							}

						});

				// 添加手动修改大小的View
				dialog.setExtraCustomViewAdapter(new ExtraCustomViewAdapter() {

					/**
					 * 显示TextView字体大小
					 * 
					 * @param sizeText
					 *            显示字体大小的TextView
					 * @param increase
					 *            在原来的大小上的增量
					 * @return 显示的sp大小
					 */
					public int showTextViewTextSize(TextView sizeText,
							int increase) {

						float pxSize = mContentTextViews.get(0).getTextSize();
						// 转换为sp
						int spSize = DensityUtils.px2sp(mThisActivity, pxSize)
								+ increase;
						sizeText.setText(spSize + "sp");

						return spSize;
					}

					@Override
					public View getExtraCustomFooterView() {

						// 添加手动设置的字体View
						LinearLayout view = (LinearLayout) View.inflate(
								mThisActivity,
								R.layout.view_extra_custom_footer_text_size,
								null);

						ImageView addBtn = (ImageView) view
								.findViewById(R.id.iv_add);
						ImageView descBtn = (ImageView) view
								.findViewById(R.id.iv_desc);
						final TextView sizeText = (TextView) view
								.findViewById(R.id.tv_size);

						// 显示当前的字体大小
						showTextViewTextSize(sizeText, 0);

						addBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								setTextSize(showTextViewTextSize(sizeText, 1));
							}

						});

						descBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								setTextSize(showTextViewTextSize(sizeText, -1));
							}

						});

						return view;

					}
				});

				dialog.show();
			}

		});

		// 设置更多选项
		mBarMoreOption.setOnClickListener(new OnClickListener() {

			private PopupWindow mPopupWindow;

			@Override
			public void onClick(View v) {

				if (PopupWindowUtils.tryToClosePopupWindow(mPopupWindow)) {
					return;
				}

				PopupWindowUtils popupWindowUtils = new PopupWindowUtils(
						mThisActivity);
				mPopupWindow = popupWindowUtils
						.newPopupWindowInstance(R.layout.popup_window_summary_body_more_option);

				// 显示
				popupWindowUtils.showNearView(mBarContainer, mBarMoreOption);

				// 复制链接点击监听
				popupWindowUtils.popupView.findViewById(R.id.pwiv_link)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								PopupWindowUtils
										.tryToClosePopupWindow(mPopupWindow);

								SystemUtils.copyTextToClipBoard(mThisActivity,
										mHeaderLink.getText().toString());

							}

						});

				// 收藏点击监听
				popupWindowUtils.popupView.findViewById(R.id.pwiv_favor)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								// TODO:收藏

							}

						});

				// 分享点击监听
				popupWindowUtils.popupView.findViewById(R.id.pwiv_share)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								PopupWindowUtils
										.tryToClosePopupWindow(mPopupWindow);

								String text = mHeaderTitle.getText().toString()
										+ ":\n"
										+ mHeaderLink.getText().toString();
								SystemUtils.startShareIntentActivity(
										mThisActivity, text);

							}

						});

				// 在浏览器中查看点击监听
				popupWindowUtils.popupView.findViewById(R.id.pwiv_browser)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								PopupWindowUtils
										.tryToClosePopupWindow(mPopupWindow);

								// 打开浏览器
								Intent intent = new Intent(Intent.ACTION_VIEW);
								Uri uri = Uri.parse(mHeaderLink.getText()
										.toString());// 网址
								intent.setData(uri);
								startActivity(intent);

							}

						});

				// 查看原文点击监听
				popupWindowUtils.popupView.findViewById(R.id.pwiv_origin)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								// TODO:

								PopupWindowUtils
										.tryToClosePopupWindow(mPopupWindow);

								// 设置标记
								isUseWebView = true;

								String loadLink = mHeaderLink.getText()
										.toString();

								String orgLink = mWebView.getUrl();

								// 隐藏正文部分
								mBodyWrapper.setVisibility(View.GONE);
								mWebContainer.setVisibility(View.VISIBLE);
								
								if (orgLink == null
										|| !loadLink.equals(orgLink)) {
									mWebView.loadUrl(loadLink);
								}

								

							}

						});

			}

		});

		// 设置WebView参数
		setWebViewParams();

	}

	/**
	 * 设置正文字体大小
	 * 
	 * @param spValue
	 *            字体sp值
	 */
	private void setTextSize(float spValue) {

		for (TextView tv : mContentTextViews) {
			tv.setTextSize(spValue);
		}

	}

	/**
	 * 获得正文样式的LayoutImageView
	 * 
	 * @param imgLink
	 *            图片显示的网址
	 * @return LayoutImageView
	 */
	private LayoutImageView getContentStyleImageView(final String imgLink) {

		final LayoutParams imageViewParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, DensityUtils.dp2px(this, 200));

		final LayoutImageView layoutImageView = new LayoutImageView(this);
		layoutImageView.setLayoutParams(imageViewParams);
		layoutImageView.setScaleType(ScaleType.FIT_CENTER);

		// 点击查看图片
		layoutImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new MainThemeOnClickDialog(SummaryBodyActivity.this,
						new DialogDataAdapter() {

							@Override
							public int[] getItemNames() {
								int[] strings = {R.string.copy_img_link,
										R.string.reload, R.string.view_big_img};
								return strings;
							}

							@Override
							public OnClickListener[] getItemOnClickListeners(
									final AlertDialog alertDialog) {

								OnClickListener[] listeners = {

										new CopyImgLinkOnClickListener(// 复制图像链接
												alertDialog, imgLink),
										new ReloadImageOnClickListener(
												// 重新加载
												alertDialog, imgLink,
												layoutImageView),
										new ViewImageOnClickListener(// 查看大图
												alertDialog, imgLink)};

								return listeners;

							}
						}).show();

			}
		});

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

		// textIsSelectable在低版本中只能由布局设置
		TextView tv = (TextView) View.inflate(this,
				R.layout.view_summary_body_content, null);
		tv.setText(text);

		if (mContentTextViews == null) {
			mContentTextViews = new ArrayList<TextView>();
		}

		// 记录下来
		mContentTextViews.add(tv);

		return tv;
	}

	/**
	 * 设置WebView的参数
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebViewParams() {
		
		// 获取webview设置
		WebSettings settings = mWebView.getSettings();

		// 双击放大缩小
		settings.setUseWideViewPort(true);

		// 放大缩小按钮
		settings.setBuiltInZoomControls(true);

		// 开启javascript
		// settings.setJavaScriptEnabled(true);

		// 拦截跳转
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				mWebView.loadUrl(url);
				return true;
			}
		});

		// 监听进度
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				if (newProgress != 100) {
					mWebProgress.setVisibility(View.VISIBLE);
					mWebProgress.setProgress(newProgress);
				} else {// 隐藏
					mWebProgress.setVisibility(View.GONE);
					mWebProgress.setProgress(0);
				}

				super.onProgressChanged(view, newProgress);
			}

		});
	}

	/**
	 * 正文头部的点击监听器
	 */
	private class HeaderOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			new MainThemeOnClickDialog(SummaryBodyActivity.this,
					new DialogDataAdapter() {

						@Override
						public int[] getItemNames() {
							int[] strings = {R.string.copy_title,
									R.string.copy_link};
							return strings;
						}

						@Override
						public OnClickListener[] getItemOnClickListeners(
								final AlertDialog alertDialog) {

							OnClickListener[] listeners = {

							new OnClickListener() {// 复制标题

										@Override
										public void onClick(View v) {
											SystemUtils.copyTextToClipBoard(
													mThisActivity, mHeaderTitle
															.getText()
															.toString());
											alertDialog.dismiss();
										}

									}, new OnClickListener() {// 复制链接

										@Override
										public void onClick(View v) {
											SystemUtils.copyTextToClipBoard(
													mThisActivity, mHeaderLink
															.getText()
															.toString());
											alertDialog.dismiss();
										}

									}};

							return listeners;
						}

					}).show();

		}
	}

	/**
	 * 查看大图的点击监听器
	 */
	private class ViewImageOnClickListener extends ImageClickListener {

		protected ViewImageOnClickListener(AlertDialog alertDialog,
				String imgLink) {
			super(alertDialog, imgLink);
		}

		@Override
		public void onClick(View v) {

			String localPathString = BitmapCacheUtils
					.url2LocalCachePath(imgLink);

			// 调用其他程序打开图片
			if (localPathString != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + localPathString),
						"image/*");
				startActivity(intent);
			}

			super.onClick(v);

		}

	}

	/**
	 * 重新加载图片的点击监听器
	 */
	private class ReloadImageOnClickListener extends ImageClickListener {

		/**
		 * 显示图片的空间
		 */
		protected final LayoutImageView layoutImageView;

		protected ReloadImageOnClickListener(AlertDialog alertDialog,
				String imgLink, LayoutImageView layoutImageView) {
			super(alertDialog, imgLink);
			this.layoutImageView = layoutImageView;
		}

		@Override
		public void onClick(View v) {
			BitmapCacheUtils.removeCacheFromLocal(imgLink);// 清除本地缓存
			BitmapCacheUtils.removeCacheFromMem(imgLink);// 清除内存缓存
			BitmapCacheUtils.displayBitmap(SummaryBodyActivity.this,
					layoutImageView, imgLink, null);

			super.onClick(v);
		}
	}

	/**
	 * 复制图像的点击监听器
	 */
	private class CopyImgLinkOnClickListener extends ImageClickListener {

		protected CopyImgLinkOnClickListener(AlertDialog alertDialog,
				String imgLink) {
			super(alertDialog, imgLink);
		}

		@Override
		public void onClick(View v) {// 复制图片链接

			SystemUtils.copyTextToClipBoard(mThisActivity, imgLink);

			super.onClick(v);
		}
	}

}
