package com.richitec.simpleimeeting.assistant.httprequestlistener;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.SimpleIMeetingAppLaunchActivity;
import com.richitec.simpleimeeting.assistant.NetworkUnavailabelActivity;
import com.richitec.simpleimeeting.assistant.SettingActivity;
import com.richitec.simpleimeeting.user.SIMUserExtension;
import com.richitec.simpleimeeting.user.SIMUserExtension.SIMUserExtAttributes;

public class RegAndLoginWithDeviceIdHttpRequestListener extends
		OnHttpRequestListener {

	private static final String LOG_TAG = RegAndLoginWithDeviceIdHttpRequestListener.class
			.getCanonicalName();

	// activity context
	private Context _mContext;

	// register and login with device combined id type
	private Reg7LoginWithDeviceIdType _mReg7LoginWithDeviceIdType;

	public RegAndLoginWithDeviceIdHttpRequestListener(Context context,
			Reg7LoginWithDeviceIdType reg7loginWithDeviceIdType)
			throws DRLContextNotActivityException {
		super();

		// set activity context and register, login with device combined id type
		_mContext = context;
		_mReg7LoginWithDeviceIdType = reg7loginWithDeviceIdType;

		// check the context if it is null or not activity
		if (null == context || !(context instanceof Activity)) {
			// activity context is null or the context not activity
			throw new DRLContextNotActivityException("the context = " + context);
		}
	}

	public RegAndLoginWithDeviceIdHttpRequestListener(Context context)
			throws DRLContextNotActivityException {
		this(context, Reg7LoginWithDeviceIdType.APP_LAUNCH);
	}

	@Deprecated
	public RegAndLoginWithDeviceIdHttpRequestListener()
			throws DRLContextNotActivityException {
		super();

		// activity context is null
		throw new DRLContextNotActivityException("the context is null");
	}

	@Override
	public void onFinished(HttpResponseResult responseResult) {
		// get http response entity string json data
		JSONObject _respJsonData = JSONUtils.toJSONObject(responseResult
				.getResponseText());

		Log.d(LOG_TAG,
				"Send register and login with device combined unique id post http request successful, response json data = "
						+ _respJsonData);

		// get the result from http response json data
		String _result = JSONUtils.getStringFromJSONObject(
				_respJsonData,
				_mContext.getResources().getString(
						R.string.bg_server_req_resp_result));

		// check result
		if (null != _result) {
			switch (Integer.parseInt(_result)) {
			case 0:
				// get register and login with device id response userId,
				// userKey and bindStatus
				String _reg7LoginWithDeviceIdRespUserId = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								_mContext
										.getResources()
										.getString(
												R.string.bg_server_login6ContactInfoBindReq_resp_userId));
				String _reg7LoginWithDeviceIdRespUserKey = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								_mContext
										.getResources()
										.getString(
												R.string.bg_server_login6ContactInfoBindReq_resp_userKey));
				String _reg7LoginWithDeviceIdRespBindStatus = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								_mContext
										.getResources()
										.getString(
												R.string.bg_server_login6reg7LoginWithDeviceIdReq_resp_bindStatus));

				Log.d(LOG_TAG,
						"Register and login with device combined unique id successful, response user id = "
								+ _reg7LoginWithDeviceIdRespUserId
								+ ", user key = "
								+ _reg7LoginWithDeviceIdRespUserKey
								+ " and bind status = "
								+ _reg7LoginWithDeviceIdRespBindStatus);

				// generate new user bean and complete other attributes
				UserBean _reg7LoginWithDeviceIdUser = new UserBean(
						_reg7LoginWithDeviceIdRespUserId, null,
						_reg7LoginWithDeviceIdRespUserKey);
				SIMUserExtension.setUserContactsInfoBeBinded(
						_reg7LoginWithDeviceIdUser,
						_reg7LoginWithDeviceIdRespBindStatus);

				// add it to user manager
				UserManager.getInstance().setUser(_reg7LoginWithDeviceIdUser);

				// update my account and contacts info bind group UI for binded
				// account logout
				if (Reg7LoginWithDeviceIdType.BINDEDACCOUNT_LOGOUT == _mReg7LoginWithDeviceIdType) {
					((SettingActivity) _mContext)
							.updateMyAccount7ContactsInfoBindGroupUI();

					// clear login user name and password to local storage
					DataStorageUtils.putObject(
							SIMUserExtAttributes.BIND_CONTACTINFO.name(), "");
					DataStorageUtils.putObject(User.password.name(), "");
				}
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
		// check register and login with device combined id type
		switch (_mReg7LoginWithDeviceIdType) {
		case BINDEDACCOUNT_LOGOUT:
			// show binded account logout failed toast
			Toast.makeText(_mContext, R.string.toast_request_exception,
					Toast.LENGTH_LONG).show();
			break;

		case APP_LAUNCH:
		default:
			Log.d(LOG_TAG,
					"Update network unavailable activity as simple iMetting application target intent activity");

			// go to network unavailable or remote bgServer internal error
			// activity
			((SimpleIMeetingAppLaunchActivity) _mContext)
					.updateIntentActivity(new Intent(_mContext,
							NetworkUnavailabelActivity.class));
			break;
		}
	}

	// inner class
	// register and login with device combined id type
	public enum Reg7LoginWithDeviceIdType {
		APP_LAUNCH, BINDEDACCOUNT_LOGOUT
	}

	// register and login with device combined unique id http request listener
	// context not activity exception
	class DRLContextNotActivityException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7371400270301940336L;

		public DRLContextNotActivityException(String detailMessage) {
			super(
					"Register and login with device combined unique id http request listener context not activity, the reason is "
							+ detailMessage
							+ ". Please use RegAndLoginWithDeviceIdHttpRequestListener constructor with activity context param instead");
		}

	}

}
