package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 2:43 PM
 */
public class SelectedAssignmentsPanel extends AbstractUpdatablePanel {

    private final AssignmentsSelector selector;

    public SelectedAssignmentsPanel( String id, AssignmentsSelector selector ) {
        super( id );
        this.selector = selector;
        init();
    }

    private void init() {
        add( new AssignmentsReportPanel( "assignments-report", selector, new DefaultReportHelper( selector ) ) );
    }
}
