package com.richitec.simpleimeeting;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DeviceUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.simpleimeeting.talkinggroup.ContactsSelectView;
import com.richitec.simpleimeeting.talkinggroup.SimpleIMeetingMainActivity;
import com.richitec.simpleimeeting.user.SIMUserExtension;
import com.richitec.simpleimeeting.utils.AppDataSaveRestoreUtils;

public class SimpleIMeetingAppLaunchActivity extends AppLaunchActivity {

	private static final String LOG_TAG = SimpleIMeetingAppLaunchActivity.class
			.getCanonicalName();

	@Override
	public Drawable splashImg() {
		return getResources().getDrawable(R.drawable.ic_splash);
	}

	@Override
	public Intent intentActivity() {
		// define target intent activity, simple iMeeting main activity
		Intent _targetIntent = new Intent(this,
				SimpleIMeetingMainActivity.class);

		return _targetIntent;
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		AddressBookManager
				.setFilterMode(AddressBookManager.FILTER_IP_AND_CODE_PREFIX);
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		ContactsSelectView.initNamePhoneticSortedContactsInfoArray();

		// load account
		AppDataSaveRestoreUtils.loadAccount();

		// get and check the generate user from local storage
		UserBean _localStorageUser = UserManager.getInstance().getUser();

		// get local storage user bind contact info and password
		String _bindContactInfo = SIMUserExtension
				.getUserBindContactInfo(_localStorageUser);
		String _password = _localStorageUser.getPassword();

		// check user bind contact info and password
		if (null != _bindContactInfo && !"".equalsIgnoreCase(_bindContactInfo)
				&& null != _password && !"".equalsIgnoreCase(_password)) {
			// binded account login
			sendBinedAccountLoginHttpRequest(_localStorageUser);
		} else {
			// register and login using device combined unique id
			sendReg7LoginWithDeviceCombinedUniqueIdHttpRequest();
		}
	}

