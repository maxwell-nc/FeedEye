package pres.nc.maxwell.feedeye.view.pager.child;

import pres.nc.maxwell.feedeye.R;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog.MainThemeAlertDialogAdapter;
import pres.nc.maxwell.feedeye.view.pager.BasePager;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

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
		
		//关于
		mBtnAbout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new MainThemeAlertDialog(mActivity).setAdapter(new MainThemeAlertDialogAdapter() {
					
					@Override
					public String getTitle() {
						return "关于...";
					}
					
					@Override
					public OnClickListener getOnConfirmClickLister(final AlertDialog alertDialog) {
						return new OnClickListener() {
							@Override
							public void onClick(View v) {
								alertDialog.dismiss();
							}
						};
					}
					
					@Override
					public OnClickListener getOnCancelClickLister(AlertDialog alertDialog) {
						return null;
					}
					
					@Override
					public View getContentView() {
						return View.inflate(mActivity, R.layout.view_about, null);
					}
					
					@Override
					public void changeViewAtLast(TextView title, FrameLayout container,
							TextView confirmButtom, TextView cancelButtom) {
						//隐藏取消按钮
						cancelButtom.setVisibility(View.GONE);
						
					}
					
				});
				
			}
			
		});
		
		//退出应用
		mBtnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new MainThemeAlertDialog(mActivity).setAdapter(new MainThemeAlertDialogAdapter() {
					
					@Override
					public String getTitle() {
						return "是否退出？";
					}
					
					@Override
					public OnClickListener getOnConfirmClickLister(final AlertDialog alertDialog) {
						
						return new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								alertDialog.dismiss();
								System.exit(0);//退出
							}
						};
						
					}
					
					@Override
					public OnClickListener getOnCancelClickLister(final AlertDialog alertDialog) {
					
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
					public void changeViewAtLast(TextView title, FrameLayout container,
							TextView confirmButtom, TextView cancelButtom) {
					}
					
				});
				
			}
			
		});
		
		getLoadingBarView().setVisibility(View.INVISIBLE);
	}

}
