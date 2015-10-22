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
import pres.nc.maxwell.feedeye.view.PopupWindowItemView;
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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
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
	 * 顶部模式文本
	 */
	private TextView mModeText;

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
	 * Web内容加载中
	 */
	private ProgressBar mWebLoading;

	/**
	 * Web内容显示WebView
	 */
	private WebView mWebView;

	/**
	 * 显示加载失败，重新加载的布局
	 */
	private RelativeLayout mWebNothingReload;

	/**
	 * 是否正在使用WebView
	 */
	private boolean isUseWebView = false;

	/**
	 * 弹出的选项
	 */
	private PopupWindow mPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				changeViewMode(false);
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

		// 顶部模式文本
		mModeText = (TextView) findViewById(R.id.tv_mode_name);

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
		mWebLoading = (ProgressBar) mWebContainer.findViewById(R.id.pb_loading);
		mWebView = (WebView) mWebContainer.findViewById(R.id.wv_origin);
		mWebNothingReload = (RelativeLayout) findViewById(R.id.rl_nothing);

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

				if (isUseWebView) {
					setWebContentTextSize();
				} else {
					setContentTextSize();
				}

			}

			/**
			 * 设置网页原文内容字体
			 */
			@SuppressWarnings("deprecation")
			private void setWebContentTextSize() {

				MainThemeOnClickDialog dialog = new MainThemeOnClickDialog(
						mThisActivity, new DialogDataAdapter() {

							@Override
							public int[] getItemNames() {
								int[] strings = {R.string.smallest_text_zoom,
										R.string.smaller_text_zoom,
										R.string.normal_text_zoom,
										R.string.larger_text_zoom,
										R.string.largest_text_zoom};
								return strings;
							}

							@Override
							public OnClickListener[] getItemOnClickListeners(
									final AlertDialog alertDialog) {
								OnClickListener[] listeners = {
										new OnClickListener() {

											@Override
											public void onClick(View v) {// 50%
												mWebView.getSettings()
														.setTextSize(
																WebSettings.TextSize.SMALLEST);
												alertDialog.dismiss();
											}
										}, new OnClickListener() {// 75%

											@Override
											public void onClick(View v) {
												mWebView.getSettings()
														.setTextSize(
																WebSettings.TextSize.SMALLER);
												alertDialog.dismiss();
											}
										}, new OnClickListener() {// 100%

											@Override
											public void onClick(View v) {
												mWebView.getSettings()
														.setTextSize(
																WebSettings.TextSize.NORMAL);
												alertDialog.dismiss();
											}
										}, new OnClickListener() {// 150%

											@Override
											public void onClick(View v) {
												mWebView.getSettings()
														.setTextSize(
																WebSettings.TextSize.LARGER);
												alertDialog.dismiss();
											}
										}, new OnClickListener() {// 175%

											@Override
											public void onClick(View v) {
												mWebView.getSettings()
														.setTextSize(
																WebSettings.TextSize.LARGEST);
												alertDialog.dismiss();
											}
										}};
								return listeners;
							}

						});

				dialog.show();
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

		// 初始化popupWindow
		final PopupWindowUtils popupWindowUtils = new PopupWindowUtils(
				mThisActivity);
		mPopupWindow = popupWindowUtils
				.newPopupWindowInstance(R.layout.popup_window_summary_body_more_option);

		// 获取优化阅读按钮
		final PopupWindowItemView pwItemLink = (PopupWindowItemView) popupWindowUtils.popupView
				.findViewById(R.id.pwiv_link);
		final PopupWindowItemView pwItemFavor = (PopupWindowItemView) popupWindowUtils.popupView
				.findViewById(R.id.pwiv_favor);
		final PopupWindowItemView pwItemShare = (PopupWindowItemView) popupWindowUtils.popupView
				.findViewById(R.id.pwiv_share);
		final PopupWindowItemView pwItemsBrowser = (PopupWindowItemView) popupWindowUtils.popupView
				.findViewById(R.id.pwiv_browser);
		final PopupWindowItemView pwItemSimpleRead = (PopupWindowItemView) popupWindowUtils.popupView
				.findViewById(R.id.pwiv_simple_read);
		final PopupWindowItemView pwItemOrigin = (PopupWindowItemView) popupWindowUtils.popupView
				.findViewById(R.id.pwiv_origin);
		final PopupWindowItemView pwItemRefresh = (PopupWindowItemView) popupWindowUtils.popupView
				.findViewById(R.id.pwiv_refresh);

		// 设置点击监听器
		setPopupWindowOnClickListener(popupWindowUtils, pwItemLink,
				pwItemFavor, pwItemShare, pwItemsBrowser, pwItemSimpleRead,
				pwItemRefresh, pwItemOrigin);

		// 设置更多选项
		mBarMoreOption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (PopupWindowUtils.tryToClosePopupWindow(mPopupWindow)) {
					return;
				}

				if (isUseWebView) {
					// 显示优化阅读
					pwItemSimpleRead.setVisibility(View.VISIBLE);
					pwItemRefresh.setVisibility(View.VISIBLE);

					pwItemOrigin.setText("返回简阅模式");
				} else {
					pwItemSimpleRead.setVisibility(View.GONE);
					pwItemSimpleRead.setVisibility(View.GONE);

					pwItemOrigin.setText("查看原文");
				}

				// 显示
				popupWindowUtils.showNearView(mBarContainer, mBarMoreOption);

			}

		});

		// 设置WebView参数
		setWebViewParams();

	}
	/**
	 * 设置popupWindow的点击监听器
	 * 
	 * @param popupWindowUtils
	 *            创建popupWindow的工具类对象
	 * @param pwItemsimpleRead
	 *            优化阅读按钮对象
	 */
	private void setPopupWindowOnClickListener(
			final PopupWindowUtils popupWindowUtils,
			final PopupWindowItemView... items) {

		// 复制链接点击监听
		items[0].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PopupWindowUtils.tryToClosePopupWindow(mPopupWindow);

				SystemUtils.copyTextToClipBoard(mThisActivity, mHeaderLink
						.getText().toString());

			}

		});

		// 收藏点击监听
		items[1].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO:收藏

			}

		});

		// 分享点击监听
		items[2].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PopupWindowUtils.tryToClosePopupWindow(mPopupWindow);

				String text = mHeaderTitle.getText().toString() + ":\n"
						+ mHeaderLink.getText().toString();
				SystemUtils.startShareIntentActivity(mThisActivity, text);

			}

		});

		// 在浏览器中查看点击监听
		items[3].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PopupWindowUtils.tryToClosePopupWindow(mPopupWindow);

				// 打开浏览器
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(mHeaderLink.getText().toString());// 网址
				intent.setData(uri);
				startActivity(intent);

			}

		});

		// 优化阅读点击监听
		items[4].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PopupWindowUtils.tryToClosePopupWindow(mPopupWindow);

				// 获取webview设置
				WebSettings settings = mWebView.getSettings();

				if ("优化阅读".equals(items[4].getText().toString())) {

					// 自适应屏幕
					settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
					settings.setLoadWithOverviewMode(true);

					items[4].setText("取消优化");
					items[4].setIcon(R.drawable.btn_unread);

				} else {

					// 自适应屏幕
					settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
					settings.setLoadWithOverviewMode(false);

					items[4].setText("优化阅读");
					items[4].setIcon(R.drawable.btn_read);

				}

			}

		});

		// 重新加载点击监听
		items[5].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PopupWindowUtils.tryToClosePopupWindow(mPopupWindow);

				// 重新加载
				mWebView.reload();

			}

		});

		// 查看原文点击监听
		items[6].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PopupWindowUtils.tryToClosePopupWindow(mPopupWindow);

				if (isUseWebView) {
					changeViewMode(false);
					return;
				}

				// 加载原文
				loadOriginWebside();

			}

		});

	}

	/**
	 * 加载原文
	 */
	private void loadOriginWebside() {

		// 设置标记
		isUseWebView = true;

		// 显示网页
		changeViewMode(true);

		String loadLink = mHeaderLink.getText().toString();

		String orgLink = mWebView.getUrl();

		// 已经加载完毕的不重新加载
		if (orgLink == null || !loadLink.equals(orgLink)
				|| mWebView.getProgress() != 100) {
			mWebView.loadUrl(loadLink);
		}

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
		settings.setJavaScriptEnabled(true);

		// 设置概览模式
		settings.setLoadWithOverviewMode(true);

		// 使用缓存
		settings.setAppCacheEnabled(true);

		// DOM Storage
		settings.setDomStorageEnabled(true);

		// 设置用户代理
		settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android ;) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");

		// 设置支持多窗口
		settings.setSupportMultipleWindows(true);

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, final String failingUrl) {

				mWebView.stopLoading();
				mWebView.clearHistory();

				// 显示重新加载
				mWebNothingReload.setVisibility(View.VISIBLE);
				mWebView.setVisibility(View.GONE);

				// 设置重新加载
				mWebNothingReload.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						mWebView.stopLoading();
						mWebView.clearHistory();

						mWebNothingReload.setVisibility(View.GONE);
						mWebView.setVisibility(View.VISIBLE);
						mWebView.loadUrl(failingUrl);
					}

				});

			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 拦截跳转
				// mWebView.loadUrl(url);
				return false;
			}
		});

		// 监听进度
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				if (newProgress != 100) {

					mWebLoading.setVisibility(View.VISIBLE);
					mWebProgress.setVisibility(View.VISIBLE);

					if (newProgress < 20) {
						mWebProgress.setProgress(20);
					} else {
						mWebProgress.setProgress(newProgress);
					}

				} else {// 隐藏

					mWebLoading.setVisibility(View.GONE);
					mWebProgress.setVisibility(View.GONE);
					mWebProgress.setProgress(20);

				}

				super.onProgressChanged(view, newProgress);
			}

		});
	}
	/**
	 * 改变视图模式
	 * 
	 * @param isToWebViewMode
	 *            是否切换到网页原文模式
	 */
	private void changeViewMode(boolean isToWebViewMode) {

		if (!isToWebViewMode) {

			// 显示正文部分
			mBodyWrapper.setVisibility(View.VISIBLE);
			mWebContainer.setVisibility(View.GONE);
			isUseWebView = false;
			mModeText.setText("简阅模式");

		} else {

			mBodyWrapper.setVisibility(View.GONE);
			mWebContainer.setVisibility(View.VISIBLE);
			isUseWebView = true;
			mModeText.setText("网页原文");

		}

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
