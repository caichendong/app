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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.StatusLine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

public class AuthService {
	public MobileServiceClient mClient;
	private MobileServiceJsonTable mTableAccounts;
	private MobileServiceJsonTable mTableAuthData;
	private Context mContext;
	private final String TAG = "AuthService";

	public AuthService(Context context) {
		mContext = context;
		try {
			mClient = new MobileServiceClient(
					"https://ccdphone.preview.azure-mobile-preview.net/",
					"wZVAdTeUhOrnopRgsjjmQOOgmKwKbs88", mContext)
					.withFilter(new MyServiceFilter());
			mTableAccounts = mClient.getTable("Accounts");
			mTableAuthData = mClient.getTable("AuthData");
		} catch (MalformedURLException e) {
			Log.e(TAG,
					"There was an error creating the Mobile Service.  Verify the URL");
		}
	}

	public void setContext(Context context) {
		mClient.setContext(context);
	}

	public String getUserId() {
		return mClient.getCurrentUser().getUserId();
	}


	public void login(String username, String password,
			TableJsonOperationCallback callback) {
		JsonObject customUser = new JsonObject();
		customUser.addProperty("username", username);
		customUser.addProperty("password", password);

		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("login", "true"));

		mTableAccounts.insert(customUser, parameters, callback);
	}

	public void getAuthData(TableJsonQueryCallback callback) {
		mTableAuthData.where().execute(callback);
	}


	public boolean isUserAuthenticated() {
		SharedPreferences settings = mContext.getSharedPreferences("UserData",
				0);
		if (settings != null) {
			String userId = settings.getString("userid", null);
			String token = settings.getString("token", null);
			if (userId != null && !userId.equals("")) {
				setUserData(userId, token);
				return true;
			}
		}
		return false;
	}


	public void setUserData(String userId, String token) {
		MobileServiceUser user = new MobileServiceUser(userId);
		user.setAuthenticationToken(token);
		mClient.setCurrentUser(user);
	}


	public void setUserAndSaveData(JsonObject jsonObject) {
		String userId = jsonObject.getAsJsonPrimitive("userId").getAsString();
		String token = jsonObject.getAsJsonPrimitive("token").getAsString();
		setUserData(userId, token);
		saveUserData();
	}


	public void saveUserData() {
		SharedPreferences settings = mContext.getSharedPreferences("UserData",
				0);
		SharedPreferences.Editor preferencesEditor = settings.edit();
		preferencesEditor.putString("userid", mClient.getCurrentUser()
				.getUserId());
		preferencesEditor.putString("token", mClient.getCurrentUser()
				.getAuthenticationToken());
		preferencesEditor.commit();
	}

	public void registerUser(String username, String password, String confirm,
			String email, TableJsonOperationCallback callback) {
		JsonObject newUser = new JsonObject();
		newUser.addProperty("username", username);
		newUser.addProperty("password", password);
		newUser.addProperty("email", email);

		mTableAccounts.insert(newUser, callback);
	}


	public void logout(boolean shouldRedirectToLogin) {
		// Clear the cookies so they won't auto login to a provider again
		CookieSyncManager.createInstance(mContext);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		// Clear the user id and token from the shared preferences
		SharedPreferences settings = mContext.getSharedPreferences("UserData",
				0);
		SharedPreferences.Editor preferencesEditor = settings.edit();
		preferencesEditor.clear();
		preferencesEditor.commit();
		// Clear the user and return to the auth activity
		mClient.logout();
		// Take the user back to the auth activity to relogin if requested
		if (shouldRedirectToLogin) {
			Intent logoutIntent = new Intent(mContext,
					CustomLoginActivity.class);
			logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(logoutIntent);
		}
	}



	private class MyServiceFilter implements ServiceFilter {

		@Override
		public void handleRequest(final ServiceFilterRequest request,
				final NextServiceFilterCallback nextServiceFilterCallback,
				final ServiceFilterResponseCallback responseCallback) {

			nextServiceFilterCallback.onNext(request,
					new ServiceFilterResponseCallback() {
						@Override
						public void onResponse(final ServiceFilterResponse response,
								Exception exception) {
							if (exception != null) {
								Log.e(TAG,
										"MyServiceFilter onResponse Exception: "
												+ exception.getMessage());
							}
							
							StatusLine status = response.getStatus();
							int statusCode = status.getStatusCode();

							if (statusCode == 401) {
								// Log the user out but don't send them to the
								// login page
								logout(false);
								AuthenticationApplication myApp = (AuthenticationApplication) mContext;
								Activity currentActivity = myApp.getCurrentActivity();
								mClient.setContext(currentActivity);
								
								currentActivity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mContext, response.getContent(), Toast.LENGTH_SHORT).show();
									}
								
									});
								
								
								// Log them out and proceed with the response
								responseCallback
										.onResponse(response, exception);

							} else {//

								responseCallback
										.onResponse(response, exception);
							}
						}
					});
		}
	}
}
