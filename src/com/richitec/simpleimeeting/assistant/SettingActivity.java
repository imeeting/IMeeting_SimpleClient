package com.richitec.simpleimeeting.assistant;

import android.os.Bundle;

import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;

public class SettingActivity extends SimpleIMeetingNavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.setting_activity_layout);

		// set title text
		setTitle(R.string.setting_nav_title_text);

		//
	}

}
