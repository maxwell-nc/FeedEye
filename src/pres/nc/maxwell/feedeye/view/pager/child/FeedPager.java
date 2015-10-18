package pres.nc.maxwell.feedeye.view.pager.child;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����ҳ���Pager
 */
public class FeedPager extends BasePager {

	/**
	 * ��䵽�������е�FrameLayout�е�View����
	 */
	private View mViewContent;

	/**
	 * �����б�
	 */
	private DragRefreshListView mListView;

	/**
	 * ����δ��ʾ��Item��Ϣ
	 */
	private ArrayList<FeedItem> mItemInfoUnshowList;

	/**
	 * �����Ѿ���ʾ����Ϣ
	 */
	private ArrayList<FeedItem> mItemInfoShowedList;

	/**
	 * һ��չʾ��Item����
	 */
	private final int SHOW_ITEM_COUNT = 20;

	/**
	 * ListViewΪ����ʾ����ʾͼƬ
	 */
	private ImageView mNothingImg;

	/**
	 * Bitmap��������
	 */
	BitmapCacheUtils mCacheUtils = new BitmapCacheUtils();

	/**
	 * ListView����������
	 */
	private FeedPagerListViewAdapter mListViewAdapter;

	public DragRefreshListView getListView() {
		return mListView;
	}

	/**
	 * ��������
	 */
	private PopupWindow mPopupWindow;

	/**
	 * ���췽��
	 * 
	 * @param mActivity
	 *            Activity
	 */
	public FeedPager(Activity mActivity) {
		super(mActivity);
	}

	/**
	 * ��ʼ��������ʾ
	 */
	@Override
	protected void initView() {
		super.initView();

		mTitle.setText("�ҵĶ���");
		mViewContent = setContainerContent(R.layout.pager_feed);
		mListView = (DragRefreshListView) mViewContent
				.findViewById(R.id.lv_feed_list);

		// ListViewΪ��ʱ��ʾ��ͼƬ
		mNothingImg = (ImageView) mViewContent.findViewById(R.id.iv_nothing);

		mItemInfoUnshowList = new ArrayList<FeedItem>();
		mItemInfoShowedList = new ArrayList<FeedItem>();

		useFunctionButton();
	}

