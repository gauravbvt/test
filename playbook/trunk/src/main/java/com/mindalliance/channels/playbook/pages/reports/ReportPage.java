package com.mindalliance.channels.playbook.pages.reports;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ref.impl.RefImpl;
import com.mindalliance.channels.playbook.report.Report;

/**
 * ...
 */


abstract public class ReportPage extends WebPage {

    public static final String REPORT_TAB_PARAM = "tabId";

    protected Label reportElement;

    public ReportPage(PageParameters parms) {
        super(parms);
        String tabId = parms.getString(REPORT_TAB_PARAM);
        Tab tab = (Tab)new RefImpl(tabId).deref();
        load(tab);
    }

    protected void configureResponse() {
        super.configureResponse();
        WebResponse response = getWebRequestCycle().getWebResponse();
        response.setContentType("application/xhtml+xml");
    }

    protected void load(Tab tab) {
        Report report = makeReport(tab);
        String reportContent = report.build();
        reportElement = new Label("report", new Model(reportContent));
        add(reportElement);
    }

    abstract Report makeReport(Tab tab);
}
