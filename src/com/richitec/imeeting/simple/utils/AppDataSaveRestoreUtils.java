package com.richitec.imeeting.simple.utils;

import android.os.Bundle;
import android.util.Log;

import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.imeeting.simple.user.SIMUserExtension;
import com.richitec.imeeting.simple.user.SIMUserExtension.SIMUserExtAttributes;

public class AppDataSaveRestoreUtils {

	private static final String LOG_TAG = AppDataSaveRestoreUtils.class
			.getCanonicalName();

	public static void onSaveInstanceState(Bundle outState) {
		UserBean user = UserManager.getInstance().getUser();
		outState.putString(User.username.name(), user.getName());
	}

	public static void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "AppDataSaveRestoreUtils - onRestoreInstanceState");

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
		UserBean _localStorageUser = new UserBean();

		// set bind contact info and password
		_localStorageUser.setPassword(DataStorageUtils.getString(User.password
				.name()));
		SIMUserExtension
				.setUserBindContactInfo(
						_localStorageUser,
						DataStorageUtils
								.getString(SIMUserExtAttributes.BIND_CONTACTINFO
										.name()));

		// save user bean and add to user manager
		UserManager.getInstance().setUser(_localStorageUser);

		Log.d(LOG_TAG,
				"AppDataSaveRestoreUtils - loadAccount, loaded account = "
						+ _localStorageUser.toString());
	}

}
