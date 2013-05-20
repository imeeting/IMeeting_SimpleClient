package com.richitec.imeeting.simple.user;

import android.content.Context;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.imeeting.simple.R;

public class SIMUserExtension {

	private static final String LOG_TAG = SIMUserExtension.class
			.getCanonicalName();

	// set simple iMetting user bind contact info
	public static void setUserBindContactInfo(UserBean user,
			String bindContactInfo) {
		setUserExtAttribute(user, SIMUserExtAttributes.BIND_CONTACTINFO,
				bindContactInfo);
	}

	// get simple iMetting user bind contact info
	public static String getUserBindContactInfo(UserBean user) {
		return getUserExtAttribute(user, SIMUserExtAttributes.BIND_CONTACTINFO);
	}

	// set simple iMetting user nickname
	public static void setUserNickname(UserBean user, String bindContactInfo) {
		setUserExtAttribute(user, SIMUserExtAttributes.NICKNAME,
				bindContactInfo);
	}

	// get simple iMetting user nickname
	public static String getUserNickname(UserBean user) {
		return getUserExtAttribute(user, SIMUserExtAttributes.NICKNAME);
	}

	// set simple iMetting user contacts info type be binded
	public static void setUserContactsInfoTypeBeBinded(UserBean user,
			String contactsInfoBeBinded) {
		setUserExtAttribute(user,
				SIMUserExtAttributes.CONTACTSINFOTYPE_BEBINDED,
				contactsInfoBeBinded);
	}

	// get simple iMetting user contacts info type be binded
	public static String getUserContactsInfoTypeBeBinded(UserBean user) {
		return getUserExtAttribute(user,
				SIMUserExtAttributes.CONTACTSINFOTYPE_BEBINDED);
	}

	// set simple iMetting user contacts info be binded
	public static void setUserContactsInfoBeBinded(UserBean user,
			String contactsInfoBeBinded) {
		// check contacts info be binded
		if (null != contactsInfoBeBinded) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// get contacts info type be binded
			String _contactsInfoTypeBeBinded = getUserContactsInfoTypeBeBinded(user);

			// check contacts info type be binded
			if (null != _contactsInfoTypeBeBinded
					&& _appContext
							.getResources()
							.getString(
									R.string.bg_server_login6reg7LoginWithDeviceId6PhoneBind_phoneBindedStatus)
							.equalsIgnoreCase(_contactsInfoTypeBeBinded)) {
				if (!contactsInfoBeBinded.startsWith(_appContext.getResources()
						.getString(R.string.phoneBinded_contactsInfoPrefix))) {
					contactsInfoBeBinded = _appContext.getResources()
							.getString(R.string.phoneBinded_contactsInfoPrefix)
							+ contactsInfoBeBinded;
				}
			}
		}

		setUserExtAttribute(user, SIMUserExtAttributes.CONTACTSINFO_BEBINDED,
				contactsInfoBeBinded);
	}

	// get simple iMetting user contacts info be binded
	public static String getUserContactsInfoBeBinded(UserBean user) {
		return getUserExtAttribute(user,
				SIMUserExtAttributes.CONTACTSINFO_BEBINDED);
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
		BIND_CONTACTINFO, NICKNAME, CONTACTSINFOTYPE_BEBINDED, CONTACTSINFO_BEBINDED
	}

}
