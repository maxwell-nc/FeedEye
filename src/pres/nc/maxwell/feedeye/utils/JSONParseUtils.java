package pres.nc.maxwell.feedeye.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pres.nc.maxwell.feedeye.domain.DiscoverItem;
import pres.nc.maxwell.feedeye.utils.HTTPUtils.OnConnectListener;

/**
 * JSON工具类
 */
public class JSONParseUtils {

	public JSONParseUtils(OnParseListener onParseListener) {
		this.onParseListener = onParseListener;
	}
	
	private OnParseListener onParseListener;
	
	public interface OnParseListener{
		
		public void OnFinishParse(ArrayList<DiscoverItem> items);

	}
	
	public void parseUrl(String jsonUrl) {

		HTTPUtils httpUtils = new HTTPUtils(new OnConnectListener() {

			private ArrayList<DiscoverItem> items;

			@Override
			public void onSuccess() {
				
				if (onParseListener!=null) {
					onParseListener.OnFinishParse(items);
				}
				
			}

			@Override
			public void onFailure() {

			}

			@Override
			public void onConnect(InputStream inputStream) {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				IOUtils.writeStream(inputStream, byteArrayOutputStream);

				String jsonString = byteArrayOutputStream.toString();
				
				items = new ArrayList<DiscoverItem>();
				
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					
					JSONArray contentArray = jsonObject.getJSONArray("content");
					for (int i = 0; i < contentArray.length(); i++) {
						
						DiscoverItem item = new DiscoverItem();
						
						JSONObject content = contentArray.getJSONObject(i);
						
						item.description = content.getString("description");
						item.encode = content.getString("encode");
						JSONArray keywordsArray = content.getJSONArray("keywords");
						item.key1 = keywordsArray.getString(0);
						item.key2 = keywordsArray.getString(1);
						item.key3 = keywordsArray.getString(2);
						item.key4 = keywordsArray.getString(3);
						item.link = content.getString("link");
						item.name = content.getString("name");
						item.type = content.getString("type");
						
						items.add(item);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});

		httpUtils.connect(jsonUrl, 50000, 200000,
				Executors.newSingleThreadExecutor());

	}

}
