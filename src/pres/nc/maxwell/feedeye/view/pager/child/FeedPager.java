package pres.nc.maxwell.feedeye.view.pager.child;

import java.util.ArrayList;
import java.util.Locale;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.MainActivity;
import pres.nc.maxwell.feedeye.activity.defalut.child.AddFeedActivity;
import pres.nc.maxwell.feedeye.activity.defalut.child.ItemDetailListActivity;
import pres.nc.maxwell.feedeye.activity.defalut.child.SearchItemActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItem;
import pres.nc.maxwell.feedeye.utils.SystemInfoUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.ArrayListLoadingMoreAdapter;
import pres.nc.maxwell.feedeye.view.LayoutImageView;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog.MainThemeAlertDialogAdapter;
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog;
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog.AlertDialogOnClickListener;
import pres.nc.maxwell.feedeye.view.MainThemeLongClickDialog.DialogDataAdapter;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 订阅页面的Pager
 */
public class FeedPager extends BasePager {

	/**
	 * 填充到父布局中的FrameLayout中的View对象
	 */
	private View mViewContent;

	/**
	 * 订阅列表
	 */
	private DragRefreshListView mListView;

	/**
	 * 保存未显示的Item信息
	 */
	private ArrayList<FeedItem> mItemInfoUnshowList;

	/**
	 * 保存已经显示的信息
	 */
	private ArrayList<FeedItem> mItemInfoShowedList;

	/**
	 * ListView为空显示的提示图片
	 */
	private ImageView mNothingImg;

	/**
	 * Bitmap三级缓存
	 */
	BitmapCacheUtils mCacheUtils = new BitmapCacheUtils();

	/**
	 * ListView数据适配器
	 */
	private FeedPagerListViewAdapter mListViewAdapter;

	public DragRefreshListView getListView() {
		return mListView;
	}

	/**
	 * 弹出窗口
	 */
	private PopupWindow mPopupWindow;

	/**
	 * 构造方法
	 * 
	 * @param mActivity
	 *            Activity
	 */
	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	/**
	 * 初始化界面显示
	 */
	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("我的订阅");
		mViewContent = setContainerContent(R.layout.pager_feed);
		mListView = (DragRefreshListView) mViewContent
				.findViewById(R.id.lv_feed_list);

		// ListView为空时显示的图片
		mNothingImg = (ImageView) mViewContent.findViewById(R.id.iv_nothing);

		mItemInfoUnshowList = new ArrayList<FeedItem>();
		mItemInfoShowedList = new ArrayList<FeedItem>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// 首先异步查询数据库
		new ReadItemInfoDBTask().execute();

		// 在上面的AsyncTask执行后会执行doWhenFinishedReadDB()

