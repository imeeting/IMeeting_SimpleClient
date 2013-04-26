package com.richitec.simpleimeeting.assistant;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.richitec.commontoolkit.utils.DeviceUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;

public class SettingActivity extends SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SettingActivity.class
			.getCanonicalName();

	// binded account login, phone and email bind alertDialog
	private AlertDialog _mBindedAccountLoginAlertDialog;
	private AlertDialog _mPhoneBindAlertDialog;
	private AlertDialog _mEmailBindAlertDialog;

	// get phone bind verification code again timer
	private final Timer GET_PHONEBIND_VERIFICATIONCODEAGAIN_TIMER = new Timer();

	// get phone bind verification code again timer task
	private TimerTask _mGetPhoneBindVerificationCodeAgainTimerTask;

	// update get phone bind verification code button title handle
	private final Handler UPDATE_PHONEBIND_VERIFICATIONCODEBTNTITLE_HANDLE = new Handler();

	// get phone bind verification code again counter
	private Integer _mGetVerificationCodeAgainCounter = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.setting_activity_layout);

		// set title text
		setTitle(R.string.setting_nav_title_text);

		// define a alertDialog builder
		final Builder ALERTDIALOG_BUILDER = new AlertDialog.Builder(this);

		// get binded account logout button
		Button _bindedAccountLogoutBtn = (Button) findViewById(R.id.set_accountLogout_button);

		// bind its on click listener
		_bindedAccountLogoutBtn
				.setOnClickListener(new BindedAccountLogoutButtonOnClickListener());

		// get phone bind image button and bind there on click listener
		ImageButton _phoneBindImgButton = (ImageButton) findViewById(R.id.set_phoneBind_imageButton);
		_phoneBindImgButton
				.setOnClickListener(new PhoneBindImgButtonOnClickListener());

		// init and create phone bind alertDialog
		ALERTDIALOG_BUILDER.setView(getLayoutInflater().inflate(
				R.layout.phonebind_dialog_layout, null));
		_mPhoneBindAlertDialog = ALERTDIALOG_BUILDER.create();

		// bind phone bind alertDialog on cancel listener
		_mPhoneBindAlertDialog
				.setOnCancelListener(new PhoneBindAlertDialogOnCancelListener());

		// get email bind image button and bind there on click listener
		ImageButton _emailBindImgButton = (ImageButton) findViewById(R.id.set_emailBind_imageButton);
		_emailBindImgButton
				.setOnClickListener(new EmailBindImgButtonOnClickListener());

		// init and create email bind alertDialog
		ALERTDIALOG_BUILDER.setView(getLayoutInflater().inflate(
				R.layout.emailbind_dialog_layout, null));
		_mEmailBindAlertDialog = ALERTDIALOG_BUILDER.create();

		// disable email bind image button, because the email bind function not
		// implementation now
		_emailBindImgButton.setEnabled(false);

		// get binded account login button
		Button _bindedAccountLoginBtn = (Button) findViewById(R.id.set_accountLogin_button);

		// bind its on click listener
		_bindedAccountLoginBtn
				.setOnClickListener(new BindedAccountLoginButtonOnClickListener());

		// init and create binded account login alertDialog
		ALERTDIALOG_BUILDER.setView(getLayoutInflater().inflate(
				R.layout.bindedaccount_login_dialog_layout, null));
		_mBindedAccountLoginAlertDialog = ALERTDIALOG_BUILDER.create();

		// test by ares
		((TextView) findViewById(R.id.set_account6deviceIdLabel_textView))
				.setText(R.string.deviceId_labelTextView_text);
		((TextView) findViewById(R.id.set_account6deviceId_textView))
				.setText(DeviceUtils.combinedUniqueId());
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
			// show phone bind alertDialog
			_mPhoneBindAlertDialog.show();

			// bind get phone bind verification code button on click listener
			((Button) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_getVerificationCodeBtn))
					.setOnClickListener(new PhoneBindAlertDialogGetVerificationCodeButtonOnClickListener());

			// bind phone bind alertDialog bind cancel and confirm button on
			// click listener
			((Button) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_cancelBindBtn))
					.setOnClickListener(new PhoneBindAlertDialogBindCancelButtonOnClickListener());
			((Button) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_confirmBindBtn))
					.setOnClickListener(new PhoneBindAlertDialogBindConfirmButtonOnClickListener());
		}

	}

	// phone bind alertDialog get verification code button on click listener
	class PhoneBindAlertDialogGetVerificationCodeButtonOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(final View v) {
			// 60 seconds per minute
			final Integer SECONDS_PER_MINUTE = 60;

			// clear get verification code again counter
			_mGetVerificationCodeAgainCounter = 0;

			// set get phone bind verification code button disable
			v.setEnabled(false);

			// update get phone bind verification code button title every 1
			// second
			GET_PHONEBIND_VERIFICATIONCODEAGAIN_TIMER
					.schedule(
							_mGetPhoneBindVerificationCodeAgainTimerTask = new TimerTask() {

								@Override
								public void run() {
									// handle on UI thread with handle
									UPDATE_PHONEBIND_VERIFICATIONCODEBTNTITLE_HANDLE
											.post(new Runnable() {

												@Override
												public void run() {
													// increase get verification
													// code again counter and
													// check
													if (SECONDS_PER_MINUTE > ++_mGetVerificationCodeAgainCounter) {
														// update get phone bind
														// verification code
														// button title
														((Button) v)
																.setText(getResources()
																		.getString(
																				R.string.phoneBind_alertDialog_getVerificationCodeBtn_disableTitle)
																		.replace(
																				"***",
																				((Integer) (SECONDS_PER_MINUTE - _mGetVerificationCodeAgainCounter))
																						.toString()));
													} else {
														// cancel the get phone
														// bind verification
														// code again timer task
														_mGetPhoneBindVerificationCodeAgainTimerTask
																.cancel();

														// enable get phone bind
														// verification code
														// button
														v.setEnabled(true);

														// update get phone bind
														// verification code
														// button title
														((Button) v)
																.setText(R.string.phoneBind_alertDialog_getVerificationCodeBtn_normalTitle);
													}
												}
											});
								}
							}, 0, 1000);

			Log.d(LOG_TAG, "Get phone bind verification code");

			// get phone bind verification code
			// ??
		}
	}

	// phone bind alertDialog on cancel listener
	class PhoneBindAlertDialogOnCancelListener implements OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			// get phone bind alertDialog phone editText
			EditText _phoneBindAlertDialogPhoneEditText = (EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_phoneEditText);

			// set phone editText on focus
			_phoneBindAlertDialogPhoneEditText.setFocusable(true);

			// clear all editText text
			_phoneBindAlertDialogPhoneEditText.setText("");
			((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_verificationCodeEditText))
					.setText("");
			((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_bindingAccount_loginPwd_editText))
					.setText("");
			((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_bindingAccount_loginConfirmPwd_editText))
					.setText("");

			// check and cancel the get phone bind verification code again timer
			// task
			if (null != _mGetPhoneBindVerificationCodeAgainTimerTask) {
				_mGetPhoneBindVerificationCodeAgainTimerTask.cancel();

				// get get phone bind verification code button
				Button _getPhoneBindVerificationCodeBtn = (Button) _mPhoneBindAlertDialog
						.findViewById(R.id.pbd_phoneBind_getVerificationCodeBtn);

				// enable get phone bind verification code button
				_getPhoneBindVerificationCodeBtn.setEnabled(true);

				// update get phone bind verification code button title
				_getPhoneBindVerificationCodeBtn
						.setText(R.string.phoneBind_alertDialog_getVerificationCodeBtn_normalTitle);
			}
		}

	}

	// phone bind alertDialog bind cancel button on click listener
	class PhoneBindAlertDialogBindCancelButtonOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// cancel phone bind alertDialog
			_mPhoneBindAlertDialog.cancel();
		}

	}

	// phone bind alertDialog bind confirm button on click listener
	class PhoneBindAlertDialogBindConfirmButtonOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// cancel phone bind alertDialog
			_mPhoneBindAlertDialog.cancel();

			Log.d(LOG_TAG, "Phone bind");

			// bind phone
			// ??
		}

	}

	// email bind image button on click listener
	class EmailBindImgButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// show email bind alertDialog
			_mEmailBindAlertDialog.show();
		}

	}

	// binded account login button on click listener
	class BindedAccountLoginButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// show binded account login alertDialog
			_mBindedAccountLoginAlertDialog.show();

			// bind binded account login alertDialog login cancel and confirm
			// button on click listener
			((Button) _mBindedAccountLoginAlertDialog
					.findViewById(R.id.bald_bindedAccount_loginCancelBtn))
					.setOnClickListener(new BindedAccountLoginAlertDialogLoginCancelButtonOnClickListener());
			((Button) _mBindedAccountLoginAlertDialog
					.findViewById(R.id.bald_bindedAccount_loginConfirmBtn))
					.setOnClickListener(new BindedAccountLoginAlertDialogLoginConfirmButtonOnClickListener());
		}

	}

	// binded account login alertDialog login cancel button on click listener
	class BindedAccountLoginAlertDialogLoginCancelButtonOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(View v) {
			// cancel binded account login alertDialog
			_mBindedAccountLoginAlertDialog.cancel();
		}

	}

	// binded account login alertDialog login confirm button on click listener
	class BindedAccountLoginAlertDialogLoginConfirmButtonOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(View v) {
			// cancel binded account login alertDialog
			_mBindedAccountLoginAlertDialog.cancel();

			Log.d(LOG_TAG, "Binded account login");

			// binded account login
			// ??
		}

	}

}
