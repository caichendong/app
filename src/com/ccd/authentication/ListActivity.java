package com.ccd.authentication;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListActivity extends Activity {

	private ListView list;
	public MobileServiceJsonTable mMessage;
	ArrayList<HashMap<String, Object>> listitem1;
	SimpleAdapter listitemAdapter;
	Context mcContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(com.ccd.authentication.R.layout.activity_list);
		AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
		AuthService authService = myApp.getAuthService();
		listitem1 = new ArrayList<HashMap<String, Object>>();

		mMessage = authService.mClient.getTable("Message");

		list = (ListView) findViewById(com.ccd.authentication.R.id.ListView01);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				
				Intent intent = new Intent( getBaseContext(), ReadMessageActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				String string = ((HashMap<String, Object>)parent.getItemAtPosition(position)).get("ID").toString().replace('"', '\0').trim();
				intent.putExtra("ID",string);
				startActivity(intent);
			}
		});
		
		listitemAdapter = new SimpleAdapter(getBaseContext(), listitem1,
				com.ccd.authentication.R.layout.list_item, new String[] {
						"ItemImage", "ItemTitle", "ItemContent" }, new int[] {
						com.ccd.authentication.R.id.ItemImage,
						com.ccd.authentication.R.id.ItemTitle,
						com.ccd.authentication.R.id.ItemContent });
		
		
		mMessage.where().select().execute(new TableJsonQueryCallback() {

			@Override
			public void onCompleted(JsonElement arg0, int arg1, Exception arg2,
					ServiceFilterResponse arg3) {
				// TODO Auto-generated method stub
				JsonArray listitem = arg0.getAsJsonArray();
				for (int i = 0; i < listitem.size(); i++) {

					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemImage",
							com.ccd.authentication.R.drawable.ic_launcher);
					map.put("ItemTitle",
							listitem.get(i).getAsJsonObject().get("title")
									.toString().replace('"', '\0'));
					map.put("ItemContent", listitem.get(i).getAsJsonObject()
							.get("content").toString().replace('"', '\0'));
					map.put("ID", listitem.get(i).getAsJsonObject().get("id").toString().replace('"', '\0'));
					listitem1.add(map);

				}
				list.setAdapter(listitemAdapter);
			}
		});
		//test data 
		// ArrayList<HashMap<String, Object>> listitem = new
		// ArrayList<HashMap<String,Object>>();
		//
		// for(int i = 0; i <; i++)
		// {
		// if(i==0){
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("ItemImage", com.ccd.authentication.R.drawable.ic_launcher);
		// map.put("ItemTitle", "Hello world");
		// map.put("ItemContent", "This is ccd project");
		// listitem.add(map);
		//
		// }
		// if(i==1){
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("ItemImage", com.ccd.authentication.R.drawable.ic_launcher);
		// map.put("ItemTitile", "Hello CCD");
		// map.put("ItemContent", "CCD is coooooooool");
		// listitem.add(map);
		//
		// }if(i==2){
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("ItemImage", com.ccd.authentication.R.drawable.ic_launcher);
		// map.put("ItemTitile", "Hello BYS");
		// map.put("ItemContent", "haha");
		// listitem.add(map);
		//
		// }
		// }

		

	}

}
