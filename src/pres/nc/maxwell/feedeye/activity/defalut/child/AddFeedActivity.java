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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * ���Ӷ���ҳ���Activity
 */
public class AddFeedActivity extends DefaultNewActivity {

	/**
	 * ������Ӱ�ť
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
	 * ��ʼ��View����
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

		mEncodingGroup = (RadioGroup) mContainerView.findViewById(R.id.rg_encoding);
		
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

		setResult(-1, null);//-1��ʾû�з�������
		return super.beforeClose();
	}
	
	/**
	 * �������
	 * 
	 * @return �����Ƿ�ɹ�����
	 */
	private boolean addItem() {

		String urlString = mUrlText.getText().toString();
		if (TextUtils.isEmpty(urlString)) {
			return false;
		}

		final String titleString = mTitleText.getText().toString();
		

		if (!(urlString.startsWith("http://") || urlString
				.startsWith("https://"))) {
			return false;// ��Ч
		}

		// ������Ϣ
		final FeedItemBean feedItemBean = new FeedItemBean();
		final FeedItemDAO feedItemDAO = new FeedItemDAO(this);

		// ����URL
		feedItemBean.setFeedURL(urlString);

		final FeedXMLParser feedXMLParser = new FeedXMLParser();

		feedXMLParser
				.setOnFinishedParseXMLListener(new OnFinishedParseXMLListener() {

					@Override
					public void onFinishedParseXML() {

						// ���ñ���
						if (TextUtils.isEmpty(titleString)) {// ����Ϊ��

							if (TextUtils.isEmpty(feedXMLParser.mFeedTitle)) {// ������Ϊ��
								feedItemBean.setTitle("�ޱ���");
							} else {// �û���д������������
								feedItemBean.setTitle(feedXMLParser.mFeedTitle);
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

						// ����Ԥ������
						if (!TextUtils.isEmpty(feedXMLParser.mFeedTime)) {
							if ("RSS".equals(feedXMLParser.mFeedType)) {
								feedItemBean.setLastTime(TimeUtils
										.gmt2Timestamp(feedXMLParser.mFeedTime));
							}
							
							
						} else {
							feedItemBean.setLastTime(new Timestamp(System
									.currentTimeMillis()));
						}

						// TODO��ͼƬ����ȡ
						feedItemBean.setPicURL("null");

						feedItemDAO.addItem(feedItemBean);

						//�������ݸ�MainActivity
						Intent returnData = new Intent();
						returnData.putExtra("feedItemBean", feedItemBean);
						setResult(0, returnData);
						
						//�ر�Activity
						finish();
					}

				});

		//��������
		feedXMLParser.parseUrl(urlString,mEncodingString);

		

		return true;
	}
}