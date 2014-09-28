package com.ccd.authentication;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ReadMessageActivity extends Activity {

	public MobileServiceJsonTable mMessage;
	public String mID;
	TextView title;
	TextView content;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_message);
		

		AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
		AuthService authService = myApp.getAuthService();
		mMessage = authService.mClient.getTable("Message");
		Intent intent = getIntent();
		mID = intent.getStringExtra("ID").replace('"', '\0').trim();
		
		mMessage.lookUp(mID, new TableJsonOperationCallback() {
			
			@Override
			public void onCompleted(JsonObject arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				title = (TextView)findViewById(R.id.textView_messageTitle);
				content = (TextView)findViewById(R.id.textView_messageContent);
				title.setText(arg0.get("title").toString().replace('"', '\0'));
				content.setText(arg0.get("content").toString().replace('"', '\0'));
			}
		});
	}
}
