package com.richitec.imeeting.simple;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.User;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.imeeting.simple.account.AccountSettingActivity;
import com.richitec.imeeting.simple.constants.SystemConstants;
import com.richitec.imeeting.simple.createmeeting.MeetingCreateActivity;

public class SimpleIMeetingAppLaunchActivity extends AppLaunchActivity {
	// main activity class name storage key
	@Override
	public Drawable splashImg() {
		return getResources().getDrawable(R.drawable.ic_splash);
	}

	@Override
	public Intent intentActivity() {
		
		loadAccount();
		Intent intent = null;
		UserBean user = UserManager.getInstance().getUser();
		if (user.getPassword() != null && !user.getPassword().equals("")
				&& user.getUserKey() != null && !user.getUserKey().equals("")) {
			intent = new Intent(SimpleIMeetingAppLaunchActivity.this,
					MeetingCreateActivity.class);
		} else {
			intent = new Intent(SimpleIMeetingAppLaunchActivity.this,
					AccountSettingActivity.class);
		}

		return intent;
	}

	@Override
	public void didFinishLaunching() {
		// traversal address book
		AddressBookManager.getInstance().traversalAddressBook();
	}

	private void loadAccount() {
		String userName = DataStorageUtils.getString(User.username.name());
		String userkey = DataStorageUtils.getString(User.userkey.name());
		String password = DataStorageUtils.getString(User.password.name());
		UserBean userBean = new UserBean();
		userBean.setName(userName);
		userBean.setUserKey(userkey);
		userBean.setPassword(password);
		UserManager.getInstance().setUser(userBean);
		Log.d(SystemConstants.TAG, "load account: " + userBean.toString());
	}


}
