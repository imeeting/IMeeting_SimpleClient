package com.richitec.simpleimeeting.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class SIMViewFactory {

	private static final String LOG_TAG = SIMViewFactory.class
			.getCanonicalName();

	// create simple imeeting view
	public static SIMBaseView createSIMView(Context context,
			Class<? extends SIMBaseView> simViewClass) {
		SIMBaseView _simBaseView = null;

		try {
			// check context if it is or not activity context
			if (!(context instanceof Activity)) {
				throw new NotActivityContextException();
			}

			// instantiate simple imeeting view instance
			_simBaseView = simViewClass.newInstance();

			// set content context
			_simBaseView.setContext(context);

			// init the present view
			_simBaseView.getPresentView();

			// init the present view's sub components
			_simBaseView.onCreate();
		} catch (NotActivityContextException e) {
			Log.e(LOG_TAG,
					"Create simple imeeting view error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
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

		return _simBaseView;
	}

	// create simple imeeting view for present
	public static View createSIMView4Present(Context context,
			Class<? extends SIMBaseView> simViewClass) {
		return createSIMView(context, simViewClass).getPresentView();
	}

	// inner class
	// not activity context exception
	static class NotActivityContextException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2467239117577148297L;

		public NotActivityContextException() {
			// new not activity context exception
			super("There is no activity context");
		}

	}

}
