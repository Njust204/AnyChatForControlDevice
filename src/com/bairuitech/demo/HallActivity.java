package com.bairuitech.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class HallActivity extends Activity implements AnyChatBaseEvent{
	public AnyChatCoreSDK anychat;
	public static final int ACTIVITY_ID_VIDEOCONFIG = 1;
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private static final int	REQUEST_DISCOVERABLE	= 4;
	
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final int MAXVALUE = 2000;
	private BluetoothAdapter bluetoothAdapter = null;
	// ɨ��õ��������豸
	private BluetoothDevice device = null;
	// ����ͨ��socket
	private BluetoothSocket btSocket = null;
	
	private String addressStr = null;
	
	private Button getInButton;
	private Button videoConfigBtn;
	private Button openBlueToothBtn;
	private Button visibleBlueToothBtn;
	private Button searchBlueToothDeviceBtn;
	private boolean IsDisConnect = false;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        InitialSDK();
//	        InitialLayout();
	        setContentView(R.layout.activity_hall);
	        
	        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        
	        getInButton = (Button)findViewById(R.id.getInBtn);
	        getInButton.setTag(1);
	        getInButton.setOnClickListener(btnClickListener);
	        videoConfigBtn = (Button)findViewById(R.id.videoConfigBtn);
	        videoConfigBtn.setOnClickListener(btnClickListener);
	        openBlueToothBtn = (Button)findViewById(R.id.openBlueTooth);
	        openBlueToothBtn.setOnClickListener(btnClickListener);
	        visibleBlueToothBtn = (Button)findViewById(R.id.blueToothVisible);
	        visibleBlueToothBtn.setOnClickListener(btnClickListener);
	        searchBlueToothDeviceBtn = (Button)findViewById(R.id.blueToothSearch);
	        searchBlueToothDeviceBtn.setOnClickListener(btnClickListener);
	    }
	 
	 OnClickListener btnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			if(v == getInButton){
				anychat.EnterRoom(Integer.parseInt(v.getTag().toString()), "");	
			}
			if(v == videoConfigBtn){
				startActivityForResult(new Intent("com.bairuitech.demo.VideoConfigActivity"), ACTIVITY_ID_VIDEOCONFIG);
			}
			
			if(v == openBlueToothBtn){
				// 初始化蓝牙
				if (bluetoothAdapter != null) {
					if (!bluetoothAdapter.isEnabled()) {
//						bluetoothAdapter.enable();
						Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					}
				} else {
					displayShortToast("设备无蓝牙，即将退出");
					HallActivity.this.finish();
				}
			}
			
			if(v == visibleBlueToothBtn){
				Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				startActivityForResult(enabler, REQUEST_DISCOVERABLE);
			}
			
			if(v == searchBlueToothDeviceBtn){
				Intent enabler = new Intent(HallActivity.this, DiscoveryActivity.class);
				startActivityForResult(enabler, REQUEST_CONNECT_DEVICE);
			}
		}
			
	};
	 
	 protected void onDestroy() {
	    	anychat.LeaveRoom(-1);
	    	anychat.Logout();
	    	anychat.Release();	// �ر�SDK�����ٷ��ص�¼����

	    	if(!IsDisConnect)
	    	{
	    		android.os.Process.killProcess(android.os.Process.myPid());
	    	}
	    	else
	    	{
	 		   Intent itent=new Intent();
		       itent.setClass(HallActivity.this, LoginActivity.class);
		       startActivity(itent);
	    	}
	    	super.onDestroy();
	    	//System.exit(0);
	    }
	    
	    protected void onResume() {
	        anychat.SetBaseEvent(this);
	        super.onResume();
	    }
	 
	    private void InitialSDK()
	    {
	        anychat = new AnyChatCoreSDK();
	        anychat.SetBaseEvent(this);
	        ApplyVideoConfig();
	    }

		public void OnAnyChatConnectMessage(boolean bSuccess) {
			// TODO Auto-generated method stub
			
		}

		public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
			// TODO Auto-generated method stub
			
		}

		public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
			// TODO Auto-generated method stub
			if(dwErrorCode == 0)
		    {
				if(dwRoomId == 1)
		    	{
					Intent intent = new Intent();  
					intent.putExtra("RoomID", dwRoomId);
					intent.putExtra("addressStr", addressStr);
					intent.setClass(HallActivity.this, RoomActivity.class); 
					startActivity(intent);   
		    	}
		    	
		    }else {
				displayShortToast("无法进入房间");
			}
		}

		public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
			// TODO Auto-generated method stub
			
		}

		public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
			// TODO Auto-generated method stub
			
		}

		public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
			// TODO Auto-generated method stub
			IsDisConnect = true;
			this.finish();
		}
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			if(resultCode == ACTIVITY_ID_VIDEOCONFIG)
			{
				ApplyVideoConfig();
			}
			
			if(requestCode == REQUEST_DISCOVERABLE){
				if(resultCode == Activity.RESULT_OK){
					displayShortToast("可见性设置完成");
				}
			}
			
			if(requestCode == REQUEST_CONNECT_DEVICE){
				if (resultCode == Activity.RESULT_OK) {
					String addressStr = data.getStringExtra("address");
					device = bluetoothAdapter.getRemoteDevice(addressStr);
					ConfigEntity.btDevice = this.device;
					displayShortToast("地址已存好");
					/*try {
						// ���UUID����ͨ���׽���
						btSocket = device.createRfcommSocketToServiceRecord(uuid);
						ConfigEntity.btSocket = this.btSocket;
					} catch (Exception e) {
						displayShortToast("ͨ连接失败");
					}

					if (btSocket != null) {
						try {
							// 这一步要进行修改
							btSocket.connect();
							displayShortToast("ͨsocket连接");

						} catch (Exception e) {
							displayShortToast("ͨ信息发送失败");
							try {
								btSocket.close();
								displayShortToast("ͨsocket关闭");
							} catch (IOException ioe2) {
								displayShortToast("ͨsocket异常");
							}
						}

						// new Thread(Thread1).start();
					}*/
				}
			}
			
			if(requestCode == REQUEST_ENABLE_BT){
				if(resultCode == Activity.RESULT_OK){
					displayShortToast("蓝牙已开启");
				}
			}
				
		}
		
		private void ApplyVideoConfig()
		{
			ConfigEntity configEntity = ConfigService.LoadConfig(this);
			if(configEntity.configMode == 1)		// �Զ�����Ƶ��������
			{
				// ���ñ�����Ƶ��������ʣ��������Ϊ0�����ʾʹ����������ģʽ��
				anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, configEntity.videoBitrate);
				if(configEntity.videoBitrate==0)
				{
					// ���ñ�����Ƶ���������
					anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL, configEntity.videoQuality);
				}
				// ���ñ�����Ƶ�����֡��
				anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL, configEntity.videoFps);
				// ���ñ�����Ƶ����Ĺؼ�֡���
				anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL, configEntity.videoFps*4);
				// ���ñ�����Ƶ�ɼ��ֱ���
				anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL, configEntity.resolution_width);
				anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL, configEntity.resolution_height);
				// ������Ƶ����Ԥ�����ֵԽ�󣬱�������Խ�ߣ�ռ��CPU��ԴҲ��Խ�ߣ�
				anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL, configEntity.videoPreset);
			}
			// ����Ƶ������Ч
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM, configEntity.configMode);
			// P2P����
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC, configEntity.enableP2P);
			// ������ƵOverlayģʽ����
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY, configEntity.videoOverlay);
			// �����������
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL, configEntity.enableAEC);
			// ƽ̨Ӳ����������
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC, configEntity.useHWCodec);
			// ��Ƶ��תģʽ����
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL, configEntity.videorotatemode);
			// ��Ƶƽ������ģʽ����
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_STREAM_SMOOTHPLAYMODE, configEntity.smoothPlayMode);
			// ��Ƶ�ɼ�������
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER, configEntity.videoCapDriver);
			// ������Ƶ�ɼ�ƫɫ��������
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA, configEntity.fixcolordeviation);
			// ��Ƶ��ʾ������
			anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL, configEntity.videoShowDriver);
		}
		
		public void displayShortToast(String str) {
			Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 220);
			toast.show();
		}
		
		
}

