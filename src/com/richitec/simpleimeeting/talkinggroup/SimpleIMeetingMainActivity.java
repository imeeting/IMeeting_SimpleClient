package com.richitec.simpleimeeting.talkinggroup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.assistant.AboutActivity;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingImageBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;
import com.richitec.simpleimeeting.view.SIMViewFactory;

public class SimpleIMeetingMainActivity extends
		SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SimpleIMeetingMainActivity.class
			.getCanonicalName();

	// simple imeeting main view type
	private SimpleIMeetingMainViewType _mMainViewType = SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS;

	// contacts select and my talking groups subviews
	private View _mContactsSelectView;
	private View _mMyTalkingGroupsView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setMainActivityContentView();

		// set navigation bar
		setMainActivityNavigationBar();

		Log.d(LOG_TAG, "_mInABContactsPresentListView = "
				+ findViewById(R.id.cs_contactsListView));
	}

	// set main activity content view
	private void setMainActivityContentView() {
		// main activity content view
		View _contentView;

		// check main view type and content view
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST: {
			// check my talking groups view
			if (null == _mMyTalkingGroupsView) {
				// create my talking groups view as content view
				_mMyTalkingGroupsView = SIMViewFactory.createSIMView4Present(
						this, MyTalkingGroupsView.class);
			} else {
				// get my talking groups' parent view
				ViewParent _myTalkingGroupsParentView = _mMyTalkingGroupsView
						.getParent();

				// check my talking groups' parent view
				if (null != _myTalkingGroupsParentView) {
					// remove my talking group list view from its parent view
					((ViewGroup) _myTalkingGroupsParentView)
							.removeView(_mMyTalkingGroupsView);
				}
			}

			_contentView = _mMyTalkingGroupsView;
		}
			break;

		default:
		case ADDRESSBOOK_CONTACTS: {
			// check contacts select content view
			if (null == _mContactsSelectView) {
				// create contacts select view as content view
				_mContactsSelectView = SIMViewFactory.createSIMView4Present(
						this, ContactsSelectView.class);
			} else {
				// get contacts select's parent view
				ViewParent _contactsSelectParentView = _mContactsSelectView
						.getParent();

				// check contacts select's parent view
				if (null != _contactsSelectParentView) {
					// remove contacts select view from its parent view
					((ViewGroup) _contactsSelectParentView)
							.removeView(_mContactsSelectView);
				}
			}

			_contentView = _mContactsSelectView;
		}
			break;
		}

		// check content view and set content view
		if (null != _contentView) {
			setContentView(_contentView);
		} else {
			Log.e(LOG_TAG,
					"No, no no ...! There is no main activity content view.");
		}
	}

	// set main activity navigation bar
	private void setMainActivityNavigationBar() {
		// set title
		setTitle("点击创建会议");

		// main activity left bar button item title id and on click listener
		int _titleId;
		OnClickListener _onClickListener;

		// check main view type
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST:
			_titleId = R.string.myTalkingGroup_leftBarButtonItem_navTitle;
			_onClickListener = new AddressBookContactsBarButtonItemOnClickListener();
			break;

		default:
		case ADDRESSBOOK_CONTACTS:
			_titleId = R.string.contactsSelect_leftBarButtonItem_navTitle;
			_onClickListener = new MyTalkingGroupBarButtonItemOnClickListener();
			break;
		}

		// check left bar button item title id and set left bar button item
		if (0 != _titleId) {
			setLeftBarButtonItem(new SimpleIMeetingBarButtonItem(this,
					BarButtonItemStyle.RIGHT_GO, _titleId, _onClickListener));
		} else {
			Log.e(LOG_TAG,
					"No, no no ...! There is no main activity left bar button item title resource.");
		}

		// set right image bar button item
		setRightBarButtonItem(new SimpleIMeetingImageBarButtonItem(this,
				android.R.drawable.ic_dialog_info, BarButtonItemStyle.RIGHT_GO,
				new MoreMenuImageBarButtonItemOnClickListener()));
	}

	// // mark the contact unselected
	// private void markContactUnselected(int contactPosition,
	// boolean isClickedOnInABContactsPresentListView) {
	// // get the selected contact
	// ContactBean _selectedContact;
	// if (isClickedOnInABContactsPresentListView) {
	// _selectedContact = _mPresentContactsInABInfoArray
	// .get(contactPosition);
	// } else {
	// _selectedContact = _mPreinTalkingGroupContactsInfoArray
	// .get(contactPosition
	// - _mTalkingGroupContactsPhoneArray.size());
	// }
	//
	// // update contact is selected flag
	// _selectedContact.getExtension().put(CONTACT_IS_SELECTED, false);
	//
	// // update in addressbook contacts present listView, if the selected
	// // contact is present in addressbook contacts present listView
	// if (_mPresentContactsInABInfoArray.contains(_selectedContact)) {
	// // get in addressbook present contacts adapter
	// InAB6In7PreinTalkingGroupContactAdapter _inABContactAdapter =
	// (InAB6In7PreinTalkingGroupContactAdapter) _mABContactsListView
	// .getAdapter();
	//
	// // get in addressbook present contacts adapter data map
	// @SuppressWarnings("unchecked")
	// Map<String, Object> _inABContactAdapterDataMap = (Map<String, Object>)
	// _inABContactAdapter
	// .getItem(_mPresentContactsInABInfoArray
	// .indexOf(_selectedContact));
	//
	// // update addressbook present contacts adapter data map and notify
	// // adapter changed
	// _inABContactAdapterDataMap.put(CONTACT_IS_SELECTED,
	// _selectedContact.getExtension().get(CONTACT_IS_SELECTED));
	// _inABContactAdapter.notifyDataSetChanged();
	// }
	//
	// // get select contact in prein talking contacts detail info list
	// // position
	// int _index = _mPreinTalkingGroupContactsInfoArray
	// .indexOf(_selectedContact);
	//
	// // remove from in and prein talking group contacts adapter data list and
	// // notify adapter changed
	// _mPreinTalkingGroupContactsInfoArray.remove(_index);
	// _mIn7PreinTalkingGroupContactsAdapterDataList
	// .remove(_mTalkingGroupContactsPhoneArray.size() + _index);
	// ((InAB6In7PreinTalkingGroupContactAdapter)
	// _mIn7PreinTalkingGroupContactsListView
	// .getAdapter()).notifyDataSetChanged();
	// }

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
			_mMainViewType = SimpleIMeetingMainViewType.MY_TALKINGGROUP_LIST;
			setMainActivityContentView();
			setMainActivityNavigationBar();
		}

	}

	// addressbook contacts bar button item on click listener
	class AddressBookContactsBarButtonItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// switch to addressbook contacts view
			Log.d(LOG_TAG, "Switch to addressbook contacts view");

			//
			_mMainViewType = SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS;
			setMainActivityContentView();
			setMainActivityNavigationBar();
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
