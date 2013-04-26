package com.richitec.simpleimeeting;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.simpleimeeting.talkinggroup.ContactsSelectView;
import com.richitec.simpleimeeting.talkinggroup.SimpleIMeetingMainActivity;

public class SimpleIMeetingAppLaunchActivity extends AppLaunchActivity {

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
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		ContactsSelectView.initNamePhoneticSortedContactsInfoArray();

		// get binded account login user info from storage and add to user
		// manager
		UserBean _localStorageUser;

		// save user bean and add to user manager
		UserManager.getInstance().setUser(
				_localStorageUser = new UserBean(DataStorageUtils
						.getString(User.username.name()), DataStorageUtils
						.getString(User.password.name()), DataStorageUtils
						.getString(User.userkey.name())));

		// check the generate user from local storage
		if (1) {
			//
		}
	}

}
