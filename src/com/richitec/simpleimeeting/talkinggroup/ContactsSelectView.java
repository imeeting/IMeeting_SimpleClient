package com.richitec.simpleimeeting.talkinggroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.addressbook.ContactBean;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.customcomponent.CTPopupWindow;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar;
import com.richitec.commontoolkit.customcomponent.ListViewQuickAlphabetBar.OnTouchListener;
import com.richitec.commontoolkit.utils.CommonUtils;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;
import com.richitec.commontoolkit.utils.StringUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.view.SIMBaseView;

public class ContactsSelectView extends SIMBaseView {

	private static final String LOG_TAG = ContactsSelectView.class
			.getCanonicalName();

	// contact in addressbook is selected flag which saved in contact bean
	// extension structured and in addressbook contact adapter data key
	private final String CONTACT_IS_SELECTED = "in address book contact is selected";

	// selected contact whose selected phone which saved in contact bean
	// extension structured
	private final String SELECTED_CONTACT_SELECTEDPHONE = "selected contact the selected phone";

	// in and prein talking group contacts adapter data keys
	private final String SELECTED_CONTACT_DISPLAYNAME = "selected_contact_displayName";
	private final String SELECTED_CONTACT_IS_IN_TALKINGGROUP = "selected_contact_is_in_talkingGroup";

	// contact search type
	private ContactSearchType _mContactSearchType = ContactSearchType.NONE;

	// all address book name phonetic sorted contacts detail info list
	private static List<ContactBean> _mAllNamePhoneticSortedContactsInfoArray;

	// present contacts in addressbook detail info list
	private List<ContactBean> _mPresentContactsInABInfoArray = _mAllNamePhoneticSortedContactsInfoArray;

	// in addressbook contacts present list view
	private ListView _mInABContactsPresentListView;

