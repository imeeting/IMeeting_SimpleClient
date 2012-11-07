package com.richitec.imeeting.simple.assistant;

import android.os.Bundle;
import android.widget.TextView;

import com.richitec.commontoolkit.utils.VersionUtils;
import com.richitec.imeeting.simple.R;
import com.richitec.imeeting.simple.customcomponent.IMeetingNavigationActivity;

public class AboutActivity extends IMeetingNavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.about_activity_layout);

		// set title text
		setTitle(R.string.about_nav_title_text);

		// set product version name
		((TextView) findViewById(R.id.product_versionName_textView))
				.setText(VersionUtils.currentVersionName(this));
	}

}
