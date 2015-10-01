package pres.nc.maxwell.feedeye.view.pager.child;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.activity.defalut.child.AddFeedActivity;
import pres.nc.maxwell.feedeye.activity.defalut.child.SearchItemActivity;
import pres.nc.maxwell.feedeye.db.FeedItemDAO;
import pres.nc.maxwell.feedeye.domain.FeedItemBean;
import pres.nc.maxwell.feedeye.utils.LogUtils;
import pres.nc.maxwell.feedeye.utils.SystemInfoUtils;
import pres.nc.maxwell.feedeye.utils.TimeUtils;
import pres.nc.maxwell.feedeye.utils.bitmap.BitmapCacheUtils;
import pres.nc.maxwell.feedeye.view.DragRefreshListView;
import pres.nc.maxwell.feedeye.view.DragRefreshListView.OnRefreshListener;
import pres.nc.maxwell.feedeye.view.ThemeAlertDialog;
import pres.nc.maxwell.feedeye.view.ThemeAlertDialog.ThemeAlertDialogAdapter;
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
	private ArrayList<FeedItemBean> mItemInfoUnshowList;

	/**
	 * �����Ѿ���ʾ����Ϣ
	 */
	private ArrayList<FeedItemBean> mItemInfoShowedList;

	/**
	 * һ��չʾ��Item����
	 */
	private final int SHOW_ITEM_COUNT = 20;

	/**
	 * ListViewΪ����ʾ����ʾͼƬ
	 */
	private ImageView mNothingImg;

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

		mItemInfoUnshowList = new ArrayList<FeedItemBean>();
		mItemInfoShowedList = new ArrayList<FeedItemBean>();

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
			mListView.setAllowLoadingMore(false);// û�и������ݣ���ֹ�������ظ���
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
				holder.mItemCount = (ImageView) view
						.findViewById(R.id.iv_item_feed_count);// ����

				view.setTag(holder);

			}

			parseBean(mItemInfoShowedList.get(position), holder);
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

			new Thread() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					mItemInfoShowedList.clear();
					mItemInfoUnshowList.clear();

					FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
					mItemInfoUnshowList = feedItemDAO.queryAllItems();

					// ����Ҫ���ص�Item
					insertMoreItem();

					// �޸�UI���������߳�ִ��
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mListViewAdapter.notifyDataSetChanged();

							if (mItemInfoUnshowList.size() > 0) {
								// �����ټ��ظ���
								mListView.setAllowLoadingMore(true);
							}

							Toast.makeText(mActivity, "ˢ�³ɹ�",
									Toast.LENGTH_SHORT).show();

							mListView.completeRefresh();
						}
					});

				};
			}.start();
		}

		@Override
		public void onLoadingMore() {

			new Thread() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// �ɹ��������������
					final int addCount = insertMoreItem();

					// �޸�UI���������߳�ִ��
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mListViewAdapter.notifyDataSetChanged();

							if (addCount == 0) {
								Toast.makeText(mActivity, "û�и���������",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mActivity,
										"�ɹ�������" + addCount + "������",
										Toast.LENGTH_SHORT).show();
							}
							if (mItemInfoUnshowList.size() <= 0) {
								// ��ֹ�ټ��ظ���
								mListView.setAllowLoadingMore(false);
							}
							mListView.completeRefresh();

						}
					});

				}

			}.start();
		}
	}

	/**
	 * ����feedItemBean����ʾ
	 * 
	 * @param feedItemBean
	 *            ������Ϣ
	 * @param viewHolder
	 *            view����
	 * @return �Ƿ�ɹ�����
	 */
	private boolean parseBean(FeedItemBean feedItemBean, ViewHolder viewHolder) {

		if (feedItemBean == null) {
			return false;
		}

		// ʹ�������������ͼƬ
		new BitmapCacheUtils().displayBitmap(viewHolder.mItemPic,
				feedItemBean.getPicURL(), R.anim.refresh_rotate);
		viewHolder.mItemTitle.setText(feedItemBean.getTitle());
		viewHolder.mItemPreview.setText(feedItemBean.getPreviewContent());
		viewHolder.mItemTime.setText(TimeUtils.timestamp2String(
				feedItemBean.getLastTime(), "HH:mm"));

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

				// TODO������ӵ���¼�

				// ���NaturePositionOnItemClickListener�Ƿ���Ч
				LogUtils.w("FeedPager", "item position:" + position);

			}
		});

		/**
		 * ��������¼�
		 */
		mListView.setOnItemLongClickListener(new ItemLongClickListener());

		// ���ˢ�¼���
		mListView.setOnRefreshListener(new ListViewRefreshListener());

	}

	class ItemLongClickListener implements OnItemLongClickListener {

		/**
		 * ��ʼ��������ť����
		 * 
		 * @param alertView
		 *            View������
		 * @param resIds
		 *            ��Դid���������
		 * @return ����˳���TextView��������
		 */
		public TextView[] initTextButtonView(View alertView, int... resIds) {
			TextView[] textViews = new TextView[resIds.length];
			for (int i = 0; i < resIds.length; i++) {
				textViews[i] = (TextView) alertView.findViewById(resIds[i]);
			}
			return textViews;
		}

		/**
		 * ����¼�
		 */
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {

			View alertView = View.inflate(mActivity,
					R.layout.view_long_click_lv_feed, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

			builder.setView(alertView);

			final AlertDialog alertDialog = builder.show();

			TextView[] textViews = initTextButtonView(alertView,
					R.id.tv_modify, R.id.tv_delete, R.id.tv_cancel);

			// ����޸ı���
			textViews[0].setOnClickListener(new ModifyClickListener(position,
					alertDialog));

			// ���ɾ����Ŀ
			textViews[1].setOnClickListener(new DeleteClickListener(
					alertDialog, position));

			// ���ȡ��
			textViews[2].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDialog.dismiss();// �Ի���ر�
				}

			});

			return true;
		}

		/**
		 * �޸ı�����������
		 */
		class ModifyClickListener implements OnClickListener {

			/**
			 * ��ʾ�ĶԻ���
			 */
			private AlertDialog alertDialog;

			/**
			 * ��Ŀ����
			 */
			private int position;

			/**
			 * ���뵱ǰ��ʾ�ĶԻ����ѡ����Ŀ����
			 * 
			 * @param alertDialog
			 *            ��ʾ�ĶԻ���
			 * @param position
			 *            ��Ŀ����
			 */
			private ModifyClickListener(int position, AlertDialog alertDialog) {
				this.position = position;
				this.alertDialog = alertDialog;
			}

			@Override
			public void onClick(View v) {

				alertDialog.dismiss();// �Ի���ر�

				//��ʾ�޸ĵĶԻ���
				new ThemeAlertDialog(mActivity).setAdapter(new ThemeAlertDialogAdapter() {
					
					/**
					 * �����
					 */
					private EditText mTitleView;

					@Override
					public String getTitle() {
						return null;
					}
					
					//ȷ�ϰ�ť�¼�
					@Override
					public OnClickListener getOnConfirmClickLister(final AlertDialog alertDialog) {
						return new OnClickListener() {

							@Override
							public void onClick(View v) {
								String newTitle = mTitleView.getText().toString();

								if (TextUtils.isEmpty(newTitle)) {//��ʾ����Ϊ��
									mTitleView.startAnimation(AnimationUtils
											.loadAnimation(mActivity,
													R.anim.edit_text_translate));

								} else {
									// �޸ı���
									modifyFeedItemTitle(position, newTitle);
									alertDialog.dismiss();
								}

							}
						};
					}
					
					//ȡ������¼�
					@Override
					public OnClickListener getOnCancelClickLister(final AlertDialog alertDialog) {
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
						View view = View.inflate(mActivity,
								R.layout.alert_dialog_container_modify_title, null);
						mTitleView = (EditText) view.findViewById(R.id.et_title);
						return view;
					}

					@Override
					public void changeViewAtLast(TextView title,
							FrameLayout container, TextView confirmButtom,
							TextView cancelButtom) {
						// ��ȡԭ���ı���
						String orgTitle = ((ViewHolder) mListView.getChildAt(position)
								.getTag()).mItemTitle.getText().toString();

						mTitleView.setText(orgTitle);
					}
					
				
				});


			}
		}

		/**
		 * ɾ�����������
		 */
		class DeleteClickListener implements OnClickListener {

			/**
			 * ��ʾ�ĶԻ���
			 */
			private AlertDialog alertDialog;

			/**
			 * ��Ŀ����
			 */
			private int position;

			/**
			 * ���뵱ǰ��ʾ�ĶԻ����ѡ����Ŀ����
			 * 
			 * @param alertDialog
			 *            ��ʾ�ĶԻ���
			 * @param position
			 *            ��Ŀ����
			 */
			public DeleteClickListener(AlertDialog alertDialog, int position) {
				this.alertDialog = alertDialog;
				this.position = position;
			}

			@Override
			public void onClick(View v) {

				alertDialog.dismiss();// �Ի���ر�

				// �ٴ�ȷ��ɾ��
				new ThemeAlertDialog(mActivity)
						.setAdapter(new ThemeAlertDialogAdapter() {

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

							//��ʾ��ɾ���ı���
							@Override
							public View getContentView() {

								View view = View
										.inflate(
												mActivity,
												R.layout.alert_dialog_container_delete_title,
												null);
								
								//��ȡԭ���ı���
								String deleteItemTitle = mItemInfoShowedList
										.get(position
												- mListView
														.getHeaderViewsCount())
										.getTitle();
								
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

		FeedItemBean feedItemBean = mItemInfoShowedList.get(dbPosition);

		feedItemBean.setTitle(newTitle);

		// �����ݿ��и���
		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.updateItem(feedItemBean);

		// ����ȫ���½���
		if (mListView.getFirstVisiblePosition() <= position
				&& position <= mListView.getLastVisiblePosition()) {
			((ViewHolder) mListView.getChildAt(position).getTag()).mItemTitle
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
	 */
	private void addNewFeedItem() {

		if (mItemInfoShowedList.size() == 0) {// ������ʱ����ʼ��adapter��ֹ��ָ���쳣
			mListViewAdapter = new FeedPagerListViewAdapter(); // ����ListView������
			mListView.setAdapter(mListViewAdapter);
			mListView.setVisibility(View.VISIBLE);
			mNothingImg.setVisibility(View.INVISIBLE);
		}

		closePopupWindow();

		Intent intent = new Intent(mActivity, AddFeedActivity.class);

		// �򿪲������ӽ��
		mActivity.startActivityForResult(intent, 1);

	}

	/**
	 * �����Ӷ�����Ϣ
	 * 
	 * @See ��Ӳ������ݣ�{@link #addTestData()}
	 * @param feedItemBean
	 *            ����˵�����bean
	 */
	public void finishedAddItem(FeedItemBean feedItemBean) {

		// �����������
		// addTestData();

		mItemInfoShowedList.add(0, feedItemBean);// �嵽��һ��

		mListViewAdapter.notifyDataSetChanged();// ˢ��������
		mListView.setSelection(mListView.getHeaderViewsCount());// ��ʾ��һ����HeaderView
	}

	/**
	 * ��Ӳ�������
	 */
	@SuppressWarnings("unused")
	private void addTestData() {

		FeedItemBean feedItemBean = new FeedItemBean();
		feedItemBean.setFeedURL("http://blog.csdn.net/maxwell_nc/rss/list");
		feedItemBean
				.setPicURL("https://avatars3.githubusercontent.com/u/14196813?v=3&s=1");
		feedItemBean.setTitle("�ҵ�GitHub"
				+ new Random().nextInt(Integer.MAX_VALUE));
		feedItemBean.setPreviewContent("������ύ�˺ܶ���룬��ӭ����ҵ�GitHub�ֿ�");
		feedItemBean.setLastTime(new Timestamp(System.currentTimeMillis()));

		FeedItemDAO feedItemDAO = new FeedItemDAO(mActivity);
		feedItemDAO.addItem(feedItemBean);

		mItemInfoShowedList.add(0, feedItemBean);// �嵽��һ��
	}

}
