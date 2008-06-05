package com.mindalliance.channels.playbook.pages.forms.tabs.playbook;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.graphs.TimelinePanel;
import com.mindalliance.channels.playbook.support.models.RefContainer;
import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 29, 2008
 * Time: 10:06:23 PM
 */
public class PlaybookTimelineTab extends AbstractFormTab {

    protected TimelinePanel timelinePanel;

    public PlaybookTimelineTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        Playbook playbook = (Playbook)getElement().deref();
        RefContainer refContainer = new RefContainer(playbook.findAllOccurrences());
        timelinePanel = new TimelinePanel("timeline", new Model(refContainer));
        addReplaceable(timelinePanel);
    }
}
