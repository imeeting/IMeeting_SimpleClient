package com.richitec.simpleimeeting.talkinggroup;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.customcomponent.CTMenu;
import com.richitec.commontoolkit.customcomponent.CTMenu.CTMenuOnItemSelectedListener;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.assistant.AboutActivity;
import com.richitec.simpleimeeting.assistant.SettingActivity;
import com.richitec.simpleimeeting.assistant.SupportActivity;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingImageBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;
import com.richitec.simpleimeeting.view.SIMBaseView;
import com.richitec.simpleimeeting.view.SIMViewFactory;

public class SimpleIMeetingMainActivity extends
		SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SimpleIMeetingMainActivity.class
			.getCanonicalName();

	// more menu ids
	private static final int SETTING_MENU = 10;
	private static final int SUPPORT_MENU = 11;
	private static final int ABOUT_MENU = 12;

	// simple imeeting main view type
	private SimpleIMeetingMainViewType _mMainViewType = SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS;

	// contacts select and my talking group list subViews
	private SIMBaseView _mContactsSelectView;
	private SIMBaseView _mMyTalkingGroupsView;

	// simple imeeting main activity content view(simple imeeting view)
	private SIMBaseView _mContentView;

	// tap to generate new talking group title textView
	private TextView _mTap2GenNewTalkingGroupTitleTextView;

	// left bar button item, switch to my talking group list and switch to
	// contacts select bar button item
	private SimpleIMeetingBarButtonItem _mSwitch2MyTalkingGroupsLeftBarButtonItem;
	private SimpleIMeetingBarButtonItem _mSwitch2ContactsSelectLeftBarButtonItem;

	// more popup menu
	private CTMenu _mMorePopupMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setMainActivityContentView();

		// set navigation title and left bar button item
		setMainActivityNavigationTitle7LeftBarButtonItem();

		// init more popup menu
		_mMorePopupMenu = new CTMenu(this);

		// add menu item
		_mMorePopupMenu.add(SETTING_MENU, R.string.moreMenu_settingMenuItem);
		_mMorePopupMenu.add(SUPPORT_MENU, R.string.moreMenu_supportMenuItem);
		_mMorePopupMenu.add(ABOUT_MENU, R.string.moreMenu_aboutMenuItem);

		// set more menu on item selected listener
		_mMorePopupMenu
				.setMenuOnItemSelectedListener(new MoreMenuOnItemSelectedListener());

		// set right image bar button item, about info image bar button item
		setRightBarButtonItem(new SimpleIMeetingImageBarButtonItem(this,
				android.R.drawable.ic_dialog_info, BarButtonItemStyle.RIGHT_GO,
				new MoreMenuImageBarButtonItemOnClickListener()));
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(LOG_TAG,
				"Simple iMeeting main activity content view(simple imeeting view) = "
						+ _mContentView + ", Contacts select view = "
						+ _mContactsSelectView
						+ " and my talking groups view = "
						+ _mMyTalkingGroupsView);

		// get simple imeeting main activity content view(simple imeeting view)
		// and call its onResume method
		_mContentView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// get simple imeeting main activity content view(simple imeeting view)
		// and call its onStop method
		_mContentView.onStop();
	}

	// switch to my talking group list view
	public void switch2myTalkingGroupsView(boolean needed2refresh) {
		switchContentView(needed2refresh, null);
	}

	// switch to contacts select view
	public void switch2contactsSelectView(
			List<String> confId7inviteNote7talkingGroupContactsPhone) {
		switchContentView(false, confId7inviteNote7talkingGroupContactsPhone);
	}

	// set main activity content view
	private void setMainActivityContentView(
			SimpleIMeetingMainViewType mainViewType) {
		// main activity content view(simple imeeting view)
		SIMBaseView _contentView;

		// save main view type
		_mMainViewType = mainViewType;

		// check main view type and content view
		switch (mainViewType) {
		case MY_TALKINGGROUP_LIST: {
			// check my talking group list content view
			if (null == _mMyTalkingGroupsView) {
				// init my talking group list content view and save
				_mMyTalkingGroupsView = SIMViewFactory.createSIMView(this,
						MyTalkingGroupsView.class);
			}

			_contentView = _mMyTalkingGroupsView;
		}
			break;

		default:
		case ADDRESSBOOK_CONTACTS: {
			// check contacts select content view
			if (null == _mContactsSelectView) {
				// init contacts select content view and save
				_mContactsSelectView = SIMViewFactory.createSIMView(this,
						ContactsSelectView.class);
			}

			_contentView = _mContactsSelectView;
		}
			break;
		}

		// check content view and set it
		if (null != _contentView) {
			// save simple imeeting main activity content view
			_mContentView = _contentView;

			setContentView(_contentView.getPresentView());
		} else {
			Log.e(LOG_TAG,
					"No, no no ...! There is no main activity content view.");
		}
	}

	private void setMainActivityContentView() {
		setMainActivityContentView(_mMainViewType);
	}

	// set navigation title and left bar button item
	private void setMainActivityNavigationTitle7LeftBarButtonItem() {
		// set title
		String _titleTag;

		// check tap to generate new talking group title textView
		if (null == _mTap2GenNewTalkingGroupTitleTextView) {
			// init tap to generate new talking group title textView
			_mTap2GenNewTalkingGroupTitleTextView = new TextView(this);

			// set title textView text, font size and color
			_mTap2GenNewTalkingGroupTitleTextView
					.setText(R.string.tap2generateNewTalkingGroup_title);
			_mTap2GenNewTalkingGroupTitleTextView
					.setTextSize(DisplayScreenUtils.sp2pix(14));
			_mTap2GenNewTalkingGroupTitleTextView.setTextColor(Color.WHITE);

			// set title on touch listener
			_mTap2GenNewTalkingGroupTitleTextView
					.setOnTouchListener(new TitleOnTouchListener());
		}

		// main activity left bar button item
		SimpleIMeetingBarButtonItem _leftBarButtonItem;

		// check main view type and set left bar button item
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST: {
			// check switch to contacts select left bar button item
			if (null == _mSwitch2ContactsSelectLeftBarButtonItem) {
				// init switch to contacts select left bar button item and save
				_mSwitch2ContactsSelectLeftBarButtonItem = new SimpleIMeetingBarButtonItem(
						this,
						BarButtonItemStyle.RIGHT_GO,
						R.string.myTalkingGroup_leftBarButtonItem_navTitle,
						new Switch2MyTalkingGroup6AddressBookContactsBarButtonItemOnClickListener());
			}

			_titleTag = getResources().getString(
					R.string.contactsSelect_leftBarButtonItem_navTitle);

			_leftBarButtonItem = _mSwitch2ContactsSelectLeftBarButtonItem;
		}
			break;

		default:
		case ADDRESSBOOK_CONTACTS: {
			// check switch to my talking group list left bar button item
			if (null == _mSwitch2MyTalkingGroupsLeftBarButtonItem) {
				// init switch to my talking group list left bar button item and
				// save
				_mSwitch2MyTalkingGroupsLeftBarButtonItem = new SimpleIMeetingBarButtonItem(
						this,
						BarButtonItemStyle.RIGHT_GO,
						R.string.contactsSelect_leftBarButtonItem_navTitle,
						new Switch2MyTalkingGroup6AddressBookContactsBarButtonItemOnClickListener());
			}

			_titleTag = getResources().getString(
					R.string.myTalkingGroup_leftBarButtonItem_navTitle);

			_leftBarButtonItem = _mSwitch2MyTalkingGroupsLeftBarButtonItem;
		}
			break;
		}

		// set title with tag
		setTitle(_mTap2GenNewTalkingGroupTitleTextView, _titleTag);

		// set left bar button item
		// check left bar button item and set it
		if (null != _leftBarButtonItem) {
			setLeftBarButtonItem(_leftBarButtonItem);
		} else {
			Log.e(LOG_TAG,
					"No, no no ...! There is no main activity left bar button item.");
		}
	}

	// switch simple imeeting main activity view
	private void switchContentView(
			boolean myTalkingGroupNeeded2Resresh,
			List<String> contactsSelectConfId7InviteNote7TalkingGroupContactsPhoneArray) {
		// set current content view onPause
		_mContentView.onPause();

		// check simple iMeeting main view type and set main activity
		// content view
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST:
			// switch to addressbook contacts view
			Log.d(LOG_TAG, "Switch to addressbook contacts view");

			setMainActivityContentView(SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS);

			// check and set conference id, invite note, talking group contacts
			// phone array
			if (null != contactsSelectConfId7InviteNote7TalkingGroupContactsPhoneArray) {
				((ContactsSelectView) _mContentView)
						.setConfId7InviteNote7TalkingGroupContactsPhoneArray(contactsSelectConfId7InviteNote7TalkingGroupContactsPhoneArray);
			}
			break;

		case ADDRESSBOOK_CONTACTS:
		default:
			// switch to my talking group view
			Log.d(LOG_TAG, "Switch to my talking group view");

			setMainActivityContentView(SimpleIMeetingMainViewType.MY_TALKINGGROUP_LIST);

			// check and set my talking group list needed to refresh
			if (myTalkingGroupNeeded2Resresh) {
				((MyTalkingGroupsView) _mContentView)
						.setMyTalkingGroupsNeeded2Refresh();
			}
			break;
		}

		// set present content view onResult
		_mContentView.onResume();

		// set main activity navigation title and left bar button item
		setMainActivityNavigationTitle7LeftBarButtonItem();
	}

	// inner class
	// simple imeeting main view type
	enum SimpleIMeetingMainViewType {
		ADDRESSBOOK_CONTACTS, MY_TALKINGGROUP_LIST
	}

	// title on touch listener
	class TitleOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// check motion event
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				Log.d(LOG_TAG, "Generate new talking group");
			}

			return false;
		}

	}

	// switch to my talking group or addressbook contacts bar button item on
	// click listener
	class Switch2MyTalkingGroup6AddressBookContactsBarButtonItemOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(View v) {
			// switch content view
			switchContentView(false, null);
		}

	}

	// more menu image bar button item on click listener
	class MoreMenuImageBarButtonItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// show more menu
			_mMorePopupMenu.showAsDropDown(v);
		}

	}

	// more menu on item selected listener
	class MoreMenuOnItemSelectedListener implements
			CTMenuOnItemSelectedListener {

		@Override
		public boolean onMenuItemSelected(CTMenu menu, int menuItemId) {
			// check popup menu
			if (_mMorePopupMenu == menu) {
				// more menu dismiss
				menu.dismiss();

				// check more menu item id
				switch (menuItemId) {
				case SETTING_MENU:
					// goto setting activity
					pushActivity(SettingActivity.class);
					break;

				case SUPPORT_MENU:
					// goto support activity
					pushActivity(SupportActivity.class);
					break;

				case ABOUT_MENU:
				default:
					// goto about activity
					pushActivity(AboutActivity.class);
					break;
				}
			}

			return false;
		}

	}

}
