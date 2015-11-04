package pres.nc.maxwell.feedeye.view.pager.child;

import java.io.File;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.utils.AppSettingUtils;
import pres.nc.maxwell.feedeye.utils.IOUtils;
import pres.nc.maxwell.feedeye.utils.SystemUtils;
import pres.nc.maxwell.feedeye.utils.VersionUtils;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog.MainThemeAlertDialogAdapter;
import pres.nc.maxwell.feedeye.view.MainThemeOnClickDialog;
import pres.nc.maxwell.feedeye.view.MainThemeOnClickDialog.DialogDataAdapter;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 设置页面的Pager
 */
public class SettingPager extends BasePager {

	/**
	 * 无图模式按钮
	 */
	private CheckBox mBtnNoImg;

	/**
	 * 清理缓存按钮
	 */
	private CheckBox mBtnClean;

	/**
	 * 夜间模式按钮
	 */
	private CheckBox mBtnDayNight;

	/**
	 * 反馈按钮
	 */
	private TextView mBtnFeedBack;

	/**
	 * 推荐给好友按钮
	 */
	private TextView mBtnRecommend;

	/**
	 * 检查更新按钮
	 */
	private TextView mBtnUpdate;

	/**
	 * 关于按钮
	 */
	private TextView mBtnAbout;

	/**
	 * 退出按钮
	 */
	private TextView mBtnExit;

	public SettingPager(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected void initView() {
		super.initView();
		mTitle.setText("设置");

		mViewContent = setContainerContent(R.layout.pager_setting);

		mBtnNoImg = (CheckBox) mViewContent.findViewById(R.id.cb_no_img);

		// 检查是否开启无图模式
		String noNetworkImg = AppSettingUtils.get(mActivity,
				AppSettingUtils.KEY_NO_IMAGE_SETTING, "false");
		if ("false".equals(noNetworkImg)) {
			mBtnNoImg.setChecked(false);// 加载网络图片
		} else {
			mBtnNoImg.setChecked(true);// 不加载网络图片
		}

		mBtnClean = (CheckBox) mViewContent.findViewById(R.id.cb_clean);
		mBtnDayNight = (CheckBox) mViewContent.findViewById(R.id.cb_day_night);

		mBtnFeedBack = (TextView) mViewContent.findViewById(R.id.tv_feedback);
		mBtnRecommend = (TextView) mViewContent.findViewById(R.id.tv_recommend);
		mBtnUpdate = (TextView) mViewContent.findViewById(R.id.tv_update);
		mBtnAbout = (TextView) mViewContent.findViewById(R.id.tv_about);
		mBtnExit = (TextView) mViewContent.findViewById(R.id.tv_exit);

	}

	@Override
	protected void initData() {
		super.initData();

		// 清理缓存
		mBtnClean.setOnClickListener(new CleanCacheOnClickListener());

		// 无图模式
		mBtnNoImg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				AppSettingUtils.set(mActivity,
						AppSettingUtils.KEY_NO_IMAGE_SETTING,
						String.valueOf(isChecked));

				if (isChecked) {
					Toast.makeText(mActivity, "已关闭加载网络图片", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(mActivity, "已开启加载网络图片", Toast.LENGTH_SHORT)
							.show();
				}
			}

		});

		// 更新
		mBtnUpdate.setOnClickListener(new UpdateOnClickListener());

