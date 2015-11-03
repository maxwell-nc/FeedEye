package pres.nc.maxwell.feedeye.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pres.nc.maxwell.feedeye.domain.DiscoverItem;
import pres.nc.maxwell.feedeye.domain.UpdateInfo;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;

/**
 * JSON工具类
 */
public class JSONParseUtils {

	/**
	 * 传入解析监听器
	 * 
	 * @param basicJsonParseListener
	 *            基本JSON解析监听器
	 */
	public JSONParseUtils(BasicJsonParseListener basicJsonParseListener) {
		this.basicJsonParseListener = basicJsonParseListener;
	}

	/**
	 * 传入解析监听器
	 * 
	 * @param onParseDiscoverItemListener
	 *            解析发现标签监听器
	 */
	public JSONParseUtils(
			OnParseDiscoverItemListener onParseDiscoverItemListener) {
		this.onParseDiscoverItemListener = onParseDiscoverItemListener;
	}

	/**
	 * 传入解析监听器
	 * 
	 * @param onParseUpdateInfoListener
	 *            解析更新信息监听器
	 */
	public JSONParseUtils(OnParseUpdateInfoListener onParseUpdateInfoListener) {
		this.onParseUpdateInfoListener = onParseUpdateInfoListener;
	}

	/**
	 * 基本JSON解析监听器
	 */
	private BasicJsonParseListener basicJsonParseListener;

	/**
	 * 解析发现标签监听器
	 */
	private OnParseDiscoverItemListener onParseDiscoverItemListener;

	/**
	 * 解析更新信息监听器
	 */
	private OnParseUpdateInfoListener onParseUpdateInfoListener;

	/**
	 * 基本JSON解析监听器
	 */
	public interface BasicJsonParseListener {

		/**
		 * 获取到JSON文本时
		 * 
		 * @param jsonString
		 *            JSON文本
		 */
		public void onGetJsonString(String jsonString);

		/**
		 * 成功读取
		 * 
		 * @param jsonString
		 *            JSON文本
		 */
		public void onSuccess(String jsonString);

		/**
		 * 读取失败
		 */
		public void onFailure();

	}

	/**
	 * 解析发现标签监听器
	 */
	public interface OnParseDiscoverItemListener {

		/**
		 * 完成解析
		 * 
		 * @param items
		 *            解析得到的数据
		 */
		public void OnFinishParse(ArrayList<DiscoverItem> items);

		/**
		 * 解析失败或中断
		 * 
		 * @param cacheItems
		 *            缓存中的数据
		 */
		public void onFailed(ArrayList<DiscoverItem> cacheItems);

	}

	/**
	 * 解析更新信息的监听器
	 */
	public interface OnParseUpdateInfoListener {

		/**
		 * 成功获取更新信息，运行在主线程
		 * 
		 * @param info
		 *            更新信息
		 */
		public void onGetUpdateInfo(UpdateInfo info);

		/**
		 * 获取失败
		 */
		public void onGetFailed();
	}

	/**
	 * 解析JSON
	 * 
	 * @param jsonUrl
	 *            JSON地址
	 */
	public void parseUrl(String jsonUrl) {

		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {

			private String jsonString;

			@Override
			public void onSuccess() {
				if (basicJsonParseListener != null) {
					basicJsonParseListener.onSuccess(jsonString);
				}
			}

			@Override
			public void onFailure() {
				if (basicJsonParseListener != null) {
					basicJsonParseListener.onFailure();
				}
			}

			@Override
			public void onConnect(InputStream inputStream) {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				IOUtils.writeStream(inputStream, byteArrayOutputStream);

				jsonString = byteArrayOutputStream.toString();

				if (basicJsonParseListener != null) {
					basicJsonParseListener.onGetJsonString(jsonString);
				}
			}

		});

