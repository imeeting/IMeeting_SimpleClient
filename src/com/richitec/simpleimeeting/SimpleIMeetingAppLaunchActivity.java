package com.richitec.simpleimeeting;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.simpleimeeting.talkinggroup.SimpleIMeetingMainActivity;

public class SimpleIMeetingAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		return getResources().getDrawable(R.drawable.ic_splash);
	}

	@Override
	public Intent intentActivity() {
		// // load account
		// loadAccount();

		// Intent intent = null;
		// UserBean user = UserManager.getInstance().getUser();
		// if (user.getPassword() != null && !user.getPassword().equals("")
		// && user.getUserKey() != null && !user.getUserKey().equals("")) {
		// intent = new Intent(SimpleIMeetingAppLaunchActivity.this,
		// MeetingCreateActivity.class);
		// } else {
		// intent = new Intent(SimpleIMeetingAppLaunchActivity.this,
		// AccountSettingActivity.class);
		// }
		//
		// return intent;

		// define target intent activity
		Intent _targetIntent = new Intent(this,
				SimpleIMeetingMainActivity.class);

		return _targetIntent;
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		SimpleIMeetingMainActivity.initNamePhoneticSortedContactsInfoArray();
	}

	// private void loadAccount() {
	// String userName = DataStorageUtils.getString(User.username.name());
	// String userkey = DataStorageUtils.getString(User.userkey.name());
	// String password = DataStorageUtils.getString(User.password.name());
	// UserBean userBean = new UserBean();
	// userBean.setName(userName);
	// userBean.setUserKey(userkey);
	// userBean.setPassword(password);
	// UserManager.getInstance().setUser(userBean);
	// Log.d(SystemConstants.TAG, "load account: " + userBean.toString());
	// }

}
