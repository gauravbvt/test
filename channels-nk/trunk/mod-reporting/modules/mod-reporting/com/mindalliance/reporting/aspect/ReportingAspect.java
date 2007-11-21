package com.mindalliance.reporting.aspect;

import com.mindalliance.reporting.Reporting;

public class ReportingAspect implements IAspectReporting {
	private Reporting t;

	public ReportingAspect(Reporting t) {
		this.t = t;
	}

	public Reporting getReporting() {
		return t;
	}
}
