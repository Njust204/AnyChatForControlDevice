package com.bairuitech.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Spinner;

public class VideoConfigActivity extends Activity {
	private LinearLayout fullLayout;
	private LinearLayout mainLayout;
	private Button saveBtn;
	private ConfigEntity configEntity;

	private RadioGroup configModeRadioGroup; // ����ģʽ��

	RadioButton configModeServerBtn; // ����������
	RadioButton configModeCustomBtn; // �Զ�������

	TextView videoBitrateLable;
	TextView resolutionLable;
	TextView videoFpsLable;
	TextView videoQualityLable;
	TextView videoPresetLable;
	TextView videoShowDriverLable;

	private CheckBox enableP2PBox;
	private CheckBox videoOverlayBox;
	private CheckBox useARMv6Box;
	private CheckBox useAECBox;
	private CheckBox useHWCodecBox;
	private CheckBox videoRotateBox;
	private CheckBox videoCapDriverBox;
	private CheckBox fixColorDeviation;
	private CheckBox smoothPlayBox;
	private Spinner videoSizeSpinner;
	private Spinner videoBitrateSpinner;
	private Spinner videoFPSSpinner;
	private Spinner videoQualitySpinner;
	private Spinner videoPresetSpinner;
	private Spinner videoShowDriverSpinner;

	private final String[] videoSizeString = { "176 x 144", "320 x 240（默认）", "352 x 288", "640 x 480", "720 x 480", "1280 x 720" };
	private final int[] videoWidthValue = { 176, 320, 352, 640, 720, 1280 };
	private final int[] videoHeightValue = { 144, 240, 288, 480, 480, 720 };

	private final String[] videoBitrateString = { "质量优先模式", "60kbps（默认）", "80kbps", "100kbps", "150kbps", "200kbps", "300kbps" };
	private final int[] videoBitrateValue = { 0, 60 * 1000, 80 * 1000, 100 * 1000, 150 * 1000, 200 * 1000, 300 * 1000 };

	private final String[] videofpsString = { "2 FPS", "4 FPS", "6 FPS", "8 FPS（默认）", "10FPS", "15FPS", "20FPS", "25FPS" };
	private final int[] videofpsValue = { 2, 4, 6, 8, 10, 15, 20, 25 };

	private final String[] qualityString = { "普通视频质量", "中等视频质量（默认）", "较好视频质量" };
	private final String[] presetString = { "最高效率，较低质量", "较高效率，较低质量", "性能均衡（默认）", "较高质量，较低效率", "最高质量，较低效率" };

	private final String[] videoShowDriverString = { "默认显示驱动", "Android 2.x兼容模式" };
	private final int[] videoShowDriverValue = { 0, 4 };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configEntity = ConfigService.LoadConfig(this);

