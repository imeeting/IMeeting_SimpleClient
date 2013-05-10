package com.richitec.imeeting.simple.assistant;

import android.os.Bundle;
import android.widget.TextView;

import com.richitec.commontoolkit.utils.VersionUtils;
import com.richitec.imeeting.simple.R;
import com.richitec.imeeting.simple.customcomponent.SimpleIMeetingNavigationActivity;
import com.richitec.imeeting.simple.utils.AppDataSaveRestoreUtils;

public class AboutActivity extends SimpleIMeetingNavigationActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// restore application data
		if (null != savedInstanceState) {
			AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);
		}

		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.about_activity_layout);

		// set title text
		setTitle(R.string.about_nav_title_text);

		// set product version name
		((TextView) findViewById(R.id.ab_product_versionName_textView))
				.setText(VersionUtils.versionName());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save application data
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);
	}

}
