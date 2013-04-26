package com.richitec.simpleimeeting;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
				user.getName());

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
			Log.d(LOG_TAG, "Binded account login successful!");

			//
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG, "Binded account login failed!");

			// register and login using device combined unique id
			sendReg7LoginWithDeviceCombinedUniqueIdHttpRequest();
		}

	}

	// register and login with device combined unique id http request listener
	class RegisterAndLoginWithDeviceIdHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			Log.d(LOG_TAG,
					"Register and login with device combined unique id successful!");

			//
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG,
					"Register and login with device combined unique id failed!");

			//
		}

	}

}
