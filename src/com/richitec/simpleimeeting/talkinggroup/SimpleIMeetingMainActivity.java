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
	}

	// set main activity content view
	private void setMainActivityContentView(
			SimpleIMeetingMainViewType mainViewType) {
		// main activity content view
		View _contentView;

		// save main view type
		_mMainViewType = mainViewType;

		// check main view type and content view
		switch (mainViewType) {
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

	private void setMainActivityContentView() {
		setMainActivityContentView(_mMainViewType);
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
			setMainActivityContentView(SimpleIMeetingMainViewType.MY_TALKINGGROUP_LIST);
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
			setMainActivityContentView(SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS);
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
