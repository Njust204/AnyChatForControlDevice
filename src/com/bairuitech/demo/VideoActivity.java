package com.bairuitech.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;

public class VideoActivity extends Activity implements AnyChatBaseEvent,
		SensorEventListener, OnClickListener {
	// private LinearLayout mainLayout;
	private static final int REQUEST_CONNECT_DEVICE = 2;

	private SurfaceView otherView;
	ProgressBar OtherProgressBar;
	ProgressBar MyProgressBar;
	private ImageView mCameraSwitchImage; // ǰ������ͷ�л���ť

	private SurfaceView myView;

	private ConfigEntity configEntity;

	public AnyChatCoreSDK anychat;
	int userID;

	private boolean bSelfVideoOpened = false; // ������Ƶ�Ƿ��Ѵ�
	private boolean bOtherVideoOpened = false; // �Է���Ƶ�Ƿ��Ѵ�

	private Timer mTimer = new Timer(true);
	private TimerTask mTimerTask;
	private Handler handler;

	private boolean bCameraNeedFocus = false; // ������Ƿ���Ҫ�Խ�
	private Date LastSportTime = new Date(); // �ϴ��˶�ʱ��
	private float LastXSpead = 0;
	private float LastYSpead = 0;
	private float LastZSpead = 0;

	private BluetoothAdapter bluetoothAdapter = null;
	private BluetoothDevice device = null;
	private BluetoothSocket btSocket = null;
	private String addressStr = null;
	private OutputStream outStream = null;
	private byte[] msgBuffer = null;
	private String transData;

	private SeekBar left_rightSeekBar;
	private SeekBar up_downsSeekBar;
	private static final int MAXVALUE = 2000;
	private static final UUID uuid = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private Handler btMsgHandler;

	private class btSendMsg implements Runnable {

		public btSendMsg() {
			// this.device = ConfigEntity.btDevice;
			// this.btSocket = ConfigEntity.btSocket;
			String addressStr = configEntity.address;
			// device = bluetoothAdapter.getRemoteDevice(addressStr);
			device = configEntity.btDevice;
			// ConfigEntity.btDevice = this.device;
			try {
				// ���UUID����ͨ���׽���
				btSocket = device.createRfcommSocketToServiceRecord(uuid);
			} catch (Exception e) {
				displayShortToast("ͨ连接失败");
			}

			if (btSocket != null) {
				try {
					btSocket.connect();
					outStream = btSocket.getOutputStream();
					displayShortToast("socket连接成功");

				} catch (Exception e) {
					displayShortToast("ͨ信息发送失败");
					try {
						btSocket.close();
						displayShortToast("socket关闭");
					} catch (IOException ioe2) {
						displayShortToast("socket异常");
					}
				}
			} else {
				displayShortToast("Socket未传递进来");
			}
		}

		public void run() {
			Looper.prepare();

			VideoActivity.this.btMsgHandler = new Handler() {
				public void handleMessage(Message msg) {
					byte[] RN = new byte[2];
					RN[0] = 0x0D;
					RN[1] = 0x0A;
					try {
						// while (true) {
						msgBuffer = ((String) msg.obj).getBytes();
						byte[] tempBroad = new byte[msgBuffer.length
								+ RN.length];
						for (int i = 0; i < tempBroad.length; i++) {
							if (i < msgBuffer.length) {
								tempBroad[i] = msgBuffer[i];
							} else {
								tempBroad[i] = RN[i - msgBuffer.length];
							}
						}
						outStream.write(tempBroad);
						outStream.flush();
						// 为马达转动增加延时
						// Thread.sleep(600);
						// }
					} catch (IOException e) {
						// displayLongToast("发送失败！");
					}
				}
			};

			Looper.loop();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configEntity = ConfigService.LoadConfig(this);
		Intent intent = getIntent();
		addressStr = intent.getStringExtra("addressStr");
		userID = Integer.parseInt(intent.getStringExtra("UserID"));
		InitialSDK();
		InitialLayout();

		new Thread(new btSendMsg()).start();
		mTimerTask = new TimerTask() {
			public void run() {
				Message mesasge = new Message();
				handler.sendMessage(mesasge);
			}
		};

		mTimer.schedule(mTimerTask, 1000, 100);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				CheckVideoStatus();
				SetVolum();
				super.handleMessage(msg);
			}
		};

		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void SetVolum() {
		OtherProgressBar.setProgress(anychat.GetUserSpeakVolume(userID));
		MyProgressBar.setProgress(anychat.GetUserSpeakVolume(-1));
	}

	// �ж���Ƶ�Ƿ��Ѵ�
	private void CheckVideoStatus() {
		if (!bOtherVideoOpened) {
			if (anychat.GetCameraState(userID) == 2
					&& anychat.GetUserVideoWidth(userID) != 0) {
				SurfaceHolder holder = otherView.getHolder();
				holder.setFormat(PixelFormat.RGB_565);
				holder.setFixedSize(anychat.GetUserVideoWidth(userID),
						anychat.GetUserVideoHeight(userID));
				Surface s = holder.getSurface();
				anychat.SetVideoPos(userID, s, 0, 0, 0, 0);
				bOtherVideoOpened = true;
			}
		}
		if (!bSelfVideoOpened) {
			if (anychat.GetCameraState(-1) == 2
					&& anychat.GetUserVideoWidth(-1) != 0) {
				SurfaceHolder holder = myView.getHolder();
				holder.setFormat(PixelFormat.RGB_565);
				holder.setFixedSize(anychat.GetUserVideoWidth(-1),
						anychat.GetUserVideoHeight(-1));
				Surface s = holder.getSurface();
				anychat.SetVideoPos(-1, s, 0, 0, 0, 0);
				bSelfVideoOpened = true;
			}
		}

	}

	private void InitialSDK() {
		anychat = new AnyChatCoreSDK();
		anychat.SetBaseEvent(this);
	}

	private void InitialLayout() {
		this.setContentView(R.layout.video_room);
		this.setTitle("与" + anychat.GetUserName(userID) + "视频中");
		myView = (SurfaceView) findViewById(R.id.surface_local);
		otherView = (SurfaceView) findViewById(R.id.surface_remote);
		mCameraSwitchImage = (ImageView) findViewById(R.id.image_switch_camera);
		mCameraSwitchImage.setOnClickListener(this);
		MyProgressBar = (ProgressBar) findViewById(R.id.progress_local);
		OtherProgressBar = (ProgressBar) findViewById(R.id.progress_remote);
		SurfaceHolder holder = otherView.getHolder();
		holder.setKeepScreenOn(true);
		anychat.UserCameraControl(userID, 1);
		anychat.UserSpeakControl(userID, 1);
		myView.setOnClickListener(this);
		if (configEntity.videoOverlay != 0) {
			myView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		anychat.UserCameraControl(-1, 1);
		anychat.UserSpeakControl(-1, 1);
		String[] strVideoCaptures = anychat.EnumVideoCapture();
		if (strVideoCaptures != null && strVideoCaptures.length > 1)
			mCameraSwitchImage.setVisibility(View.VISIBLE);

		left_rightSeekBar = (SeekBar) findViewById(R.id.left_right_SB);
		up_downsSeekBar = (SeekBar) findViewById(R.id.up_down_SB);
		left_rightSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		up_downsSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		left_rightSeekBar.setMax(MAXVALUE);
		up_downsSeekBar.setMax(MAXVALUE);

	}

	OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
		}
	};

	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub

			Integer tempProgressValue = progress + 500;

			// 左右用6，上下用7
			/*
			 * 指令格式：#<num>P<pwm>.... T<time>\r\n #8P600T1000\r\n
			 */
			if (seekBar == left_rightSeekBar) {
				transData = "#6P" + tempProgressValue.toString() + "T100";
				// leftrightTextView.setText(transData);
			} else if (seekBar == up_downsSeekBar) {
				transData = "#7P" + tempProgressValue.toString() + "T100";
				// updownTextView.setText(transData);
			}
			if (VideoActivity.this.btMsgHandler != null) {
				Message tmpBtMsg = VideoActivity.this.btMsgHandler
						.obtainMessage();
				tmpBtMsg.obj = transData;
				VideoActivity.this.btMsgHandler.sendMessage(tmpBtMsg);
			}

		}
	};

	private void refreshAV() {
		anychat.UserCameraControl(userID, 1);
		anychat.UserSpeakControl(userID, 1);
		anychat.UserCameraControl(-1, 1);
		anychat.UserSpeakControl(-1, 1);
		bOtherVideoOpened = false;
		bSelfVideoOpened = false;
	}

	protected void onRestart() {
		super.onRestart();
		refreshAV();

	}

	protected void onResume() {
		super.onResume();

	}

	protected void onPause() {
		super.onPause();
		anychat.UserCameraControl(userID, 0);
		anychat.UserSpeakControl(userID, 0);
		anychat.UserCameraControl(-1, 0);
		anychat.UserSpeakControl(-1, 0);
	}

	@Override
	protected void onStop() {
		// sensorManager.unregisterListener(this);
		super.onStop();

		try {
			if (null != this.outStream) {
				this.outStream.close();

			}
			if (null != this.btSocket) {
				this.btSocket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		mTimer.cancel();
		finish();
	}

	public void OnAnyChatConnectMessage(boolean bSuccess) {
		// TODO Auto-generated method stub

	}

	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub
		Log.e("********VideoActivity*********", "OnAnyChatEnterRoomMessage");

	}

	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		// TODO Auto-generated method stub

	}

	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub

	}

	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		// TODO Auto-generated method stub
		Log.e("********VideoActivity*********", "OnAnyChatOnlineUserMessage   "
				+ dwUserNum);
		refreshAV();
	}

	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		// TODO Auto-generated method stub
		Log.e("********VideoActivity*********", "OnAnyChatUserAtRoomMessage"
				+ dwUserId);
		if (dwUserId == userID) {
			if (!bEnter) {
				anychat.UserCameraControl(dwUserId, 0);
				anychat.UserSpeakControl(dwUserId, 0);
				bOtherVideoOpened = false;
			} else {
				anychat.UserCameraControl(dwUserId, 1);
				anychat.UserSpeakControl(dwUserId, 1);
			}
		}

	}

	public void onSensorChanged(android.hardware.SensorEvent event) {
		float X = event.values[0]; // ˮƽx������ٶ� �����徲ֹʱ�ڣ�0--1֮�䣩
		float Y = event.values[1]; // ˮƽY������ٶ� �����徲ֹʱ�ڣ�0--1֮�䣩
		float Z = event.values[1]; // ��ֱZ������ٶ� �����徲ֹʱ�ڣ�9.5--10֮�䣩

		if ((Math.abs(X - LastXSpead) <= 0.5)
				&& (Math.abs(Y - LastYSpead) <= 0.5)
				&& (Math.abs(Z - LastZSpead) <= 0.5)) // ��ֹ״̬
		{
			Date now = new Date();
			long interval = now.getTime() - LastSportTime.getTime();
			if (bCameraNeedFocus && interval > 1000) {
				bCameraNeedFocus = false;
				anychat.CameraAutoFocus();
			}
		} else {
			bCameraNeedFocus = true;
			LastSportTime.setTime(System.currentTimeMillis());
		}
		LastXSpead = X;
		LastYSpead = Y;
		LastZSpead = Z;
	}

	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mCameraSwitchImage) {
			String strVideoCaptures[] = anychat.EnumVideoCapture();
			String temp = anychat.GetCurVideoCapture();
			for (int i = 0; i < strVideoCaptures.length; i++) {
				if (!temp.equals(strVideoCaptures[i])) {
					anychat.UserCameraControl(-1, 0);
					bSelfVideoOpened = false;
					anychat.SelectVideoCapture(strVideoCaptures[i]);
					anychat.UserCameraControl(-1, 1);
					break;
				}
			}
		}
	}

	private void displayShortToast(String displayString) {
		Toast.makeText(VideoActivity.this, displayString, Toast.LENGTH_SHORT)
				.show();
	}

}
