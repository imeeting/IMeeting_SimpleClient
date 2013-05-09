package com.richitec.simpleimeeting;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.simpleimeeting.talkinggroup.ContactsSelectView;
import com.richitec.simpleimeeting.talkinggroup.SimpleIMeetingMainActivity;
import com.richitec.simpleimeeting.util.AppDataSaveRestoreUtil;

public class SimpleIMeetingAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		return getResources().getDrawable(R.drawable.ic_splash);
	}

	@Override
	public Intent intentActivity() {
		 // load account
		AppDataSaveRestoreUtil.loadAccount();

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
	public boolean didFinishLaunching() {
		// traversal address book
		AddressBookManager.setFilterMode(AddressBookManager.FILTER_IP_AND_CODE_PREFIX);
		AddressBookManager.getInstance().traversalAddressBook();

		// init all name phonetic sorted contacts info array
		ContactsSelectView.initNamePhoneticSortedContactsInfoArray();
		return false;
	}

}
