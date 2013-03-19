package com.richitec.simpleimeeting.assistant;

import android.os.Bundle;
import android.widget.TextView;

import com.richitec.commontoolkit.utils.VersionUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;

public class AboutActivity extends SimpleIMeetingNavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.about_activity_layout);

		// set title text
		setTitle(R.string.about_nav_title_text);

		// set product version name
		((TextView) findViewById(R.id.product_versionName_textView))
				.setText(VersionUtils.versionName());
	}

}
