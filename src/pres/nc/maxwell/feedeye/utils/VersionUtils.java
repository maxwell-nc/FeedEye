package pres.nc.maxwell.feedeye.utils;

import pres.nc.maxwell.feedeye.domain.UpdateInfo;
import pres.nc.maxwell.feedeye.utils.JSONParseUtils.OnParseUpdateInfoListener;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog;
import pres.nc.maxwell.feedeye.view.MainThemeAlertDialog.MainThemeAlertDialogAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 版本工具类
 */
public class VersionUtils {

	/**
	 * 获取包信息
	 * 
	 * @param context
	 *            上下文
	 * @return 包信息
	 */
	public static PackageInfo getPackageInfo(Context context) {
		// 获取版本名称
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// can not reach
			e.printStackTrace();
		}
		return packageInfo;
	}

	/**
	 * 获取版本名称
	 * 
	 * @param context
	 *            上下文
	 * @return 成功则返回版本名称，失败则返回null
	 */
	public static String getVersionName(Context context) {

		PackageInfo packageInfo = getPackageInfo(context);

		if (packageInfo != null) {
			String versionName = packageInfo.versionName;
			return versionName;
		}

		return null;

	}

	/**
	 * 获取版本代码
	 * 
	 * @param context
	 *            上下文
	 * @return 成功返回版本号，失败返回-1
	 */
	public static int getVersionCode(Context context) {

		int versionCode = -1;

		PackageInfo packageInfo = getPackageInfo(context);

		if (packageInfo != null) {
			versionCode = packageInfo.versionCode;
		}

		return versionCode;

	}

	/**
	 * 异步检查更新
	 * 
	 * @param activity
	 *            用于显示提示框的Activitiy
	 */
	public static void checkUpdate(final Activity activity) {

		// 获取当前版本
		final int currentVersionCode = getVersionCode(activity);

		// 获取网络版本
		new JSONParseUtils(new OnParseUpdateInfoListener() {

			@Override
			public void onGetUpdateInfo(final UpdateInfo info) {

				if (currentVersionCode != -1
						&& currentVersionCode < info.versionCode) {

					new MainThemeAlertDialog(activity)
							.setAdapter(new MainThemeAlertDialogAdapter() {

								@Override
								public String getTitle() {
									return "发现新的版本";
								}

								@Override
								public OnClickListener getOnConfirmClickLister(
										final AlertDialog alertDialog) {
									return new OnClickListener() {

										@Override
										public void onClick(View v) {
											alertDialog.dismiss();

											// 打开下载页面
											Intent intent = new Intent(
													Intent.ACTION_VIEW,
													Uri.parse(info.updateUrl));
											activity.startActivity(intent);
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
									TextView updateDescView = new TextView(
											activity);
									updateDescView.setText(info.updateDesc);
									return updateDescView;
								}

								@Override
								public void changeViewAtLast(TextView title,
										FrameLayout container,
										TextView confirmButtom,
										TextView cancelButtom) {

								}
							});

				} else {

					Toast.makeText(activity, "已经是最新版本", Toast.LENGTH_SHORT)
							.show();

				}

			}

			@Override
			public void onGetFailed() {
				Toast.makeText(activity, "请检查网络状况", Toast.LENGTH_SHORT).show();
			}

		}).parseUpdateInfo("http://10.0.3.2:8080/feedeye/version.json");

	}

}
