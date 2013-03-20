package com.richitec.simpleimeeting.talkinggroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.addressbook.ContactBean;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar.OnTouchListener;
import com.richitec.commontoolkit.utils.StringUtils;
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

	// contact in addressbook is selected flag which saved in contact bean
	// extension structured and in addressbook contact adapter data key
	private final String CONTACT_IS_SELECTED = "in address book contact is selected";

	// simple imeeting main view type
	private SimpleIMeetingMainViewType _mMainViewType = SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS;

	// contact search type
	private ContactSearchType _mContactSearchType = ContactSearchType.NONE;

	// all address book name phonetic sorted contacts detail info list
	private static List<ContactBean> _mAllNamePhoneticSortedContactsInfoArray;

	// present contacts in addressbook detail info list
	private List<ContactBean> _mPresentContactsInABInfoArray = _mAllNamePhoneticSortedContactsInfoArray;

	// in addressbook contacts present list view
	private ListView _mInABContactsPresentListView;

	// init all name phonetic sorted contacts info array
	public static void initNamePhoneticSortedContactsInfoArray() {
		_mAllNamePhoneticSortedContactsInfoArray = AddressBookManager
				.getInstance().getAllNamePhoneticSortedContactsInfoArray();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		// setMainActivityContentView();
		setContentView(SIMViewFactory.createSIMView(ContactsSelectView.class));
		// setContentView(getLayoutInflater().inflate(
		// R.layout.contacts_select_view_layout, null));

		// set navigation bar
		setMainActivityNavigationBar();

		// init contacts in addressbook present list view
		// get contacts in addressbook present list view
		_mInABContactsPresentListView = (ListView) findViewById(R.id.cs_contactsListView);

		// set contacts in addressbook present listView adapter
		_mInABContactsPresentListView
				.setAdapter(generateInABContactAdapter(_mPresentContactsInABInfoArray));
		// init contacts in addressbook present listView quick alphabet bar and
		// add on touch listener
		new ListViewQuickAlphabetBar(_mInABContactsPresentListView)
				.setOnTouchListener(new ContactsInABPresentListViewQuickAlphabetBarOnTouchListener());

		// bind contacts in addressbook present listView on item click listener
		_mInABContactsPresentListView
				.setOnItemClickListener(new ContactsInABPresentListViewOnItemClickListener());

		// init contact search edit text
		// bind contact search editText text watcher
		((EditText) findViewById(R.id.cs_cl_contactSearchEditText))
				.addTextChangedListener(new ContactSearchEditTextTextWatcher());
	}

	// set main activity content view
	private void setMainActivityContentView() {
		// main activity content view layout resource id
		int _layoutResID;

		// check main view type
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST:
			_layoutResID = R.layout.my_talkinggroup_view_layout;
			break;

		default:
		case ADDRESSBOOK_CONTACTS:
			_layoutResID = R.layout.contacts_select_view_layout;
			break;
		}

		// check content view layout resource id and set content view
		if (0 != _layoutResID) {
			setContentView(_layoutResID);
		} else {
			Log.e(LOG_TAG,
					"No, no no ...! There is no main activity content view layout resource.");
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

	// generate in addressbook contact adapter
	private ListAdapter generateInABContactAdapter(
			List<ContactBean> presentContactsInAB) {
		// in addressbook contacts adapter data keys
		final String PRESENT_CONTACT_NAME = "present_contact_name";
		final String PRESENT_CONTACT_PHONES = "present_contact_phones";

		// set addressbook contacts list view present data list
		List<Map<String, ?>> _addressBookContactsPresentDataList = new ArrayList<Map<String, ?>>();

		for (ContactBean _contact : presentContactsInAB) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// get contact name and phone matching indexes
			SparseIntArray _nameMatchingIndexes = (SparseIntArray) _contact
					.getExtension().get(
							AddressBookManager.NAME_MATCHING_INDEXES);
			@SuppressWarnings("unchecked")
			List<List<Integer>> _phoneMatchingIndexes = (List<List<Integer>>) _contact
					.getExtension().get(
							AddressBookManager.PHONENUMBER_MATCHING_INDEXES);

			// set data
			if (ContactSearchType.CHARACTER == _mContactSearchType
					|| ContactSearchType.CHINESE_CHARACTER == _mContactSearchType) {
				// get display name
				SpannableString _displayName = new SpannableString(
						_contact.getDisplayName());

				// set attributed
				for (int i = 0; i < _nameMatchingIndexes.size(); i++) {
					// get key and value
					Integer _nameCharMatchedPos = getRealPositionInContactDisplayName(
							_contact.getDisplayName(),
							_nameMatchingIndexes.keyAt(i));
					Integer _nameCharMatchedLength = _nameMatchingIndexes
							.get(_nameMatchingIndexes.keyAt(i));

					_displayName
							.setSpan(
									new ForegroundColorSpan(Color.BLUE),
									_nameCharMatchedPos,
									AddressBookManager.NAME_CHARACTER_FUZZYMATCHED_LENGTH == _nameCharMatchedLength ? _nameCharMatchedPos + 1
											: _nameCharMatchedPos
													+ _nameCharMatchedLength,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				_dataMap.put(PRESENT_CONTACT_NAME, _displayName);
			} else {
				_dataMap.put(PRESENT_CONTACT_NAME, _contact.getDisplayName());
			}
			if (ContactSearchType.PHONE == _mContactSearchType) {
				// get format phone number string
				SpannableString _formatPhoneNumberString = new SpannableString(
						_contact.getFormatPhoneNumbers());

				// get format phone number string separator "\n" positions
				List<Integer> _sepPositions = StringUtils.subStringPositions(
						_contact.getFormatPhoneNumbers(), "\n");

				// set attributed
				for (int i = 0; i < _phoneMatchingIndexes.size(); i++) {
					// check the phone matched
					if (0 != _phoneMatchingIndexes.get(i).size()) {
						// get begin and end position
						int _beginPos = _phoneMatchingIndexes.get(i).get(0);
						int _endPos = _phoneMatchingIndexes.get(i).get(
								_phoneMatchingIndexes.get(i).size() - 1) + 1;

						// check matched phone
						if (1 <= i) {
							_beginPos += _sepPositions.get(i - 1) + 1;
							_endPos += _sepPositions.get(i - 1) + 1;
						}

						_formatPhoneNumberString.setSpan(
								new ForegroundColorSpan(Color.BLUE), _beginPos,
								_endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}

				_dataMap.put(PRESENT_CONTACT_PHONES, _formatPhoneNumberString);
			} else {
				_dataMap.put(PRESENT_CONTACT_PHONES,
						_contact.getFormatPhoneNumbers());
			}

			// put alphabet index
			_dataMap.put(CTListAdapter.ALPHABET_INDEX,
					_contact.getNamePhoneticsString());

			// get in address book contact is selected flag saved in contact
			// bean extension structured
			Boolean _isSelected = (Boolean) _contact.getExtension().get(
					CONTACT_IS_SELECTED);
			if (null == _isSelected) {
				_contact.getExtension().put(CONTACT_IS_SELECTED, false);
			}
			_dataMap.put(CONTACT_IS_SELECTED,
					_contact.getExtension().get(CONTACT_IS_SELECTED));

			// add data to list
			_addressBookContactsPresentDataList.add(_dataMap);
		}

		// get address book contacts listView adapter
		InAB6In7PreinTalkingGroupContactAdapter _addressBookContactsListViewAdapter = (InAB6In7PreinTalkingGroupContactAdapter) _mInABContactsPresentListView
				.getAdapter();

		return null == _addressBookContactsListViewAdapter ? new InAB6In7PreinTalkingGroupContactAdapter(
				this,
				_addressBookContactsPresentDataList,
				R.layout.addressbook_contact_layout,
				new String[] { PRESENT_CONTACT_NAME, PRESENT_CONTACT_PHONES,
						CONTACT_IS_SELECTED },
				new int[] {
						R.id.adressBook_contact_displayName_textView,
						R.id.addressBook_contact_phoneNumber_textView,
						R.id.addressBook_contact_unsel6sel_imageView_parentFrameLayout })
				: _addressBookContactsListViewAdapter
						.setData(_addressBookContactsPresentDataList);
	}

	// get real position in contact display name with original position
	private Integer getRealPositionInContactDisplayName(String displayName,
			Integer origPosition) {
		int _realPos = 0;

		int _tmpPos = 0;
		boolean _prefixHasChar = false;

		for (int i = 0; i < displayName.length(); i++) {
			if (String.valueOf(displayName.charAt(i))
					.matches("[\u4e00-\u9fa5]")) {
				if (_prefixHasChar) {
					_prefixHasChar = false;

					_tmpPos += 1;
				}

				if (_tmpPos == origPosition) {
					_realPos = i;

					break;
				}

				_tmpPos += 1;
			} else if (' ' == displayName.charAt(i)) {
				if (_prefixHasChar) {
					_prefixHasChar = false;

					_tmpPos += 1;
				}
			} else {
				if (_tmpPos == origPosition) {
					_realPos = i;

					break;
				}

				_prefixHasChar = true;
			}
		}

		return _realPos;
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

	// contact search type
	enum ContactSearchType {
		NONE, CHARACTER, CHINESE_CHARACTER, PHONE
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

	// in addressbook and prein talking group contact adapter
	class InAB6In7PreinTalkingGroupContactAdapter extends CTListAdapter {

		public InAB6In7PreinTalkingGroupContactAdapter(Context context,
				List<Map<String, ?>> data, int itemsLayoutResId,
				String[] dataKeys, int[] itemsComponentResIds) {
			super(context, data, itemsLayoutResId, dataKeys,
					itemsComponentResIds);
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item data object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// textView
			if (view instanceof TextView) {
				// set view text
				((TextView) view)
						.setText(null == _itemData ? ""
								: _itemData instanceof SpannableString ? (SpannableString) _itemData
										: _itemData.toString());
			}
			// contact in talking group flag imageView or contact in addressbook
			// selected/unselected flag imageView parent frameLayout
			else if (view instanceof ImageView || view instanceof FrameLayout) {
				try {
					// define item data boolean and convert item data to boolean
					Boolean _itemDataBoolean = (Boolean) _itemData;

					// check view type
					if (view instanceof ImageView) {
						// get it's parent relativeLayout
						RelativeLayout _parentRelativeLayout = (RelativeLayout) view
								.getParent();

						// contact in talking group flag imageView
						if (_itemDataBoolean) {
							// hide in talking group image view
							((ImageView) view).setVisibility(View.INVISIBLE);

							// clear it's parent relativeLayout background, set
							// transparent
							_parentRelativeLayout
									.setBackgroundColor(Color.TRANSPARENT);
						} else {
							// show in talking group image view
							((ImageView) view).setVisibility(View.VISIBLE);

							// reset it's parent relativeLayout background
							_parentRelativeLayout
									.setBackgroundResource(R.drawable.in7preintalkinggroup_contact_bg);
						}
					} else if (view instanceof FrameLayout) {
						// contact in address book selected/unselected flag
						// imageView parent frameLayout get selected and
						// unselected imageView
						ImageView _selectedImageView = (ImageView) ((FrameLayout) view)
								.findViewById(R.id.addressBook_contact_selected_imageView);
						ImageView _unselectedImageView = (ImageView) ((FrameLayout) view)
								.findViewById(R.id.addressBook_contact_unselected_imageView);

						if (_itemDataBoolean) {
							// show selected imageView and hide unselected
							// imageView
							_selectedImageView.setVisibility(View.VISIBLE);
							_unselectedImageView.setVisibility(View.GONE);
						} else {
							// show unselected imageView and hide selected
							// imageView
							_unselectedImageView.setVisibility(View.VISIBLE);
							_selectedImageView.setVisibility(View.GONE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to boolean error, item data = "
									+ _itemData);
				}
			}
		}

	}

	// contacts in addressbook present listView quick alphabet bar on touch
	// listener
	class ContactsInABPresentListViewQuickAlphabetBarOnTouchListener extends
			OnTouchListener {

		@Override
		protected boolean onTouch(RelativeLayout alphabetRelativeLayout,
				ListView dependentListView, MotionEvent event,
				Character alphabeticalCharacter) {
			// get scroll position
			if (dependentListView.getAdapter() instanceof CTListAdapter) {
				// get dependent listView adapter
				CTListAdapter _commonListAdapter = (CTListAdapter) dependentListView
						.getAdapter();

				for (int i = 0; i < _commonListAdapter.getCount(); i++) {
					// get alphabet index
					@SuppressWarnings("unchecked")
					String _alphabetIndex = (String) ((Map<String, ?>) _commonListAdapter
							.getItem(i)).get(CTListAdapter.ALPHABET_INDEX);

					// check alphabet index
					if (null == _alphabetIndex
							|| _alphabetIndex.startsWith(String.valueOf(
									alphabeticalCharacter).toLowerCase(
									Locale.getDefault()))) {
						// set selection
						dependentListView.setSelection(i);

						break;
					}
				}
			} else {
				Log.e(LOG_TAG, "Dependent listView adapter = "
						+ dependentListView.getAdapter() + " and class name = "
						+ dependentListView.getAdapter().getClass().getName());
			}

			return true;
		}

	}

	// contacts in addressbook present listView on item click listener
	class ContactsInABPresentListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// // get the click item view data: contact object
			// ContactBean _clickItemViewData = _mPresentContactsInABInfoArray
			// .get((int) id);
			//
			// // get contact is selected flag
			// Boolean _isSelected = (Boolean) _clickItemViewData.getExtension()
			// .get(CONTACT_IS_SELECTED);
			//
			// // check the click item view data(contact object) is selected
			// if (_isSelected) {
			// // mark contact unselected
			// markContactUnselected((int) id, true);
			// } else {
			// // check the click item view data
			// if (null == _clickItemViewData.getPhoneNumbers()) {
			// // show contact has no phone number alert dialog
			// new AlertDialog.Builder(ContactSelectActivity.this)
			// .setTitle(
			// R.string.contact_hasNoPhone_alertDialog_title)
			// .setMessage(_clickItemViewData.getDisplayName())
			// .setPositiveButton(
			// R.string.contact_hasNoPhone_alertDialog_reselectBtn_title,
			// null).show();
			// } else {
			// switch (_clickItemViewData.getPhoneNumbers().size()) {
			// case 1:
			// // mark contact selected
			// markContactSelected(_clickItemViewData
			// .getPhoneNumbers().get(0), (int) id, true);
			//
			// break;
			//
			// default:
			// // set contact phone numbers for selecting
			// _mContactPhoneNumbersSelectPopupWindow
			// .setContactPhones4Selecting(
			// _clickItemViewData.getDisplayName(),
			// _clickItemViewData.getPhoneNumbers(),
			// position);
			//
			// // show contact phone numbers select popup window
			// _mContactPhoneNumbersSelectPopupWindow.showAtLocation(
			// parent, Gravity.CENTER, 0, 0);
			//
			// break;
			// }
			// }
			// }
		}

	}

	// contact search editText text watcher
	class ContactSearchEditTextTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// update contact search type
			if (null == s || 0 == s.length()) {
				_mContactSearchType = ContactSearchType.NONE;
			} else if (s.toString().matches("^[0-9]*$")) {
				_mContactSearchType = ContactSearchType.PHONE;
			} else if (s.toString().matches(".*[\u4e00-\u9fa5].*")) {
				_mContactSearchType = ContactSearchType.CHINESE_CHARACTER;
			} else {
				_mContactSearchType = ContactSearchType.CHARACTER;
			}

			// update present contacts in addressbook detail info list
			switch (_mContactSearchType) {
			case CHARACTER:
				_mPresentContactsInABInfoArray = AddressBookManager
						.getInstance().getContactsByName(s.toString());
				break;

			case CHINESE_CHARACTER:
				_mPresentContactsInABInfoArray = AddressBookManager
						.getInstance().getContactsByChineseName(s.toString());
				break;

			case PHONE:
				_mPresentContactsInABInfoArray = AddressBookManager
						.getInstance().getContactsByPhone(s.toString());
				break;

			case NONE:
			default:
				_mPresentContactsInABInfoArray = _mAllNamePhoneticSortedContactsInfoArray;
				break;
			}

			// update contacts in addressbook present listView adapter
			_mInABContactsPresentListView
					.setAdapter(generateInABContactAdapter(_mPresentContactsInABInfoArray));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

	}

}
