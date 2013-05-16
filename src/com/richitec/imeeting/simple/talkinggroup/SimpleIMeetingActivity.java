package com.richitec.imeeting.simple.talkinggroup;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.richitec.commontoolkit.utils.VersionUtils;
import com.richitec.commontoolkit.utils.VersionUtils.APPUPGRADEMODE;
import com.richitec.imeeting.simple.R;
import com.richitec.imeeting.simple.assistant.AboutActivity;
import com.richitec.imeeting.simple.assistant.SettingActivity;
import com.richitec.imeeting.simple.assistant.SupportActivity;
import com.richitec.imeeting.simple.customcomponent.SimpleIMeetingBarButtonItem;
import com.richitec.imeeting.simple.customcomponent.SimpleIMeetingImageBarButtonItem;
import com.richitec.imeeting.simple.customcomponent.SimpleIMeetingNavigationActivity;
import com.richitec.imeeting.simple.talkinggroup.MyTalkingGroupsView.MyTalkingGroupsViewRefreshType;
import com.richitec.imeeting.simple.utils.AppDataSaveRestoreUtils;
import com.richitec.imeeting.simple.view.SIMBaseView;
import com.richitec.imeeting.simple.view.SIMViewFactory;

public class SimpleIMeetingActivity extends SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SimpleIMeetingActivity.class
			.getCanonicalName();

	// more menu ids
	private static final int SETTING_MENU = 10;
	private static final int SUPPORT_MENU = 11;
	private static final int ABOUT_MENU = 12;

	// my account changed request code
	private static final int MYACCOUNT_CHANGED = 0;

	// simple imeeting activity content view type
	private SimpleIMeetingActivityContentViewType _mMainViewType = SimpleIMeetingActivityContentViewType.ADDRESSBOOK_CONTACTS;

	// contacts select and my talking group list subViews
	private SIMBaseView _mContactsSelectView;
	private SIMBaseView _mMyTalkingGroupsView;

	// my talking group list needed to refresh later flag, default is false
	private boolean _mMyTalkingGroupsNeeded2RefreshLater = false;

	// simple imeeting main activity content view(simple imeeting view)
	private SIMBaseView _mContentView;

	// tap to generate new talking group and contacts selecting title textView
	private TextView _mTap2GenNewTalkingGroupTitleTextView;
	private TextView _mContactsSelectingTitleTextView;

	// left bar button item, switch to my talking group list and switch to
	// contacts select bar button item
	private SimpleIMeetingBarButtonItem _mSwitch2MyTalkingGroupsLeftBarButtonItem;
	private SimpleIMeetingBarButtonItem _mSwitch2ContactsSelectLeftBarButtonItem;

	// more popup menu
	private CTMenu _mMorePopupMenu;

	// my account web socket notifier needed to reconnect later flag, default is
	// false
	private boolean _mMyAccountWebSocketNotifierNeeded2ReconnectLater = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// restore application data
		if (null != savedInstanceState) {
			AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);
		}

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

		// check for upgrading imeeting simple client
		VersionUtils.upgradeApp(this,
				getResources().getString(R.string.app_id), getResources()
						.getString(R.string.appvcenter_url),
				APPUPGRADEMODE.AUTO);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// get simple imeeting main activity content view(simple imeeting view)
		// and call its onDestroy method
		_mContentView.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save application data
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		// show exit imeeting simple client alert dialog
		new AlertDialog.Builder(this)
				.setTitle(R.string.simpleIMeeting_exitAlertDialog_title)
				.setMessage(R.string.simpleIMeeting_exitAlertDialog_message)
				.setPositiveButton(
						R.string.simpleIMeeting_exitAlertDialog_exitButton_title,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// exit imeeting simple client
								System.exit(0);
							}
						})
				.setNegativeButton(
						R.string.simpleIMeeting_exitAlertDialog_cancelButton_title,
						null).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// check request code
		switch (requestCode) {
		case MYACCOUNT_CHANGED:
			// check result code
			switch (resultCode) {
			case RESULT_OK:
				// check data
				if (null != data) {
					// get intent extra data
					Bundle _data = data.getExtras();

					// check intent extra data and get my account changed flag
					if (null != _data) {
						// check my account changed or not
						if (_data
								.getBoolean(SettingActivity.SETTING_CHANGEDMYACCOUNT_KEY)) {
							// check current content view type
							if (_mMyTalkingGroupsView == _mContentView) {
								// refresh my talking group list
								((MyTalkingGroupsView) _mContentView)
										.setMyTalkingGroupsNeeded2Refresh(MyTalkingGroupsViewRefreshType.TALKINGGROUPS);

								// reconnect my account web socket notifier
								((MyTalkingGroupsView) _mContentView)
										.reconnectMyAccountWebSocketNotifier();
							} else {
								// mark my talking group list needed to refresh
								// later
								_mMyTalkingGroupsNeeded2RefreshLater = true;

								// check my talking groups view and disconnect
								// my account web socket notifier
								if (null != _mMyTalkingGroupsView) {
									((MyTalkingGroupsView) _mMyTalkingGroupsView)
											.disconnectMyAccountWebSocketNotifier();

									// mark my account web socket notifier
									// needed to reconnect later
									_mMyAccountWebSocketNotifierNeeded2ReconnectLater = true;
								}
							}
						}
					}
				} else {
					Log.e(LOG_TAG, "On activity result, intent data = " + data);
				}
				break;

			default:
				// nothing to do
				break;
			}
			break;

		default:
			// nothing to do
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// switch to my talking group list view
	public void switch2myTalkingGroupsView(
			MyTalkingGroupsViewRefreshType refreshType) {
		switchContentView(refreshType, null);
	}

	// switch to contacts select view
	public void switch2contactsSelectView(
			List<String> confId7inviteNote7talkingGroupContactsPhone) {
		switchContentView(null, confId7inviteNote7talkingGroupContactsPhone);
	}

	// set contacts select navigation title and back bar button item as left
	// navigation bar button item
	public void setContactsSelectNavigationTitle7BackBarButtonItem(
			SimpleIMeetingActivityContentViewType contentViewType) {
		// check contacts selecting title textView
		if (null == _mContactsSelectingTitleTextView) {
			// init contacts selecting title textView
			_mContactsSelectingTitleTextView = new TextView(this);

			// set title textView text, font size and color
			_mContactsSelectingTitleTextView
					.setText(R.string.contactsSelecting_tipTitle);
			_mContactsSelectingTitleTextView.setTextSize(DisplayScreenUtils
					.sp2pix(14));
			_mContactsSelectingTitleTextView.setTextColor(Color.WHITE);
		}

		// set title with tag
		setTitle(
				_mContactsSelectingTitleTextView,
				getResources().getString(
						R.string.contactsSelecting_tipTitle_tag));

		// set left bar button item
		setLeftBarButtonItem(new SimpleIMeetingBarButtonItem(this,
				BarButtonItemStyle.LEFT_BACK,
				R.string.contactsSelecting_backBarButtonItem_navTitle,
				new ContactsSelectingBackBarButtonItemOnClickListener(
						contentViewType)));
	}

	// mark my talking group list needed not to refresh later
	public void markMyTalkingGroupsNeededNot2RefreshLater() {
		// clear my talking group list needed to refresh later flag
		_mMyTalkingGroupsNeeded2RefreshLater = false;
	}

	// reconnect my account web socket notifier
	public void reconnectMyAccountWebSocketNotifier() {
		// check my account web socket notifier need or not to reconnect
		if (_mMyAccountWebSocketNotifierNeeded2ReconnectLater) {
			// clear my account web socket notifier needed to reconnect later
			// flag
			_mMyAccountWebSocketNotifierNeeded2ReconnectLater = false;

			((MyTalkingGroupsView) _mContentView)
					.reconnectMyAccountWebSocketNotifier();
		}
	}

	// set main activity content view
	private void setMainActivityContentView(
			SimpleIMeetingActivityContentViewType mainViewType) {
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
			MyTalkingGroupsViewRefreshType myTalkingGroupsViewResreshType,
			List<String> contactsSelectConfId7InviteNote7TalkingGroupContactsPhoneArray) {
		// set current content view onPause
		_mContentView.onPause();

		// check simple iMeeting main view type and set main activity
		// content view
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST:
			// switch to addressbook contacts view
			Log.d(LOG_TAG, "Switch to addressbook contacts view");

			setMainActivityContentView(SimpleIMeetingActivityContentViewType.ADDRESSBOOK_CONTACTS);

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

			setMainActivityContentView(SimpleIMeetingActivityContentViewType.MY_TALKINGGROUP_LIST);

			// check and set my talking group list needed to refresh
			if (null != myTalkingGroupsViewResreshType) {
				((MyTalkingGroupsView) _mContentView)
						.setMyTalkingGroupsNeeded2Refresh(myTalkingGroupsViewResreshType);
			}
			break;
		}

		// set present content view onResult
		_mContentView.onResume();

		// set main activity navigation title and left bar button item
		setMainActivityNavigationTitle7LeftBarButtonItem();
	}

	// inner class
	// simple imeeting activity content view type
	enum SimpleIMeetingActivityContentViewType {
		ADDRESSBOOK_CONTACTS, MY_TALKINGGROUP_LIST
	}

	// title on touch listener
	class TitleOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// check motion event
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				// generate new talking group with new talking group listener
				// implementation
				((NewTalkingGroupListener) _mContentView)
						.generateNewTalkingGroup();
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
			// save current content view
			SIMBaseView _contentView = _mContentView;

			// check content view type and switch content view
			if (_mMyTalkingGroupsNeeded2RefreshLater
					&& _mContactsSelectView == _mContentView) {
				// clear my talking group list needed to refresh later flag
				_mMyTalkingGroupsNeeded2RefreshLater = false;

				switchContentView(MyTalkingGroupsViewRefreshType.TALKINGGROUPS,
						null);
			} else {
				switchContentView(null, null);
			}

			// check content view type and reconnect my account web socket
			// notifier
			if (_mMyAccountWebSocketNotifierNeeded2ReconnectLater
					&& _mContactsSelectView == _contentView) {
				// clear my account web socket notifier needed to reconnect
				// later flag
				_mMyAccountWebSocketNotifierNeeded2ReconnectLater = false;

				((MyTalkingGroupsView) _mContentView)
						.reconnectMyAccountWebSocketNotifier();
			}
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
					pushActivityForResult(SettingActivity.class,
							MYACCOUNT_CHANGED);
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

	// contacts selecting back bar button item on click listener
	class ContactsSelectingBackBarButtonItemOnClickListener implements
			OnClickListener {

		// contacts selecting sponsor type
		private SimpleIMeetingActivityContentViewType _mContactsSelectingSponsorType;

		@Deprecated
		public ContactsSelectingBackBarButtonItemOnClickListener() {
			super();
		}

		public ContactsSelectingBackBarButtonItemOnClickListener(
				SimpleIMeetingActivityContentViewType sponsorType) {
			super();

			// save sponsor type
			_mContactsSelectingSponsorType = sponsorType;
		}

		@Override
		public void onClick(View v) {
			// cancel selecting contacts for adding to talking group
			((ContactsSelectView) _mContentView)
					.cancelSelectingContacts4Adding2TalkingGroup(_mContactsSelectingSponsorType);

			// set main activity navigation title and left bar button item
			setMainActivityNavigationTitle7LeftBarButtonItem();
		}
	}

}
