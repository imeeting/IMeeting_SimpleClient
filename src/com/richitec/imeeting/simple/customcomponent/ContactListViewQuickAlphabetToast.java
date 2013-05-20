package com.richitec.imeeting.simple.customcomponent;

import android.content.Context;
import android.view.Gravity;

import com.richitec.commontoolkit.customcomponent.CTToast;
import com.richitec.imeeting.simple.R;

//Chinese Telephone contact list quick alphabet toast
public class ContactListViewQuickAlphabetToast extends CTToast {

	public ContactListViewQuickAlphabetToast(Context context) {
		super(context, R.layout.contactlist_quickalphabet_toast_content_layout);

		// set text, duration and gravity
		setText("");
		setDuration(LENGTH_TRANSIENT);
		setGravity(Gravity.CENTER, 0, 0);
	}

}