		// 设置点击没有订阅信息的图片添加订阅
		mNothingImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				addNewFeedItem();

			}

		});

		// 同步完成执行
		// TODO：同步未实现
		// new FeedItemDAO(mActivity).completeSynchronized();

	}

	/**
	 * 读取FeedItem信息的异步任务
	 */
	class ReadItemInfoDBTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// 子线程

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			feedItemDAO.queryAllItems(mItemInfoUnshowList);

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {// 主线程

			// 数据库读取完成
			doWhenFinishedReadDB();

		}

	}

	/**
	 * 完成数据库读取后执行的操作
	 */
	private void doWhenFinishedReadDB() {

		// 设置ListView适配器
		mListViewAdapter = new FeedPagerListViewAdapter(mItemInfoUnshowList,
				mItemInfoShowedList, 20);

		// 插入要加载的Item
		mListViewAdapter.insertMoreItem();

		// 设置ListView适配器，添加数据
		if (mItemInfoShowedList.size() == 0) {// 无数据
			// 不显示加载条
			getLoadingBarView().setVisibility(View.INVISIBLE);
			mListView.setVisibility(View.INVISIBLE);// 防止下拉BUG

			// 提示没有数据，需要添加
			mNothingImg.setVisibility(View.VISIBLE);
		} else {// 有数据
			setListViewData(500);
		}

		// 添加监听器
		addListViewListener();
	}

	/**
	 * 使用功能按钮，初始化按钮
	 */
	@Override
	protected void useFunctionButton() {

		super.useFunctionButton();

		mFuncButtonLeft.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_search));// 搜索按钮
		mFuncButtonLeft.setVisibility(View.VISIBLE);

		// 搜索按钮事件
		mFuncButtonLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, SearchItemActivity.class);
				// 传递数据
				intent.putExtra("ShowedList", mItemInfoShowedList);
				intent.putExtra("UnShowList", mItemInfoUnshowList);
				mActivity.startActivity(intent);
			}

		});

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_add));// 添加按钮
		mFuncButtonRight.setVisibility(View.VISIBLE);

		// 添加按钮事件
		mFuncButtonRight.setOnClickListener(new AddFeedOnClickListener());

	};

	/**
	 * 设置ListView适配器,加载数据
	 * 
	 * @param delayTime
	 *            延迟时间
	 */
	private void setListViewData(final int delayTime) {
		new Thread() {
			public void run() {

				// 延迟加载，防止进入时卡屏
				try {
					Thread.sleep(delayTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mActivity.runOnUiThread(new Runnable() {
					public void run() {

						// 设置ListView适配器
						mListView.setAdapter(mListViewAdapter);

						// 不显示加载条
						getLoadingBarView().setVisibility(View.INVISIBLE);
					}
				});

			};
		}.start();
	}

	/**
	 * 利用ViewHolder优化ListView，减少findViewById的次数
	 */
	static class ViewHolder {
		public LayoutImageView mItemPic; // 图片
		public TextView mItemTitle; // 订阅标题
		public TextView mItemPreview; // 订阅预览
		public TextView mItemTime; // 时间
		public ImageView mItemCount; // 未读数
	}

	/**
	 * 订阅列表的适配器
	 */
	class FeedPagerListViewAdapter extends ArrayListLoadingMoreAdapter<FeedItem> {

		public FeedPagerListViewAdapter(ArrayList<FeedItem> unshowList,
				ArrayList<FeedItem> showedList, int onceShowedCount) {
			mListView.super(unshowList, showedList, onceShowedCount);

		}

		@Override
		public int getCount() {

			int itemCount = super.getCount();

			if (mItemInfoUnshowList.size() == 0) {// 没有更多了
				mTitle.setText("我的订阅(" + itemCount + ")");
			} else {
				mTitle.setText("我的订阅(" + itemCount + "+)");
			}

			return itemCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout view;
			ViewHolder holder;

			// 复用ConvertView
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 复用View并取出holder
				view = (RelativeLayout) convertView;
				holder = (ViewHolder) view.getTag();

				// 检查是否复用ConvertView
				// LogUtils.v("FeedPager", "复用View");

			} else {
				// 不可复用

				view = (RelativeLayout) View.inflate(mActivity,
						R.layout.view_lv_item_feed, null);

				// 利用ViewHolder记录子孩子View对象
				holder = new ViewHolder();

				holder.mItemPic = (LayoutImageView) view
						.findViewById(R.id.iv_item_feed_pic);// 图片
				holder.mItemTitle = (TextView) view
						.findViewById(R.id.tv_item_feed_title);// 标题
				holder.mItemPreview = (TextView) view
						.findViewById(R.id.tv_item_feed_preview);// 预览
				holder.mItemTime = (TextView) view
						.findViewById(R.id.tv_item_feed_time);// 时间

				view.setTag(holder);

			}

			parseFeedItem(mItemInfoShowedList.get(position), holder);
			return view;
		}

	}

	/**
	 * ListView刷新监听器，用于写下拉刷新逻辑和上拉加载逻辑
	 */
	class ListViewRefreshListener implements OnRefreshListener {

		@Override
		public void onDragRefresh() {

			mItemInfoShowedList.clear();
			mItemInfoUnshowList.clear();

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			feedItemDAO.queryAllItems(mItemInfoUnshowList);

			// 插入要加载的Item
			mListViewAdapter.insertMoreItem();

			// 修改UI必须在主线程执行
			mListViewAdapter.notifyDataSetChanged();

			if (mItemInfoUnshowList.size() > 0) {
				// 允许再加载更多
				mListView.isAllowLoadingMore = true;
			}

			Toast.makeText(mActivity, "刷新成功", Toast.LENGTH_SHORT).show();

			mListView.completeRefresh();

		}

		@Override
		public void onLoadingMore() {

			// 成功插入的数据条数
			final int addCount = mListViewAdapter.insertMoreItem();

			// 修改UI必须在主线程执行
			mListViewAdapter.notifyDataSetChanged();

			if (addCount == 0) {
				Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mActivity, "成功加载了" + addCount + "条数据",
						Toast.LENGTH_SHORT).show();
			}
			if (mItemInfoUnshowList.size() <= 0) {
				// 禁止再加载更多
				mListView.isAllowLoadingMore = false;
			}
			mListView.completeRefresh();
		}
	}

	/**
	 * 解析FeedItem并显示
	 * 
	 * @param feedItem
	 *            订阅信息
	 * @param viewHolder
	 *            view集合
	 * @return 是否成功解析
	 */
	private boolean parseFeedItem(FeedItem feedItem, ViewHolder viewHolder) {

		if (feedItem == null) {
			return false;
		}

		// 使用三级缓存加载图片
		BitmapCacheUtils.displayBitmapOnLayoutChange(mActivity, viewHolder.mItemPic,
				feedItem.picURL, null);

		viewHolder.mItemTitle.setText(feedItem.baseInfo.title);
		viewHolder.mItemPreview.setText(feedItem.baseInfo.summary);
		viewHolder.mItemTime.setText(TimeUtils.timestamp2String(
				feedItem.baseInfo.time, "a HH:mm", Locale.getDefault()));

		return true;

	}
	/**
	 * 添加ListView的各种监听器
	 */
	private void addListViewListener() {

		// 设置每项Item的点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 转换为从0开始的位置
				position = position - mListView.getHeaderViewsCount();

				Intent intent = new Intent(mActivity,
						ItemDetailListActivity.class);
				intent.putExtra("FeedItem", mItemInfoShowedList.get(position));
				mActivity.startActivity(intent);

			}
		});

		/**
		 * 长按点击事件
		 */
		mListView.setOnItemLongClickListener(new ItemLongClickListener());

		// 添加刷新监听
		mListView.setOnRefreshListener(new ListViewRefreshListener());

	}

	/**
	 * 长按点击监听器
	 */
	class ItemLongClickListener implements OnItemLongClickListener {

		/**
		 * 点击事件
		 */
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {

			new MainThemeLongClickDialog(mActivity, new DialogDataAdapter() {

				@Override
				public int getLayoutViewId() {
					return R.layout.view_long_click_lv_feed;
				}

				@Override
				public int[] getTextViewResIds() {
					int[] ids = {R.id.tv_modify, R.id.tv_delete, R.id.tv_cancel};
					return ids;
				}

				@Override
				public OnClickListener[] getItemOnClickListener(
						final AlertDialog alertDialog) {

					OnClickListener[] listeners = {
							new ModifyClickListener(position, alertDialog),// 修改标题
							new DeleteClickListener(position, alertDialog), // 删除
							new OnClickListener() {// 取消

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();// 对话框关闭
								}

							}};

					return listeners;
				}
			}).show();

			return true;
		}

		/**
		 * 修改标题点击监听器
		 */
		class ModifyClickListener extends AlertDialogOnClickListener {

			public ModifyClickListener(int position, AlertDialog alertDialog) {
				super(position, alertDialog);
			}

			@Override
			public void onClick(View v) {

				alertDialog.dismiss();// 对话框关闭

				// 显示修改的对话框
				new MainThemeAlertDialog(mActivity)
						.setAdapter(new MainThemeAlertDialogAdapter() {

							/**
							 * 输入框
							 */
							private EditText mTitleView;

							@Override
							public String getTitle() {
								return null;
							}

							// 确认按钮事件
							@Override
							public OnClickListener getOnConfirmClickLister(
									final AlertDialog alertDialog) {
								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										String newTitle = mTitleView.getText()
												.toString();

										if (TextUtils.isEmpty(newTitle)) {// 提示不能为空
											mTitleView
													.startAnimation(AnimationUtils
															.loadAnimation(
																	mActivity,
																	R.anim.edit_text_translate));

										} else {
											// 修改标题
											modifyFeedItemTitle(position,
													newTitle);
											alertDialog.dismiss();
										}

									}
								};
							}

							// 取消点击事件
							@Override
							public OnClickListener getOnCancelClickLister(
									final AlertDialog alertDialog) {
								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										// 关闭对话框
										alertDialog.dismiss();
									}
								};
							}

							@Override
							public View getContentView() {
								View view = View
										.inflate(
												mActivity,
												R.layout.alert_dialog_container_modify_title,
												null);
								mTitleView = (EditText) view
										.findViewById(R.id.et_title);
								return view;
							}

							@Override
							public void changeViewAtLast(TextView title,
									FrameLayout container,
									TextView confirmButtom,
									TextView cancelButtom) {

								// 获取原来的标题
								String orgTitle = ((ViewHolder) mListView
										.getChildAt(
												position
														- mListView
																.getFirstVisiblePosition())
										.getTag()).mItemTitle.getText()
										.toString();

								mTitleView.setText(orgTitle);
							}

						});

			}
		}

		/**
		 * 删除点击监听器
		 */
		class DeleteClickListener extends AlertDialogOnClickListener {

			public DeleteClickListener(int position, AlertDialog alertDialog) {
				super(position, alertDialog);
			}

			@Override
			public void onClick(View v) {

				alertDialog.dismiss();// 对话框关闭

				// 再次确认删除
				new MainThemeAlertDialog(mActivity)
						.setAdapter(new MainThemeAlertDialogAdapter() {

							@Override
							public String getTitle() {
								return "是否确定删除？";
							}

							// 点击时删除条目
							@Override
							public OnClickListener getOnConfirmClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {

										deleteFeedItem(position);
										// 关闭信息框
										alertDialog.dismiss();
									}
								};

							}

							// 点击取消删除
							@Override
							public OnClickListener getOnCancelClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										// 关闭信息框
										alertDialog.dismiss();
									}
								};

							}

							// 显示被删除的标题
							@Override
							public View getContentView() {

								View view = View
										.inflate(
												mActivity,
												R.layout.alert_dialog_container_delete_title,
												null);

								// 获取原来的标题
								String deleteItemTitle = mItemInfoShowedList
										.get(position
												- mListView
														.getHeaderViewsCount()).baseInfo.title;

								((TextView) view
										.findViewById(R.id.tv_delete_title))
										.setText(deleteItemTitle);

								return view;
							}

							@Override
							public void changeViewAtLast(TextView title,
									FrameLayout container,
									TextView confirmButtom,
									TextView cancelButtom) {

							}
						});

			}

		}

	}

	/**
	 * 修改列表中的Item标题，并且刷新数据库
	 * 
	 * @param position
	 *            要修改的Item的位置
	 * @param newTitle
	 *            新的标题
	 */
	private void modifyFeedItemTitle(int position, String newTitle) {

		int dbPosition = position - mListView.getHeaderViewsCount();// 转成正确的下标，相对于数据库

		FeedItem feedItem = mItemInfoShowedList.get(dbPosition);

		feedItem.baseInfo.title = newTitle;

		// 从数据库中更新
		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.updateItem(feedItem);

		// 不完全更新界面
		if (mListView.getFirstVisiblePosition() <= position
				&& position <= mListView.getLastVisiblePosition()) {
			((ViewHolder) mListView.getChildAt(
					position - mListView.getFirstVisiblePosition()).getTag()).mItemTitle
					.setText(newTitle);
		}

	}

	/**
	 * 删除列表中的Item，并且刷新数据库
	 * 
	 * @param position
	 *            要删除的Item的位置
	 */
	private void deleteFeedItem(int position) {

		position = position - mListView.getHeaderViewsCount();// 转成正确的下标，相对于数据库

		// 从数据库中删除
		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.removeItem(mItemInfoShowedList.get(position));

		// 从适配器数据中删除并通知数据更新
		mItemInfoShowedList.remove(position);
		mListViewAdapter.notifyDataSetChanged();
	}

	/**
	 * 添加订阅按钮的点击监听器
	 */
	class AddFeedOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (closePopupWindow()) {
				mPopupWindow.dismiss();
				return;
			}

			View popupView = View.inflate(mActivity,
					R.layout.popup_window_add_feed, null);

			// 测量宽高
			popupView.measure(0, 0);
			int popupViewWidth = popupView.getMeasuredWidth();
			int popupViewHeight = popupView.getMeasuredHeight();

			mPopupWindow = new PopupWindow(popupView, popupViewWidth,
					popupViewHeight);

			// 透明背景
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));

			// 设置动画
			mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

			// 设置焦点
			mPopupWindow.setFocusable(true);

			// 显示
			mPopupWindow.showAtLocation(mContainer, Gravity.TOP + Gravity.LEFT,
					(int) mFuncButtonRight.getRight() - popupViewWidth,
					(int) (mFuncButtonRight.getBottom() + SystemInfoUtils
							.getStatusBarHeight(mActivity)));

			// 添加订阅
			popupView.findViewById(R.id.pwiv_add).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {

							addNewFeedItem();

						}

					});

			// 分享应用
			popupView.findViewById(R.id.pwiv_share).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 分享
							Intent intent = new Intent();
							intent.setAction("android.intent.action.SEND");
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.setType("text/plain");
							intent.putExtra(Intent.EXTRA_TEXT,
									"我发现了一个好玩的应用，他的名字叫做FeedEye，赶紧来下载吧！地址是：https://github.com/maxwell-nc/FeedEye");
							mActivity.startActivity(intent);

							closePopupWindow();
						}
					});

			// 帮助和反馈
			popupView.findViewById(R.id.pwiv_help).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 打开项目页面
							Intent intent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("https://github.com/maxwell-nc/FeedEye"));
							mActivity.startActivity(intent);
							closePopupWindow();
						}
					});

		}
	}

	/**
	 * 关闭popupWindow
	 * 
	 * @return 是否成功关闭
	 */
	private boolean closePopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			return true;
		}
		return false;
	}

	/**
	 * 添加一个新的订阅信息
	 * 
	 * @see MainActivity#onActivityResult
	 */
	private void addNewFeedItem() {

		closePopupWindow();

		Intent intent = new Intent(mActivity, AddFeedActivity.class);

		// 打开并获得添加结果
		mActivity.startActivityForResult(intent, 1);

	}

	/**
	 * 完成添加订阅信息
	 * 
	 * @See 添加测试数据：{@link #addTestData()}
	 * @param feedItem
	 *            添加了的FeedItem
	 */
	public void finishedAddItem(FeedItem feedItem) {

		if (null == feedItem) {
			return;
		}

		if (mItemInfoShowedList.size() == 0) {// 无数据时
			mListView.setAdapter(mListViewAdapter);
			mListView.setVisibility(View.VISIBLE);
			mNothingImg.setVisibility(View.INVISIBLE);
		}

		mItemInfoShowedList.add(0, feedItem);// 插到第一个

		mListViewAdapter.notifyDataSetChanged();// 刷新适配器
		mListView.setSelection(mListView.getHeaderViewsCount());// 显示第一个非HeaderView
	}

}
