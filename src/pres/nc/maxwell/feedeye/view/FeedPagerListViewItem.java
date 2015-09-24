package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ��װFeedPager�е�ListView��ÿһ��Item����View���ṩ��View�Ķ���initListViewItem�����ֶ����ó�ʼ��
 */
public class FeedPagerListViewItem {

	private Activity mActivity;

	private View mListViewItem; // Item��View����

	private ImageView mItemPic; // ͼƬ
	private TextView mItemTitle; // ���ı���
	private TextView mItemPreview; // ����Ԥ��
	private TextView mItemTime; // ʱ��
	private ImageView mItemCount; // δ����

	private boolean isInitView = false;// �Ƿ��Ѿ���ʼ��View����
	private FeedItemBean mBean; // item������

	public ImageView getItemPic() {
		return mItemPic;
	}

	public TextView getItemTitle() {
		return mItemTitle;
	}

	public TextView getItemPreview() {
		return mItemPreview;
	}

	public TextView getItemTime() {
		return mItemTime;
	}

	public ImageView getItemCount() {
		return mItemCount;
	}

	/**
	 * ����ListView��Item����
	 * 
	 * @return ListView��Item����
	 */
	public View getItemView() {
		return mListViewItem;
	}

	/**
	 * ���캯��
	 * 
	 * @param mActivity
	 */
	public FeedPagerListViewItem(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	/**
	 * ��ʼ��ListView��Item��View���󣬲���������
	 */
	public void initListViewItem() {
		mListViewItem = View.inflate(mActivity, R.layout.view_lv_item_feed,
				null);

		mItemPic = (ImageView) mListViewItem
				.findViewById(R.id.iv_item_feed_pic);
		mItemTitle = (TextView) mListViewItem
				.findViewById(R.id.tv_item_feed_title);
		mItemPreview = (TextView) mListViewItem
				.findViewById(R.id.tv_item_feed_preview);
		mItemTime = (TextView) mListViewItem
				.findViewById(R.id.tv_item_feed_time);
		mItemCount = (ImageView) mListViewItem
				.findViewById(R.id.iv_item_feed_count);

		isInitView = true;
	}

	/**
	 * ����feedItemBean����ʾ
	 * 
	 * @param feedItemBean
	 *            ������Ϣ
	 * @return �Ƿ�ɹ�����
	 */
	public boolean parseBean(FeedItemBean feedItemBean) {

		if (isInitView == false) {// ��ֹδ��ʼ��
			initListViewItem();
		}

		if (feedItemBean != null) {
			this.mBean = feedItemBean;
		} else {
			return false;
		}

		// ʹ�������������ͼƬ
		new BitmapCacheUtils().displayBitmap(mItemPic, mBean.getPicURL(),
				R.drawable.anim_refresh_rotate);
		mItemTitle.setText(mBean.getTitle());
		mItemPreview.setText(mBean.getPreviewContent());
		mItemTime.setText(TimeUtils.timestamp2String(mBean.getLastTime(),
				"HH:mm"));

		return true;
	}

}
