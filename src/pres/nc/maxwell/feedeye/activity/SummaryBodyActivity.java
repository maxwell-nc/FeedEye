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
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog;
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog.DialogDataAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
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

		mBodyWrapper = (ScrollView) findViewById(R.id.sv_wrapper);
		mBodyContainer = (LinearLayout) mBodyWrapper
				.findViewById(R.id.ll_container);

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

				MainThemeLongClickDialog dialog = new MainThemeLongClickDialog(
						SummaryBodyActivity.this, new DialogDataAdapter() {

							@Override
							public int[] getTextViewResIds() {
								int[] ids = {R.id.tv_link, R.id.tv_reload,
										R.id.tv_view};
								return ids;
							}

							@Override
							public int getLayoutViewId() {
								return R.layout.view_long_click_content_image;
							}

							@Override
							public OnClickListener[] getItemOnClickListener(
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
						});

				dialog.show();
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
				R.layout.textview_summary_body_content_fragment, null);
		tv.setText(text);

		return tv;
	}

	/**
	 * 默认的图片点击监听器
	 */
	private class ImageClickListener implements OnClickListener {

		/**
		 * 消息对话框
		 */
		protected final AlertDialog alertDialog;

		/**
		 * 图片链接
		 */
		protected final String imgLink;

		/**
		 * 初始化
		 * 
		 * @param alertDialog
		 *            消息对话框
		 * @param imgLink
		 *            图片链接
		 */
		protected ImageClickListener(AlertDialog alertDialog, String imgLink) {
			this.alertDialog = alertDialog;
			this.imgLink = imgLink;
		}

		/**
		 * 默认取消对话框
		 */
		@Override
		public void onClick(View v) {
			alertDialog.dismiss();
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

			ClipboardManager clipManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipManager.setText(imgLink);

			Toast.makeText(SummaryBodyActivity.this, "复制成功", Toast.LENGTH_SHORT)
					.show();

			super.onClick(v);
		}
	}

}