	// define contact phone numbers select popup window
	private final ContactPhoneNumbersSelectPopupWindow _mContactPhoneNumbersSelectPopupWindow = new ContactPhoneNumbersSelectPopupWindow(
			R.layout.contact_phonenumbers_select_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	// in and prein talking group contacts list view
	private ListView _mIn7PreinTalkingGroupContactsListView;

	// talking group attendees phone list
	private List<String> _mTalkingGroupContactsPhoneArray = new ArrayList<String>();

	// prein talking group contacts detail info list
	private final List<ContactBean> _mPreinTalkingGroupContactsInfoArray = new ArrayList<ContactBean>();

	// in and prein talking group contacts adapter data list
	private final List<Map<String, ?>> _mIn7PreinTalkingGroupContactsAdapterDataList = new ArrayList<Map<String, ?>>();

	// define new talking group atarted time select popup window
	private final NewTalkingGroupStartedTimeSelectPopupWindow _mNewTalkingGroupStartedTimeSelectPopupWindow = new NewTalkingGroupStartedTimeSelectPopupWindow(
			R.layout.talkinggroup_startedtime_select_layout,
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	// init all name phonetic sorted contacts info array
	public static void initNamePhoneticSortedContactsInfoArray() {
		_mAllNamePhoneticSortedContactsInfoArray = AddressBookManager
				.getInstance().getAllNamePhoneticSortedContactsInfoArray();
	}

	@Override
	public int presentViewLayout() {
		// return contacts select view layout
		return R.layout.contacts_select_view_layout;
	}

	@Override
	public void initSubComponents() {
		// test by ares
		_mTalkingGroupContactsPhoneArray.add("13770662051");
		_mTalkingGroupContactsPhoneArray.add("14756498708");

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

		// init attendees phone in talking group and contacts in prein talking
		// group list view
		_mIn7PreinTalkingGroupContactsListView = (ListView) findViewById(R.id.cs_selectedContactsListView);

		// generate in and prein talking group contact adapter
		// process in talking group attendees phone list, then set prein talking
		// group contacts list view present data list
		for (int i = 0; i < _mTalkingGroupContactsPhoneArray.size(); i++) {
			// add data to list
			_mIn7PreinTalkingGroupContactsAdapterDataList
					.add(generateIn6PreinTalkingGroupAdapterData(
							_mTalkingGroupContactsPhoneArray.get(i), true));
		}

		// set contacts in and prein talking group listView adapter
		_mIn7PreinTalkingGroupContactsListView
				.setAdapter(new InAB6In7PreinTalkingGroupContactAdapter(
						getContext(),
						_mIn7PreinTalkingGroupContactsAdapterDataList,
						R.layout.in7prein_talkinggroup_contact_layout,
						new String[] { SELECTED_CONTACT_DISPLAYNAME,
								SELECTED_CONTACT_IS_IN_TALKINGGROUP },
						new int[] { R.id.ipitgc_contactDisplayName_textView,
								R.id.ipitgc_contactPreinTalkingGroup_imageView }));

		// bind contacts in and prein talking group listView item click listener
		_mIn7PreinTalkingGroupContactsListView
				.setOnItemClickListener(new ContactsIn7PreinTalkingGroupListViewOnItemClickListener());

		// bind invite selected contacts to talking group button on click
		// listener
		((Button) findViewById(R.id.cs_inviteSelectedContacts2talkingGroup_button))
				.setOnClickListener(new InviteSelectedContacts2TalkingGroupButtonOnClickListener());
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
				getContext(),
				_addressBookContactsPresentDataList,
				R.layout.addressbook_contact_layout,
				new String[] { PRESENT_CONTACT_NAME, PRESENT_CONTACT_PHONES,
						CONTACT_IS_SELECTED },
				new int[] { R.id.abc_contactDisplayName_textView,
						R.id.abc_contactPhoneNumber_textView,
						R.id.abc_contact_unsel6sel_imageView_parentFrameLayout })
				: _addressBookContactsListViewAdapter
						.setData(_addressBookContactsPresentDataList);
	}

	// generate in or prein talking group adapter data
	private Map<String, Object> generateIn6PreinTalkingGroupAdapterData(
			String displayName6Phone, boolean isInTalkingGroup) {
		Map<String, Object> _dataMap = new HashMap<String, Object>();

		// set data
		_dataMap.put(
				SELECTED_CONTACT_DISPLAYNAME,
				isInTalkingGroup ? AddressBookManager.getInstance()
						.getContactsDisplayNamesByPhone(displayName6Phone)
						.get(0) : displayName6Phone);
		_dataMap.put(SELECTED_CONTACT_IS_IN_TALKINGGROUP, isInTalkingGroup);

		return _dataMap;
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

	// mark contact selected
	private void markContactSelected(String selectedPhone, int contactPosition,
			boolean isPresentInABContactsListView) {
		// get selected contacts relativeLayout
		RelativeLayout _selectedContactsRelativeLayout = (RelativeLayout) findViewById(R.id.cs_selectedContacts_relativeLayout);

		// check selected contacts is visible or invisible or gone
		if (View.VISIBLE != _selectedContactsRelativeLayout.getVisibility()) {
			// get contacts list relativeLayout
			RelativeLayout _contactsListRelativeLayout = (RelativeLayout) findViewById(R.id.cs_contactsList_relativeLayout);

			// add padding right 6dp for contacts list relativeLayout
			_contactsListRelativeLayout.setPadding(0, 0,
					DisplayScreenUtils.dp2pix(6), 0);

			// update contacts list relativeLayout background
			_contactsListRelativeLayout
					.setBackgroundResource(R.drawable.img_contactslist4select_relativelayout_bg);

			// show selected contacts relativeLayout
			_selectedContactsRelativeLayout.setVisibility(View.VISIBLE);
		}

		// get selected contact object
		ContactBean _selectedContact;

		// check if it is present in address book contacts listView
		if (isPresentInABContactsListView) {
			_selectedContact = _mPresentContactsInABInfoArray
					.get(contactPosition);
		} else {
			_selectedContact = _mAllNamePhoneticSortedContactsInfoArray
					.get(contactPosition);
		}

		// check the selected contact is in talking group attendees
		if (_mTalkingGroupContactsPhoneArray.contains(selectedPhone)) {
			Toast.makeText(
					getContext(),
					AddressBookManager.getInstance()
							.getContactsDisplayNamesByPhone(selectedPhone)
							.get(0)
							+ getContext()
									.getResources()
									.getString(
											R.string.toast_selectedContact_existedInTalkingGroup_attendees),
					Toast.LENGTH_SHORT).show();

			return;
		}

		// set selected contact the selected phone
		_selectedContact.getExtension().put(SELECTED_CONTACT_SELECTEDPHONE,
				selectedPhone);

		// update contact is selected flag
		_selectedContact.getExtension().put(CONTACT_IS_SELECTED, true);

		// update address book contacts listView, if the selected contact is
		// present in address book contacts listView
		if (isPresentInABContactsListView) {
			// get in address book present contacts adapter
			InAB6In7PreinTalkingGroupContactAdapter _inABContactAdapter = (InAB6In7PreinTalkingGroupContactAdapter) _mInABContactsPresentListView
					.getAdapter();

			// get in address book present contacts adapter data map
			@SuppressWarnings("unchecked")
			Map<String, Object> _inABContactAdapterDataMap = (Map<String, Object>) _inABContactAdapter
					.getItem(contactPosition);

			// update address book present contacts adapter data map and notify
			// adapter changed
			_inABContactAdapterDataMap.put(CONTACT_IS_SELECTED,
					_selectedContact.getExtension().get(CONTACT_IS_SELECTED));
			_inABContactAdapter.notifyDataSetChanged();
		}

		// add to in and prein talking group contacts adapter data list and
		// notify adapter changed
		_mPreinTalkingGroupContactsInfoArray.add(_selectedContact);
		_mIn7PreinTalkingGroupContactsAdapterDataList
				.add(generateIn6PreinTalkingGroupAdapterData(
						_selectedContact.getDisplayName(), false));
		((InAB6In7PreinTalkingGroupContactAdapter) _mIn7PreinTalkingGroupContactsListView
				.getAdapter()).notifyDataSetChanged();
	}

	// mark the contact unselected
	private void markContactUnselected(int contactPosition,
			boolean isClickedOnInABContactsPresentListView) {
		// get the selected contact
		ContactBean _selectedContact;
		if (isClickedOnInABContactsPresentListView) {
			_selectedContact = _mPresentContactsInABInfoArray
					.get(contactPosition);
		} else {
			_selectedContact = _mPreinTalkingGroupContactsInfoArray
					.get(contactPosition
							- _mTalkingGroupContactsPhoneArray.size());
		}

		// update contact is selected flag
		_selectedContact.getExtension().put(CONTACT_IS_SELECTED, false);

		// update in addressbook contacts present listView, if the selected
		// contact is present in addressbook contacts present listView
		if (_mPresentContactsInABInfoArray.contains(_selectedContact)) {
			// get in addressbook present contacts adapter
			InAB6In7PreinTalkingGroupContactAdapter _inABContactAdapter = (InAB6In7PreinTalkingGroupContactAdapter) _mInABContactsPresentListView
					.getAdapter();

			// get in addressbook present contacts adapter data map
			@SuppressWarnings("unchecked")
			Map<String, Object> _inABContactAdapterDataMap = (Map<String, Object>) _inABContactAdapter
					.getItem(_mPresentContactsInABInfoArray
							.indexOf(_selectedContact));

			// update addressbook present contacts adapter data map and notify
			// adapter changed
			_inABContactAdapterDataMap.put(CONTACT_IS_SELECTED,
					_selectedContact.getExtension().get(CONTACT_IS_SELECTED));
			_inABContactAdapter.notifyDataSetChanged();
		}

		// get select contact in prein talking contacts detail info list
		// position
		int _index = _mPreinTalkingGroupContactsInfoArray
				.indexOf(_selectedContact);

		// remove from in and prein talking group contacts adapter data list and
		// notify adapter changed
		_mPreinTalkingGroupContactsInfoArray.remove(_index);
		_mIn7PreinTalkingGroupContactsAdapterDataList
				.remove(_mTalkingGroupContactsPhoneArray.size() + _index);
		((InAB6In7PreinTalkingGroupContactAdapter) _mIn7PreinTalkingGroupContactsListView
				.getAdapter()).notifyDataSetChanged();
	}

	// send invite to talking group sms
	private void sendInviteSMS(String inviteNote) {
		// sms recipient
		StringBuilder _recipients = new StringBuilder();

		// process each invited contacts phone numbers
		for (int i = 0; i < _mPreinTalkingGroupContactsInfoArray.size(); i++) {
			_recipients.append(_mPreinTalkingGroupContactsInfoArray.get(i)
					.getExtension().get(SELECTED_CONTACT_SELECTEDPHONE));

			// add more recipient
			if (_mPreinTalkingGroupContactsInfoArray.size() - 1 != i) {
				_recipients.append(',');
			}
		}

		// define send sms intent
		Intent _sendSMSIntent = new Intent(Intent.ACTION_SENDTO,
				Uri.parse("smsto:" + _recipients));

		// send sms body
		_sendSMSIntent.putExtra("sms_body", inviteNote);

		// check and start send sms activity
		if (CommonUtils.isIntentAvailable(_sendSMSIntent)) {
			getContext().startActivity(_sendSMSIntent);
		} else {
			Log.e(LOG_TAG, "Device not support send SMS");
		}
	}

	// inner class
	// contact search type
	enum ContactSearchType {
		NONE, CHARACTER, CHINESE_CHARACTER, PHONE
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
								.findViewById(R.id.abc_contactSelected_imageView);
						ImageView _unselectedImageView = (ImageView) ((FrameLayout) view)
								.findViewById(R.id.abc_contactUnselected_imageView);

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
			// get the click item view data: contact object
			ContactBean _clickItemViewData = _mPresentContactsInABInfoArray
					.get((int) id);

			// get contact is selected flag
			Boolean _isSelected = (Boolean) _clickItemViewData.getExtension()
					.get(CONTACT_IS_SELECTED);

			// check the click item view data(contact object) is selected
			if (_isSelected) {
				// mark contact unselected
				markContactUnselected((int) id, true);
			} else {
				// check the click item view data
				if (null == _clickItemViewData.getPhoneNumbers()) {
					// show contact has no phone number alert dialog
					new AlertDialog.Builder(getContext())
							.setTitle(
									R.string.contactsSelect_contactHasNoPhone_alertDialog_title)
							.setMessage(_clickItemViewData.getDisplayName())
							.setPositiveButton(
									R.string.contactsSelect_contactHasNoPhone_alertDialog_reselectBtn_title,
									null).show();
				} else {
					// check click item view data
					switch (_clickItemViewData.getPhoneNumbers().size()) {
					case 1:
						// mark contact selected
						markContactSelected(_clickItemViewData
								.getPhoneNumbers().get(0), (int) id, true);
						break;

					default: {
						// set contact phone numbers for selecting
						_mContactPhoneNumbersSelectPopupWindow
								.setContactPhones4Selecting(
										_clickItemViewData.getDisplayName(),
										_clickItemViewData.getPhoneNumbers(),
										position);

						// show contact phone numbers select popup window
						_mContactPhoneNumbersSelectPopupWindow.showAtLocation(
								parent, Gravity.CENTER, 0, 0);
					}
						break;
					}
				}
			}
		}

	}

	// contact phone numbers select popup window
	class ContactPhoneNumbersSelectPopupWindow extends CTPopupWindow {

		// select contact position
		private int _mSelectContactPosition;

		public ContactPhoneNumbersSelectPopupWindow(int resource, int width,
				int height, boolean focusable, boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public ContactPhoneNumbersSelectPopupWindow(int resource, int width,
				int height) {
			super(resource, width, height);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {
			// get contact phones select phone button parent linearLayout
			LinearLayout _phoneBtnParentLinearLayout = (LinearLayout) getContentView()
					.findViewById(
							R.id.cps_contactPhonesSelect_phoneButtons_linearLayout);

			// bind contact phone select phone button click listener
			for (int i = 0; i < _phoneBtnParentLinearLayout.getChildCount(); i++) {
				((Button) _phoneBtnParentLinearLayout.getChildAt(i))
						.setOnClickListener(new ContactPhoneSelectPhoneBtnOnClickListener());
			}

			// bind contact phone select phone listView item click listener
			((ListView) getContentView().findViewById(
					R.id.cps_contactPhonesSelect_phonesListView))
					.setOnItemClickListener(new ContactPhoneSelectPhoneListViewOnItemClickListener());

			// bind contact phone select cancel button click listener
			((Button) getContentView().findViewById(
					R.id.cps_contactPhonesSelect_cancelBtn))
					.setOnClickListener(new ContactPhoneSelectCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// hide contact phones select phone list view
			((ListView) getContentView().findViewById(
					R.id.cps_contactPhonesSelect_phonesListView))
					.setVisibility(View.GONE);

			// get contact phones select phone button parent linearLayout and
			// hide it
			LinearLayout _phoneBtnParentLinearLayout = (LinearLayout) getContentView()
					.findViewById(
							R.id.cps_contactPhonesSelect_phoneButtons_linearLayout);
			_phoneBtnParentLinearLayout.setVisibility(View.GONE);

			// process phone button
			for (int i = 0; i < _phoneBtnParentLinearLayout.getChildCount(); i++) {
				// hide contact phones select phone button
				((Button) _phoneBtnParentLinearLayout.getChildAt(i))
						.setVisibility(View.GONE);
			}
		}

		// set contact phone number for selecting
		public void setContactPhones4Selecting(String displayName,
				List<String> phoneNumbers, int position) {
			// update select contact position
			_mSelectContactPosition = position;

			// set contact phones select title textView text
			((TextView) getContentView().findViewById(
					R.id.cps_contactPhonesSelect_titleTextView))
					.setText(getContext()
							.getResources()
							.getString(
									R.string.contactPhonesSelect_selectPopupWindow_titleTextView_text)
							.replace("***", displayName));

			// check phone numbers for selecting
			if (2 <= phoneNumbers.size() && phoneNumbers.size() <= 3) {
				// get contact phones select phone button parent linearLayout
				// and show it
				LinearLayout _phoneBtnParentLinearLayout = (LinearLayout) getContentView()
						.findViewById(
								R.id.cps_contactPhonesSelect_phoneButtons_linearLayout);
				_phoneBtnParentLinearLayout.setVisibility(View.VISIBLE);

				// process phone button
				for (int i = 0; i < phoneNumbers.size(); i++) {
					// get contact phones select phone button
					Button _phoneBtn = (Button) _phoneBtnParentLinearLayout
							.getChildAt(i);

					// set button text and show it
					_phoneBtn.setText(phoneNumbers.get(i));
					_phoneBtn.setVisibility(View.VISIBLE);
				}
			} else {
				// get contact phones select phone list view
				ListView _phoneListView = (ListView) getContentView()
						.findViewById(
								R.id.cps_contactPhonesSelect_phonesListView);

				// set phone list view adapter
				_phoneListView
						.setAdapter(new ArrayAdapter<String>(
								CTApplication.getContext(),
								R.layout.contact_phonenumbers_select_phoneslist_item_layout,
								phoneNumbers));

				// show phone list view
				_phoneListView.setVisibility(View.VISIBLE);
			}
		}

		// inner class
		// contact phone select phone button on click listener
		class ContactPhoneSelectPhoneBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// get phone button text
				String _selectedPhone = (String) ((Button) v).getText();

				// dismiss contact phone select popup window
				dismiss();

				// mark contact selected
				markContactSelected(_selectedPhone, _mSelectContactPosition,
						true);
			}

		}

		// contact phone select phone listView on item click listener
		class ContactPhoneSelectPhoneListViewOnItemClickListener implements
				OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// get phone listView item data
				String _selectedPhone = (String) ((TextView) view).getText();

				// dismiss contact phone select popup window
				dismiss();

				// mark contact selected
				markContactSelected(_selectedPhone, _mSelectContactPosition,
						true);
			}

		}

		// contact phone select cancel button on click listener
		class ContactPhoneSelectCancelBtnOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss contact phone select popup window
				dismiss();
			}

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

