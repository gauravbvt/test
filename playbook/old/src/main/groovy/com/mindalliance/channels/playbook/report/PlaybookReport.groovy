package com.mindalliance.channels.playbook.report

import groovy.xml.MarkupBuilder
import com.mindalliance.channels.playbook.ifm.Tab

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2008
 * Time: 2:55:37 PM
 */
class PlaybookReport extends Report {

    PlaybookReport(Tab tab) {
        super(tab)
    }

    public void buildBody(MarkupBuilder xml) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getTitle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void buildIndex(MarkupBuilder xml) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}