package com.mindalliance.channels.playbook.pages.forms.tabs.team;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.ifm.info.Location;
import com.mindalliance.channels.playbook.ifm.project.resources.Team;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 23, 2008
 * Time: 7:25:29 PM
 */
public class TeamJurisdictionTab  extends AbstractFormTab {

    protected LocationPanel jurisdictionPanel;

    public TeamJurisdictionTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        Team team = (Team)getElement().deref();
        jurisdictionPanel = new LocationPanel("jurisdiction", this, "jurisdiction", false, feedback);
        addReplaceable(jurisdictionPanel);
    }
}
