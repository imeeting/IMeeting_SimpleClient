package com.richitec.simpleimeeting.user;

import android.util.Log;

import com.richitec.commontoolkit.user.UserBean;

public class SIMUserExtension {

	private static final String LOG_TAG = SIMUserExtension.class
			.getCanonicalName();

	// set simple iMetting user bind contact info
	public static void setUserBindContactInfo(UserBean user,
			String bindContactInfo) {
		// check user bean
		if (null != user) {
			// set simple iMeeting user bind contact info
			user.setValue(SIMUserExtAttributes.BindContactInfo.name(),
					bindContactInfo);
		} else {
			Log.e(LOG_TAG,
					"Set simple iMeeting user bind contact info error, user = "
							+ user);
		}
	}

	// get simple iMetting user bind contact info
	public static String getUserBindContactInfo(UserBean user) {
		String _ret = null;

		// check user bean
		if (null != user) {
			_ret = (String) user.getValue(SIMUserExtAttributes.BindContactInfo
					.name());
		} else {
			Log.e(LOG_TAG,
					"Get simple iMeeting user bind contact info error, user = "
							+ user);
		}

		return _ret;
	}

	// inner class
	// simple iMeeting user extension attributes
	public static enum SIMUserExtAttributes {
		BindContactInfo
	}

}
