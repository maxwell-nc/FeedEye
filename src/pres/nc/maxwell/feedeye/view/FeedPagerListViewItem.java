package pres.nc.maxwell.feedeye.view;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * ��װFeedPager�е�ListView��ÿһ��Item����View���ṩ��View�Ķ���
 */
public class FeedPagerListViewItem {
	
	private Activity mActivity;
	
	private View mListViewItem;		//Item��View����
	
	private ImageView mItemPic;		//ͼƬ
	private TextView mItemTitle;	//���ı���
	private TextView mItemPreview;	//����Ԥ��
	private TextView mItemTime;		//ʱ��
	private ImageView mItemCount;	//δ����
	
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
	 * @return ListView��Item����
	 */
	public View getItemView(){
		return mListViewItem;
	}
	
	
	/**
	 * ���캯��
	 * @param mActivity
	 */
	public FeedPagerListViewItem(Activity mActivity) {
		super();
		this.mActivity = mActivity;
		
		initListViewItem();
	}


	/**
	 * ��ʼ��ListView��Item��View����
	 */
	private void initListViewItem() {
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
	}

}
