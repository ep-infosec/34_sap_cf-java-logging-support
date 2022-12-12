package com.sap.hcp.cf.logging.common.converter;

import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public final class CustomFieldMatchers {

	private CustomFieldMatchers() {
	}

	public static Matcher<Map<? extends String, ? extends Object>> hasCustomField(String key, String value,
			int index) {
		return Matchers.both(Matchers.<String, Object>hasEntry("k", key))
				.and(Matchers.<String, Object>hasEntry("v", value)).and(Matchers.<String, Object>hasEntry("i", index));
	}
}
