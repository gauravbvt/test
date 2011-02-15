package com.mindalliance.channels.pages.reports;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 2:43 PM
 */
public class SelectedAssignmentsPanel extends Panel {

    private final AssignmentsSelector selector;

    public SelectedAssignmentsPanel( String id, AssignmentsSelector selector ) {
        super( id );
        this.selector = selector;
        init();
    }

    private void init() {
        // todo
    }
}
