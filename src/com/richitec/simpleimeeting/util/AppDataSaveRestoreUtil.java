package com.richitec.simpleimeeting.util;

import android.os.Bundle;
import android.util.Log;

import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.simpleimeeting.constants.SIMUserExtAttributes;

public class AppDataSaveRestoreUtil {

	private static final String LOG_TAG = AppDataSaveRestoreUtil.class
			.getCanonicalName();

	public static void onSaveInstanceState(Bundle outState) {
		UserBean user = UserManager.getInstance().getUser();
		outState.putString(User.username.name(), user.getName());
	}

	public static void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "AppDataSaveRestoreUtil - onRestoreInstanceState");

		if (!AddressBookManager.getInstance().isInited()) {
			AddressBookManager.getInstance().traversalAddressBook();
			AddressBookManager.getInstance().registContactOberver();
		}

		String userName = savedInstanceState.getString(User.username.name());

		UserBean user = UserManager.getInstance().getUser();
		if (userName == null || userName.equals("")) {
		} else if (user.getName() == null || user.getName().equals("")) {
			loadAccount();
		}

	}

	public static void loadAccount() {
		// get binded account login user info from storage and add to user
		// manager
		UserBean _localStorageUser = new UserBean(
				DataStorageUtils.getString(User.username.name()),
				DataStorageUtils.getString(User.password.name()),
				DataStorageUtils.getString(User.userkey.name()));
		_localStorageUser.setValue(SIMUserExtAttributes.bindContactInfo.name(),
				DataStorageUtils.getString(SIMUserExtAttributes.bindContactInfo
						.name()));

		// save user bean and add to user manager
		UserManager.getInstance().setUser(_localStorageUser);

		Log.d(LOG_TAG,
				"AppDataSaveRestoreUtil - loadAccount, loaded account = "
						+ _localStorageUser.toString());
	}

}