	// send binded account login httpRequest
	private void sendBinedAccountLoginHttpRequest(UserBean user) {
		// generate binded account login param map
		Map<String, String> _bindedAccountLoginParamMap = new HashMap<String, String>();

		// set some params
		_bindedAccountLoginParamMap.put(
				getResources().getString(R.string.bg_server_userLoginName),
				SIMUserExtension.getUserBindContactInfo(user));
		_bindedAccountLoginParamMap.put(
				getResources().getString(R.string.bg_server_userLoginPwd),
				user.getPassword());

		// post the http request
		HttpUtils.postRequest(getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.login_url),
				PostRequestFormat.URLENCODED, _bindedAccountLoginParamMap,
				null, HttpRequestType.SYNCHRONOUS,
				new BindedAccountLoginHttpRequestListener());
	}

	// send register and login with device combined unique id httpRequest
	private void sendReg7LoginWithDeviceCombinedUniqueIdHttpRequest() {
		// generate register and login with device combined unique id param map
		Map<String, String> _reg7LoginWithDeviceIdParamMap = new HashMap<String, String>();

		// set some params
		_reg7LoginWithDeviceIdParamMap.put(
				getResources().getString(
						R.string.bg_server_reg7LoginWithDeviceId_deviceId),
				DeviceUtils.combinedUniqueId());

		// post the http request
		HttpUtils.postRequest(getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.reg7LoginWithDeviceId_url),
				PostRequestFormat.URLENCODED, _reg7LoginWithDeviceIdParamMap,
				null, HttpRequestType.SYNCHRONOUS,
				new RegisterAndLoginWithDeviceIdHttpRequestListener());
	}

	// inner class
	// binded account login http request listener
	class BindedAccountLoginHttpRequestListener extends OnHttpRequestListener {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// get http response entity string json data
			JSONObject _respJsonData = JSONUtils.toJSONObject(responseResult
					.getResponseText());

			Log.d(LOG_TAG,
					"Send binded account login post http request successful, response json data = "
							+ _respJsonData);

			// get the result from http response json data
			String _result = JSONUtils.getStringFromJSONObject(_respJsonData,
					getResources()
							.getString(R.string.bg_server_req_resp_result));

			// check result
			if (null != _result && 0 == Integer.parseInt(_result)) {
				// get binded account login response userId, userKey and
				// nickname
				String _bindedAccountLoginRespUserId = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								getResources()
										.getString(
												R.string.bg_server_login6ContactInfoBindReq_resp_userId));
				String _bindedAccountLoginRespUserKey = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								getResources()
										.getString(
												R.string.bg_server_login6ContactInfoBindReq_resp_userKey));
				String _bindedAccountLoginRespNickname = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								getResources()
										.getString(
												R.string.bg_server_login6ContactInfoBindReq_resp_nickname));

				Log.d(LOG_TAG,
						"Binded account login successful, response user id = "
								+ _bindedAccountLoginRespUserId
								+ ", user key = "
								+ _bindedAccountLoginRespUserKey
								+ " and nickname = "
								+ _bindedAccountLoginRespNickname);

				// get the user from user manager and complete other
				// attributes
				UserBean _bindedAccountLoginRespUser = UserManager
						.getInstance().getUser();
				_bindedAccountLoginRespUser
						.setName(_bindedAccountLoginRespUserId);
				_bindedAccountLoginRespUser
						.setUserKey(_bindedAccountLoginRespUserKey);
				SIMUserExtension.setUserNickname(_bindedAccountLoginRespUser,
						_bindedAccountLoginRespNickname);
			} else {
				processBindedAccountLoginException(responseResult);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG,
					"Send binded account login post http request failed!");

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
					default:
						Log.e(LOG_TAG,
								"Bined account login failed, user name or password is wrong");

						// show binded account login user name or password wrong
						// toast
						Toast.makeText(SimpleIMeetingAppLaunchActivity.this,
								R.string.toast_login_userName6Pwd_wrong,
								Toast.LENGTH_SHORT).show();

						break;
					}
				} else {
					Log.e(LOG_TAG,
							"Binded account login failed, bg_server return result is null");
				}
				break;
			}

			// register and login using device combined unique id
			sendReg7LoginWithDeviceCombinedUniqueIdHttpRequest();
		}

	}

	// register and login with device combined unique id http request listener
	class RegisterAndLoginWithDeviceIdHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// get http response entity string json data
			JSONObject _respJsonData = JSONUtils.toJSONObject(responseResult
					.getResponseText());

			Log.d(LOG_TAG,
					"Send register and login with device combined unique id post http request successful, response json data = "
							+ _respJsonData);

			// get the result from http response json data
			String _result = JSONUtils.getStringFromJSONObject(_respJsonData,
					getResources()
							.getString(R.string.bg_server_req_resp_result));

			// check result
			if (null != _result) {
				switch (Integer.parseInt(_result)) {
				case 0:
					// get register and login with device id response userId and
					// userKey
					String _reg7LoginWithDeviceIdRespUserId = JSONUtils
							.getStringFromJSONObject(
									_respJsonData,
									getResources()
											.getString(
													R.string.bg_server_login6ContactInfoBindReq_resp_userId));
					String _reg7LoginWithDeviceIdRespUserKey = JSONUtils
							.getStringFromJSONObject(
									_respJsonData,
									getResources()
											.getString(
													R.string.bg_server_login6ContactInfoBindReq_resp_userKey));

					Log.d(LOG_TAG,
							"Register and login with device combined unique id successful, response user id = "
									+ _reg7LoginWithDeviceIdRespUserId
									+ " and user key = "
									+ _reg7LoginWithDeviceIdRespUserKey);

					// generate new user bean and add to user manager
					UserManager.getInstance().setUser(
							new UserBean(_reg7LoginWithDeviceIdRespUserId,
									null, _reg7LoginWithDeviceIdRespUserKey));
					break;

				default:
					Log.e(LOG_TAG,
							"Register and login with device combined unique id failed, bg_server return result is unrecognized");

					processReg7LoginWithDeviceIdException();
					break;
				}
			} else {
				Log.e(LOG_TAG,
						"Register and login with device combined unique id failed, bg_server return result is null");

				processReg7LoginWithDeviceIdException();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG,
					"Send register and login with device combined unique id post http request failed!");

			processReg7LoginWithDeviceIdException();
		}

		// process register and login with device combined unique id exception
		private void processReg7LoginWithDeviceIdException() {
			// go to network unavailable or remote bgServer internal error
			// activity
			// ??
		}

	}

}
