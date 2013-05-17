package com.richitec.imeeting.simple.assistant.httprequestlistener;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;
import com.richitec.imeeting.simple.R;
import com.richitec.imeeting.simple.SimpleIMeetingAppLaunchActivity;
import com.richitec.imeeting.simple.assistant.SettingActivity;
import com.richitec.imeeting.simple.user.SIMUserExtension;
import com.richitec.imeeting.simple.user.SIMUserExtension.SIMUserExtAttributes;

public class BindedAccountLoginHttpRequestListener extends
		OnHttpRequestListener {

	private static final String LOG_TAG = BindedAccountLoginHttpRequestListener.class
			.getCanonicalName();

	// activity context
	private Context _mContext;

	// binded account login type
	private BindedAccountLoginType _mLoginType;

	// binded account manual login user name and password
	private String _mManualLoginUserName;
	private String _mManualLoginPwd;

	public BindedAccountLoginHttpRequestListener(Context context,
			BindedAccountLoginType loginType)
			throws BALContextNotActivityException {
		super();

		// set activity context and login type
		_mContext = context;
		_mLoginType = loginType;

		// check the context if it is null or not activity
		if (null == context || !(context instanceof Activity)) {
			// activity context is null or the context not activity
			throw new BALContextNotActivityException("the context = " + context);
		}
	}

	public BindedAccountLoginHttpRequestListener(Context context)
			throws BALContextNotActivityException {
		this(context, BindedAccountLoginType.AUTO);
	}

	@Deprecated
	public BindedAccountLoginHttpRequestListener()
			throws BALContextNotActivityException {
		super();

		// activity context is null
		throw new BALContextNotActivityException("the context is null");
	}

	// set binded account manual login user name and password
	public BindedAccountLoginHttpRequestListener setManualLoginUserName7Pwd(
			String manualLoginUserName, String manualLoginPwd) {
		// save binded account manual login user name and password
		_mManualLoginUserName = manualLoginUserName;
		_mManualLoginPwd = manualLoginPwd;

		return this;
	}

	@Override
	public void onFinished(HttpResponseResult responseResult) {
		// check binded account login type
		if (null != _mLoginType
				&& BindedAccountLoginType.MANUAL.equals(_mLoginType)) {
			// close binded account user login process dialog
			((SettingActivity) _mContext)
					.closeAsynchronousHttpRequestProgressDialog();
		}

		// get http response entity string json data
		JSONObject _respJsonData = JSONUtils.toJSONObject(responseResult
				.getResponseText());

		Log.d(LOG_TAG,
				"Send binded account login post http request successful, response json data = "
						+ _respJsonData);

		// get the result from http response json data
		String _result = JSONUtils.getStringFromJSONObject(
				_respJsonData,
				_mContext.getResources().getString(
						R.string.bg_server_req_resp_result));

		// check result
		if (null != _result && 0 == Integer.parseInt(_result)) {
			// get binded account login response userId, userKey, nickname and
			// bindStatus
			String _bindedAccountLoginRespUserId = JSONUtils
					.getStringFromJSONObject(
							_respJsonData,
							_mContext
									.getResources()
									.getString(
											R.string.bg_server_login6ContactInfoBindReq_resp_userId));
			String _bindedAccountLoginRespUserKey = JSONUtils
					.getStringFromJSONObject(
							_respJsonData,
							_mContext
									.getResources()
									.getString(
											R.string.bg_server_login6ContactInfoBindReq_resp_userKey));
			String _bindedAccountLoginRespNickname = JSONUtils
					.getStringFromJSONObject(
							_respJsonData,
							_mContext
									.getResources()
									.getString(
											R.string.bg_server_login6ContactInfoBindReq_resp_nickname));
			String _bindedAccountLoginRespBindStatus = JSONUtils
					.getStringFromJSONObject(
							_respJsonData,
							_mContext
									.getResources()
									.getString(
											R.string.bg_server_login6reg7LoginWithDeviceId6PhoneBindReq_resp_bindStatus));

			Log.d(LOG_TAG,
					"Binded account login successful, response user id = "
							+ _bindedAccountLoginRespUserId + ", user key = "
							+ _bindedAccountLoginRespUserKey + ", nickname = "
							+ _bindedAccountLoginRespNickname
							+ " and bind status = "
							+ _bindedAccountLoginRespBindStatus);

			// check binded account login type
			if (null != _mLoginType
					&& BindedAccountLoginType.MANUAL.equals(_mLoginType)) {
				// cancel binded account manual login alertDialog
				((SettingActivity) _mContext)
						.getBindedAccountManualLoginAlertDialog().cancel();

				// save manual login user name and password to local storage
				DataStorageUtils.putObject(
						SIMUserExtAttributes.BIND_CONTACTINFO.name(),
						_mManualLoginUserName);
				DataStorageUtils.putObject(User.password.name(),
						StringUtils.md5(_mManualLoginPwd));

				// generate manual login user and set password and bind contact
				// info
				UserBean _manualLoginUser = new UserBean();
				_manualLoginUser.setPassword(StringUtils.md5(_mManualLoginPwd));
				SIMUserExtension.setUserBindContactInfo(_manualLoginUser,
						_mManualLoginUserName);

				// add manual login user to user manager
				UserManager.getInstance().setUser(_manualLoginUser);
			}

			// get the user from user manager and complete other
			// attributes
			UserBean _bindedAccountLoginRetUser = UserManager.getInstance()
					.getUser();
			_bindedAccountLoginRetUser.setName(_bindedAccountLoginRespUserId);
			_bindedAccountLoginRetUser
					.setUserKey(_bindedAccountLoginRespUserKey);
			SIMUserExtension.setUserNickname(_bindedAccountLoginRetUser,
					_bindedAccountLoginRespNickname);
			SIMUserExtension.setUserContactsInfoBeBinded(
					_bindedAccountLoginRetUser,
					_bindedAccountLoginRespBindStatus);

			// check binded account login type
			if (null != _mLoginType
					&& BindedAccountLoginType.MANUAL.equals(_mLoginType)) {
				// update my account and contacts info bind group UI
				((SettingActivity) _mContext)
						.updateMyAccount7ContactsInfoBindGroupUI(true);
			}
		} else {
			processBindedAccountLoginException(responseResult);
		}
	}

	@Override
	public void onFailed(HttpResponseResult responseResult) {
		Log.e(LOG_TAG, "Send binded account login post http request failed!");

		processBindedAccountLoginException(responseResult);
	}

	// process binded account login exception
	private void processBindedAccountLoginException(
			HttpResponseResult responseResult) {
		// get and check response result
		switch (responseResult.getStatusCode()) {
		case HttpStatus.SC_ACCEPTED:
		case HttpStatus.SC_CREATED:
		case HttpStatus.SC_OK:
			// get the result from http response json data
			String _result = JSONUtils.getStringFromJSONObject(
					JSONUtils.toJSONObject(responseResult.getResponseText()),
					_mContext.getResources().getString(
							R.string.bg_server_req_resp_result));

			// check the result
			if (null != _result) {
				switch (Integer.parseInt(_result)) {
				case 0:
					return;

				case 1:
				default:
					Log.e(LOG_TAG,
							"Bined account login failed, user name or password is wrong");

					// check binded account login type
					if (null != _mLoginType
							&& BindedAccountLoginType.MANUAL
									.equals(_mLoginType)) {
						// show binded account login user name or password wrong
						// toast
						Toast.makeText(
								_mContext,
								R.string.toast_bindedAccount_login_userName6Pwd_wrong,
								Toast.LENGTH_LONG).show();
					} else {
						// get simple iMeeting application launch toast
						Toast _simpleIMeetingAppLaunchToast = ((SimpleIMeetingAppLaunchActivity) _mContext)
								.getSimpleIMeetingAppLaunchToast();

						// set text
						_simpleIMeetingAppLaunchToast
								.setText(R.string.toast_bindedAccount_login_userName6Pwd_wrong);

						// show binded account login user name or password wrong
						// toast
						_simpleIMeetingAppLaunchToast.show();
					}
					break;
				}
			} else {
				Log.e(LOG_TAG,
						"Binded account login failed, bg_server return result is null");

				// check binded account login type
				if (null != _mLoginType
						&& BindedAccountLoginType.MANUAL.equals(_mLoginType)) {
					// show binded account login failed toast
					Toast.makeText(_mContext, R.string.toast_request_exception,
							Toast.LENGTH_LONG).show();
				}
			}
			break;

		default:
			// check binded account login type
			if (null != _mLoginType
					&& BindedAccountLoginType.MANUAL.equals(_mLoginType)) {
				// show binded account login failed toast
				Toast.makeText(_mContext, R.string.toast_request_exception,
						Toast.LENGTH_LONG).show();
			}
			break;
		}

		// check binded account login type
		if (null != _mLoginType
				&& BindedAccountLoginType.AUTO.equals(_mLoginType)) {
			Log.w(LOG_TAG,
					"Register and login using device combined unique id because binded account login failed");

			// register and login using device combined unique id
			((SimpleIMeetingAppLaunchActivity) _mContext)
					.sendReg7LoginWithDeviceCombinedUniqueIdHttpRequest();
		}
	}

	// inner class
	// binded account login type
	public enum BindedAccountLoginType {
		AUTO, MANUAL
	}

	// binded account login http request listener context not activity exception
	class BALContextNotActivityException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -916775482996632193L;

		public BALContextNotActivityException(String detailMessage) {
			super(
					"Binded account login http request listener context not activity, the reason is "
							+ detailMessage
							+ ". Please use BindedAccountLoginHttpRequestListener constructor with activity context param instead");
		}

	}

}
