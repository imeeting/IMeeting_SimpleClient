package com.richitec.simpleimeeting.user;

import android.util.Log;

import com.richitec.commontoolkit.user.UserBean;

public class SIMUserExtension {

	private static final String LOG_TAG = SIMUserExtension.class
			.getCanonicalName();

	// set simple iMetting user bind contact info
	public static void setUserBindContactInfo(UserBean user,
			String bindContactInfo) {
		setUserExtAttribute(user, SIMUserExtAttributes.BindContactInfo,
				bindContactInfo);
	}

	// get simple iMetting user bind contact info
	public static String getUserBindContactInfo(UserBean user) {
		return getUserExtAttribute(user, SIMUserExtAttributes.BindContactInfo);
	}

	// set simple iMetting user nickname
	public static void setUserNickname(UserBean user, String bindContactInfo) {
		setUserExtAttribute(user, SIMUserExtAttributes.Nickname,
				bindContactInfo);
	}

	// get simple iMetting user nickname
	public static String getUserNickname(UserBean user) {
		return getUserExtAttribute(user, SIMUserExtAttributes.Nickname);
	}

	// set user extension attribute with key and value
	private static void setUserExtAttribute(UserBean user,
			SIMUserExtAttributes extAttribute, String extAttributeValue) {
		// check user bean
		if (null != user) {
			// set simple iMeeting user extension attribute
			user.setValue(extAttribute.name(), extAttributeValue);
		} else {
			Log.e(LOG_TAG,
					"Set simple iMeeting user extension attribute error, user = "
							+ user);
		}
	}

	// get user extension attribute with key
	private static String getUserExtAttribute(UserBean user,
			SIMUserExtAttributes extAttribute) {
		String _ret = null;

		// check user bean
		if (null != user) {
			_ret = (String) user.getValue(extAttribute.name());
		} else {
			Log.e(LOG_TAG,
					"Get simple iMeeting user extension attribute error, user = "
							+ user);
		}

		return _ret;
	}

	// inner class
	// simple iMeeting user extension attributes
	public static enum SIMUserExtAttributes {
		BindContactInfo, Nickname
	}

}
