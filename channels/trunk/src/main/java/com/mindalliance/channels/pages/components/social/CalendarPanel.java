package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.pages.Updatable;

/**
 * Calendar panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/14/11
 * Time: 9:31 AM
 */
public class CalendarPanel extends AbstractSocialListPanel {

    private final Updatable updatable;

    public CalendarPanel( String id, Updatable updatable, boolean collapsible ) {
        super(id, collapsible);
        this.updatable = updatable;
        init();
    }

    protected void init() {
        super.init();
        // todo
    }
}
