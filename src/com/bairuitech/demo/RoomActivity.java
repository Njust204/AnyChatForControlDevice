package com.bairuitech.demo;

import java.io.OutputStream;
import java.util.ArrayList;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatTextMsgEvent;

import android.R.color;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class RoomActivity extends Activity implements AnyChatBaseEvent, AnyChatTextMsgEvent {
	private LinearLayout fullLayout;
	private LinearLayout mainLayout;

	private ListView userListView;
	private BaseAdapter userListAdapter;

	public AnyChatCoreSDK anychat;

	private ArrayList<String> idList = new ArrayList<String>();
	private ArrayList<String> userList = new ArrayList<String>();

	private String addressStr = null;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userListAdapter = new UserListListAdapter(this);
		InitialSDK();
		Intent intent = getIntent();
		intent.getIntExtra("RoomID", 0);
		addressStr = intent.getStringExtra("addressStr");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InitialLayout();

	}

	private void InitialSDK() {
		anychat = new AnyChatCoreSDK();
		anychat.SetBaseEvent(this);
		anychat.SetTextMessageEvent(this);
	}

	private void InitialLayout() {
		this.setTitle("视频聊天");

		fullLayout = new LinearLayout(this);
		// fullLayout.setBackgroundColor(Color.WHITE);
		fullLayout.setOrientation(LinearLayout.VERTICAL);
		fullLayout.setOnTouchListener(touchListener);

		mainLayout = new LinearLayout(this);
		mainLayout.setBackgroundColor(Color.TRANSPARENT);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setOnTouchListener(touchListener);

		LinearLayout sendLayout = new LinearLayout(this);
		sendLayout.setOrientation(LinearLayout.HORIZONTAL);
		mainLayout.addView(sendLayout, new LayoutParams(LayoutParams.FILL_PARENT, ScreenInfo.HEIGHT / 10));

		
		
		TextView tv = new TextView(this);
		tv.setBackgroundColor(Color.GRAY);
		tv.setTextColor(Color.WHITE);
		tv.setPadding(0, 2, 0, 2);
		tv.setTextSize(18);
		tv.setText("在线人员");
		tv.setBackgroundColor(Color.GRAY);
		mainLayout.addView(tv, new LayoutParams(LayoutParams.FILL_PARENT, ScreenInfo.HEIGHT * 1 / 20));

		
		
		userListView = new ListView(this);
		userListView.setCacheColorHint(0);
		userListView.setBackgroundColor(Color.TRANSPARENT);
		userListView.setAdapter(userListAdapter);
		userListView.setOnItemClickListener(itemClickListener);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1;
		mainLayout.addView(userListView, layoutParams);
		
        
		fullLayout.addView(mainLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// fullLayout.addView(btnLayout,new
		// LayoutParams(ScreenInfo.WIDTH,ScreenInfo.HEIGHT/10));
		this.setContentView(fullLayout);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			StartVideoChat(arg2);
		}
	};

	public class UserListListAdapter extends BaseAdapter {
		private Context context;

		public UserListListAdapter(Context context) {
			this.context = context;
		}

		public int getCount() {
			return userList.size();
		}

		public Object getItem(int position) {
			return userList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(context);
			tv.setTextColor(Color.YELLOW);
			tv.setPadding(4, 4, 4, 4);
			tv.setTextSize(24);
			tv.setBackgroundColor(color.black);
			tv.setText(userList.get(position));
			return tv;
		}
	}


	public void StartVideoChat(int position) {
		Intent intent = new Intent();
		intent.putExtra("UserID", idList.get(position));
		intent.putExtra("addressStr", addressStr);
		intent.setClass(RoomActivity.this, VideoActivity.class);
		startActivity(intent);
	}

	private OnTouchListener touchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent e) {
			// TODO Auto-generated method stub
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				try {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(RoomActivity.this.getCurrentFocus()
							.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception excp) {

				}
				break;
			case MotionEvent.ACTION_UP:

				break;
			}
			return false;
		}
	};

	protected void onDestroy() {
		Log.e("******RoomActivity***********", "RoomActivity  onDestroy");
		anychat.LeaveRoom(-1);
		super.onDestroy();
	}

	protected void onResume() {
		anychat.SetBaseEvent(this);
		idList.clear();
		userList.clear();
		int[] userID = anychat.GetOnlineUser();
		for (int i = 0; i < userID.length; i++) {
			idList.add("" + userID[i]);
		}
		for (int i = 0; i < userID.length; i++) {
			userList.add(anychat.GetUserName(userID[i]));
		}
		userListAdapter.notifyDataSetChanged();
		super.onResume();
	}

	public void OnAnyChatConnectMessage(boolean bSuccess) {
		// TODO Auto-generated method stub

	}

	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub
		Log.e("********RoomActivity*********", "OnAnyChatEnterRoomMessage");

	}

	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		// TODO Auto-generated method stub

	}

	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub

	}

	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		// TODO Auto-generated method stub
		Log.e("********RoomActivity*********", "OnAnyChatOnlineUserMessage   " + dwUserNum);

	}

	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		// TODO Auto-generated method stub
		if (bEnter) {
			idList.add("" + dwUserId);
			userList.add(anychat.GetUserName(dwUserId));
			userListAdapter.notifyDataSetChanged();

		} else {
			for (int i = 0; i < idList.size(); i++) {
				if (idList.get(i).equals("" + dwUserId)) {
					idList.remove(i);
					userList.remove(i);
					userListAdapter.notifyDataSetChanged();
				}
			}

		}
	}

	public void OnAnyChatTextMessage(int dwFromUserid, int dwToUserid, boolean bSecret, String message) {
	}

}
