package com.mindalliance.channels.playbook.pages.reports;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.transformer.XsltTransformerBehavior;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ref.impl.RefImpl;
import com.mindalliance.channels.playbook.report.Report;
import com.mindalliance.channels.playbook.support.RefUtils;

/**
 * ...
 */


abstract public class ReportPage extends WebPage {

    public static final String REPORT_TAB_PARAM = "tabId";
    public static final String REPORT_MIMETYPE_PARAM = "mimeType";

    protected Label reportElement;

    public ReportPage(PageParameters parms) {
        super(parms);
        String tabId = parms.getString(REPORT_TAB_PARAM);
        Tab tab = (Tab)new RefImpl(tabId).deref();
        String mimeType = parms.getString(REPORT_MIMETYPE_PARAM);
        load(tab, mimeType);
    }

    protected void configureResponse() {
        super.configureResponse();
        WebResponse response = getWebRequestCycle().getWebResponse();
        response.setContentType("application/xhtml+xml");
    }

    protected void load(Tab tab, String mimeType) {
        Report report = makeReport(tab);
        String reportXml = report.build();
        reportElement = new Label(getComponentId(mimeType), new Model(reportXml));
        reportElement.setEscapeModelStrings(false);
        reportElement.add(new XsltTransformerBehavior());
        add(reportElement);
    }

    private String getComponentId(String mimeType) {
        String prefix;
        if (mimeType.endsWith("xml")) prefix = "xml";
        else if (mimeType.endsWith("xhtml") || mimeType.endsWith("html")) prefix = "xhtml";
        else throw new RuntimeException(mimeType + " not supported");
        return prefix + "_report";
    }

    abstract Report makeReport(Tab tab);
}
