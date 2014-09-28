/*
 Copyright 2013 Microsoft Corp
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ccd.authentication;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoggedInActivity extends BaseActivity {
	
	private final String TAG = "LoggedInActivity";
	private TextView mLblUserIdValue;
	private TextView mLblUsernameValue;
	private Button mBtnLogout;
	private Button mBtnList;
	private Button mBtnAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logged_in);

		//get UI elements
		mLblUserIdValue = (TextView) findViewById(R.id.lblUserIdValue);
		mLblUsernameValue = (TextView) findViewById(R.id.lblUsernameValue);
		mBtnLogout = (Button) findViewById(R.id.btnLogout);
		mBtnList = (Button) findViewById(R.id.button_List);
		mBtnAdd = (Button)findViewById(R.id.button_Add);
		
		//Set click listeners
		mBtnLogout.setOnClickListener(logoutClickListener);
		mBtnList.setOnClickListener(listClickListener);
		mBtnAdd.setOnClickListener(addClickListener);
		
		AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
		AuthService authService = myApp.getAuthService();
		
		mLblUserIdValue.setText(authService.getUserId());
		
		//Fetch auth data (the username) on load
		authService.getAuthData(new TableJsonQueryCallback() {			
			@Override
			public void onCompleted(JsonElement result, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonArray results = result.getAsJsonArray();
					JsonElement item = results.get(0);
					mLblUsernameValue.setText(item.getAsJsonObject().getAsJsonPrimitive("UserName").getAsString());
				} else {
					Log.e(TAG, "There was an exception getting auth data: " + exception.getMessage());
				}
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logged_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		}
		return super.onOptionsItemSelected(item);
	}
	
	View.OnClickListener logoutClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			//Just trigger a logout if this is clicked
			mAuthService.logout(true);
		}
	};
	
	View.OnClickListener listClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(),ListActivity.class);
			startActivity(intent);
			
		}
	};
	
	View.OnClickListener addClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(),LeaveMessageActivity.class);
			startActivity(intent);
			
		}
	};
	
}
