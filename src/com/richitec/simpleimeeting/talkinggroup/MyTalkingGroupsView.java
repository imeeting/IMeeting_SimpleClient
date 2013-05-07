package com.richitec.simpleimeeting.talkinggroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.view.SIMBaseView;

public class MyTalkingGroupsView extends SIMBaseView {

	private static final String LOG_TAG = MyTalkingGroupsView.class
			.getCanonicalName();

	// my talking group listView item adapter data keys
	private final String GROUP_STARTEDTIME = "group_startedTime";
	private final String GROUP_ID = "group_id";
	private final String GROUP_STATUS = "group_status";
	private final String GROUP_SELECTED4ITEM = "group_selected_for_item";
	private final String GROUP_SELECTED4DETAIL = "group_selected_for_detail";

	// my talking group started time date format, format unix timeStamp
	private final DateFormat MYTALKINGGROUP_STARTEDTIMEDATEFORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.getDefault());

	// my talking group list needed to refresh
	private boolean _mMyTalkingGroupsNeeded2Refresh = true;

	// my talking groups pager
	private JSONObject _mMyTalkingGroupsPager;

	// my talking groups info array
	private JSONArray _mMyTalkingGroupsInfoArray = new JSONArray();

	// my talking group list view
	private ListView _mMyTalkingGroupListView;

	// selected talking group index
	private Integer _mSelectedTalkingGroupIndex = null;

	// selected talking group attendees phone array
	private List<String> _mSelectedTalkingGroupAttendeesPhoneArray = new ArrayList<String>();

	// my talking group attendee list view
	private ListView _mMyTalkingGroupAttendeeListView;

	@Override
	public int presentViewLayout() {
		// return my talking group list view layout
		return R.layout.my_talkinggroups_view_layout;
	}

	@Override
	public void onCreate() {
		// get my talking group list view
		_mMyTalkingGroupListView = (ListView) findViewById(R.id.mtg_talkingGroupListView);

		// set my talking group listView on item click listener
		_mMyTalkingGroupListView
				.setOnItemClickListener(new MyTalkingGroupListViewOnItemClickListener());

		// get my talking group attendee list view
		_mMyTalkingGroupAttendeeListView = (ListView) findViewById(R.id.mtg_talkingGroupAttendeeListView);

		// bind add contacts to talking group button on click listener
		((Button) findViewById(R.id.mtg_addContacts2talkingGroup_button))
				.setOnClickListener(new AddContacts2talkingGroupButtonOnClickListener());
	}

	@Override
	public void onResume() {
		// check my talking group list needed to refresh flag
		if (true == _mMyTalkingGroupsNeeded2Refresh) {
			Log.d(LOG_TAG, "Refresh my talking group list");

			// reset my talking group list needed to refresh flag
			_mMyTalkingGroupsNeeded2Refresh = false;

			// get my talking group list
			// generate get my talking group list param map
			Map<String, String> _getMyTalkingGroupsParamMap = new HashMap<String, String>();

			// set some params
			_getMyTalkingGroupsParamMap
					.put(getContext()
							.getResources()
							.getString(
									R.string.bg_server_getMyTalkingGroups6scheduleNewTalkingGroup_userName),
							UserManager.getInstance().getUser().getName());

			// post the http request
			HttpUtils.postSignatureRequest(
					getContext().getResources().getString(R.string.server_url)
							+ getContext().getResources().getString(
									R.string.myTalkingGroup_list_url),
					PostRequestFormat.URLENCODED, _getMyTalkingGroupsParamMap,
					null, HttpRequestType.ASYNCHRONOUS,
					new GetMyTalkingGroupListHttpRequestListener());
		}
	}

	// set my talking group list needed to refresh
	public void setMyTalkingGroupsNeeded2Refresh() {
		_mMyTalkingGroupsNeeded2Refresh = true;
	}

	// generate my talking group listView adapter data list
	private List<Map<String, ?>> generateMyTalkingGroupListDataList(
			JSONArray myTalkingGroupsInfoArray) {
		// my talking group list data list
		List<Map<String, ?>> _dataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < myTalkingGroupsInfoArray.length(); i++) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// get group info json object
			JSONObject _groupInfoJsonObject = JSONUtils
					.getJSONObjectFromJSONArray(myTalkingGroupsInfoArray, i);

			// get group started timestamp, time, id and status
			Long _groupStartedTimestamp = /* 1000 * */JSONUtils
					.getLongFromJSONObject(
							_groupInfoJsonObject,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getMyTalkingGroupsReq_resp_startedTimestamp));
			Object _groupStartedTime = getContext().getResources().getString(
					R.string.myTalkingGroup_startedTime_hint)
					+ MYTALKINGGROUP_STARTEDTIMEDATEFORMAT
							.format(_groupStartedTimestamp);
			Object _groupId = getContext().getResources().getString(
					R.string.myTalkingGroup_groupId_hint)
					+ JSONUtils
							.getStringFromJSONObject(
									_groupInfoJsonObject,
									getContext()
											.getResources()
											.getString(
													R.string.bg_server_getMyTalkingGroups6newTalkingGroupIdReq_resp_id));
			Object _groupStatus = JSONUtils
					.getStringFromJSONObject(
							_groupInfoJsonObject,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getMyTalkingGroupsReq_resp_status));

			// check my talking group status and reset my talking group started
			// time, group id and status
			if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_myTalkingGroup_talkingGroupOpened)
					.equalsIgnoreCase((String) _groupStatus)) {
				// dark sea green foreground color span
				ForegroundColorSpan _darkSeaGreenForegroundColorSpan = new ForegroundColorSpan(
						getContext().getResources().getColor(
								R.color.dark_seagreen));

				_groupStartedTime = new SpannableString(
						(String) _groupStartedTime);
				((SpannableString) _groupStartedTime).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupStartedTime).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_groupId = new SpannableString((String) _groupId);
				((SpannableString) _groupId).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupId).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_groupStatus = new SpannableString(getContext().getResources()
						.getString(R.string.myTalkingGroup_groupStatus_hint)
						+ getContext().getResources().getString(
								R.string.myTalkingGroup_groupStatus_opened));
				((SpannableString) _groupStatus).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupStatus).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_myTalkingGroup_talkingGroupSchedule)
					.equalsIgnoreCase((String) _groupStatus)) {
				// milliseconds per second， seconds per day, hour and minute
				final Long MILLISECONDS_PER_SECOND = 1000L;
				final Long SECONDS_PER_DAY = 24 * 60 * 60L;
				final Long SECONDS_PER_HOUR = 60 * 60L;
				final Long SECONDS_PER_MINUTE = 60L;

				// get talking group start remainder time
				Long _remainderTime = (_groupStartedTimestamp - Calendar
						.getInstance().getTimeInMillis())
						/ MILLISECONDS_PER_SECOND;

				// check it and init talking group status string format
				String _talkingGroupStatusString;
				if (0 >= _remainderTime) {
					_talkingGroupStatusString = getContext().getResources()
							.getString(
									R.string.myTalkingGroup_groupStatus_broken);

					Log.e(LOG_TAG, "This is a invalidate talking group");
				} else {
					_talkingGroupStatusString = getContext()
							.getResources()
							.getString(
									R.string.myTalkingGroup_groupStatus_unopened);

					// days
					if (SECONDS_PER_DAY <= _remainderTime) {
						_talkingGroupStatusString = _talkingGroupStatusString
								.replace(
										"***",
										_remainderTime
												/ SECONDS_PER_DAY
												+ getContext()
														.getResources()
														.getString(
																R.string.remainderTime_daySuffix));
					}
					// hours
					else if (SECONDS_PER_HOUR <= _remainderTime) {
						_talkingGroupStatusString = _talkingGroupStatusString
								.replace(
										"***",
										_remainderTime
												/ SECONDS_PER_HOUR
												+ getContext()
														.getResources()
														.getString(
																R.string.remainderTime_hourSuffix));
					}
					// minutes
					else {
						_talkingGroupStatusString = _talkingGroupStatusString
								.replace(
										"***",
										_remainderTime
												/ SECONDS_PER_MINUTE
												+ getContext()
														.getResources()
														.getString(
																R.string.remainderTime_minuteSuffix));
					}
				}

				// update my talking group status
				_groupStatus = getContext().getResources().getString(
						R.string.myTalkingGroup_groupStatus_hint)
						+ _talkingGroupStatusString;
			}

			// set data
			_dataMap.put(GROUP_SELECTED4ITEM, getContext().getResources()
					.getDrawable(R.drawable.mytalkinggroup_normal_bg));
			_dataMap.put(GROUP_STARTEDTIME, _groupStartedTime);
			_dataMap.put(GROUP_ID, _groupId);
			_dataMap.put(GROUP_STATUS, _groupStatus);
			_dataMap.put(GROUP_SELECTED4DETAIL, null);

			// add to data list
			_dataList.add(_dataMap);
		}

		return _dataList;
	}

	// generate my talking group attendee adapter
	private ListAdapter generateMyTalkingGroupAttendeeAdapter(
			String myTalkingGroupStatus,
			JSONArray myTalkingGroupAttendeesInfoArray) {
		// my talking group attendee listView item adapter data keys
		final String ATTENDEE_DISPLAYNAME = "attendee_displayName";
		final String ATTENDEE_STATUS = "attendee_status";

		// clear selected talking group attendees phone array
		_mSelectedTalkingGroupAttendeesPhoneArray.clear();

		// my talking group attendee list data list
		List<Map<String, ?>> _myTalkingGroupAttendeesDataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < myTalkingGroupAttendeesInfoArray.length(); i++) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// define attendee display name
			Object _attendeeDisplayName;

			// get attendee info json object
			JSONObject _attendeeInfoJsonObject = JSONUtils
					.getJSONObjectFromJSONArray(
							myTalkingGroupAttendeesInfoArray, i);

			// get attendee status, nickname and phone
			Object _attendeeStatus = JSONUtils
					.getStringFromJSONObject(
							_attendeeInfoJsonObject,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getTalkingGroupAttendeesReq_resp_status));
			String _attebdeeNickname = JSONUtils
					.getStringFromJSONObject(
							_attendeeInfoJsonObject,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getTalkingGroupAttendeesReq_resp_nickname));
			String _attendeePhone = JSONUtils
					.getStringFromJSONObject(
							_attendeeInfoJsonObject,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getTalkingGroupAttendeesReq_resp_phone));

			// add the attendee phone to selected talking group attendees phone
			// array
			_mSelectedTalkingGroupAttendeesPhoneArray.add(_attendeePhone);

			// check attendee nickname and phone to set attendee display name
			if (null != _attebdeeNickname
					&& !"".equalsIgnoreCase(_attebdeeNickname)) {
				// use nickname if has
				_attendeeDisplayName = _attebdeeNickname;
			} else {
				// get phone ownership in addressbook
				_attendeeDisplayName = _attendeePhone;
			}

			// check talking group status, its attendee status and reset talking
			// group attendee display name
			if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_myTalkingGroup_talkingGroupSchedule)
					.equalsIgnoreCase((String) myTalkingGroupStatus)) {
				_attendeeStatus = null;
			} else if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_talkingGroupAttendee_attendeeIn)
					.equalsIgnoreCase((String) _attendeeStatus)) {
				_attendeeStatus = getContext().getResources().getDrawable(
						android.R.drawable.presence_online);

				// dark sea green foreground color span
				ForegroundColorSpan _darkSeaGreenForegroundColorSpan = new ForegroundColorSpan(
						getContext().getResources().getColor(
								R.color.dark_seagreen));

				_attendeeDisplayName = new SpannableString(
						(String) _attendeeDisplayName);
				((SpannableString) _attendeeDisplayName).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _attendeeDisplayName).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_talkingGroupAttendee_attendeeOut)
					.equalsIgnoreCase((String) _attendeeStatus)) {
				_attendeeStatus = getContext().getResources().getDrawable(
						android.R.drawable.presence_invisible);
			}

			// set data
			_dataMap.put(ATTENDEE_STATUS, _attendeeStatus);
			_dataMap.put(ATTENDEE_DISPLAYNAME, _attendeeDisplayName);

			// add to data list
			_myTalkingGroupAttendeesDataList.add(_dataMap);
		}

		// get my talking group attendee listView adapter
		MyTalkingGroup7MyTalkingGroupAttendeeAdapter _myTalkingGroupAttendeesListViewAdapter = (MyTalkingGroup7MyTalkingGroupAttendeeAdapter) _mMyTalkingGroupAttendeeListView
				.getAdapter();

		return null == _myTalkingGroupAttendeesListViewAdapter ? new MyTalkingGroup7MyTalkingGroupAttendeeAdapter(
				getContext(), _myTalkingGroupAttendeesDataList,
				R.layout.talkinggroup_attendee_layout, new String[] {
						ATTENDEE_STATUS, ATTENDEE_DISPLAYNAME }, new int[] {
						R.id.talkingGroup_attendee_status_imageView,
						R.id.talkingGroup_attendee_displayName_textView })
				: _myTalkingGroupAttendeesListViewAdapter
						.setData(_myTalkingGroupAttendeesDataList);
	}

	// inner class
	// get my talking group list http request listener
	class GetMyTalkingGroupListHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// get http response entity string json data
			JSONObject _respJsonData = JSONUtils.toJSONObject(responseResult
					.getResponseText());

			Log.d(LOG_TAG,
					"Send get my talking group list post http request successful, response json data = "
							+ _respJsonData);

			// get the pager and list from http response json data
			_mMyTalkingGroupsPager = JSONUtils
					.getJSONObjectFromJSONObject(
							_respJsonData,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getMyTalkingGroupsReq_resp_pager));
			_mMyTalkingGroupsInfoArray = JSONUtils
					.getJSONArrayFromJSONObject(
							_respJsonData,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getMyTalkingGroupsReq_resp_list));

			// get loading my talking group relativeLayout, no talking group tip
			// textView and my talking groups linearLayout
			RelativeLayout _loadingMyTalkingGroupRelativeLayout = (RelativeLayout) findViewById(R.id.mtg_loadingMyTalkingGroup_relativeLayout);
			TextView _noTalkingGroupTipTextView = (TextView) findViewById(R.id.mtg_noTalkingGroup_tip_textView);
			LinearLayout _myTalkingGroupsLinearLayout = (LinearLayout) findViewById(R.id.mtg_myTalkingGroup7attendees_linearLayout);

			// check my talking groups info array
			if (0 != _mMyTalkingGroupsInfoArray.length()) {
				// hide loading my talking group relativeLayout
				_loadingMyTalkingGroupRelativeLayout.setVisibility(View.GONE);

				// hide no talking group tip textView and show my talking groups
				_noTalkingGroupTipTextView.setVisibility(View.GONE);
				_myTalkingGroupsLinearLayout.setVisibility(View.VISIBLE);

				// set my talking group listView adapter
				_mMyTalkingGroupListView
						.setAdapter(new MyTalkingGroup7MyTalkingGroupAttendeeAdapter(
								getContext(),
								generateMyTalkingGroupListDataList(_mMyTalkingGroupsInfoArray),
								R.layout.my_talkinggroup_layout,
								new String[] { GROUP_SELECTED4ITEM,
										GROUP_STARTEDTIME, GROUP_ID,
										GROUP_STATUS, GROUP_SELECTED4DETAIL },
								new int[] {
										R.id.my_talkingGroup_parentRelativeLayout,
										R.id.my_talkingGroup_startedTime,
										R.id.my_talkingGroup_groupId,
										R.id.my_talkingGroup_groupStatus,
										R.id.my_talkingGroup_detailInfo_imageView }));
			} else {
				Log.i(LOG_TAG, "There is no talking group with me now");

				// hide loading my talking group relativeLayout
				_loadingMyTalkingGroupRelativeLayout.setVisibility(View.GONE);

				// show no talking group tip textView and hide my talking groups
				_noTalkingGroupTipTextView.setVisibility(View.VISIBLE);
				_myTalkingGroupsLinearLayout.setVisibility(View.GONE);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG,
					"Send get my talking group list post http request failed!");

			// show get my talking group list failed toast
			Toast.makeText(getContext(), R.string.toast_request_exception,
					Toast.LENGTH_LONG).show();

			// hide loading my talking group relativeLayout
			((RelativeLayout) findViewById(R.id.mtg_loadingMyTalkingGroup_relativeLayout))
					.setVisibility(View.GONE);

			// hide no talking group tip textView and show my talking groups
			((TextView) findViewById(R.id.mtg_noTalkingGroup_tip_textView))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.mtg_myTalkingGroup7attendees_linearLayout))
					.setVisibility(View.VISIBLE);
		}

	}

	// my talking group and my talking group attendee adapter
	class MyTalkingGroup7MyTalkingGroupAttendeeAdapter extends CTListAdapter {

		public MyTalkingGroup7MyTalkingGroupAttendeeAdapter(Context context,
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
			// my talking group detail info or my talking group attendee status
			// imageView
			else if (view instanceof ImageView
					|| view instanceof RelativeLayout) {
				try {
					// define item data drawable and convert item data to
					// drawable
					Drawable _itemDataDrawable = (Drawable) _itemData;

					// check, set imageView image and set its visibility
					if (null != _itemDataDrawable && view instanceof ImageView) {
						((ImageView) view).setImageDrawable(_itemDataDrawable);

						if (View.VISIBLE != view.getVisibility()) {
							view.setVisibility(View.VISIBLE);
						}
					} else if (null != _itemDataDrawable
							&& view instanceof RelativeLayout) {
						view.setBackgroundDrawable(_itemDataDrawable);
					} else {
						view.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to drawable error, item data = "
									+ _itemData);
				}
			}
		}

	}

	// my talking group listView on item click listener
	class MyTalkingGroupListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// define and get last pressed talking group index
			Integer _lastPressedTalkingGroupIndex = _mSelectedTalkingGroupIndex;

			// mark selected row
			_mSelectedTalkingGroupIndex = position;

			// get selected talking group info
			JSONObject _selectedTalkingGroupInfo = JSONUtils
					.getJSONObjectFromJSONArray(_mMyTalkingGroupsInfoArray,
							(int) id);

			// update selected talking group item
			if (_lastPressedTalkingGroupIndex != _mSelectedTalkingGroupIndex) {
				// get my talking group adapter
				MyTalkingGroup7MyTalkingGroupAttendeeAdapter _myTalkingGroupAdapter = (MyTalkingGroup7MyTalkingGroupAttendeeAdapter) _mMyTalkingGroupListView
						.getAdapter();

				// check last pressed talking group index
				if (null != _lastPressedTalkingGroupIndex) {
					// get my talking group adapter data map for last selected
					@SuppressWarnings("unchecked")
					Map<String, Object> _myTalkingGroupAdapterDataMap = (Map<String, Object>) _myTalkingGroupAdapter
							.getItem(_lastPressedTalkingGroupIndex);

					// recover last selected talking group item background and
					// hide detail info
					// update adapter data map for last selected
					_myTalkingGroupAdapterDataMap.put(
							GROUP_SELECTED4ITEM,
							getContext().getResources().getDrawable(
									R.drawable.mytalkinggroup_normal_bg));
					_myTalkingGroupAdapterDataMap.put(GROUP_SELECTED4DETAIL,
							null);
				}

				// get my talking group adapter data map for current selected
				@SuppressWarnings("unchecked")
				Map<String, Object> _myTalkingGroupAdapterDataMap = (Map<String, Object>) _myTalkingGroupAdapter
						.getItem((int) id);

				// update selected talking group item background and show detail
				// info
				// update adapter data map for current selected
				_myTalkingGroupAdapterDataMap.put(
						GROUP_SELECTED4ITEM,
						getContext().getResources().getDrawable(
								R.drawable.img_mytalkinggroup_touchdown_bg));
				_myTalkingGroupAdapterDataMap.put(
						GROUP_SELECTED4DETAIL,
						getContext().getResources().getDrawable(
								R.drawable.img_mytalkinggroup_detailinfo));

				// notify adapter changed
				_myTalkingGroupAdapter.notifyDataSetChanged();
			}

			// get the selected my talking group attendee list
			// generate get the selected my talking group attendee list
			// param map
			Map<String, String> _getTalkingGroupAttendeesParamMap = new HashMap<String, String>();

			// set some params
			_getTalkingGroupAttendeesParamMap
					.put(getContext()
							.getResources()
							.getString(
									R.string.bg_server_getTalkingGroupAttendees6scheduleNewTalkingGroup6inviteNewAddedContacts2TalkingGroup_confId),
							JSONUtils
									.getStringFromJSONObject(
											_selectedTalkingGroupInfo,
											getContext()
													.getResources()
													.getString(
															R.string.bg_server_getMyTalkingGroups6newTalkingGroupIdReq_resp_id)));

			// post the http request
			HttpUtils.postSignatureRequest(
					getContext().getResources().getString(R.string.server_url)
							+ getContext().getResources().getString(
									R.string.get_attendeeList_url),
					PostRequestFormat.URLENCODED,
					_getTalkingGroupAttendeesParamMap, null,
					HttpRequestType.ASYNCHRONOUS,
					new GetTalkingGroupAttendeeListHttpRequestListener());
		}

	}

	// get the selected my talking group attendee list http request listener
	class GetTalkingGroupAttendeeListHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// get http response entity string json data
			JSONArray _respJsonData = JSONUtils.toJSONArray(responseResult
					.getResponseText());

			Log.d(LOG_TAG,
					"Send get talking group attendee list post http request successful, response json data = "
							+ _respJsonData);

			// get selected talking group info
			JSONObject _selectedTalkingGroupInfo = JSONUtils
					.getJSONObjectFromJSONArray(_mMyTalkingGroupsInfoArray,
							_mSelectedTalkingGroupIndex);

			// update selected talking group attendee listView adapter
			_mMyTalkingGroupAttendeeListView
					.setAdapter(generateMyTalkingGroupAttendeeAdapter(
							JSONUtils
									.getStringFromJSONObject(
											_selectedTalkingGroupInfo,
											getContext()
													.getResources()
													.getString(
															R.string.bg_server_getMyTalkingGroupsReq_resp_status)),
							_respJsonData));

			// get my talking group attendee listView relativeLayout
			RelativeLayout _myTalkingGroupAttendeeListViewRelativeLayout = (RelativeLayout) findViewById(R.id.mtg_talkingGroupAttendeeList_relativeLayout);

			// check my talking group attendee listView relativeLayout
			// visibility and show it
			if (View.VISIBLE != _myTalkingGroupAttendeeListViewRelativeLayout
					.getVisibility()) {
				_myTalkingGroupAttendeeListViewRelativeLayout
						.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			Log.e(LOG_TAG,
					"Send get talking group attendee list post http request failed");

			// show get selected talking group attendee list failed toast
			Toast.makeText(getContext(), R.string.toast_request_exception,
					Toast.LENGTH_LONG).show();

			// get my talking group attendee listView relativeLayout
			RelativeLayout _myTalkingGroupAttendeeListViewRelativeLayout = (RelativeLayout) findViewById(R.id.mtg_talkingGroupAttendeeList_relativeLayout);

			// check my talking group attendee listView relativeLayout
			// visibility and hide it
			if (View.VISIBLE == _myTalkingGroupAttendeeListViewRelativeLayout
					.getVisibility()) {
				_myTalkingGroupAttendeeListViewRelativeLayout
						.setVisibility(View.GONE);
			}
		}

	}

	// add contacts to talking group button on click listener
	class AddContacts2talkingGroupButtonOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// define invite new contact to talking group note and conference
			// id, invite note, new added contacts phone array
			String _confId;
			String _inviteNewContact2TalkingGroupNote;
			List<String> _confId7inviteNote7newAddedContactsPhoneList = new ArrayList<String>();

			// get selected talking group info
			JSONObject _selectedTalkingGroupInfo = JSONUtils
					.getJSONObjectFromJSONArray(_mMyTalkingGroupsInfoArray,
							_mSelectedTalkingGroupIndex);

			// init conference id and add to conference id, invite note, new
			// added contacts phone array as param
			_confId = JSONUtils
					.getStringFromJSONObject(
							_selectedTalkingGroupInfo,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_getMyTalkingGroups6newTalkingGroupIdReq_resp_id));
			_confId7inviteNote7newAddedContactsPhoneList.add(_confId);

			// init invite new contact to talking group note and add to
			// conference id, invite note, new added contacts phone array as
			// param
			_inviteNewContact2TalkingGroupNote = getContext()
					.getResources()
					.getString(R.string.talkingGroupAttendee_inviteNoteText)
					.replaceFirst(
							"\\*\\*\\*",
							MYTALKINGGROUP_STARTEDTIMEDATEFORMAT.format(/*
																		 * 1000
																		 * *
																		 */JSONUtils
									.getLongFromJSONObject(
											_selectedTalkingGroupInfo,
											getContext()
													.getResources()
													.getString(
															R.string.bg_server_getMyTalkingGroupsReq_resp_startedTimestamp))))
					.replace("***", _confId);
			_confId7inviteNote7newAddedContactsPhoneList
					.add(_inviteNewContact2TalkingGroupNote);

			// add the selected talking group attendees phone array to
			// conference id, invite note, new added contacts phone array as
			// param
			_confId7inviteNote7newAddedContactsPhoneList
					.addAll(_mSelectedTalkingGroupAttendeesPhoneArray);

			// switch to contacts select view
			((SimpleIMeetingMainActivity) getContext())
					.switch2contactsSelectView(_confId7inviteNote7newAddedContactsPhoneList);
		}

	}

}
