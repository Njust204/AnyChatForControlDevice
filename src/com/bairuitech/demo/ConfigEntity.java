package com.bairuitech.demo;

import java.security.PublicKey;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConfigEntity {
	public static final int VIDEO_MODE_SERVERCONFIG = 0;	// ��������Ƶ��������
	public static final int VIDEO_MODE_CUSTOMCONFIG = 1;	// �Զ�����Ƶ��������
	
	public static final int VIDEO_QUALITY_NORMAL = 2;		// ��ͨ��Ƶ����
	public static final int VIDEO_QUALITY_GOOD = 3;			// �е���Ƶ����
	public static final int VIDEO_QUALITY_BEST = 4;			// �Ϻ���Ƶ����
	
	public boolean IsSaveNameAndPw;
	public String name = "";
	public String password = "";

	public String ip = "";
	public int port;
	
	public int configMode = VIDEO_MODE_SERVERCONFIG;
	public int resolution_width = 0;
	public int resolution_height = 0;
	
	public int videoBitrate = 0;
	public int videoFps = 0;
	public int videoQuality = VIDEO_QUALITY_GOOD;
	public int videoPreset = 1;
	public int videoOverlay = 1;							// ������Ƶ�Ƿ����Overlayģʽ
	public int videorotatemode = 0;							// ������Ƶ��תģʽ
	public int videoCapDriver = 0;							// ������Ƶ�ɼ���0 Ĭ�ϣ� 1 V4L2
	public int fixcolordeviation = 0;						// �������Ƶ�ɼ�ƫɫ��0 �ر�(Ĭ�ϣ��� 1 ����
	
	public int enableP2P = 1;
	public int useARMv6Lib = 0;								// �Ƿ�ǿ��ʹ��ARMv6ָ���Ĭ�����ں��Զ��ж�
	public int enableAEC = 0;								// �Ƿ�ʹ�û��������
	public int useHWCodec = 0;								// �Ƿ�ʹ��ƽ̨����Ӳ���������
	public int smoothPlayMode = 0;							// �Ƿ�ʹ��ƽ������ģʽ����ʱ���Ÿ�ƽ��������ʱ���������ˣ��ر�ʱ������������[Ĭ��]��������ʱ�������ݿ�ס��
	public int videoShowDriver = 0;							// ��Ƶ��ʾ��0 Ĭ�ϣ� 4 Android 2.x����ģʽ��
	
	public static BluetoothDevice btDevice = null;
	public static BluetoothSocket btSocket = null;
	public static String address = null;
}
