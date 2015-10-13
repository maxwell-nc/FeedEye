package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.domain.FeedXMLBaseInfo;
import pres.nc.maxwell.feedeye.domain.FeedXMLContentInfo;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser.OnFinishParseXMLListener;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
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
 * ��Ӷ���ҳ���Activity
 */
public class AddFeedActivity extends DefaultNewActivity {

	/**
	 * Activity����
	 */
	private final AddFeedActivity mThisActivity = this;

	/**
	 * �����Ӱ�ť
	 */
	private ImageView mFinishButtonView;

	/**
	 * ��ַ�����
	 */
	private EditText mUrlText;

	/**
	 * ���������
	 */
	private EditText mTitleText;

	/**
	 * ���뵥ѡ��
	 */
	private RadioGroup mEncodingGroup;

	/**
	 * �����ʽ��Ĭ��UTF-8
	 */
	private String mEncodingString = "utf-8";

	/**
	 * ������֡����
	 */
	private FrameLayout mLoadingFrame;

	/**
	 * Ҫ��ӵĶ��ĵĵ�ַ
	 */
	private String mUrlString;

	/**
	 * �Զ����ͼ��
	 */
	private ImageView mCustomImage;

	/**
	 * �Զ����ͼƬ·��
	 */
	private TextView mCustomImagePath;

	/**
	 * XML������
	 */
	private FeedXMLParser mFeedXMLParser;

	/**
	 * ��ʼ��View����
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

		mCustomImage = (ImageView) mCustomContainerView
				.findViewById(R.id.iv_pic);
		mCustomImagePath = (TextView) mCustomContainerView
				.findViewById(R.id.tv_pic_tips);

		mEncodingGroup = (RadioGroup) mCustomContainerView
				.findViewById(R.id.rg_encoding);
		mLoadingFrame = (FrameLayout) mCustomContainerView
				.findViewById(R.id.fl_loading);

	}

	/**
	 * ��ʼ������
	 */
	@Override
	protected void initData() {
		super.initData();

		/**
		 * �����ɰ�ť
		 */
		mFinishButtonView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				addItem();

			}

		});

		/**
		 * �жϱ�������
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

		// �Զ���ͼ��
		mCustomImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				// ������Ϊ1
				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {// ��ȡ�Զ����ͼ��

			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();// ��ȡ���ص�URI
				String customImagePath = IOUtils.getAbsolutePathFromURI(
						mThisActivity, uri);
				mCustomImagePath.setText(customImagePath);
				// ��ʾ����
				BitmapCacheUtils.displayBitmap(mThisActivity, mCustomImage,
						customImagePath);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected boolean beforeClose() {

		if (mFeedXMLParser != null) {
			mFeedXMLParser.cancelParse();
		}
		setResult(-1, null);// -1��ʾû�з�������
		return super.beforeClose();
	}

	/**
	 * ��Ӷ�����Ϣ
	 */
	public void addItem() {

		mUrlString = mUrlText.getText().toString();

		if (TextUtils.isEmpty(mUrlString)) {// ��ʾ����Ϊ��

			mUrlText.startAnimation(AnimationUtils.loadAnimation(mThisActivity,
					R.anim.edit_text_translate));

			return;
		}

		// ��ʾ������
		mLoadingFrame.setVisibility(View.VISIBLE);
		// ��ֹ���ύ
		mFinishButtonView.setVisibility(View.INVISIBLE);

		final String titleString = mTitleText.getText().toString();

		// �Զ���ȫhttpͷ
		if (!(mUrlString.startsWith("http://") || mUrlString
				.startsWith("https://"))) {

			mUrlString = "http://" + mUrlString;

		}

		// �����Ϣ
		final FeedItem feedItem = new FeedItem();
		final FeedItemDAO feedItemDAO = new FeedItemDAO(this);

		// ����URL
		feedItem.feedURL = mUrlString;

		mFeedXMLParser = new FeedXMLParser();

		mFeedXMLParser
				.setOnFinishedParseXMLListener(new OnFinishParseXMLListener() {

					@Override
					public void onFinishParseBaseInfo(boolean result,
							FeedXMLBaseInfo baseInfo) {


						if (result) {// �ɹ���ȡ

							// ���û�����Ϣ
							feedItem.baseInfo = baseInfo;

							// �Զ������ñ���
							if (!TextUtils.isEmpty(titleString)) {// �û��Զ���

								feedItem.baseInfo.title = titleString;

							} else {// �Զ���ȡ

								if (TextUtils.isEmpty(feedItem.baseInfo.title)) {// �û���д������������

									feedItem.baseInfo.title = "�ޱ���";
								}

							}

							// ��Ԥ������
							if (TextUtils.isEmpty(feedItem.baseInfo.summary)) {
								feedItem.baseInfo.summary = "û�н��յ�����";
							}

							// �޻�ȡ��ʱ�䣬����Ϊ��ǰʱ��
							if (feedItem.baseInfo.time == null) {
								feedItem.baseInfo.time = new Timestamp(System
										.currentTimeMillis());
							}

							// ����ͼƬ
							String customImagePath = mCustomImagePath.getText()
									.toString();

							if (customImagePath.startsWith("/")) {// ʹ�ñ���ͼƬ

								feedItem.picURL = customImagePath;

							} else {// û������

								// favicon��ȡ

								Pattern p = Pattern
										.compile("(?<=//|)((\\w)+\\.)+\\w+");// ƥ�䶥������

								Matcher m = p.matcher(mUrlString);

								if (m.find()) {

									// ��ȫhttpͷ
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

							// ���ö���URL
							feedItem.feedURL = mUrlString;

							// ���ñ��뷽ʽ
							feedItem.encoding = mEncodingString;

							feedItemDAO.addItem(feedItem);

							// �������ݸ�MainActivity
							Intent returnData = new Intent();
							returnData.putExtra("FeedItem", feedItem);

							setResult(0, returnData);

							Toast.makeText(mThisActivity, "��ӳɹ�",
									Toast.LENGTH_LONG).show();

							// �ر�Activity
							finish();

						} else {// ��ȡʧ��

							// �ָ�
							mLoadingFrame.setVisibility(View.GONE);
							mFinishButtonView.setVisibility(View.VISIBLE);

							Toast.makeText(mThisActivity, "��ȡʧ�ܣ������ַ������",
									Toast.LENGTH_LONG).show();
						}
						
					}

					@Override
					public void onFinishParseContent(boolean result,
							ArrayList<FeedXMLContentInfo> contentInfos) {
						//����Ҫ
					}

					

				});

		// ��������
		mFeedXMLParser.parse(mUrlString, mEncodingString,
				FeedXMLParser.TYPE_PARSE_BASE_INFO);

	}
}
