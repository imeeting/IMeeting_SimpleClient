package com.richitec.imeeting.simple.customcomponent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.richitec.imeeting.simple.R;

public class SimpleIMeetingDate7TimePicker extends LinearLayout {

	// calendar
	private Calendar _mCalendar;

	// date and time picker
	private DatePicker _mDatePicker;
	private TimePicker _mTimePicker;

	// date picker month and week day wheel
	WheelView _mDatePickerMonth7WeekDayWheel;

	// time picker hour and minute wheel
	WheelView _mTimePickerHourWheel;
	WheelView _mTimePickerMinuteWheel;

	public SimpleIMeetingDate7TimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);

		// set orientation and background
		setOrientation(LinearLayout.VERTICAL);

		// get simple imeeting date and time picker parent view
		ViewGroup _simDate7TimePickerParent = (ViewGroup) ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.sim_date7timepicker_layout, null);

		// get simple imeeting date and time picker
		LinearLayout _simpleImeetingDate7TimePicker = (LinearLayout) _simDate7TimePickerParent
				.findViewById(R.id.simd7tp_date7timePicker_linearLayout);

		// remove simple date and time picker from its parent view
		_simDate7TimePickerParent.removeView(_simpleImeetingDate7TimePicker);

		// add simple imeeting date and time picker
		addView(_simpleImeetingDate7TimePicker);

		// get current calendar
		_mCalendar = Calendar.getInstance();

		// init date and time picker wheel
		// date picker
		// following days
		final int FOLLOWINGDAYS = (0 == _mCalendar.get(Calendar.YEAR) % 4 && 0 != _mCalendar
				.get(Calendar.YEAR) % 100) ? 365 : 364;

		// get date picker month and week day wheel
		_mDatePickerMonth7WeekDayWheel = (WheelView) findViewById(R.id.simd7tp_datePicker_month7weekDayWhel);

		// set date picker month and week day wheel view adapter
		_mDatePickerMonth7WeekDayWheel
				.setViewAdapter(new DatePickerMonth7WeekDayTextWheelAdapter(
						context, FOLLOWINGDAYS));

		// add date picker month and week day wheel changed listener
		_mDatePickerMonth7WeekDayWheel
				.addChangingListener(new DatePickerMonth7WeekDayWheelChangedListener());

		// update date picker date for current
		updateDate(_mCalendar.get(Calendar.YEAR),
				_mCalendar.get(Calendar.MONTH),
				_mCalendar.get(Calendar.DAY_OF_MONTH));

		// time picker
		// hour and minute min and max value
		final int HOUR7MINUTE_MIN = 0;
		final int HOURS_PER_DAY_24FORMAT_MAX = 23;
		final int MINUTES_PER_HOUR_MAX = 59;

		// get time picker hour wheel
		_mTimePickerHourWheel = (WheelView) findViewById(R.id.simd7tp_timePicker_hourWheel);

		// set time picker hour wheel view adapter, current hour and cyclic
		_mTimePickerHourWheel
				.setViewAdapter(new TimePickerHour7MinuteNumericWheelAdapter(
						context, HOUR7MINUTE_MIN, HOURS_PER_DAY_24FORMAT_MAX));
		_mTimePickerHourWheel.setCurrentItem(_mCalendar
				.get(Calendar.HOUR_OF_DAY));
		_mTimePickerHourWheel.setCyclic(true);

		// add time picker hour wheel changed listener
		_mTimePickerHourWheel
				.addChangingListener(new TimePickerHourWheelChangedListener());

		// get time picker minute wheel
		_mTimePickerMinuteWheel = (WheelView) findViewById(R.id.simd7tp_timePicker_minuteWheel);

		// set time picker minute wheel view adapter, current minute and cyclic
		_mTimePickerMinuteWheel
				.setViewAdapter(new TimePickerHour7MinuteNumericWheelAdapter(
						context, HOUR7MINUTE_MIN, MINUTES_PER_HOUR_MAX, "%02d"));
		_mTimePickerMinuteWheel.setCurrentItem(_mCalendar.get(Calendar.MINUTE));
		_mTimePickerMinuteWheel.setCyclic(true);

		// add time picker minute wheel changed listener
		_mTimePickerMinuteWheel
				.addChangingListener(new TimePickerMinuteWheelChangedListener());

		// set time picker current hour and minute
		setCurrentHour(_mCalendar.get(Calendar.HOUR_OF_DAY));
		setCurrentMinute(_mCalendar.get(Calendar.MINUTE));
	}

	public SimpleIMeetingDate7TimePicker(Context context) {
		this(context, null);
	}

	public void setCalendar(Calendar calendar) {
		// update calendar
		_mCalendar = calendar;

		// update date picker month and week day wheel current item and date
		_mDatePickerMonth7WeekDayWheel.setCurrentItem(0);
		updateDate(_mCalendar.get(Calendar.YEAR),
				_mCalendar.get(Calendar.MONTH),
				_mCalendar.get(Calendar.DAY_OF_MONTH));

		// update time picker hour wheel current item and current hour
		_mTimePickerHourWheel.setCurrentItem(_mCalendar
				.get(Calendar.HOUR_OF_DAY));
		setCurrentHour(_mCalendar.get(Calendar.HOUR_OF_DAY));

		// update time picker minute wheel current item and current minute
		_mTimePickerMinuteWheel.setCurrentItem(_mCalendar.get(Calendar.MINUTE));
		setCurrentMinute(_mCalendar.get(Calendar.MINUTE));
	}

	// get date picker
	public DatePicker getDatePicker() {
		// check date picker
		if (null == _mDatePicker) {
			// create new date picker
			_mDatePicker = new DatePicker(getContext());
		}

		return _mDatePicker;
	}

	// get time picker
	public TimePicker getTimePicker() {
		// check time picker
		if (null == _mTimePicker) {
			// create new time picker
			_mTimePicker = new TimePicker(getContext());

			// using 24 hour format
			_mTimePicker.setIs24HourView(Boolean.valueOf(true));
		}

		return _mTimePicker;
	}

	// get day of month of date picker
	public int getDayOfMonth() {
		return getDatePicker().getDayOfMonth();
	}

	// get month of date picker
	public int getMonth() {
		return getDatePicker().getMonth();
	}

	// get year of date picker
	public int getYear() {
		return getDatePicker().getYear();
	}

	// update date of date picker
	public void updateDate(int year, int month, int dayOfMonth) {
		getDatePicker().updateDate(year, month, dayOfMonth);
	}

	// set date picker on date changed listener
	public void setOnDateChangedListener(
			OnDateChangedListener onDateChangedListener) {
		getDatePicker().init(_mDatePicker.getYear(), _mDatePicker.getMonth(),
				_mDatePicker.getDayOfMonth(), onDateChangedListener);
	}

	// get current hour of time picker
	public int getCurrentHour() {
		return getTimePicker().getCurrentHour();
	}

	// set current hour of time picker
	public void setCurrentHour(int currentHour) {
		getTimePicker().setCurrentHour(currentHour);
	}

	// get current minute of time picker
	public int getCurrentMinute() {
		return getTimePicker().getCurrentMinute();
	}

	// set current minute of time picker
	public void setCurrentMinute(int currentMinute) {
		getTimePicker().setCurrentMinute(currentMinute);
	}

	// get hour is 24 format of time picker
	public boolean is24HourView() {
		return getTimePicker().is24HourView();
	}

	// set hour is 24 format of time picker
	public void setIs24HourView(boolean is24HourView) {
		getTimePicker().setIs24HourView(is24HourView);
	}

	// set time picker on time changed listener
	public void setOnTimeChangedListener(
			OnTimeChangedListener onTimeChangedListener) {
		getTimePicker().setOnTimeChangedListener(onTimeChangedListener);
	}

	// inner class
	// date picker month and week day text wheel adapter
	class DatePickerMonth7WeekDayTextWheelAdapter extends
			AbstractWheelTextAdapter {

		// today text
		private final String TODAY = "今天";

		// following days
		private int _mFollowingDays;

		public DatePickerMonth7WeekDayTextWheelAdapter(Context context,
				int followingDays) {
			super(context, R.layout.sim_date7timepicker_month7weekday_layout,
					NO_RESOURCE);

			// save following days
			_mFollowingDays = followingDays;
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			// get item view
			View _itemView = super.getItem(index, convertView, parent);

			// generate new calendar of following day
			Calendar _followingDayCalendar = (Calendar) _mCalendar.clone();
			_followingDayCalendar.roll(Calendar.DAY_OF_YEAR, index);

			// get month day textView
			TextView _dayOfMonth = (TextView) _itemView
					.findViewById(R.id.simd7tp_month7weekDay_monthDayTextView);

			// set month day textView text
			if (0 == index) {
				_dayOfMonth.setText("");
			} else {
				_dayOfMonth.setText(new SimpleDateFormat("M月d日", Locale
						.getDefault()).format(_followingDayCalendar.getTime()));
			}

			// get week day textView
			TextView _dayOfWeek = (TextView) _itemView
					.findViewById(R.id.simd7tp_month7weekDay_weekDayTextView);

			// set week day textView text and text color
			if (0 == index) {
				_dayOfWeek.setText(TODAY);
				_dayOfWeek.setTextColor(getResources().getColor(
						R.color.deepSky_blue));
			} else {
				_dayOfWeek.setText(new SimpleDateFormat("EEE", Locale
						.getDefault()).format(_followingDayCalendar.getTime()));
				_dayOfWeek.setTextColor(Color.GRAY);
			}

			return _itemView;
		}

		@Override
		public int getItemsCount() {
			return _mFollowingDays + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			// not use text
			return "";
		}

	}

	// time picker hour and minute numeric wheel adapter
	class TimePickerHour7MinuteNumericWheelAdapter extends NumericWheelAdapter {

		public TimePickerHour7MinuteNumericWheelAdapter(Context context,
				int minValue, int maxValue) {
			super(context, minValue, maxValue);

			// set time picker hour and minute numeric wheel adapter item
			setTimePickerHour7MinuteNumericWheelAdapterItem();
		}

		public TimePickerHour7MinuteNumericWheelAdapter(Context context,
				int minValue, int maxValue, String format) {
			super(context, minValue, maxValue, format);

			// set time picker hour and minute numeric wheel adapter item
			setTimePickerHour7MinuteNumericWheelAdapterItem();
		}

		// set time picker hour and minute numeric wheel adapter item
		private void setTimePickerHour7MinuteNumericWheelAdapterItem() {
			setItemResource(R.layout.sim_date7timepicker_wheel_textitem);
			setItemTextResource(R.id.simd7tp_wheelTextView);
		}

	}

	// date picker month and week day wheel changed listener
	class DatePickerMonth7WeekDayWheelChangedListener implements
			OnWheelChangedListener {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// generate new calendar of selected day
			Calendar _selectedDayCalendar = (Calendar) _mCalendar.clone();
			_selectedDayCalendar.roll(Calendar.DAY_OF_YEAR, newValue);

			// update date picker date
			updateDate(_selectedDayCalendar.get(Calendar.YEAR),
					_selectedDayCalendar.get(Calendar.MONTH),
					_selectedDayCalendar.get(Calendar.DAY_OF_MONTH));
		}

	}

	// time picker hour wheel changed listener
	class TimePickerHourWheelChangedListener implements OnWheelChangedListener {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// set date picker new selected hour
			setCurrentHour(newValue);
		}

	}

	// time picker minute wheel changed listener
	class TimePickerMinuteWheelChangedListener implements
			OnWheelChangedListener {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// set date picker new selected minute
			setCurrentMinute(newValue);
		}

	}

}
