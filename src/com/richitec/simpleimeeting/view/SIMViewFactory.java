package com.richitec.simpleimeeting.view;

import android.util.Log;
import android.view.View;

public class SIMViewFactory {

	private static final String LOG_TAG = SIMViewFactory.class
			.getCanonicalName();

	// create simple imeeting view
	public static View createSIMView(Class<? extends SIMBaseView> simViewClass) {
		View _presentView = null;

		try {
			// instantiate simple imeeting view instance and get the present
			// view
			_presentView = simViewClass.newInstance().presentView();
		} catch (InstantiationException e) {
			Log.e(LOG_TAG,
					"Instantiate "
							+ simViewClass
							+ " simple imeeting view error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e(LOG_TAG,
					"Can't instantiate a simple imeeting view, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		}

		return _presentView;
	}

}
