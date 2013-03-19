package com.richitec.simpleimeeting.talkinggroup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.assistant.AboutActivity;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingImageBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;

public class SimpleIMeetingMainActivity extends
		SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SimpleIMeetingMainActivity.class
			.getCanonicalName();

	// simple imeeting main view type
	private SimpleIMeetingMainViewType _mMainViewType = SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS == _mMainViewType ? R.layout.contacts_select_activity_layout
				: 0);

		// set title
		setTitle("点击创建会议");

		// set navigation bar left and right button item
		// left bar button item
		setLeftBarButtonItem(new SimpleIMeetingBarButtonItem(this,
				BarButtonItemStyle.RIGHT_GO,
				R.string.contactsSelect_leftBarButtonItem_navTitle,
				new MyTalkingGroupBarButtonItemOnClickListener()));

		// right image bar button item
		setRightBarButtonItem(new SimpleIMeetingImageBarButtonItem(this,
				android.R.drawable.ic_dialog_info, BarButtonItemStyle.RIGHT_GO,
				new MoreMenuImageBarButtonItemOnClickListener()));
	}

	// inner class
	// simple imeeting main view type
	enum SimpleIMeetingMainViewType {
		ADDRESSBOOK_CONTACTS, MY_TALKINGGROUP_LIST
	}

	// my talking group bar button item on click listener
	class MyTalkingGroupBarButtonItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// switch to my talking group view
			Log.d(LOG_TAG, "Switch to my talking group view");

			//
		}

	}

	// more menu image bar button item on click listener
	class MoreMenuImageBarButtonItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to about activity
			pushActivity(AboutActivity.class);
		}

	}

}
