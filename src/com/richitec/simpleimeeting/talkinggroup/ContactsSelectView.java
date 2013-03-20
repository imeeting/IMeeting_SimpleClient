package com.richitec.simpleimeeting.talkinggroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.view.SIMBaseView;

public class ContactsSelectView extends SIMBaseView {

	public ContactsSelectView() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public View presentView() {
		// get layout inflater
		LayoutInflater _layoutInflater = (LayoutInflater) CTApplication
				.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return _layoutInflater.inflate(R.layout.contacts_select_view_layout,
				null);
	}

}
