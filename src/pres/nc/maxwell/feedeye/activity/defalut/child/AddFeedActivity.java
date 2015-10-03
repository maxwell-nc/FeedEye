package pres.nc.maxwell.feedeye.activity.defalut.child;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.DefaultNewActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser;
import pres.nc.maxwell.feedeye.engine.FeedXMLParser.OnFinishedParseXMLListener;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import android.content.Intent;
import android.os.Environment;
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

				boolean finishAdd = addItem();
				LogUtils.e("AddFeedActivity", finishAdd + "");

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

	}

	@Override
	protected boolean beforeClose() {

		setResult(-1, null);// -1��ʾû�з�������
		return super.beforeClose();
	}

	/**
	 * ������
	 * 
	 * @return �����Ƿ�ɹ����
	 */
	public boolean addItem() {

		// ��ʾ������
		mLoadingFrame.setVisibility(View.VISIBLE);
		// ��ֹ���ύ
		mFinishButtonView.setVisibility(View.INVISIBLE);

		mUrlString = mUrlText.getText().toString();
		if (TextUtils.isEmpty(mUrlString)) {
			return false;
		}

		final String titleString = mTitleText.getText().toString();

		//�Զ���ȫhttpͷ
		if (!(mUrlString.startsWith("http://") || mUrlString
				.startsWith("https://"))) {

			mUrlString = "http://" + mUrlString;

		}

		// �����Ϣ
		final FeedItemBean feedItemBean = new FeedItemBean();
		final FeedItemDAO feedItemDAO = new FeedItemDAO(this);

		// ����URL
		feedItemBean.setFeedURL(mUrlString);

		final FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new OnFinishedParseXMLListener() {

					@Override
					public void onFinishedParseXMLBaseInfo(boolean result) {

						if (result) {// �ɹ���ȡ
							// ���ñ���
							if (TextUtils.isEmpty(titleString)) {// ����Ϊ��

								if (TextUtils.isEmpty(feedXMLParser.mFeedTitle)) {// ������Ϊ��
									feedItemBean.setTitle("�ޱ���");
								} else {// �û���д������������
									feedItemBean
											.setTitle(feedXMLParser.mFeedTitle);
								}

							} else {// �û��Զ���
								feedItemBean.setTitle(titleString);
							}

							// ����Ԥ������
							if (!TextUtils.isEmpty(feedXMLParser.mFeedSummary)) {
								feedItemBean
										.setPreviewContent(feedXMLParser.mFeedSummary);
							} else {
								feedItemBean.setPreviewContent("û�н��յ�����");
							}

							// ����ʱ��
							if (!TextUtils.isEmpty(feedXMLParser.mFeedTime)) {

								if ("RSS".equals(feedXMLParser.mFeedType)) {
									feedItemBean.setLastTime(TimeUtils
											.varString2Timestamp(feedXMLParser.mFeedTime));
								}

							} else {
								feedItemBean.setLastTime(new Timestamp(System
										.currentTimeMillis()));
							}

							// favicon��ȡ

							Pattern p = Pattern
									.compile("(?<=//|)((\\w)+\\.)+\\w+");// ƥ�䶥������

							Matcher m = p.matcher(mUrlString);

							if (m.find()) {

								// ��ȫhttpͷ
								String host = m.group();
								if (host.startsWith("http://")
										|| host.startsWith("https://")) {
									feedItemBean.setPicURL(host
											+ "/favicon.ico");
								} else {
									feedItemBean.setPicURL("http://" + host
											+ "/favicon.ico");
								}

							} else {
								feedItemBean.setPicURL("null");
							}

							//���Ա���ͼ
							feedItemBean.setPicURL(Environment.getExternalStorageDirectory()
									.getAbsolutePath() + "/Download/1.png");
							LogUtils.w("AddFeedActivity", Environment.getExternalStorageDirectory()
									.getAbsolutePath() + "/Download/1.png");
							
							feedItemDAO.addItem(feedItemBean);

							// �������ݸ�MainActivity
							Intent returnData = new Intent();
							returnData.putExtra("feedItemBean", feedItemBean);

							// ��������
							if (feedXMLParser.mFeedType == "RSS") {
								returnData.putExtra("count",
										feedXMLParser.mItemCount);
							} else if (feedXMLParser.mFeedType == "ATOM") {
								returnData.putExtra("count",
										feedXMLParser.mEntryCount);
							}

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

				});

		// ��������
		feedXMLParser.parseUrl(mUrlString, mEncodingString);

		return true;
	}
}
