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
 * 封装FeedPager中的ListView的每一个Item对象View，提供子View的对象，initListViewItem必须手动调用初始化
 */
public class FeedPagerListViewItem {

	private Activity mActivity;

	private View mListViewItem; // Item的View对象

	private ImageView mItemPic; // 图片
	private TextView mItemTitle; // 订阅标题
	private TextView mItemPreview; // 订阅预览
	private TextView mItemTime; // 时间
	private ImageView mItemCount; // 未读数

	private boolean isInitView = false;// 是否已经初始化View对象
	private FeedItemBean mBean; // item的数据

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
	 * 返回ListView的Item对象
	 * 
	 * @return ListView的Item对象
	 */
	public View getItemView() {
		return mListViewItem;
	}

	/**
	 * 构造函数
	 * 
	 * @param mActivity
	 */
	public FeedPagerListViewItem(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	/**
	 * 初始化ListView的Item的View对象，不主动调用
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
	 * 解析feedItemBean并显示
	 * 
	 * @param feedItemBean
	 *            订阅信息
	 * @return 是否成功解析
	 */
	public boolean parseBean(FeedItemBean feedItemBean) {

		if (isInitView == false) {// 防止未初始化
			initListViewItem();
		}

		if (feedItemBean != null) {
			this.mBean = feedItemBean;
		} else {
			return false;
		}

		// 使用三级缓存加载图片
		new BitmapCacheUtils().displayBitmap(mItemPic, mBean.getPicURL(),
				R.drawable.anim_refresh_rotate);
		mItemTitle.setText(mBean.getTitle());
		mItemPreview.setText(mBean.getPreviewContent());
		mItemTime.setText(TimeUtils.timestamp2String(mBean.getLastTime(),
				"HH:mm"));

		return true;
	}

}