	@Override
	protected void initData() {
		super.initData();

		// �����첽��ѯ���ݿ�
		new ReadItemInfoDBTask().execute();

		// �������AsyncTaskִ�к��ִ��doWhenFinishedReadDB()

		// ���õ��û�ж�����Ϣ��ͼƬ��Ӷ���
		mNothingImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				addNewFeedItem();

			}

		});

		// ͬ�����ִ��
		// TODO��ͬ��δʵ��
		// new FeedItemDAO(mActivity).completeSynchronized();

	}

	/**
	 * ��ȡFeedItem��Ϣ���첽����
	 */
	class ReadItemInfoDBTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {// ���߳�

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			mItemInfoUnshowList = feedItemDAO.queryAllItems();

			return null;

		}

		@Override
		protected void onProgressUpdate(Void... values) {// ���߳�
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {// ���߳�

			// ���ݿ��ȡ���
			doWhenFinishedReadDB();

		}
	}

	/**
	 * ������ݿ��ȡ��ִ�еĲ���
	 */
	private void doWhenFinishedReadDB() {

		// ����Ҫ���ص�Item
		insertMoreItem();

		// ����ListView���������������
		if (mItemInfoShowedList.size() == 0) {// ������
			// ����ʾ������
			getLoadingBarView().setVisibility(View.INVISIBLE);
			mListView.setVisibility(View.INVISIBLE);// ��ֹ����BUG

			// ��ʾû�����ݣ���Ҫ���
			mNothingImg.setVisibility(View.VISIBLE);
		} else {// ������
			setListViewData(500);
		}

		// ��Ӽ�����
		addListViewListener();
	}

	/**
	 * ʹ�ù��ܰ�ť����ʼ����ť
	 */
	@Override
	protected void useFunctionButton() {

		super.useFunctionButton();

		mFuncButtonLeft.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_search));// ������ť
		mFuncButtonLeft.setVisibility(View.VISIBLE);

		// ������ť�¼�
		mFuncButtonLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, SearchItemActivity.class);
				// ��������
				intent.putExtra("ShowedList", mItemInfoShowedList);
				intent.putExtra("UnShowList", mItemInfoUnshowList);
				mActivity.startActivity(intent);
			}

		});

		mFuncButtonRight.setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.btn_title_add));// ��Ӱ�ť
		mFuncButtonRight.setVisibility(View.VISIBLE);

		// ��Ӱ�ť�¼�
		mFuncButtonRight.setOnClickListener(new AddFeedOnClickListener());

	};

	/**
	 * ����ListView������,��������
	 * 
	 * @param delayTime
	 *            �ӳ�ʱ��
	 */
	private void setListViewData(final int delayTime) {
		new Thread() {
			public void run() {

				// �ӳټ��أ���ֹ����ʱ����
				try {
					Thread.sleep(delayTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mActivity.runOnUiThread(new Runnable() {
					public void run() {

						mListViewAdapter = new FeedPagerListViewAdapter();
						// ����ListView������
						mListView.setAdapter(mListViewAdapter);

						// ����ʾ������
						getLoadingBarView().setVisibility(View.INVISIBLE);
					}
				});

			};
		}.start();
	}

	/**
	 * ����и�������������������
	 * 
	 * @return ����˵���Ŀ��
	 */
	private int insertMoreItem() {
		int addCount = 0;// Ҫ��ӵ�����

		if (mItemInfoUnshowList.size() == 0) {// û���ݿ��Լ�����
			return 0;
		}

		// ��ʣ������
		if (mItemInfoUnshowList.size() > SHOW_ITEM_COUNT) {
			addCount = SHOW_ITEM_COUNT;
		} else {// ʣ������ȫ������
			addCount = mItemInfoUnshowList.size();

			// û�и������ݣ���ֹ�������ظ���
			mListView.isAllowLoadingMore = false;
		}

		// ��ӵ���ʾ�б�
		for (int i = 0; i < addCount; i++) {
			mItemInfoShowedList.add(mItemInfoUnshowList.get(i));
		}
		for (int i = addCount - 1; i >= 0; i--) {
			mItemInfoUnshowList.remove(i);
		}

		return addCount;
	};

	/**
	 * ����ViewHolder�Ż�ListView������findViewById�Ĵ���
	 */
	static class ViewHolder {
		public ImageView mItemPic; // ͼƬ
		public TextView mItemTitle; // ���ı���
		public TextView mItemPreview; // ����Ԥ��
		public TextView mItemTime; // ʱ��
		public ImageView mItemCount; // δ����
	}

	/**
	 * �����б��������
	 */
	class FeedPagerListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			int itemCount = mItemInfoShowedList.size();

			if (mItemInfoUnshowList.size() == 0) {// û�и�����
				mTitle.setText("�ҵĶ���(" + itemCount + ")");
			} else {
				mTitle.setText("�ҵĶ���(" + itemCount + "+)");
			}

			return itemCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			RelativeLayout view;
			ViewHolder holder;

			// ����ConvertView
			if (convertView != null && convertView instanceof RelativeLayout) {
				// ����View��ȡ��holder
				view = (RelativeLayout) convertView;
				holder = (ViewHolder) view.getTag();

				// ����Ƿ���ConvertView��ƽʱ����Ҫ��ӡ����ʱ
				// LogUtils.v("FeedPager", "����View");

			} else {
				// ���ɸ���

				view = (RelativeLayout) View.inflate(mActivity,
						R.layout.view_lv_item_feed, null);

				// ����ViewHolder��¼�Ӻ���View����
				holder = new ViewHolder();

				holder.mItemPic = (ImageView) view
						.findViewById(R.id.iv_item_feed_pic);// ͼƬ
				holder.mItemTitle = (TextView) view
						.findViewById(R.id.tv_item_feed_title);// ����
				holder.mItemPreview = (TextView) view
						.findViewById(R.id.tv_item_feed_preview);// Ԥ��
				holder.mItemTime = (TextView) view
						.findViewById(R.id.tv_item_feed_time);// ʱ��
				// holder.mItemCount = (ImageView) view
				// .findViewById(R.id.iv_item_feed_count);// ����

				view.setTag(holder);

			}

			parseFeedItem(mItemInfoShowedList.get(position), holder);
			return view;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

	}

	/**
	 * ListViewˢ�¼�����������д����ˢ���߼������������߼�
	 */
	class ListViewRefreshListener implements OnRefreshListener {

		@Override
		public void onDragRefresh() {

			mItemInfoShowedList.clear();
			mItemInfoUnshowList.clear();

			FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
			mItemInfoUnshowList = feedItemDAO.queryAllItems();

			// ����Ҫ���ص�Item
			insertMoreItem();

			// �޸�UI���������߳�ִ��
			mListViewAdapter.notifyDataSetChanged();

			if (mItemInfoUnshowList.size() > 0) {
				// �����ټ��ظ���
				mListView.isAllowLoadingMore = true;
			}

			Toast.makeText(mActivity, "ˢ�³ɹ�", Toast.LENGTH_SHORT).show();

			mListView.completeRefresh();

		}

		@Override
		public void onLoadingMore() {

			// �ɹ��������������
			final int addCount = insertMoreItem();

			// �޸�UI���������߳�ִ��
			mListViewAdapter.notifyDataSetChanged();

			if (addCount == 0) {
				Toast.makeText(mActivity, "û�и���������", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mActivity, "�ɹ�������" + addCount + "������",
						Toast.LENGTH_SHORT).show();
			}
			if (mItemInfoUnshowList.size() <= 0) {
				// ��ֹ�ټ��ظ���
				mListView.isAllowLoadingMore = false;
			}
			mListView.completeRefresh();
		}
	}

	/**
	 * ����FeedItem����ʾ
	 * 
	 * @param feedItem
	 *            ������Ϣ
	 * @param viewHolder
	 *            view����
	 * @return �Ƿ�ɹ�����
	 */
	private boolean parseFeedItem(FeedItem feedItem, ViewHolder viewHolder) {

		if (feedItem == null) {
			return false;
		}

		// ʹ�������������ͼƬ
		BitmapCacheUtils.displayBitmap(mActivity, viewHolder.mItemPic,
				feedItem.picURL,null);

		viewHolder.mItemTitle.setText(feedItem.baseInfo.title);
		viewHolder.mItemPreview.setText(feedItem.baseInfo.summary);
		viewHolder.mItemTime.setText(TimeUtils.timestamp2String(
				feedItem.baseInfo.time, "a HH:mm", Locale.getDefault()));

		return true;

	}
	/**
	 * ���ListView�ĸ��ּ�����
	 */
	private void addListViewListener() {

		// ����ÿ��Item�ĵ���¼�
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ת��Ϊ��0��ʼ��λ��
				position = position - mListView.getHeaderViewsCount();

				Intent intent = new Intent(mActivity,
						ItemDetailListActivity.class);
				intent.putExtra("FeedItem", mItemInfoShowedList.get(position));
				mActivity.startActivity(intent);

			}
		});

		/**
		 * ��������¼�
		 */
		mListView.setOnItemLongClickListener(new ItemLongClickListener());

		// ���ˢ�¼���
		mListView.setOnRefreshListener(new ListViewRefreshListener());

	}

	/**
	 * �������������
	 */
	class ItemLongClickListener implements OnItemLongClickListener {

		/**
		 * ����¼�
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
							new ModifyClickListener(position, alertDialog),// �޸ı���
							new DeleteClickListener(position, alertDialog), // ɾ��
							new OnClickListener() {// ȡ��

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();// �Ի���ر�
								}

							}};

					return listeners;
				}
			}).show();

			return true;
		}

		/**
		 * �޸ı�����������
		 */
		class ModifyClickListener extends AlertDialogOnClickListener {

			public ModifyClickListener(int position, AlertDialog alertDialog) {
				super(position, alertDialog);
			}

			@Override
			public void onClick(View v) {

				alertDialog.dismiss();// �Ի���ر�

				// ��ʾ�޸ĵĶԻ���
				new MainThemeAlertDialog(mActivity)
						.setAdapter(new MainThemeAlertDialogAdapter() {

							/**
							 * �����
							 */
							private EditText mTitleView;

							@Override
							public String getTitle() {
								return null;
							}

							// ȷ�ϰ�ť�¼�
							@Override
							public OnClickListener getOnConfirmClickLister(
									final AlertDialog alertDialog) {
								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										String newTitle = mTitleView.getText()
												.toString();

										if (TextUtils.isEmpty(newTitle)) {// ��ʾ����Ϊ��
											mTitleView
													.startAnimation(AnimationUtils
															.loadAnimation(
																	mActivity,
																	R.anim.edit_text_translate));

										} else {
											// �޸ı���
											modifyFeedItemTitle(position,
													newTitle);
											alertDialog.dismiss();
										}

									}
								};
							}

							// ȡ������¼�
							@Override
							public OnClickListener getOnCancelClickLister(
									final AlertDialog alertDialog) {
								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										// �رնԻ���
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

								// ��ȡԭ���ı���
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
		 * ɾ�����������
		 */
		class DeleteClickListener extends AlertDialogOnClickListener {

			public DeleteClickListener(int position, AlertDialog alertDialog) {
				super(position, alertDialog);
			}

			@Override
			public void onClick(View v) {

				alertDialog.dismiss();// �Ի���ر�

				// �ٴ�ȷ��ɾ��
				new MainThemeAlertDialog(mActivity)
						.setAdapter(new MainThemeAlertDialogAdapter() {

							@Override
							public String getTitle() {
								return "�Ƿ�ȷ��ɾ����";
							}

							// ���ʱɾ����Ŀ
							@Override
							public OnClickListener getOnConfirmClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {

										deleteFeedItem(position);
										// �ر���Ϣ��
										alertDialog.dismiss();
									}
								};

							}

							// ���ȡ��ɾ��
							@Override
							public OnClickListener getOnCancelClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										// �ر���Ϣ��
										alertDialog.dismiss();
									}
								};

							}

							// ��ʾ��ɾ���ı���
							@Override
							public View getContentView() {

								View view = View
										.inflate(
												mActivity,
												R.layout.alert_dialog_container_delete_title,
												null);

								// ��ȡԭ���ı���
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
	 * �޸��б��е�Item���⣬����ˢ�����ݿ�
	 * 
	 * @param position
	 *            Ҫ�޸ĵ�Item��λ��
	 * @param newTitle
	 *            �µı���
	 */
	private void modifyFeedItemTitle(int position, String newTitle) {

		int dbPosition = position - mListView.getHeaderViewsCount();// ת����ȷ���±꣬��������ݿ�

		FeedItem feedItem = mItemInfoShowedList.get(dbPosition);

		feedItem.baseInfo.title = newTitle;

		// �����ݿ��и���
		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.updateItem(feedItem);

		// ����ȫ���½���
		if (mListView.getFirstVisiblePosition() <= position
				&& position <= mListView.getLastVisiblePosition()) {
			((ViewHolder) mListView.getChildAt(
					position - mListView.getFirstVisiblePosition()).getTag()).mItemTitle
					.setText(newTitle);
		}

	}

	/**
	 * ɾ���б��е�Item������ˢ�����ݿ�
	 * 
	 * @param position
	 *            Ҫɾ����Item��λ��
	 */
	private void deleteFeedItem(int position) {

		position = position - mListView.getHeaderViewsCount();// ת����ȷ���±꣬��������ݿ�

		// �����ݿ���ɾ��
		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.removeItem(mItemInfoShowedList.get(position));

		// ��������������ɾ����֪ͨ���ݸ���
		mItemInfoShowedList.remove(position);
		mListViewAdapter.notifyDataSetChanged();
	}

	/**
	 * ��Ӷ��İ�ť�ĵ��������
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

			// �������
			popupView.measure(0, 0);
			int popupViewWidth = popupView.getMeasuredWidth();
			int popupViewHeight = popupView.getMeasuredHeight();

			mPopupWindow = new PopupWindow(popupView, popupViewWidth,
					popupViewHeight);

			// ͸������
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));

			// ���ö���
			mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

			// ���ý���
			mPopupWindow.setFocusable(true);

			// ��ʾ
			mPopupWindow.showAtLocation(mContainer, Gravity.TOP + Gravity.LEFT,
					(int) mFuncButtonRight.getRight() - popupViewWidth,
					(int) (mFuncButtonRight.getBottom() + SystemInfoUtils
							.getStatusBarHeight(mActivity)));

			// ��Ӷ���
			popupView.findViewById(R.id.pwiv_add).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {

							addNewFeedItem();

						}

					});

			// ����Ӧ��
			popupView.findViewById(R.id.pwiv_share).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// ����
							Intent intent = new Intent();
							intent.setAction("android.intent.action.SEND");
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.setType("text/plain");
							intent.putExtra(Intent.EXTRA_TEXT,
									"�ҷ�����һ�������Ӧ�ã��������ֽ���FeedEye���Ͻ������ذɣ���ַ�ǣ�https://github.com/maxwell-nc/FeedEye");
							mActivity.startActivity(intent);

							closePopupWindow();
						}
					});

			// �����ͷ���
			popupView.findViewById(R.id.pwiv_help).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// ����Ŀҳ��
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
	 * �ر�popupWindow
	 * 
	 * @return �Ƿ�ɹ��ر�
	 */
	private boolean closePopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			return true;
		}
		return false;
	}

	/**
	 * ���һ���µĶ�����Ϣ
	 * 
	 * @see MainActivity#onActivityResult
	 */
	private void addNewFeedItem() {

		closePopupWindow();

		Intent intent = new Intent(mActivity, AddFeedActivity.class);

		// �򿪲������ӽ��
		mActivity.startActivityForResult(intent, 1);

	}

	/**
	 * �����Ӷ�����Ϣ
	 * 
	 * @See ��Ӳ������ݣ�{@link #addTestData()}
	 * @param feedItem
	 *            ����˵�FeedItem
	 */
	public void finishedAddItem(FeedItem feedItem) {

		if (null == feedItem) {
			return;
		}

		if (mItemInfoShowedList.size() == 0) {// ������ʱ����ʼ��adapter��ֹ��ָ���쳣
			mListViewAdapter = new FeedPagerListViewAdapter(); // ����ListView������
			mListView.setAdapter(mListViewAdapter);
			mListView.setVisibility(View.VISIBLE);
			mNothingImg.setVisibility(View.INVISIBLE);
		}

		mItemInfoShowedList.add(0, feedItem);// �嵽��һ��

		mListViewAdapter.notifyDataSetChanged();// ˢ��������
		mListView.setSelection(mListView.getHeaderViewsCount());// ��ʾ��һ����HeaderView
	}

	/**
	 * TODO:ɾ���˶δ��� ��Ӳ�������
	 */
	@SuppressWarnings("unused")
	private void addTestData() {

		FeedItem feedItem = new FeedItem();
		feedItem.feedURL = "http://blog.csdn.net/maxwell_nc/rss/list";
		feedItem.picURL = "https://avatars3.githubusercontent.com/u/14196813?v=3&s=1";
		feedItem.baseInfo.title = "�ҵ�GitHub"
				+ new Random().nextInt(Integer.MAX_VALUE);
		feedItem.baseInfo.summary = "������ύ�˺ܶ���룬��ӭ����ҵ�GitHub�ֿ�";
		feedItem.baseInfo.time = new Timestamp(System.currentTimeMillis());
		feedItem.encoding = "utf-8";
		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.addItem(feedItem);

		mItemInfoShowedList.add(0, feedItem);// �嵽��һ��
	}
}
