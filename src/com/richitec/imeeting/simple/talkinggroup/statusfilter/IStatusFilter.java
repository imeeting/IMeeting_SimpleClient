package com.richitec.imeeting.simple.talkinggroup.statusfilter;

import java.util.Map;

public interface IStatusFilter {
	public Map<String, String> filterStatus(Map<String, String> newAttendee,
			Map<String, String> oldAttendee);
}
