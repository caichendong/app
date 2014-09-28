package com.ccd.authentication;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LeaveMessageActivity extends Activity {
	public MobileServiceJsonTable mMessage;
	private Button mAdd;
	private EditText mTitle;
	private EditText mContent;
	private AuthService authService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave_message);
		AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
		authService = myApp.getAuthService();
		
		mMessage =  authService.mClient.getTable("Message");
		mAdd = (Button) findViewById(R.id.button_AddMessage);
		mTitle = (EditText) findViewById(R.id.editText_title);
		mContent = (EditText) findViewById(R.id.editText_content);
		
		
		mAdd.setOnClickListener(mclick);
		
	}
	View.OnClickListener mclick = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			JsonObject message = new JsonObject();
			message.addProperty("title", mTitle.getText().toString());
			message.addProperty("content", mContent.getText().toString());
			message.addProperty("createBy",authService.getUserId() );
			message.addProperty("date", java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
			mMessage.insert(message, new TableJsonOperationCallback() {
				
				@Override
				public void onCompleted(JsonObject arg0, Exception arg1,
						ServiceFilterResponse arg2) {
					// TODO Auto-generated method stub
					if (arg1 !=null)
					{
						Toast.makeText(getBaseContext(), arg1.getMessage(), Toast.LENGTH_LONG).show();
						
					}
				}
			});
		}
	};
}
