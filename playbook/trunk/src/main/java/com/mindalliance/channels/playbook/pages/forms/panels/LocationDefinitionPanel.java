package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 24, 2008
 * Time: 1:10:46 PM
 */
public class LocationDefinitionPanel extends AbstractComponentPanel {

    public LocationDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }
    
    protected void load() {
        super.load();
        // TODO
    }
}