		// 反馈
		mBtnFeedBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 打开项目页面
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("https://github.com/maxwell-nc/FeedEye"));
				mActivity.startActivity(intent);

			}

		});

		// 推荐给好友
		mBtnRecommend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SystemUtils
						.startShareIntentActivity(
								mActivity,
								"我发现了一个好玩的应用，它的名字叫做简约(FeedEye)，赶紧来下载吧！地址是：https://github.com/maxwell-nc/FeedEye");
			}

		});

		// 关于
		mBtnAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new MainThemeAlertDialog(mActivity)
						.setAdapter(new MainThemeAlertDialogAdapter() {

							@Override
							public String getTitle() {
								return "关于...";
							}

							@Override
							public OnClickListener getOnConfirmClickLister(
									final AlertDialog alertDialog) {
								return new OnClickListener() {
									@Override
									public void onClick(View v) {
										alertDialog.dismiss();
									}
								};
							}

							@Override
							public OnClickListener getOnCancelClickLister(
									AlertDialog alertDialog) {
								return null;
							}

							@Override
							public View getContentView() {
								return View.inflate(mActivity,
										R.layout.view_about, null);
							}

							@Override
							public void changeViewAtLast(TextView title,
									FrameLayout container,
									TextView confirmButtom,
									TextView cancelButtom) {
								// 隐藏取消按钮
								cancelButtom.setVisibility(View.GONE);

							}

						});

			}

		});

		// 退出应用
		mBtnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new MainThemeAlertDialog(mActivity)
						.setAdapter(new MainThemeAlertDialogAdapter() {

							@Override
							public String getTitle() {
								return "是否退出？";
							}

							@Override
							public OnClickListener getOnConfirmClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										alertDialog.dismiss();
										System.exit(0);// 退出
									}
								};

							}

							@Override
							public OnClickListener getOnCancelClickLister(
									final AlertDialog alertDialog) {

								return new OnClickListener() {

									@Override
									public void onClick(View v) {
										alertDialog.dismiss();
									}
								};

							}

							@Override
							public View getContentView() {
								return null;
							}

							@Override
							public void changeViewAtLast(TextView title,
									FrameLayout container,
									TextView confirmButtom,
									TextView cancelButtom) {
							}

						});

			}

		});

		getLoadingBarView().setVisibility(View.INVISIBLE);
	}

	/**
	 * 清理缓存点击监听器
	 */
	private class CleanCacheOnClickListener implements OnClickListener {

		private class CleanCacheDialogAdapter
				implements
					MainThemeAlertDialogAdapter {

			private File file;

			public CleanCacheDialogAdapter(String dir) {
				file = IOUtils.getDirInSdcard(dir);
			}

			@Override
			public String getTitle() {
				return "清理缓存";
			}

			@Override
			public OnClickListener getOnConfirmClickLister(
					final AlertDialog alertDialog) {

				return new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
						if (IOUtils.removeDir(file)) {
							Toast.makeText(mActivity, "成功删除缓存",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mActivity, "删除缓存失败",
									Toast.LENGTH_SHORT).show();
						}
					}

				};
			}

			@Override
			public OnClickListener getOnCancelClickLister(
					final AlertDialog alertDialog) {

				return new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
					}

				};
			}

			@Override
			public View getContentView() {
				TextView tv = new TextView(mActivity);
				tv.setText("删除缓存？（" + file.listFiles().length + "个文件）");
				tv.setTextColor(mActivity.getResources().getColor(R.color.red));
				return tv;
			}

			@Override
			public void changeViewAtLast(TextView title, FrameLayout container,
					TextView confirmButtom, TextView cancelButtom) {
			}

		}

		@Override
		public void onClick(View v) {

			new MainThemeOnClickDialog(mActivity, new DialogDataAdapter() {

				@Override
				public OnClickListener[] getItemOnClickListeners(
						final AlertDialog alertDialog) {
					// TODO Auto-generated method stub

					OnClickListener[] listeners = {new OnClickListener() {// 清理内容缓存

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();
									new MainThemeAlertDialog(mActivity)
											.setAdapter(new CleanCacheDialogAdapter(
													"/FeedEye/DetailCache"));
								}

							}, new OnClickListener() {// 清理图片缓存

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();
									new MainThemeAlertDialog(mActivity)
											.setAdapter(new CleanCacheDialogAdapter(
													"/FeedEye/ImgCache"));
								}

							}, new OnClickListener() {// 清理错误日志

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();
									new MainThemeAlertDialog(mActivity)
											.setAdapter(new CleanCacheDialogAdapter(
													"/FeedEye/ErrLog"));
								}

							}};

					return listeners;
				}

				@Override
				public int[] getItemNames() {
					int[] ids = {R.string.clean_content_cache,
							R.string.clean_img_cache, R.string.clean_err_log};
					return ids;
				}

			}).show();

		}
	}

	/**
	 * 点击检查更新的监听器
	 */
	private class UpdateOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			new MainThemeOnClickDialog(mActivity, new DialogDataAdapter() {

				private String autoUpdate;

				@Override
				public OnClickListener[] getItemOnClickListeners(
						final AlertDialog alertDialog) {

					OnClickListener[] listeners = {new OnClickListener() {// 立即更新

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();
									Toast.makeText(mActivity, "检查更新",
											Toast.LENGTH_SHORT).show();
									VersionUtils.checkUpdate(mActivity);
								}

							}, new OnClickListener() {// 关闭或开启更新

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();

									if ("on".equals(autoUpdate)) {
										AppSettingUtils
												.set(mActivity,
														AppSettingUtils.KEY_UPDATE_SETTING,
														"off");
										Toast.makeText(mActivity, "已关闭自动检查更新",
												Toast.LENGTH_SHORT).show();
									} else {
										AppSettingUtils
												.set(mActivity,
														AppSettingUtils.KEY_UPDATE_SETTING,
														"on");
										Toast.makeText(mActivity, "已开启自动检查更新",
												Toast.LENGTH_SHORT).show();
									}

								}

							}};

					return listeners;
				}

				@Override
				public int[] getItemNames() {
					autoUpdate = AppSettingUtils.get(mActivity,
							AppSettingUtils.KEY_UPDATE_SETTING, "on");

					if ("on".equals(autoUpdate)) {// 已开启自动更新，显示关闭
						int[] ids = {R.string.update_now,
								R.string.close_auto_update};
						return ids;
					} else {// 已关闭自动更新，显示开启
						int[] ids = {R.string.update_now,
								R.string.open_auto_update};
						return ids;
					}

				}

			}).show();

		}
	}

}
