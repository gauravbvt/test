package com.mindalliance.channels.pages;

import org.apache.wicket.markup.html.WebPage;

import javax.servlet.http.HttpServletResponse;

/**
 * A custom error page, nothing fancy...
 */
public class ErrorPage extends WebPage {

    public ErrorPage() {
    }

    @Override
    protected void configureResponse() {
        super.configureResponse();
        getWebRequestCycle().getWebResponse().getHttpServletResponse().setStatus(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

}
