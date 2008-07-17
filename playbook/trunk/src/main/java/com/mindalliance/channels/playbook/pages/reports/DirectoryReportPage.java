package com.mindalliance.channels.playbook.pages.reports;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.report.ResourceDirectory;
import com.mindalliance.channels.playbook.report.Report;
import org.apache.wicket.PageParameters;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 17, 2008
 * Time: 7:26:10 AM
 */
public class DirectoryReportPage extends ReportPage {

    public DirectoryReportPage(PageParameters parms) {
        super(parms);
    }

    Report makeReport(Tab tab) {
        return new ResourceDirectory(tab);
    }

    protected void load(Tab tab) {
        super.load(tab);
    }
}
