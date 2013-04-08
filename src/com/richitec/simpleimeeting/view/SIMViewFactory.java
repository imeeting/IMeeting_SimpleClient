package com.richitec.simpleimeeting.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class SIMViewFactory {

	private static final String LOG_TAG = SIMViewFactory.class
			.getCanonicalName();

	// create simple imeeting view for present
	public static View createSIMView4Present(Context context,
			Class<? extends SIMBaseView> simViewClass) {
		View _presentView = null;

		try {
			// check context if it is or not activity context
			if (!(context instanceof Activity)) {
				throw new NotActivityContextException();
			}

			// instantiate simple imeeting view instance
			SIMBaseView _simBaseView = simViewClass.newInstance();

			// set content context
			_simBaseView.setContext(context);

			// get the present view as the return result
			_presentView = _simBaseView.getPresentView();

			// init the present view's sub components
			_simBaseView.initSubComponents();
		} catch (NotActivityContextException e) {
			Log.e(LOG_TAG,
					"Create simple imeeting view for present error, exception message = "
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

		return _presentView;
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