	// contacts in and prein talking group listView on item click listener
	class ContactsIn7PreinTalkingGroupListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// check clicked item position
			if (position >= _mTalkingGroupContactsPhoneArray.size()) {
				// mark contact unselected
				markContactUnselected((int) id, false);
			}
		}

	}

	// invite selected contacts to talking group button on click listener
	class InviteSelectedContacts2TalkingGroupButtonOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// check in and prein talking group contacts size
			if (!_mPreinTalkingGroupContactsInfoArray.isEmpty()) {
				// check needs to show select talking group start date and time
				// popup window
				if (_mTalkingGroupContactsPhoneArray.isEmpty()) {
					// send get new talking group id http request
					// ??

					// test by ares
					// set got new talking group id
					_mNewTalkingGroupStartedTimeSelectPopupWindow
							.setGotNewTalkingGroupId("233455");

					// set current calendar for new talking group started time
					// select date and time picker
					_mNewTalkingGroupStartedTimeSelectPopupWindow
							.setCurrentCalendar4Date7timePicker(Calendar
									.getInstance());

					// show new talking group started time select popup window
					_mNewTalkingGroupStartedTimeSelectPopupWindow
							.showAtLocation(v, Gravity.CENTER, 0, 0);
				} else {
					// send invite sms
					sendInviteSMS("再次邀请的。。。");
				}
			} else {
				// show select one contact at least toast
				Toast.makeText(getContext(),
						R.string.toast_selectedContact_atLeastSelectOneContact,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	// new talking group started time select popup window
	class NewTalkingGroupStartedTimeSelectPopupWindow extends CTPopupWindow {

		// new talking group id
		private String _mNewTalkingGroupId;

		// new talking group started time select date and time picker
		private DatePicker _mNewTalkingGroupStartedTimeSelectDatePicker;
		private TimePicker _mNewTalkingGroupStartedTimeSelectTimePicker;

		public NewTalkingGroupStartedTimeSelectPopupWindow(int resource,
				int width, int height, boolean focusable,
				boolean isBindDefListener) {
			super(resource, width, height, focusable, isBindDefListener);
		}

		public NewTalkingGroupStartedTimeSelectPopupWindow(int resource,
				int width, int height) {
			super(resource, width, height);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {
			// bind new talking group started time select cancel button click
			// listener
			((Button) getContentView().findViewById(
					R.id.tgsts_talkingGroupStartedTimeSelect_cancelBtn))
					.setOnClickListener(new NewTalkingGroupStartedTimeSelectCancelButtonOnClickListener());

			// bind new talking group started time select confirm button click
			// listener
			((Button) getContentView().findViewById(
					R.id.tgsts_talkingGroupStartedTimeSelect_confirmBtn))
					.setOnClickListener(new NewTalkingGroupStartedTimeSelectConfirmButtonOnClickListener());

			// bind new talking group started time select copy invite note
			// button on click listener
			((Button) getContentView().findViewById(
					R.id.tgsts_talkingGroupStartedTimeSelect_copyInviteNoteBtn))
					.setOnClickListener(new NewTalkingGroupStartedTimeSelectCopyInviteNoteButtonOnClickListener());

			// get new talking group started time select date picker
			_mNewTalkingGroupStartedTimeSelectDatePicker = (DatePicker) getContentView()
					.findViewById(
							R.id.tgsts_talkingGroupStartedTimeSelect_datePicker);

			// bind its on date changed listener
			_mNewTalkingGroupStartedTimeSelectDatePicker
					.init(1970,
							1,
							1,
							new NewTalkingGroupStartedTimeSelectDatePickerOnDateChangedListener());

			// get new talking group started time select time picker
			_mNewTalkingGroupStartedTimeSelectTimePicker = (TimePicker) getContentView()
					.findViewById(
							R.id.tgsts_talkingGroupStartedTimeSelect_timePicker);

			// set its is 24 hour view
			_mNewTalkingGroupStartedTimeSelectTimePicker
					.setIs24HourView(Boolean.valueOf(true));

			// bind its on time changed listener
			_mNewTalkingGroupStartedTimeSelectTimePicker
					.setOnTimeChangedListener(new NewTalkingGroupStartedTimeSelectTimePickerOnTimeChangedListener());
		}

		@Override
		protected void resetPopupWindow() {
			// nothing to do
		}

		// set got new talking group id
		public void setGotNewTalkingGroupId(String talkingGroupId) {
			// save got new talking group id
			_mNewTalkingGroupId = talkingGroupId;
		}

		// set current calendar for new talking group started time select date
		// and time picker
		public void setCurrentCalendar4Date7timePicker(Calendar calendar) {
			// get current calendar year, month, day, hour and minute
			int _year = calendar.get(Calendar.YEAR);
			int _month = calendar.get(Calendar.MONTH);
			int _day = calendar.get(Calendar.DAY_OF_MONTH);
			int _hour = calendar.get(Calendar.HOUR_OF_DAY);
			int _minute = calendar.get(Calendar.MINUTE);

			// update new talking group started time select date picker
			_mNewTalkingGroupStartedTimeSelectDatePicker.updateDate(_year,
					_month, _day);

			// update new talking group started time select time picker hour and
			// minute
			_mNewTalkingGroupStartedTimeSelectTimePicker.setCurrentHour(_hour);
			_mNewTalkingGroupStartedTimeSelectTimePicker
					.setCurrentMinute(_minute);
		}

		// show new talking group selected started time
		private void showSelectedStartedTime() {
			// get selected date and time string format
			StringBuilder _selectedDate7timeString = new StringBuilder();

			// format selected date and time string
			_selectedDate7timeString
					.append(_mNewTalkingGroupStartedTimeSelectDatePicker
							.getYear())
					.append(getContext().getResources().getString(
							R.string.datePicker_yearSuffix))
					.append(_mNewTalkingGroupStartedTimeSelectDatePicker
							.getMonth())
					.append(getContext().getResources().getString(
							R.string.datePicker_monthSuffix))
					.append(_mNewTalkingGroupStartedTimeSelectDatePicker
							.getDayOfMonth())
					.append(getContext().getResources().getString(
							R.string.datePicker_daySuffix))
					.append(" ")
					.append(_mNewTalkingGroupStartedTimeSelectTimePicker
							.getCurrentHour())
					.append(":")
					.append(_mNewTalkingGroupStartedTimeSelectTimePicker
							.getCurrentMinute());

			// set new talking group invite note textView text
			((TextView) getContentView()
					.findViewById(
							R.id.tgsts_talkingGroupStartedTimeSelect_inviteNoteTextView))
					.setText(getContext()
							.getResources()
							.getString(
									R.string.talkingGroupStartedTimeSelect_inviteNoteTextView_hint)
							.replaceFirst("\\*\\*\\*",
									_selectedDate7timeString.toString())
							.replace("***", _mNewTalkingGroupId));
		}

		// inner class
		// new talking group started time select cancel button on click
		// listener
		class NewTalkingGroupStartedTimeSelectCancelButtonOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss new talking group started time select popup window
				dismiss();
			}

		}

		// new talking group started time select confirm button on click
		// listener
		class NewTalkingGroupStartedTimeSelectConfirmButtonOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss new talking group started time select popup window
				dismiss();

				// send new talking group http request
				// ??

				// send invite sms
				sendInviteSMS("第一次邀请的。。。");
			}

		}

		// new talking group started time select copy invite note button on
		// click listener
		class NewTalkingGroupStartedTimeSelectCopyInviteNoteButtonOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, "Copy new talking group invite note paste board");

				// copy new talking group invite note paste board
				// ??
			}

		}

		// new talking group started time select date picker on date changed
		// listener
		class NewTalkingGroupStartedTimeSelectDatePickerOnDateChangedListener
				implements OnDateChangedListener {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// update talking group started time
				showSelectedStartedTime();
			}

		}

		// new talking group started time select time picker on time changed
		// listener
		class NewTalkingGroupStartedTimeSelectTimePickerOnTimeChangedListener
				implements OnTimeChangedListener {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// update talking group started time
				showSelectedStartedTime();
			}

		}

	}

}
