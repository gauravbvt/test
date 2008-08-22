package com.mindalliance.channels.playbook.pages.forms.tabs.individual;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.RefListPanel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 20, 2008
 * Time: 4:46:02 PM
 */
public class IndividualJobsTab extends AbstractFormTab {

    private RefListPanel jobsPanel;

    public IndividualJobsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        jobsPanel = new RefListPanel("jobs", this, new RefQueryModel<List<Ref>>(getProject(), new Query("findAllJobsOf", getElement())));
        add(jobsPanel);
    }
}