		InitialLayout();
	}

	private void InitialLayout() {
		this.setTitle("配置");
		fullLayout = new LinearLayout(this);
		fullLayout.setBackgroundColor(Color.WHITE);
		fullLayout.setOrientation(LinearLayout.VERTICAL);
		fullLayout.setOnTouchListener(touchListener);

		mainLayout = new LinearLayout(this);
		mainLayout.setBackgroundColor(Color.WHITE);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setOnTouchListener(touchListener);

		enableP2PBox = new CheckBox(this);
		enableP2PBox.setText("启用P2P网络连接");
		enableP2PBox.setTextColor(Color.BLACK);
		mainLayout.addView(enableP2PBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		enableP2PBox.setChecked(configEntity.enableP2P != 0);

		videoOverlayBox = new CheckBox(this);
		videoOverlayBox.setText("Overlay视频模式");
		videoOverlayBox.setTextColor(Color.BLACK);
		mainLayout.addView(videoOverlayBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		videoOverlayBox.setChecked(configEntity.videoOverlay != 0);

		videoRotateBox = new CheckBox(this);
		videoRotateBox.setText("翻转视频");
		videoRotateBox.setTextColor(Color.BLACK);
		mainLayout.addView(videoRotateBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		videoRotateBox.setChecked(configEntity.videorotatemode != 0);

		videoCapDriverBox = new CheckBox(this);
		videoCapDriverBox.setText("Video4Linux驱动");
		videoCapDriverBox.setTextColor(Color.BLACK);
		mainLayout.addView(videoCapDriverBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		videoCapDriverBox.setChecked(configEntity.videoCapDriver == 1);

		fixColorDeviation = new CheckBox(this);
		fixColorDeviation.setText("本地视频采集偏色修正");
		fixColorDeviation.setTextColor(Color.BLACK);
		mainLayout.addView(fixColorDeviation, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		fixColorDeviation.setChecked(configEntity.fixcolordeviation != 0);

		smoothPlayBox = new CheckBox(this);
		smoothPlayBox.setText("视频平滑播放模式");
		smoothPlayBox.setTextColor(Color.BLACK);
		mainLayout.addView(smoothPlayBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		smoothPlayBox.setChecked(configEntity.smoothPlayMode != 0);

		useARMv6Box = new CheckBox(this);
		useARMv6Box.setText("强制使用ARMv6指令集（安全模式）");
		useARMv6Box.setTextColor(Color.BLACK);
		mainLayout.addView(useARMv6Box, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		useARMv6Box.setChecked(configEntity.useARMv6Lib != 0);

		useAECBox = new CheckBox(this);
		useAECBox.setText("启用回音消除（AEC）");
		useAECBox.setTextColor(Color.BLACK);
		mainLayout.addView(useAECBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		useAECBox.setChecked(configEntity.enableAEC != 0);

		useHWCodecBox = new CheckBox(this);
		useHWCodecBox.setText("启用平台内置硬件编解码（需重启应用程序）");
		useHWCodecBox.setTextColor(Color.BLACK);
		mainLayout.addView(useHWCodecBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		useHWCodecBox.setChecked(configEntity.useHWCodec != 0);

		//插入视频显示驱动设置
		videoShowDriverLable = new TextView(this);
		videoShowDriverLable.setTextColor(Color.BLACK);
		videoShowDriverLable.setText("视频显示驱动：");
		mainLayout.addView(videoShowDriverLable, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		videoShowDriverSpinner = new Spinner(this);
		LinearLayout.LayoutParams videoShowDriverLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		videoShowDriverLP.gravity = Gravity.RIGHT;
		mainLayout.addView(videoShowDriverSpinner, videoShowDriverLP);
		ArrayAdapter<String> videoShowDriverAdapter;
		videoShowDriverAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, videoShowDriverString);
		videoShowDriverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		videoShowDriverSpinner.setAdapter(videoShowDriverAdapter);
		videoShowDriverSpinner.setVisibility(View.VISIBLE);
		int iSelectVideoDriver = 0;
		for (int i = 0; i < videoShowDriverValue.length; i++) {
			if (videoShowDriverValue[i] == configEntity.videoShowDriver) {
				iSelectVideoDriver = i;
				break;
			}
		}
		videoShowDriverSpinner.setSelection(iSelectVideoDriver);

		// 插入配置模式选择项
		TextView configModeLable = new TextView(this);
		configModeLable.setTextColor(Color.BLACK);
		configModeLable.setText("选择配置模式：");
		mainLayout.addView(configModeLable, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		configModeRadioGroup = new RadioGroup(this);
		configModeServerBtn = new RadioButton(this);
		configModeCustomBtn = new RadioButton(this);
		configModeServerBtn.setTextColor(Color.BLACK);
		configModeCustomBtn.setTextColor(Color.BLACK);
		configModeServerBtn.setText("服务器配置参数");
		configModeCustomBtn.setText("自定义配置参数");
		configModeRadioGroup.addView(configModeServerBtn);
		configModeRadioGroup.addView(configModeCustomBtn);
		configModeServerBtn.setOnClickListener(listener);
		configModeCustomBtn.setOnClickListener(listener);

		if (configEntity.configMode == ConfigEntity.VIDEO_MODE_SERVERCONFIG)
			configModeServerBtn.setChecked(true);
		else
			configModeCustomBtn.setChecked(true);

		mainLayout.addView(configModeRadioGroup, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		//插入视频分辨率
		resolutionLable = new TextView(this);
		resolutionLable.setTextColor(Color.BLACK);
		resolutionLable.setText("选择视频分辨率：");
		mainLayout.addView(resolutionLable, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		videoSizeSpinner = new Spinner(this);
		LinearLayout.LayoutParams videoSizeLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		videoSizeLP.gravity = Gravity.RIGHT;
		mainLayout.addView(videoSizeSpinner, videoSizeLP);
		ArrayAdapter<String> videoSizeAdapter;
		videoSizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, videoSizeString);
		videoSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		videoSizeSpinner.setAdapter(videoSizeAdapter);
		videoSizeSpinner.setVisibility(View.VISIBLE);
		int iSelectVideoSize = 0;
		for (int i = 0; i < videoWidthValue.length; i++) {
			if (videoWidthValue[i] == configEntity.resolution_width) {
				iSelectVideoSize = i;
				break;
			}
		}
		videoSizeSpinner.setSelection(iSelectVideoSize);

		//插入码率
		videoBitrateLable = new TextView(this);
		videoBitrateLable.setTextColor(Color.BLACK);
		videoBitrateLable.setText("选择视频码率：");
		mainLayout.addView(videoBitrateLable, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		videoBitrateSpinner = new Spinner(this);
		LinearLayout.LayoutParams videobitrateLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		videobitrateLP.gravity = Gravity.RIGHT;
		mainLayout.addView(videoBitrateSpinner, videobitrateLP);
		ArrayAdapter<String> videoBitrateAdapter;
		videoBitrateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, videoBitrateString);
		videoBitrateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		videoBitrateSpinner.setAdapter(videoBitrateAdapter);
		videoBitrateSpinner.setVisibility(View.VISIBLE);
		int iSelectVideoBitrate = 0;
		for (int i = 0; i < videoBitrateValue.length; i++) {
			if (videoBitrateValue[i] == configEntity.videoBitrate) {
				iSelectVideoBitrate = i;
				break;
			}
		}
		videoBitrateSpinner.setSelection(iSelectVideoBitrate);

		// 选择视频帧率：
		videoFpsLable = new TextView(this);
		videoFpsLable.setTextColor(Color.BLACK);
		videoFpsLable.setText("选择视频帧率：");
		mainLayout.addView(videoFpsLable, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		videoFPSSpinner = new Spinner(this);
		LinearLayout.LayoutParams videofpsLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		videofpsLP.gravity = Gravity.RIGHT;
		mainLayout.addView(videoFPSSpinner, videofpsLP);
		ArrayAdapter<String> videofpsAdapter;
		videofpsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, videofpsString);
		videofpsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		videoFPSSpinner.setAdapter(videofpsAdapter);
		videoFPSSpinner.setVisibility(View.VISIBLE);
		int iSelectFps = 0;
		for (int i = 0; i < videofpsValue.length; i++) {
			if (videofpsValue[i] == configEntity.videoFps) {
				iSelectFps = i;
				break;
			}
		}
		videoFPSSpinner.setSelection(iSelectFps);

		// 选择视频质量：
		videoQualityLable = new TextView(this);
		videoQualityLable.setTextColor(Color.BLACK);
		videoQualityLable.setText("选择视频质量：");
		mainLayout.addView(videoQualityLable, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		videoQualitySpinner = new Spinner(this);
		LinearLayout.LayoutParams videoqualityLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		videoqualityLP.gravity = Gravity.RIGHT;
		mainLayout.addView(videoQualitySpinner, videoqualityLP);
		ArrayAdapter<String> qualityAdapter;
		qualityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, qualityString);
		qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		videoQualitySpinner.setAdapter(qualityAdapter);
		videoQualitySpinner.setSelection(configEntity.videoQuality - 2);
		videoQualitySpinner.setVisibility(View.VISIBLE);

		// 选择视频预设参数：
		videoPresetLable = new TextView(this);
		videoPresetLable.setTextColor(Color.BLACK);
		videoPresetLable.setText("选择视频预设参数：");
		mainLayout.addView(videoPresetLable, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		videoPresetSpinner = new Spinner(this);
		LinearLayout.LayoutParams videopresetLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		videopresetLP.gravity = Gravity.RIGHT;
		mainLayout.addView(videoPresetSpinner, videopresetLP);
		ArrayAdapter<String> presetAdapter;
		// ����ѡ������ArrayAdapter��������
		presetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, presetString);
		// ���������б�ķ��
		presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		videoPresetSpinner.setAdapter(presetAdapter);
		// ����Ĭ��ֵ
		videoPresetSpinner.setSelection(configEntity.videoPreset - 1);
		videoPresetSpinner.setVisibility(View.VISIBLE);

		// �������ģʽ��ȷ���Ƿ���Ҫ��ʾ�Զ����������
		CustomControlsShow(configEntity.configMode == 0 ? false : true);

		// ����ײ���ť
		LinearLayout btnLayout = new LinearLayout(this);
		btnLayout.setOrientation(LinearLayout.HORIZONTAL);

		saveBtn = new Button(this);
		saveBtn.setText("保存设置");
		btnLayout.addView(saveBtn, new LayoutParams(ScreenInfo.WIDTH, LayoutParams.WRAP_CONTENT));
		saveBtn.setOnClickListener(listener);

		ScrollView sv = new ScrollView(this);
		sv.addView(mainLayout);
		fullLayout.addView(sv, new LayoutParams(ScreenInfo.WIDTH, ScreenInfo.HEIGHT * 8 / 10));

		fullLayout.addView(btnLayout, new LayoutParams(ScreenInfo.WIDTH, ScreenInfo.HEIGHT / 10));
		this.setContentView(fullLayout);

	}

	OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			if (v == saveBtn) {
				SaveConfig();
			} else if (v == configModeServerBtn) {
				CustomControlsShow(false);
			} else if (v == configModeCustomBtn) {
				CustomControlsShow(true);
			}
		}
	};

	private void CustomControlsShow(boolean bShow) {
		if (bShow) {
			videoSizeSpinner.setVisibility(View.VISIBLE);
			videoBitrateSpinner.setVisibility(View.VISIBLE);
			videoFPSSpinner.setVisibility(View.VISIBLE);
			videoQualitySpinner.setVisibility(View.VISIBLE);
			videoPresetSpinner.setVisibility(View.VISIBLE);

			videoBitrateLable.setVisibility(View.VISIBLE);
			resolutionLable.setVisibility(View.VISIBLE);
			videoFpsLable.setVisibility(View.VISIBLE);
			videoQualityLable.setVisibility(View.VISIBLE);
			videoPresetLable.setVisibility(View.VISIBLE);
		} else {
			videoSizeSpinner.setVisibility(View.GONE);
			videoBitrateSpinner.setVisibility(View.GONE);
			videoFPSSpinner.setVisibility(View.GONE);
			videoQualitySpinner.setVisibility(View.GONE);
			videoPresetSpinner.setVisibility(View.GONE);

			videoBitrateLable.setVisibility(View.GONE);
			resolutionLable.setVisibility(View.GONE);
			videoFpsLable.setVisibility(View.GONE);
			videoQualityLable.setVisibility(View.GONE);
			videoPresetLable.setVisibility(View.GONE);
		}
	}

	private void SaveConfig() {
		configEntity.configMode = configModeServerBtn.isChecked() ? ConfigEntity.VIDEO_MODE_SERVERCONFIG : ConfigEntity.VIDEO_MODE_CUSTOMCONFIG;

		configEntity.resolution_width = videoWidthValue[videoSizeSpinner.getSelectedItemPosition()];
		configEntity.resolution_height = videoHeightValue[videoSizeSpinner.getSelectedItemPosition()];
		configEntity.videoBitrate = videoBitrateValue[videoBitrateSpinner.getSelectedItemPosition()];
		configEntity.videoFps = videofpsValue[videoFPSSpinner.getSelectedItemPosition()];
		configEntity.videoQuality = videoQualitySpinner.getSelectedItemPosition() + 2;
		configEntity.videoPreset = videoPresetSpinner.getSelectedItemPosition() + 1;
		configEntity.videoOverlay = videoOverlayBox.isChecked() ? 1 : 0;
		configEntity.videorotatemode = videoRotateBox.isChecked() ? 1 : 0;
		configEntity.videoCapDriver = videoCapDriverBox.isChecked() ? 1 : 0;
		configEntity.fixcolordeviation = fixColorDeviation.isChecked() ? 1 : 0;
		if (configEntity.videoCapDriver == 1) // Video4Linux��֧��Overlayģʽ
			configEntity.videoOverlay = 0;

		configEntity.smoothPlayMode = smoothPlayBox.isChecked() ? 1 : 0;

		configEntity.enableP2P = enableP2PBox.isChecked() ? 1 : 0;
		configEntity.useARMv6Lib = useARMv6Box.isChecked() ? 1 : 0;
		configEntity.enableAEC = useAECBox.isChecked() ? 1 : 0;
		configEntity.useHWCodec = useHWCodecBox.isChecked() ? 1 : 0;
		configEntity.videoShowDriver = videoShowDriverValue[videoShowDriverSpinner.getSelectedItemPosition()];

		ConfigService.SaveConfig(this, configEntity);

		this.setResult(HallActivity.ACTIVITY_ID_VIDEOCONFIG);
		this.finish();
	}

	OnTouchListener touchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent e) {
			// TODO Auto-generated method stub
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				try {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(VideoConfigActivity.this.getCurrentFocus()
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

}
