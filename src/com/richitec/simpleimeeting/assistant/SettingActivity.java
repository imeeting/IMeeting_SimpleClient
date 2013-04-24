package com.richitec.simpleimeeting.assistant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;

public class SettingActivity extends SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SettingActivity.class
			.getCanonicalName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.setting_activity_layout);

		// set title text
		setTitle(R.string.setting_nav_title_text);

		// get binded account logout button
		Button _bindedAccountLogoutBtn = (Button) findViewById(R.id.set_accountLogout_button);

		// bind its on click listener
		_bindedAccountLogoutBtn
				.setOnClickListener(new BindedAccountLogoutButtonOnClickListener());

		// get phone, email bind image button and bind there on click listener
		ImageButton _phoneBindImgButton = (ImageButton) findViewById(R.id.set_phoneBind_imageButton);
		_phoneBindImgButton
				.setOnClickListener(new PhoneBindImgButtonOnClickListener());

		ImageButton _emailBindImgButton = (ImageButton) findViewById(R.id.set_emailBind_imageButton);
		_emailBindImgButton
				.setOnClickListener(new EmailBindImgButtonOnClickListener());

		// get binded account login button
		Button _bindedAccountLoginBtn = (Button) findViewById(R.id.set_accountLogin_button);

		// bind its on click listener
		_bindedAccountLoginBtn
				.setOnClickListener(new BindedAccountLoginButtonOnClickListener());

		// test by ares
		((TextView) findViewById(R.id.set_account6deviceIdLabel_textView))
				.setText(R.string.deviceId_labelTextView_text);
		((TextView) findViewById(R.id.set_account6deviceId_textView))
				.setText("1qaz2wsx-cde3vfr4-45rtfgvb");
		_phoneBindImgButton.setEnabled(false);
	}

	// inner class
	// binded account logout button on click listener
	class BindedAccountLogoutButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Binded account logout");

			// binded account logout
			// ??
		}

	}

	// phone bind image button on click listener
	class PhoneBindImgButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Phone bind");

			// bind phone
			// ??
		}

	}

	// email bind image button on click listener
	class EmailBindImgButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Email bind");

			// bind email
			// ??
		}

	}

	// binded account login button on click listener
	class BindedAccountLoginButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Binded account login");

			// binded account login
			// ??
		}

	}

}
