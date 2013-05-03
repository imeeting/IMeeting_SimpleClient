package com.richitec.simpleimeeting.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public abstract class SIMBaseView {

	private static final String LOG_TAG = SIMBaseView.class.getCanonicalName();

	// content context
	private Context _mContext;

	// present view
	private View _mView;

	public Context getContext() {
		return _mContext;
	}

	public void setContext(Context context) {
		_mContext = context;
	}

	// simple imeeting base view present view resource id
	public abstract int presentViewLayout();

	// initialize present view's components and set its attributes
	public abstract void onCreate();

	// not needed to call simple imeeting base view onStart method
	public void onStart() {
		Log.w(LOG_TAG, "Simple iMeeting base view onStart not implement");
	}

	// refresh present view's data again, not needed to call simple imeeting
	// base view onResume method
	public void onResume() {
		Log.w(LOG_TAG, "Simple iMeeting base view onResume not implement");
	}

	// not needed to call simple imeeting base view onRestart method
	public void onRestart() {
		Log.w(LOG_TAG, "Simple iMeeting base view onRestart not implement");
	}

	// not needed to call simple imeeting base view onPause method
	public void onPause() {
		Log.w(LOG_TAG, "Simple iMeeting base view onPause not implement");
	}

	// not needed to call simple imeeting base view onStop method
	public void onStop() {
		Log.w(LOG_TAG, "Simple iMeeting base view onStop not implement");
	}

	// not needed to call simple imeeting base view onDestroy method
	public void onDestroy() {
		Log.w(LOG_TAG, "Simple iMeeting base view onDestroy not implement");
	}

	// get simple imeeting base view present view
	public View getPresentView() {
		View _presentView;

		// check present view
		if (null == _mView) {
			// get layout inflater
			LayoutInflater _layoutInflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// inflater present view, save it and return
			_presentView = _mView = _layoutInflater.inflate(
					presentViewLayout(), null);
		} else {
			// return immediately
			_presentView = _mView;
		}

		return _presentView;
	}

	// find view by id
	public View findViewById(int id) {
		View _view;

		// get parent view
		View _presentView = getPresentView();

		// check present view
		if (null == _presentView) {
			_view = null;

			Log.e(LOG_TAG, "Simple imeeting base view present view is null.");
		} else {
			_view = _presentView.findViewById(id);
		}

		return _view;
	}

}
