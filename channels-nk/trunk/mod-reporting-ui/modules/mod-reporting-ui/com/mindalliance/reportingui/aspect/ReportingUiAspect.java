package com.mindalliance.reportingui.aspect;

import com.mindalliance.reportingui.ReportingUi;

public class ReportingUiAspect implements IAspectReportingUi {
	private ReportingUi t;

	public ReportingUiAspect(ReportingUi t) {
		this.t = t;
	}

	public ReportingUi getReportingUi() {
		return t;
	}
}
