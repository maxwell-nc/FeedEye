package pres.nc.maxwell.feedeye.activity;

import pres.nc.maxwell.feedeye.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AddFeedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_feed);
		super.onCreate(savedInstanceState);
	}
	
}
