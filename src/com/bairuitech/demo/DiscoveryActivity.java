package com.bairuitech.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * ���༯��ListActivity,��Ҫ��ɨ�貢��ʾ���������е������豸 ���ظ�BlueTooth
 * 
 * @author royal
 */
public class DiscoveryActivity extends ListActivity {

	// ��ȡ�ֻ�Ĭ���ϵ�����������
	private BluetoothAdapter blueToothAdapter = null;

	// ��ÿһ��HashMap��ֵ�Ե������豸��Ϣ��ŵ�list�����в����ļ����ַ��ķ�ʽ���ֳ���
	private ArrayList<HashMap<String, String>> list = null;
	// ��������������ɨ�赽�������豸��list
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();

	// private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	// private ArrayAdapter<String> mNewDevicesArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (blueToothAdapter == null) {
			displayLongToast("ADAPTER=null!!");
		}
		/* ʹ���򴰿�ȫ�� */
		// ����һ��û��title��ȫ������
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// ��discovery.xml�ļ����ַ��
		setContentView(R.layout.discovery);
		setResult(Activity.RESULT_CANCELED);
		list = new ArrayList<HashMap<String, String>>();
//        list.clear();
		// ��ɨ�趼��ÿһ�������豸�ŵ�list�У������ָ�ͻ���
		displayLongToast("正在寻找设备");
		showDevices();
	}

	/**
	 * ��ɨ�趼��ÿһ�������豸�ŵ�list�У������ָ�ͻ��ˡ�
	 */
	public void showDevices() {
		// ��ȡ��������Ե������豸
		Set<BluetoothDevice> devices = blueToothAdapter.getBondedDevices();

		if (devices.size() > 0) {
			Iterator<BluetoothDevice> it = devices.iterator();
			BluetoothDevice bluetoothDevice = null;
			while (it.hasNext()) {
				bluetoothDevice = it.next();
				// ��ÿһ����ȡ���������豸����ƺ͵�ַ��ŵ�HashMap�����У����磺xx:xx:xx:xx:xx:
				// royal
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("address", bluetoothDevice.getAddress());
				map.put("name", bluetoothDevice.getName());
				// ��list���ڴ�ų��ֵ������豸����ŵ���ÿ���豸��map
				list.add(map);
				// ��list���ڴ�ŵ��������ÿһ�������豸����
				_devices.add(bluetoothDevice);
			}

			// ����һ���򵥵��Զ��岼�ַ�񣬸�����������ȷ�����Ӧ�������googleһ��SimpleAdapter�Ͳο�һЩ����
			SimpleAdapter listAdapter = new SimpleAdapter(this, list, R.layout.device, new String[] { "address", "name" }, new int[] { R.id.address,
					R.id.name });
			this.setListAdapter(listAdapter);
		}
	}

	/**
	 * list�������¼� ���豸ɨ����ʾ��ɺ󣬿�ѡ������Ӧ���豸�������ӡ�
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent result = new Intent();
		String addressStr = _devices.get(position).getAddress();
		// ��ַֻȡ��17λ����ȻaddressStr��address��һ�� xx:xx:xx:xx:xx:xx
		String address = addressStr.substring(addressStr.length() - 17);
		ConfigEntity.address = address;
		result.putExtra("address", address);
		setResult(RESULT_OK, result);
		finish();
	}

	public void displayLongToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}
	/*
	 * private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { String
	 * action = intent.getAction(); // When discovery finds a device 搜索到设备 if
	 * (BluetoothDevice.ACTION_FOUND.equals(action)) { // Get the
	 * BluetoothDevice object from the Intent BluetoothDevice device =
	 * intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // If it's
	 * already paired, skip it, because it's been listed already if
	 * (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	 * mNewDevicesArrayAdapter.add(device.getName() + "\n" +
	 * device.getAddress()); } // When discovery is finished, change the
	 * Activity title } else if
	 * (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	 * setProgressBarIndeterminateVisibility(false);//搜索完毕，loading条不可见
	 * setTitle("选择设备"); if (mNewDevicesArrayAdapter.getCount() == 0) { String
	 * noDevices = "未找到设备"; mNewDevicesArrayAdapter.add(noDevices); } } } };
	 */
}
