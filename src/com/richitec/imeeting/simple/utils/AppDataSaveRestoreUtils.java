package com.richitec.imeeting.simple.utils;

import android.os.Bundle;
import android.util.Log;

import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.imeeting.simple.talkinggroup.ContactsSelectView;
import com.richitec.imeeting.simple.user.SIMUserExtension;
import com.richitec.imeeting.simple.user.SIMUserExtension.SIMUserExtAttributes;

public class AppDataSaveRestoreUtils {

	private static final String LOG_TAG = AppDataSaveRestoreUtils.class
			.getCanonicalName();

	public static void onSaveInstanceState(Bundle outState) {
		// get memory storage user bean
		UserBean _memStorageUser = UserManager.getInstance().getUser();

		// put user common keys: user name, password and userKey
		outState.putString(User.username.name(), _memStorageUser.getName());
		outState.putString(User.password.name(), _memStorageUser.getPassword());
		outState.putString(User.userkey.name(), _memStorageUser.getUserKey());

		// put user extension keys: bind contactInfo, nickname, contactsInfo and
		// its type beBinded
		outState.putString(SIMUserExtAttributes.BIND_CONTACTINFO.name(),
				SIMUserExtension.getUserBindContactInfo(_memStorageUser));
		outState.putString(SIMUserExtAttributes.NICKNAME.name(),
				SIMUserExtension.getUserNickname(_memStorageUser));
		outState.putString(SIMUserExtAttributes.CONTACTSINFOTYPE_BEBINDED
				.name(), SIMUserExtension
				.getUserContactsInfoTypeBeBinded(_memStorageUser));
		outState.putString(SIMUserExtAttributes.CONTACTSINFO_BEBINDED.name(),
				SIMUserExtension.getUserContactsInfoBeBinded(_memStorageUser));
	}

	public static void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "AppDataSaveRestoreUtils - onRestoreInstanceState");

		// check and traversal addressBook if needed
		if (!AddressBookManager.getInstance().isInited()) {
			AddressBookManager.getInstance().traversalAddressBook();
			AddressBookManager.getInstance().registContactOberver();

			// init all name phonetic sorted contacts info array
			ContactsSelectView.initNamePhoneticSortedContactsInfoArray();
		}

		// get user from user manager
		UserBean _user = UserManager.getInstance().getUser();

		// get saved instance state bundle storage data
		String _userName = savedInstanceState.getString(User.username.name());
		String _password = savedInstanceState.getString(User.password.name());
		String _userKey = savedInstanceState.getString(User.userkey.name());
		String _bindContactInfo = savedInstanceState
				.getString(SIMUserExtAttributes.BIND_CONTACTINFO.name());
		String _nickname = savedInstanceState
				.getString(SIMUserExtAttributes.NICKNAME.name());
		String _contactsInfoTypeBeBinded = savedInstanceState
				.getString(SIMUserExtAttributes.CONTACTSINFOTYPE_BEBINDED
						.name());
		String _contactsInfoBeBinded = savedInstanceState
				.getString(SIMUserExtAttributes.CONTACTSINFO_BEBINDED.name());

		// check user name
		if (null != _userName && !("").equalsIgnoreCase(_userName)) {
			_user.setName(_userName);
		} else {
			Log.e(LOG_TAG,
					"Get user name from saved instance state bundle error, user name = "
							+ _userName);
		}

		// check password
		if (null != _password && !("").equalsIgnoreCase(_password)) {
			_user.setPassword(_password);
		}

		// check user key
		if (null != _userKey && !("").equalsIgnoreCase(_userKey)) {
			_user.setUserKey(_userKey);
		} else {
			Log.e(LOG_TAG,
					"Get user key from saved instance state bundle error, user key = "
							+ _userKey);
		}

		// check bind contactInfo
		if (null != _bindContactInfo
				&& !("").equalsIgnoreCase(_bindContactInfo)) {
			SIMUserExtension.setUserBindContactInfo(_user, _bindContactInfo);
		}

		// check nickname
		if (null != _nickname && !("").equalsIgnoreCase(_nickname)) {
			SIMUserExtension.setUserNickname(_user, _nickname);
		}

		// check contactsInfo type beBinded
		if (null != _contactsInfoTypeBeBinded
				&& !("").equalsIgnoreCase(_contactsInfoTypeBeBinded)) {
			SIMUserExtension.setUserContactsInfoTypeBeBinded(_user,
					_contactsInfoTypeBeBinded);
		}

		// check contactsInfo beBinded
		if (null != _contactsInfoBeBinded
				&& !("").equalsIgnoreCase(_contactsInfoBeBinded)) {
			SIMUserExtension.setUserContactsInfoBeBinded(_user,
					_contactsInfoBeBinded);
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
