package com.richitec.imeeting.simple.assistant;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.DeviceUtils;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;
import com.richitec.imeeting.simple.R;
import com.richitec.imeeting.simple.assistant.httprequestlistener.BindedAccountLoginHttpRequestListener;
import com.richitec.imeeting.simple.assistant.httprequestlistener.BindedAccountLoginHttpRequestListener.BindedAccountLoginType;
import com.richitec.imeeting.simple.assistant.httprequestlistener.RegAndLoginWithDeviceIdHttpRequestListener;
import com.richitec.imeeting.simple.assistant.httprequestlistener.RegAndLoginWithDeviceIdHttpRequestListener.Reg7LoginWithDeviceIdType;
import com.richitec.imeeting.simple.customcomponent.SimpleIMeetingNavigationActivity;
import com.richitec.imeeting.simple.user.SIMUserExtension;
import com.richitec.imeeting.simple.user.SIMUserExtension.SIMUserExtAttributes;
import com.richitec.imeeting.simple.utils.AppDataSaveRestoreUtils;

public class SettingActivity extends SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SettingActivity.class
			.getCanonicalName();

	// my account changed flag, login or logout
	public static final String SETTING_CHANGEDMYACCOUNT_KEY = "setting_activity_changed_myaccount";

	// asynchronous http request progress dialog
	private ProgressDialog _mAsynchronousHttpRequestProgressDialog;

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
		// restore application data
		if (null != savedInstanceState) {
			AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);
		}

		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.setting_activity_layout);

		// set title text
		setTitle(R.string.setting_nav_title_text);

		// update my account and contacts info bind group UI
		updateMyAccount7ContactsInfoBindGroupUI(false);

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

		// bind binded account login alertDialog on cancel listener
		_mBindedAccountLoginAlertDialog
				.setOnCancelListener(new BindedAccountLoginAlertDialogOnCancelListener());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save application data
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);
	}

	public AlertDialog getBindedAccountManualLoginAlertDialog() {
		return _mBindedAccountLoginAlertDialog;
	}

	// update my account and contacts info bind group UI with my account changed
	// flag
	public void updateMyAccount7ContactsInfoBindGroupUI(boolean myAccountChanged) {
		// get login user
		UserBean _loginUser = UserManager.getInstance().getUser();

		// get and check login user bind contact info
		String _bindContactInfo = SIMUserExtension
				.getUserBindContactInfo(_loginUser);

		// get contacts info binded
		String _contactsInfoBeBinded = SIMUserExtension
				.getUserContactsInfoBeBinded(_loginUser);

		// set account or device id or contacts info be binded textView text and
		// its label textView text
		((TextView) findViewById(R.id.set_account6deviceId6contactsInfoBeBinded_textView))
				.setText(null != _bindContactInfo ? _bindContactInfo
						: null != _contactsInfoBeBinded ? _contactsInfoBeBinded
								: DeviceUtils.combinedUniqueId());
		((TextView) findViewById(R.id.set_account6deviceId6contactsInfoBeBindedLabel_textView))
				.setText(null != _bindContactInfo ? R.string.myAccount_labelTextView_text
						: null != _contactsInfoBeBinded ? R.string.contactsInfoBeBinded_labelTextView_text
								: R.string.deviceId_labelTextView_text);

		// show or hide binded account logout button
		((Button) findViewById(R.id.set_accountLogout_button))
				.setVisibility(null != _bindContactInfo ? View.VISIBLE
						: View.GONE);

		// get and check contacts info binded type
		String _contactsInfoTypeBeBinded = SIMUserExtension
				.getUserContactsInfoTypeBeBinded(_loginUser);
		((ImageButton) findViewById(R.id.set_phoneBind_imageButton))
				.setEnabled(null != _contactsInfoTypeBeBinded ? getResources()
						.getString(
								R.string.bg_server_login6reg7LoginWithDeviceId6PhoneBind_phoneBindedStatus)
						.equalsIgnoreCase(_contactsInfoTypeBeBinded) ? false
						: true
						: true);

		// check my account changed or not
		if (myAccountChanged) {
			// set my account changed flag to setting intent extra data
			getIntent().putExtra(SETTING_CHANGEDMYACCOUNT_KEY, true);
		}
	}

	// close asynchronous http request process dialog
	public void closeAsynchronousHttpRequestProgressDialog() {
		// check and dismiss asynchronous http request process dialog
		if (null != _mAsynchronousHttpRequestProgressDialog) {
			_mAsynchronousHttpRequestProgressDialog.dismiss();
		}
	}

	// check and cancel the get phone bind verification code again timer task
	private void cancelGetPhoneBindVerificationCodeAgainTimerTask() {
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

	// inner class
	// // data ownership mode
	// public enum DataOwnershipMode {
	// DEVICE, BINDED_ACCOUNT
	// }

	// binded account logout button on click listener
	class BindedAccountLogoutButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// init and show binded account logout process dialog
			_mAsynchronousHttpRequestProgressDialog = ProgressDialog
					.show(SettingActivity.this,
							null,
							getString(R.string.asynchronousHttpRequest_progressDialog_message),
							true);

			// revert to register and login with device combined unique id
			// generate register and login with device combined unique id param
			// map
			Map<String, String> _reg7LoginWithDeviceIdParamMap = new HashMap<String, String>();

			// set some params
			_reg7LoginWithDeviceIdParamMap
					.put(getResources()
							.getString(
									R.string.bg_server_reg7LoginWithDeviceId6ContactInfoBind_deviceId),
							DeviceUtils.combinedUniqueId());
			_reg7LoginWithDeviceIdParamMap.put(
					getResources().getString(R.string.bg_server_deviceBrand),
					Build.BRAND);
			_reg7LoginWithDeviceIdParamMap.put(
					getResources().getString(R.string.bg_server_deviceModel),
					Build.MODEL);
			_reg7LoginWithDeviceIdParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceOS_version),
					Build.VERSION.RELEASE);
			_reg7LoginWithDeviceIdParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceOS_APILevel),
					Build.VERSION.SDK);
			_reg7LoginWithDeviceIdParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceDisplayScreen_width),
					Integer.toString(DisplayScreenUtils.screenWidth()));
			_reg7LoginWithDeviceIdParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceDisplayScreen_height),
					Integer.toString(DisplayScreenUtils.screenHeight()));

			// post the http request
			try {
				HttpUtils
						.postRequest(
								getResources().getString(R.string.server_url)
										+ getResources()
												.getString(
														R.string.reg7login_withDeviceId_url),
								PostRequestFormat.URLENCODED,
								_reg7LoginWithDeviceIdParamMap,
								null,
								HttpRequestType.ASYNCHRONOUS,
								new RegAndLoginWithDeviceIdHttpRequestListener(
										SettingActivity.this,
										Reg7LoginWithDeviceIdType.BINDEDACCOUNT_LOGOUT));
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send register and login with device combined id post http request error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
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

			// get phone bind will be binded phone number
			String _willBeBindedPhone = ((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_phoneEditText)).getText()
					.toString();

			// check phone bind will be binded phone number
			if (null == _willBeBindedPhone
					|| "".equalsIgnoreCase(_willBeBindedPhone)) {
				Log.w(LOG_TAG, "There is no phone to being binded");

				Toast.makeText(SettingActivity.this,
						R.string.toast_phoneBind_phoneNumber_null,
						Toast.LENGTH_SHORT).show();
			} else {
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
														// increase get
														// verification code
														// again counter and
														// check
														if (SECONDS_PER_MINUTE > ++_mGetVerificationCodeAgainCounter) {
															// update get phone
															// bind verification
															// code button title
															((Button) v)
																	.setText(getResources()
																			.getString(
																					R.string.phoneBind_alertDialog_getVerificationCodeBtn_disableTitle)
																			.replace(
																					"***",
																					((Integer) (SECONDS_PER_MINUTE - _mGetVerificationCodeAgainCounter))
																							.toString()));
														} else {
															// cancel the get
															// phone bind
															// verification code
															// again timer task
															_mGetPhoneBindVerificationCodeAgainTimerTask
																	.cancel();

															// enable get phone
															// bind verification
															// code button
															v.setEnabled(true);

															// update get phone
															// bind verification
															// code button title
															((Button) v)
																	.setText(R.string.phoneBind_alertDialog_getVerificationCodeBtn_normalTitle);
														}
													}
												});
									}
								}, 0, 1000);

				// get phone bind verification code
				// generate get phone bind verification code param map
				Map<String, String> _getPhoneBindVerificationCodeParamMap = new HashMap<String, String>();

				// set some params
				_getPhoneBindVerificationCodeParamMap.put(getResources()
						.getString(R.string.bg_server_phoneBind_phoneNumber),
						_willBeBindedPhone);

				// post the http request
				HttpUtils
						.postRequest(
								getResources().getString(R.string.server_url)
										+ getResources()
												.getString(
														R.string.retrieve_phoneBind_authCode_url),
								PostRequestFormat.URLENCODED,
								_getPhoneBindVerificationCodeParamMap,
								null,
								HttpRequestType.ASYNCHRONOUS,
								new GetPhoneBindVerificationCodeHttpRequestListener());
			}
		}
	}

	// get phone bind verification code http request listener
	class GetPhoneBindVerificationCodeHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// get http response entity string json data
			JSONObject _respJsonData = JSONUtils.toJSONObject(responseResult
					.getResponseText());

			Log.d(LOG_TAG,
					"Send get phone bind verification code post http request successful, response json data = "
							+ _respJsonData);

			// get the result from http response json data
			String _result = JSONUtils.getStringFromJSONObject(_respJsonData,
					getResources()
							.getString(R.string.bg_server_req_resp_result));

			// check result
			if (null != _result && 0 == Integer.parseInt(_result)) {
				Log.d(LOG_TAG, "Get phone bind verification code successful!");
			} else {
				processGetPhoneBindVerificationCodeException(responseResult);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG,
					"Send get phone bind verification code post http request failed!");

			processGetPhoneBindVerificationCodeException(responseResult);
		}

		// process get phone bind verification code exception
		private void processGetPhoneBindVerificationCodeException(
				HttpResponseResult responseResult) {
			// make get phone bind verification code failed toast
			Toast _getPhoneBindVerificationCodeFailedToast = Toast.makeText(
					SettingActivity.this, R.string.toast_request_exception,
					Toast.LENGTH_LONG);

			// get and check response result
			switch (responseResult.getStatusCode()) {
			case HttpStatus.SC_ACCEPTED:
			case HttpStatus.SC_CREATED:
			case HttpStatus.SC_OK:
				// get the result from http response json data
				String _result = JSONUtils
						.getStringFromJSONObject(
								JSONUtils.toJSONObject(responseResult
										.getResponseText()),
								getResources().getString(
										R.string.bg_server_req_resp_result));

				// check the result
				if (null != _result) {
					switch (Integer.parseInt(_result)) {
					case 0:
						break;

					case 1:
						Log.e(LOG_TAG,
								"Get phone bined verification code failed, the being binded phone number is empty");

						// show get phone bind verification code phone empty
						// toast
						Toast.makeText(SettingActivity.this,
								R.string.toast_phoneBind_phoneNumber_null,
								Toast.LENGTH_LONG).show();
						break;

					case 2:
						Log.d(LOG_TAG,
								"Get phone bined verification code failed, the being binded phone number is invalid");

						// show get phone bind verification code phone invalid
						// toast
						Toast.makeText(SettingActivity.this,
								R.string.toast_phoneBind_phoneNumber_invalid,
								Toast.LENGTH_LONG).show();
						break;

					case 3:
						Log.d(LOG_TAG,
								"Get phone bind verification code failed, the being bined phone number had been binded with other device");

						// show get phone bind verification code phone had been
						// binded toast
						Toast.makeText(SettingActivity.this,
								R.string.toast_phoneBind_phoneNumber_binded,
								Toast.LENGTH_LONG).show();
						break;

					default:
						Log.e(LOG_TAG,
								"Get phone bind verification code failed, bg_server return result is unrecognized");

						// show get phone bind verification code failed toast
						_getPhoneBindVerificationCodeFailedToast.show();
						break;
					}
				} else {
					Log.e(LOG_TAG,
							"Get phone bind verification code failed, bg_server return result is null");

					// show get phone bind verification code failed toast
					_getPhoneBindVerificationCodeFailedToast.show();
				}
				break;

			default:
				// show get phone bind verification code failed toast
				_getPhoneBindVerificationCodeFailedToast.show();
				break;
			}

			// cancel the get phone bind verification code again timer task
			cancelGetPhoneBindVerificationCodeAgainTimerTask();
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

			// cancel the get phone bind verification code again timer task
			cancelGetPhoneBindVerificationCodeAgainTimerTask();
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
			// get and check phone bind will be binded phone number
			String _willBeBindedPhone = ((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_phoneEditText)).getText()
					.toString();
			if (null == _willBeBindedPhone
					|| "".equalsIgnoreCase(_willBeBindedPhone)) {
				Log.w(LOG_TAG, "There is no phone to being binded");

				Toast.makeText(SettingActivity.this,
						R.string.toast_phoneBind_phoneNumber_null,
						Toast.LENGTH_SHORT).show();

				return;
			}

			// get and check verification code
			String _verificationCode = ((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_phoneBind_verificationCodeEditText))
					.getText().toString();
			if (null == _verificationCode
					|| "".equalsIgnoreCase(_verificationCode)) {
				Log.w(LOG_TAG,
						"There is no verification code for binding phone");

				Toast.makeText(SettingActivity.this,
						R.string.toast_phoneBind_verificationCode_null,
						Toast.LENGTH_SHORT).show();

				return;
			}

			// get and check login password
			String _loginPwd = ((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_bindingAccount_loginPwd_editText))
					.getText().toString();
			if (null == _loginPwd || "".equalsIgnoreCase(_loginPwd)) {
				Log.w(LOG_TAG, "There is no login password for binding phone");

				Toast.makeText(SettingActivity.this,
						R.string.toast_phoneBind_loginPassword_null,
						Toast.LENGTH_SHORT).show();

				return;
			}

			// get and check login confirmation password
			String _loginConfirmationPwd = ((EditText) _mPhoneBindAlertDialog
					.findViewById(R.id.pbd_bindingAccount_loginConfirmPwd_editText))
					.getText().toString();
			if (null == _loginConfirmationPwd
					|| "".equalsIgnoreCase(_loginConfirmationPwd)) {
				Log.w(LOG_TAG,
						"There is no login confirmation password for binding phone");

				Toast.makeText(SettingActivity.this,
						R.string.toast_phoneBind_loginConfirmationPwd_null,
						Toast.LENGTH_SHORT).show();

				return;
			}

			// check two login password
			if (!_loginPwd.equalsIgnoreCase(_loginConfirmationPwd)) {
				Log.w(LOG_TAG,
						"The login password not matched with the confirmation one");

				Toast.makeText(SettingActivity.this,
						R.string.toast_phoneBind_loginTwoPwd_notMatched,
						Toast.LENGTH_LONG).show();

				return;
			}

			// init and show phone bind process dialog
			_mAsynchronousHttpRequestProgressDialog = ProgressDialog
					.show(SettingActivity.this,
							null,
							getString(R.string.asynchronousHttpRequest_progressDialog_message),
							true);

			// phone bind confirm
			// generate confirm bind phone param map
			Map<String, String> _confirmBindPhoneParamMap = new HashMap<String, String>();

			// set some params
			_confirmBindPhoneParamMap.put(
					getResources().getString(
							R.string.bg_server_phoneBind_phoneNumber),
					_willBeBindedPhone);
			_confirmBindPhoneParamMap.put(
					getResources().getString(
							R.string.bg_server_phoneBind_verificationCode),
					_verificationCode);
			_confirmBindPhoneParamMap.put(
					getResources().getString(
							R.string.bg_server_phoneBind_loginPassword),
					_loginPwd);
			_confirmBindPhoneParamMap.put(
					getResources().getString(
							R.string.bg_server_phoneBind_loginConfirmationPwd),
					_loginConfirmationPwd);
			_confirmBindPhoneParamMap
					.put(getResources()
							.getString(
									R.string.bg_server_reg7LoginWithDeviceId6ContactInfoBind_deviceId),
							DeviceUtils.combinedUniqueId());

			// post the http request
			HttpUtils.postRequest(getResources().getString(R.string.server_url)
					+ getResources().getString(R.string.user_register_url),
					PostRequestFormat.URLENCODED, _confirmBindPhoneParamMap,
					null, HttpRequestType.ASYNCHRONOUS,
					new PhoneBindConfirmHttpRequestListener());
		}

	}

	// phone bind confirm http request listener
	class PhoneBindConfirmHttpRequestListener extends OnHttpRequestListener {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// close phone bind process dialog
			closeAsynchronousHttpRequestProgressDialog();

			// get http response entity string json data
			JSONObject _respJsonData = JSONUtils.toJSONObject(responseResult
					.getResponseText());

			Log.d(LOG_TAG,
					"Send phone bind confirm post http request successful, response json data = "
							+ _respJsonData);

			// get the result from http response json data
			String _result = JSONUtils.getStringFromJSONObject(_respJsonData,
					getResources()
							.getString(R.string.bg_server_req_resp_result));

			// check result
			if (null != _result && 0 == Integer.parseInt(_result)) {
				// cancel phone bind alertDialog
				_mPhoneBindAlertDialog.cancel();

				// get binded phone number, password and add them to local data
				// storage
				String _bindedPhone = ((EditText) _mPhoneBindAlertDialog
						.findViewById(R.id.pbd_phoneBind_phoneEditText))
						.getText().toString();
				DataStorageUtils.putObject(
						SIMUserExtAttributes.BIND_CONTACTINFO.name(),
						_bindedPhone);

				String _loginPwd = StringUtils
						.md5(((EditText) _mPhoneBindAlertDialog
								.findViewById(R.id.pbd_bindingAccount_loginPwd_editText))
								.getText().toString());
				DataStorageUtils.putObject(User.password.name(), _loginPwd);

				// get confirm bind phone response userId and userKey
				String _confirmBindPhoneRespUserId = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								getResources()
										.getString(
												R.string.bg_server_login6ContactInfoBindReq_resp_userId));
				String _confirmBindPhoneRespUserKey = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								getResources()
										.getString(
												R.string.bg_server_login6ContactInfoBindReq_resp_userKey));
				String _confirmBindPhoneRespBindStatus = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								getResources()
										.getString(
												R.string.bg_server_login6reg7LoginWithDeviceId6PhoneBindReq_resp_bindStatus));

				Log.d(LOG_TAG, "Bind phone successful, response user id = "
						+ _confirmBindPhoneRespUserId + ", user key = "
						+ _confirmBindPhoneRespUserKey
						+ " and contacts info be binded = "
						+ _confirmBindPhoneRespBindStatus);

				// generate new binded generate user bean and set other
				// attributes
				UserBean _newBindedGenerateUser = new UserBean(
						_confirmBindPhoneRespUserId, _loginPwd,
						_confirmBindPhoneRespUserKey);
				SIMUserExtension.setUserBindContactInfo(_newBindedGenerateUser,
						_bindedPhone);
				SIMUserExtension
						.setUserContactsInfoTypeBeBinded(
								_newBindedGenerateUser,
								_confirmBindPhoneRespBindStatus);

				// add it to user manager
				UserManager.getInstance().setUser(_newBindedGenerateUser);

				// update my account and contacts info bind group UI
				updateMyAccount7ContactsInfoBindGroupUI(false);
			} else {
				processConfirmBindPhoneException(responseResult);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG, "Send phone bind confirm post http request failed!");

			processConfirmBindPhoneException(responseResult);
		}

		// process confirm bind phone exception
		private void processConfirmBindPhoneException(
				HttpResponseResult responseResult) {
			// make confirm bind phone failed toast
			Toast _confirmBindPhoneFailedToast = Toast.makeText(
					SettingActivity.this, R.string.toast_request_exception,
					Toast.LENGTH_LONG);

			// get and check response result
			switch (responseResult.getStatusCode()) {
			case HttpStatus.SC_ACCEPTED:
			case HttpStatus.SC_CREATED:
			case HttpStatus.SC_OK:
				// get the result from http response json data
				String _result = JSONUtils
						.getStringFromJSONObject(
								JSONUtils.toJSONObject(responseResult
										.getResponseText()),
								getResources().getString(
										R.string.bg_server_req_resp_result));

				// check the result
				if (null != _result) {
					switch (Integer.parseInt(_result)) {
					case 0:
						break;

					case 1:
						Log.e(LOG_TAG,
								"Confirm bind phone failed, the verification code is empty");

						// show confirm bind phone verification code phone empty
						// toast
						Toast.makeText(SettingActivity.this,
								R.string.toast_phoneBind_verificationCode_null,
								Toast.LENGTH_LONG).show();
						break;

					case 2:
						Log.e(LOG_TAG,
								"Confirm bind phone failed, the verification code is wrong");

						// show confirm bind phone verification code wrong toast
						Toast.makeText(
								SettingActivity.this,
								R.string.toast_phoneBind_verificationCode_wrong,
								Toast.LENGTH_LONG).show();
						break;

					case 5:
						Log.e(LOG_TAG,
								"Confirm bind phone failed, the login password not matched with the confirmation one");

						// show confirm bind phone verification code two
						// password not matched toast
						Toast.makeText(
								SettingActivity.this,
								R.string.toast_phoneBind_loginTwoPwd_notMatched,
								Toast.LENGTH_LONG).show();
						break;

					case 6:
					case 7:
						Log.e(LOG_TAG,
								"Confirm bind phone failed, the verification code is timeout");

						// show confirm bind phone verification code timeout
						// toast
						Toast.makeText(
								SettingActivity.this,
								R.string.toast_phoneBind_verificationCode_timeout,
								Toast.LENGTH_LONG).show();
						break;

					default:
						Log.e(LOG_TAG,
								"Confirm bind phone failed, bg_server return result is unrecognized");

						// show confirm bind phone failed toast
						_confirmBindPhoneFailedToast.show();
						break;
					}
				} else {
					Log.e(LOG_TAG,
							"Confirm bind phone failed, bg_server return result is null");

					// show confirm bind phone failed toast
					_confirmBindPhoneFailedToast.show();
				}
				break;

			default:
				// show confirm bind phone failed toast
				_confirmBindPhoneFailedToast.show();
				break;
			}
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

	// binded account login alertDialog on cancel listener
	class BindedAccountLoginAlertDialogOnCancelListener implements
			OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			// get binded account login alertDialog login user name editText
			EditText _bindedAccountAlertDialogLoginUserNameEditText = (EditText) _mBindedAccountLoginAlertDialog
					.findViewById(R.id.bald_bindedAccount_loginUserName_editText);

			// set login user name editText on focus
			_bindedAccountAlertDialogLoginUserNameEditText.setFocusable(true);

			// clear all editText text
			_bindedAccountAlertDialogLoginUserNameEditText.setText("");
			((EditText) _mBindedAccountLoginAlertDialog
					.findViewById(R.id.bald_bindedAccount_loginPwd_editText))
					.setText("");
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
			// get and check binded account login user name
			String _loginUserName = ((EditText) _mBindedAccountLoginAlertDialog
					.findViewById(R.id.bald_bindedAccount_loginUserName_editText))
					.getText().toString();
			if (null == _loginUserName || "".equalsIgnoreCase(_loginUserName)) {
				Log.w(LOG_TAG, "There is no user name for binded account login");

				Toast.makeText(SettingActivity.this,
						R.string.toast_bindedAccount_login_userName_null,
						Toast.LENGTH_SHORT).show();

				return;
			}

			// get and check binded account login password
			String _loginPassword = ((EditText) _mBindedAccountLoginAlertDialog
					.findViewById(R.id.bald_bindedAccount_loginPwd_editText))
					.getText().toString();
			if (null == _loginPassword || "".equalsIgnoreCase(_loginPassword)) {
				Log.w(LOG_TAG, "There is no password for binded account login");

				Toast.makeText(SettingActivity.this,
						R.string.toast_bindedAccount_login_password_null,
						Toast.LENGTH_SHORT).show();

				return;
			}

			// init and show binded account user login process dialog
			_mAsynchronousHttpRequestProgressDialog = ProgressDialog
					.show(SettingActivity.this,
							null,
							getString(R.string.asynchronousHttpRequest_progressDialog_message),
							true);

			// binded account user login
			// generate binded account login param map
			Map<String, String> _bindedAccountLoginParamMap = new HashMap<String, String>();

			// set some params
			_bindedAccountLoginParamMap.put(
					getResources().getString(R.string.bg_server_userLoginName),
					_loginUserName);
			_bindedAccountLoginParamMap.put(
					getResources().getString(R.string.bg_server_userLoginPwd),
					StringUtils.md5(_loginPassword));
			_bindedAccountLoginParamMap.put(
					getResources().getString(R.string.bg_server_deviceBrand),
					Build.BRAND);
			_bindedAccountLoginParamMap.put(
					getResources().getString(R.string.bg_server_deviceModel),
					Build.MODEL);
			_bindedAccountLoginParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceOS_version),
					Build.VERSION.RELEASE);
			_bindedAccountLoginParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceOS_APILevel),
					Build.VERSION.SDK);
			_bindedAccountLoginParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceDisplayScreen_width),
					Integer.toString(DisplayScreenUtils.screenWidth()));
			_bindedAccountLoginParamMap.put(
					getResources().getString(
							R.string.bg_server_deviceDisplayScreen_height),
					Integer.toString(DisplayScreenUtils.screenHeight()));

			// post the http request
			try {
				HttpUtils.postRequest(
						getResources().getString(R.string.server_url)
								+ getResources().getString(R.string.login_url),
						PostRequestFormat.URLENCODED,
						_bindedAccountLoginParamMap, null,
						HttpRequestType.ASYNCHRONOUS,
						new BindedAccountLoginHttpRequestListener(
								SettingActivity.this,
								BindedAccountLoginType.MANUAL)
								.setManualLoginUserName7Pwd(_loginUserName,
										_loginPassword));
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send binded account login post http request error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

	}

}
