package com.richitec.simpleimeeting.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public abstract class SIMBaseView {

	private static final String LOG_TAG = SIMBaseView.class.getCanonicalName();

	// content context
	private Context _mContext;

	public Context getContext() {
		return _mContext;
	}

	public void setContext(Context context) {
		_mContext = context;
	}

	// simple imeeting base view present view resource id
	public abstract int presentViewLayout();

	// initialize present view's components and set its attributes
	public abstract void initSubComponents();

	// get simple imeeting base view present view
	public View getPresentView() {
		// get layout inflater
		LayoutInflater _layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// inflater present view and return
		return _layoutInflater.inflate(presentViewLayout(), null);
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
