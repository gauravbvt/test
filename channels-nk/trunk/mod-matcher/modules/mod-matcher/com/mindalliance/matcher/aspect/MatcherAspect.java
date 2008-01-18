package com.mindalliance.matcher.aspect;

import com.mindalliance.matcher.Matcher;

public class MatcherAspect implements IAspectMatcher {
	private Matcher t;

	public MatcherAspect(Matcher t) {
		this.t = t;
	}

	public Matcher getMatcher() {
		return t;
	}
}
