package com.mindalliance.matcher.aspect;

import com.mindalliance.matcher.Matcher;
import com.ten60.netkernel.urii.IURAspect;

public interface IAspectMatcher extends IURAspect {
	public Matcher getMatcher();
}
