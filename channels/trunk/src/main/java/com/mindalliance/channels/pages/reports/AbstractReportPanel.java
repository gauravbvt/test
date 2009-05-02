package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.QueryService;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Abstract report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 1, 2009
 * Time: 7:39:45 PM
 */
public abstract class AbstractReportPanel extends Panel {

    protected AbstractReportPanel( String s ) {
        super( s );
    }

    protected AbstractReportPanel( String s, IModel<?> iModel ) {
        super( s, iModel );
    }

    /**
     * Get query service.
     * @return a query service
     */
    protected QueryService getQueryService() {
        return getChannels().getQueryService();
    }

    /**
     * Get analyst.
     * @return an analyst
     */
    protected Analyst getAnalyst() {
        return getChannels().getAnalyst();
    }

    private Channels getChannels() {
        return Channels.instance();
    }

}
