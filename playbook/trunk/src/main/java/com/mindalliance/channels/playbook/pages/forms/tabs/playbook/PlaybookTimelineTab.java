package com.mindalliance.channels.playbook.pages.forms.tabs.playbook;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.components.SVGPanel;
import org.apache.wicket.behavior.StringHeaderContributor;

import java.util.UUID;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 29, 2008
 * Time: 10:06:23 PM
 */
public class PlaybookTimelineTab extends AbstractFormTab {

    protected SVGPanel svgPanel;

    public PlaybookTimelineTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        svgPanel = new SVGPanel("timeline", this, "timeline", EDITABLE, feedback);
        addReplaceable(svgPanel);
    }
}