		httpUtils.connect(jsonUrl, 50000, 100000,
				Executors.newSingleThreadExecutor());
	}

	/**
	 * 解析发现标签的网络JSON
	 * 
	 * @param jsonUrl
	 *            JSON地址
	 */
	public void parseDiscoverItem(String jsonUrl) {

		basicJsonParseListener = new BasicJsonParseListener() {

			private ArrayList<DiscoverItem> items;
			private ArrayList<DiscoverItem> cacheItems;

			@Override
			public void onSuccess(String jsonString) {
				if (onParseDiscoverItemListener != null) {
					onParseDiscoverItemListener.OnFinishParse(items);
				}
			}

			@Override
			public void onFailure() {
				cacheItems = putJSONInItemsList(getCache());

				if (onParseDiscoverItemListener != null) {
					onParseDiscoverItemListener.onFailed(cacheItems);
				}
			}

			@Override
			public void onGetJsonString(String jsonString) {
				setCache(jsonString);

				items = putJSONInItemsList(jsonString);
			}

		};

		parseUrl(jsonUrl);

	}
	
	/**
	 * 解析更新信息
	 * 
	 * @param jsonUrl
	 *            JSON地址
	 */
	public void parseUpdateInfo(String jsonUrl) {
		//TODO：
		basicJsonParseListener = new BasicJsonParseListener() {
			
			private UpdateInfo info = new UpdateInfo();
			
			@Override
			public void onSuccess(String jsonString) {
				if (onParseUpdateInfoListener != null) {
					onParseUpdateInfoListener.onGetUpdateInfo(info);
				}
			}
			
			@Override
			public void onFailure() {
				if (onParseUpdateInfoListener != null) {
					onParseUpdateInfoListener.onGetFailed();
				}
			}
			
			@Override
			public void onGetJsonString(String jsonString) {
				try {
					
					JSONObject jsonObject = new JSONObject(jsonString);
					info.versionCode = jsonObject.getInt("versionCode");
					info.updateDesc = jsonObject.getString("updateDesc");
					info.updateUrl = jsonObject.getString("updateUrl");
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		};
		
		parseUrl(jsonUrl);
		
	}

	
	
	/**
	 * 转换JSON文本为DiscoverItem集合
	 * 
	 * @param jsonString
	 *            JSON文本
	 * @return DiscoverItem集合
	 */
	private ArrayList<DiscoverItem> putJSONInItemsList(String jsonString) {

		ArrayList<DiscoverItem> items = new ArrayList<DiscoverItem>();

		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			JSONArray contentArray = jsonObject.getJSONArray("content");
			for (int i = 0; i < contentArray.length(); i++) {

				DiscoverItem item = new DiscoverItem();

				JSONObject content = contentArray.getJSONObject(i);

				item.description = content.getString("description");
				item.encode = content.getString("encode");
				JSONArray keywordsArray = content.getJSONArray("keywords");

				item.labels = new String[4];
				item.labels[0] = keywordsArray.getString(0);
				item.labels[1] = keywordsArray.getString(1);
				item.labels[2] = keywordsArray.getString(2);
				item.labels[3] = keywordsArray.getString(3);

				JSONArray colorMarksArray = content.getJSONArray("colorMarks");
				item.colorMarks = new int[4];
				item.colorMarks[0] = colorMarksArray.getInt(0);
				item.colorMarks[1] = colorMarksArray.getInt(1);
				item.colorMarks[2] = colorMarksArray.getInt(2);
				item.colorMarks[3] = colorMarksArray.getInt(3);

				item.link = content.getString("link");
				item.name = content.getString("name");
				item.type = content.getInt("type");

				items.add(item);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return items;
	}

	/**
	 * 设置缓存
	 * 
	 * @param jsonString
	 *            JSON文本
	 */
	private void setCache(String jsonString) {

		File file = IOUtils.getFileInSdcard("/FeedEye/DiscoverCache",
				"discover.json");

		BufferedWriter bufferedWriter = null;

		try {
			FileWriter fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(jsonString);
			bufferedWriter.flush();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}

	}

	/**
	 * 获取缓存
	 * 
	 * @return 缓存JSON
	 */
	private String getCache() {

		File file = IOUtils.getFileInSdcard("/FeedEye/DiscoverCache",
				"discover.json");

		String JsonString = null;
		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader(file));

			String line = "";
			StringBuffer buffer = new StringBuffer();

			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			JsonString = buffer.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return JsonString;

	}

}
